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
import java.util.List;

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
}
