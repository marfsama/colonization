package com.marf.colonization.rebuild;


import com.marf.colonization.mpskit.Ss;
import com.marf.colonization.saves.section.Unit;

import java.util.List;

import static com.marf.colonization.decompile.cmodules.Data.DAT_5338_savegame_header;
import static com.marf.colonization.rebuild.Util.clamp;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Minimap {
    private final Resources resources;
    private final Canvas canvas;
    private final GameData gameData;

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_9c7a_minimap_min_y
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_9c7c_minimap_min_x
     */
    private Point minimapMin = new Point();
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84ea_number_of_x_tiles_in_viewport
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84ec_number_of_y_tiles_in_viewport
     */
    private Point viewportTiles = new Point();

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0838_minimap_fractions_colors_table
     */
    private int[] fractionsColorsTable = new int[]{
            0xC, 0x9, 0xE, 0xD,
            0xF, 0x95, 0x36, 0xB,
            0x43, 0x6F, 0x75, 0x47,
            0x7, 0xB, 0x9, 0xA
    };

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_a526_terrain_minimap_colors*/
    private int[] terrainMinimapColorsRgb = new int[32];

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_84ee_some_width */
    public int someWidth;
    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_84f0_some_height */
    public int someHeight;

    public Minimap(Resources resources, Canvas canvas, GameData gameData) {
        System.out.println("create minimap with " + resources + " " + canvas);
        this.resources = resources;
        this.canvas = canvas;
        this.gameData = gameData;
    }

    /** @see com.marf.colonization.decompile.cmodules.Module14#FUN_7f05_0068_module_14_precalculate_minimap_terrain_pixels */
    public void init() {
        List<Ss.Sprite> terrainTiles = resources.getTerrain();
        for (int i = 0; i < 8; i++) {
            terrainMinimapColorsRgb[i] = terrainTiles.get(i).getIndexedImage()[8*16+8];
            terrainMinimapColorsRgb[i+8] = terrainTiles.get(i).getIndexedImage()[8*16+8];
            terrainMinimapColorsRgb[i+16] = terrainTiles.get(i).getIndexedImage()[8*16+8];
        }

        // artic
        terrainMinimapColorsRgb[17] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x18)).getIndexedImage()[8*16+8];
        // sea
        terrainMinimapColorsRgb[18] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x19)).getIndexedImage()[8*16+8];
        // sea lane
        terrainMinimapColorsRgb[19] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x1a)).getIndexedImage()[8*16+8];
        // unused special tile id 0x21 - maps to desert (0x1)
        terrainMinimapColorsRgb[20] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x21)).getIndexedImage()[8*16+8];
        // unused special tile id 0x31 - maps to desert (0x1)
        terrainMinimapColorsRgb[21] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x31)).getIndexedImage()[8*16+8];
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14#FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds
     */
    public void calculateMinimapBounds() {
        int initialX = gameData.viewportCenter.x - 28;  // 28 pixels left from center
        int initialY = gameData.viewportCenter.y - 19;  // 19 pixels up from center

        minimapMin.y = clamp(1, initialX, gameData.gameMap.mapSize.width - 57);
        minimapMin.x = clamp(1, initialY, gameData.gameMap.mapSize.height - 40);
    }


    /**
     * FUN_7f05_048a
     */
    public void renderMinimapPanel(int power) {
        calculateMinimapBounds();
        if (resources.getWoodTile() == null) {
            canvas.fillRect(241, 8, 79, 41, 0);
        } else {
            canvas.drawSpriteSheetEntry(resources.getWoodTile(), 241, 8, 79, 41, 0, 0);
        }

        canvas.drawRect(251, 8, 308, 48, 6);

        // draw minimap
        renderMinimap(power);


        // TODO: this does not work as expected
        // draw current visible rectangle
        int y1 = max(gameData.viewportMin.y, minimapMin.y) + 9;
        int x1 = max(gameData.viewportMin.x, minimapMin.x) + 252;

        int y2 = min(gameData.viewportMax.y, minimapMin.y + 38) - minimapMin.y + 9;
        int x2 = min(gameData.viewportMax.x, minimapMin.x + 55) + 252;
        canvas.drawRect(x1, y1, x2, y2, 15);

        // flip to front screen


    }

    /**
     * FUN_7f05_0346_module_14_draw_minimap
     */
    private void renderMinimap(int power) {
        renderMinimap(minimapMin.x, minimapMin.y, 70, 39, power, false);
    }


    /**
     * FUN_7f05_0118_module_14_draw_minimap
     */
    private void renderMinimap(int xPos, int yPos, int width, int height, int power, boolean flag) {
        int playerMask = 0;
        if (power != 0) {
            playerMask = 0x10 << power;
        }

        if (height <= 0) {
            return;
        }

        GameMap gameMap = gameData.gameMap;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int terrainOffset = gameMap.getMapOffset(xPos + x, yPos + y);
                int terrainType = gameMap.terrain[terrainOffset];
                int surface = gameMap.surface[terrainOffset];
                int visitor = gameMap.visitor[terrainOffset];
                int visibility = gameMap.visibility[terrainOffset];

                int color = determineMinimapColor(terrainType, visitor, surface, visibility, power, playerMask, flag, xPos + x, yPos + y);

                canvas.setPixel(xPos + x + 252, yPos + y + 9, color);
            }
        }

    }

    private int determineMinimapColor(
            int terrainType, int visitorData, int surfaceFlags, int visibilityData,
            int currentPlayer, int playerMask, boolean forceVisibility,
            int tileX, int tileY) {

        // Force visibility override
        if (forceVisibility) {
            return 0x0F; // Bright white
        }

        // Check player visibility
        if (playerMask != 0 && (visibilityData & playerMask) == 0) {
            return 0x00; // Black (fog of war)
        }

        // Check for village/tribe
        if ((surfaceFlags & 0x02) != 0) {
            int index = (visitorData >> 4) & 0x0F;
            return fractionsColorsTable[index];
        }

        // Check for units
        if ((surfaceFlags & 0x01) != 0) {
            Unit unit = gameData.gameMap.getUnitAt(tileX, tileY);

            int color = 0;

            if (unit != null) {
                // Check if unit belongs to current player
                if ((unit.getNationIndex().getId() & playerMask) != 0 || DAT_5338_savegame_header.field_0x22 != 0) {
                    int index = (visitorData >> 4) & 0x0F;
                    color = fractionsColorsTable[index];
                }

                // Check for privateer
                if (unit.getType().getId() == 0x10 && DAT_5338_savegame_header.field_0x22 != 0 && unit.getPrevious() < 0) {
                    color = 0x08; // don't show color
                }

                return color;
            }
        }

        // Default: terrain-based color
        if ((terrainType & 0x20) != 0) {
            // Special terrain handling
            boolean specialCondition = (terrainType & 0x80) != 0;
            return (byte) (specialCondition ? 0x1B : 0x1C);
        } else {
            // Normal terrain color mapping
            int terrainIndex = terrainType & 0x1F;
            return terrainMinimapColorsRgb[terrainIndex];
        }
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102#FUN_8007_000c_module_14_102_calculate_viewport()
     */

    public void calculateViewport() {
        // calculate number of tiles which fit in the viewport
        viewportTiles.x = 15 << gameData.zoomLevel;
        viewportTiles.y = 12 << gameData.zoomLevel;

        // colony view is always 5x5 tiles at full zoom. Note that only 3x3 tiles are painted
        if (gameData.colonyView) {
            viewportTiles.x = 5;
            viewportTiles.y = 5;
            gameData.zoomLevel = 0;
        }

        gameData.tileSize = 0x10 >> gameData.zoomLevel;

        // calculate viewport top left of the viewport
        gameData.viewportMin.y = gameData.viewportCenter.y - (viewportTiles.y / 2);
        gameData.viewportMin.x = gameData.viewportCenter.x - (viewportTiles.x / 2);

        // clamp viewport to the map size (if not in colony view)
        if (!gameData.colonyView) {
            // adjust the top left corner so that it is not < 1 and that all viewport tiles can be displayed.
            // Note that the first and last column and row are not drawn
            gameData.viewportMin.x = clamp(1, gameData.viewportMin.x, gameData.gameMap.mapSize.width - viewportTiles.x - 1);
            gameData.viewportMin.y = clamp(1, gameData.viewportMin.y, gameData.gameMap.mapSize.height - viewportTiles.y - 1);
        }

        // when the viewport is bigger than the map (in smalled and 2nd smallest zoom level),
        // there are gaps left and right and top and bottom.
        // This calculates the offset of the map drawing into the viewport (from top left)
        gameData.viewportOffset.x = 0;
        gameData.viewportOffset.y = 0;
        // Handle maps smaller than viewport (X-axis)
        if (gameData.gameMap.mapSize.width - 2 < viewportTiles.x) {
            gameData.viewportMin.x = 1;
            gameData.viewportOffset.x = (viewportTiles.x - gameData.gameMap.mapSize.width + 2) / 2;
            viewportTiles.x = gameData.gameMap.mapSize.width - 2;
        }

        // Handle maps smaller than viewport (Y-axis)
        if (gameData.gameMap.mapSize.height - 2 < viewportTiles.y) {
            gameData.viewportMin.y = 1;
            gameData.viewportOffset.x = (viewportTiles.y - gameData.gameMap.mapSize.height + 2) / 2;
            viewportTiles.y = gameData.gameMap.mapSize.height - 2;
        }

        // Calculate display dimensions based on DAT_0150 flag
        if (gameData.DAT_0150_some_flag) {
            if (!gameData.colonyView) {
                // Normal zoomed display
                someWidth = (15 << gameData.zoomLevel) + 2;
                someHeight = (12 << gameData.zoomLevel) + 2;
            } else {
                // Special display mode calculations
                calculateSpecialDisplayDimensions();
            }
        } else {
            // Full map display
            someWidth = gameData.gameMap.mapSize.width;
            someHeight = gameData.gameMap.mapSize.height;
        }

        // Final zoom-dependent calculations
        gameData.zoomLevelPercent = 0x64 >> gameData.zoomLevel;  // 100 >> zoomLevel
        gameData.scrollAmount = (0x5 << gameData.zoomLevel) + 0x5;

        // Calculate viewport maximum bounds
        gameData.viewportMax.x = gameData.viewportMin.x + viewportTiles.x - 1;
        gameData.viewportMax.y = gameData.viewportMin.y + viewportTiles.y - 1;
    }

    private void calculateSpecialDisplayDimensions() {
        // X-dimension calculation
        int maxX = Math.min(gameData.viewportMin.x + 5, gameData.gameMap.mapSize.width - 1);
        int minX = Math.max(0, gameData.viewportMin.x - 1);  // Clamp to 0
        someWidth = maxX - minX + 1;

        // Y-dimension calculation
        int maxY = Math.min(gameData.viewportMin.y + 5, gameData.gameMap.mapSize.height - 1);
        int minY = Math.max(0, gameData.viewportMin.y - 1);  // Clamp to 0
        someHeight = maxY - minY + 1;
    }

}
