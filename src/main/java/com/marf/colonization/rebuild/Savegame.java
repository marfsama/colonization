package com.marf.colonization.rebuild;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.marf.colonization.saves.SaveFileReader;
import com.marf.colonization.saves.SavesReader;
import com.marf.colonization.saves.section.SaveFile;
import com.marf.colonization.saves.section.Tables;
import com.marf.colonization.saves.section.Viewport;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;

public class Savegame {
    private final GameData gameData;

    public Savegame(GameData gameData) {
        this.gameData = gameData;
    }

    /** FUN_8a1f_073c_module_19_load_savegame */
    public void loadSavegame(String filename) {

        try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/COLONY01.SAV").getAbsoluteFile())) {
            ObjectMapper tableMapper = new ObjectMapper(new YAMLFactory());
            Tables tables = tableMapper.readValue(SavesReader.class.getResourceAsStream("/tables.yaml"), Tables.class);

            SaveFileReader reader = new SaveFileReader(tables, stream);
            SaveFile saveFile = reader.readAll();

            // header stuff
            gameData.mapSize = new Dimension(saveFile.getHeader().getMapSize().getX(), saveFile.getHeader().getMapSize().getY());
            // player

            // viewport
            Viewport viewport = saveFile.getViewports().get(saveFile.getHeader().getViewportPower());
            gameData.viewportCenter = new Point(viewport.getX(), viewport.getY());
            gameData.zoomLevel = viewport.getZoom();


        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

}
