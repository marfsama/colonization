package com.marf.colonization.rebuild;

import com.marf.colonization.mpskit.Madspack;
import com.marf.colonization.mpskit.Ss;
import lombok.Getter;

import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
public class Resources {
    /** viceroy.pal */
    private Color[] palette;
    private List<Ss.Sprite> woodTile;
    private List<Ss.Sprite> terrain;


    /**
     * Does stuff from FUN_8a1f_2a58_module_19_119_load_game_resources
     */
    public static Resources load() throws IOException {
        Resources resources = new Resources();
        resources.palette = resources.loadPalette("VICEROY.PAL");
        resources.woodTile = resources.loadSpriteSheet("WOODTILE.SS");
        resources.terrain = resources.loadSpriteSheet("TERRAIN.SS");
        return resources;
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
