package com.marf.colonization.rebuild;

import com.marf.colonization.mpskit.Ff;
import com.marf.colonization.mpskit.Madspack;
import com.marf.colonization.mpskit.Ss;
import lombok.Getter;

import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Getter
public class Resources {
    /** viceroy.pal */
    private Color[] palette;
    private BufferedImage woodTile;
    private List<Ss.Sprite> terrain;
    /** PHYS0.SS */
    private List<Ss.Sprite> surface;
    /** ICONS.SS */
    private List<Ss.Sprite> icons;
    /**
     * FONTTINY.FF
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_088e_fonttiny_address */
    private Ff.Font fontTiny;
    /**
     * FONTINTR.FF
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_2618_fontintr_address */
    private Ff.Font fontIntr;

    /**
     * NAMES.TXT
     * @see "FUN_8a1f_14bc_module_19_119_read_config_text_labels_pedia"
     * */
    private TxtFile names;

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_51e8_unit_config_array */
    private List<UnitTypeConfig> unitTypeConfigs = new ArrayList<>();
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_5496_order_letters
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_977e_order_name_indices
     */
    private List<OrderConfig> orderConfigs = new ArrayList<>();


    /**
     * Does stuff from FUN_8a1f_2a58_module_19_119_load_game_resources
     */
    public static Resources load() throws IOException {
        Resources resources = new Resources();
        resources.palette = resources.loadPalette("VICEROY.PAL");
        // Sprite Sheets
        resources.woodTile = resources.loadSpriteSheet("WOODTILE.SS").get(0).getImage();
        resources.terrain = resources.loadSpriteSheet("TERRAIN.SS");
        resources.surface = resources.loadSpriteSheet("PHYS0.SS");
        resources.icons = resources.loadSpriteSheet("ICONS.SS");
        // Fonts
        resources.fontTiny = resources.loadFont("FONTTINY.FF");
        resources.fontIntr = resources.loadFont("FONTINTR.FF");
        resources.names = TxtFile.open(Path.of("src/main/resources", "NAMES.TXT"));

        resources.loadUnitTypeConfigs();
        resources.loadOrderConfigs();
        return resources;
    }

    private Ff.Font loadFont(String filename) throws IOException {
        try (FileImageInputStream stream = new FileImageInputStream(Path.of("src/main/resources", filename).toFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            Ff ff = new Ff(madspack);
            return ff.read();
        }
    }

    private List<Ss.Sprite> loadSpriteSheet(String filename) throws IOException {
        try (FileImageInputStream stream = new FileImageInputStream(Path.of("src/main/resources", filename).toFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            Ss ss = new Ss(madspack);
            return ss.getSprites();
        }
    }

    private Color[] loadPalette(String filename) throws IOException {
        byte[] colorData = Files.readAllBytes(Path.of("src/main/resources", filename));
        this.palette = new Color[colorData.length / 3];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = new Color((colorData[i * 3] & 0xff) / 64.0f, (colorData[i * 3 + 1] & 0xff) / 64.0f, (colorData[i * 3 + 2] & 0xff) / 64.0f);
        }
        return palette;
    }

    /** @see "parts of FUN_8a1f_14bc_module_19_119_read_config_text_labels_pedia" */
    private void loadUnitTypeConfigs() {
        // note: this is very similar to the c code, thats the reason this code looks kinda weird
        names.seek("UNIT");

        for (int i = 0; i < 0x17; i++) {
            names.nextLine();
            UnitTypeConfig type = new UnitTypeConfig();
            type.setName(names.readTokenAsString());
            type.setIcon(names.readTokenAsInteger());
            type.setMovement(1 << names.readTokenAsInteger());
            type.setCombat(names.readTokenAsInteger());
            type.setAttack(names.readTokenAsInteger());
            type.setCargo(names.readTokenAsInteger());
            type.setSize(names.readTokenAsInteger());
            type.setCost(names.readTokenAsInteger());
            type.setTools(names.readTokenAsInteger());
            type.setGuns(names.readTokenAsInteger());
            type.setHull(names.readTokenAsInteger());
            type.setRole(names.readTokenAsInteger());

            unitTypeConfigs.add(type);
        }
    }

    /** @see "parts of FUN_8a1f_14bc_module_19_119_read_config_text_labels_pedia" */
    private void loadOrderConfigs() {
        // note: this is very similar to the c code, thats the reason this code looks kinda weird
        names.seek("ORDERS");

        for (int i = 0; i < 0xd; i++) {
            names.nextLine();
            OrderConfig orderConfig = new OrderConfig();
            orderConfig.setName(names.readTokenAsString());
            orderConfig.setOrderLetter(names.readTokenAsString().charAt(0));
            orderConfigs.add(orderConfig);
        }
    }

    private static class TxtFile {
        private final String content;
        private Scanner currentScanner;
        private Scanner currentLine;

        private TxtFile(String content) {
            this.content = content;
        }

        public static TxtFile open(Path path) throws IOException {
            return new TxtFile(Files.readString(path));
        }

        /** @see "FUN_8778_001a_module_17_text_file_prepare_read_section" */
        public void seek(String sectionName) {
            currentScanner = new Scanner(content);
            while (currentScanner.hasNext()) {
                String line = currentScanner.nextLine();
                if (line.equals("@"+sectionName)) {
                    break;
                }
            }
            if (!currentScanner.hasNext()) {
                throw new IllegalStateException("cannot find section "+sectionName);
            }
        }

        /** @see "Module17#FUN_8778_0106_read_line" */
        public void nextLine() {
            String nextLine = currentScanner.nextLine();
            // if the line has a comment...remove it
            int commentIndex = nextLine.indexOf(";");
            if (commentIndex >= 0) {
                nextLine = nextLine.substring(0, commentIndex);
            }
            this.currentLine = new Scanner(nextLine);
            this.currentLine.useDelimiter(", *");
        }

        /** @see "Module17#FUN_8778_01c8_add_comma_seperated_token_to_string_table" */
        public String readTokenAsString() {
            return currentLine.next().trim();
        }

        /** @see "Module17#FUN_8778_0198_parse_next_token_as_int" */
        public int readTokenAsInteger() {
            String nextToken = currentLine.next();
            return Integer.parseInt(nextToken.trim());
        }
    }
}
