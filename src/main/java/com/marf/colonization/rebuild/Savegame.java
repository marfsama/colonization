package com.marf.colonization.rebuild;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.marf.colonization.decompile.cmodules.SavegameHeader;
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

        try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/"+filename).getAbsoluteFile())) {
            ObjectMapper tableMapper = new ObjectMapper(new YAMLFactory());
            Tables tables = tableMapper.readValue(SavesReader.class.getResourceAsStream("/tables.yaml"), Tables.class);

            SaveFileReader reader = new SaveFileReader(tables, stream);
            SaveFile saveFile = reader.readAll();

            // header stuff
            gameData.savegameHeader = new SavegameHeader();
            gameData.savegameHeader.active_unit = saveFile.getHeader().getActiveUnit();
            gameData.savegameHeader.viewport_power = saveFile.getHeader().getViewportPower();
            gameData.savegameHeader.maybe_player_controlled_power = saveFile.getHeader().getPlayerControlledPower();
            gameData.savegameHeader.maybe_current_player = saveFile.getHeader().getMaybe_current_player();
            gameData.savegameHeader.field_0x22_maybe_current_turn = saveFile.getHeader().getTurn();

            gameData.gameMap.mapSize = new Dimension(saveFile.getHeader().getMapSize().getX(), saveFile.getHeader().getMapSize().getY());
            // player

            // viewport
            Viewport viewport = saveFile.getViewports().get(saveFile.getHeader().getViewportPower());
            gameData.viewportCenter = new Point(viewport.getX(), viewport.getY());
            gameData.zoomLevel = viewport.getZoom();

            // maps
            gameData.gameMap.terrain = saveFile.getMap().getTerrain();
            gameData.gameMap.surface = saveFile.getMap().getSurface();
            gameData.gameMap.visitor = saveFile.getMap().getVisitor();
            gameData.gameMap.visibility = saveFile.getMap().getVisibility();
            gameData.gameMap.units = saveFile.getUnits();
            gameData.gameMap.indianVillages = saveFile.getIndianVillages().stream().map(IndianVillage::new).toList();
            gameData.gameMap.indianTribes = saveFile.getIndianTribes();
            gameData.gameMap.colonies = saveFile.getColonies().stream().map(Colony::new).toList();


        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

}
