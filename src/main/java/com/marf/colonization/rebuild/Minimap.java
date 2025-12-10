package com.marf.colonization.rebuild;


import com.marf.colonization.mpskit.Ss;
import com.marf.colonization.saves.section.Unit;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.marf.colonization.rebuild.Util.clamp;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @see com.marf.colonization.decompile.cmodules.Module14_Minimap
 * @see com.marf.colonization.decompile.cmodules.Module14_102_Map
 */
public class Minimap {
    public static final int MINIMAP_PANEL_X = 241;
    public static final int MINIMAP_PANEL_Y = 8;
    public static final int MINIMAP_PANEL_WIDTH = 79;
    public static final int MINIMAP_PANEL_HEIGHT = 41;

    public static final int MINIMAP_MAP_X = MINIMAP_PANEL_X + 11;
    public static final int MINIMAP_MAP_Y = MINIMAP_PANEL_Y;
    public static final int MINIMAP_MAP_WIDTH = 57;
    public static final int MINIMAP_MAP_HEIGHT = 41;
    private final Resources resources;
    private final Canvas canvas;
    private final GameData gameData;

    /**
     * Top left corner of the map shown in the minimap. Note: the minimap is 57 pixels wide and the game map also,
     * so the x coordinate is always zero.
     *
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

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_a526_terrain_minimap_colors
     */
    private int[] terrainMinimapColorsRgb = new int[32];

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84ee_some_width
     */
    public int someWidth;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84f0_some_height
     */
    public int someHeight;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0188_maybe_prime_resource_per_terrain_type
     */
    public int[] primeResourcePerTerrainType = {6, 1, 2, 3, 4, 5, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, -1, 7, -1, 0xC, 0xD};

    public Minimap(Resources resources, Canvas canvas, GameData gameData) {
        System.out.println("create minimap with " + resources + " " + canvas);
        this.resources = resources;
        this.canvas = canvas;
        this.gameData = gameData;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_Minimap#FUN_7f05_0068_module_14_precalculate_minimap_terrain_pixels
     */
    public void init() {
        List<Ss.Sprite> terrainTiles = resources.getTerrain();
        for (int i = 0; i < 8; i++) {
            terrainMinimapColorsRgb[i] = terrainTiles.get(i).getIndexedImage()[8 * 16 + 8];
            terrainMinimapColorsRgb[i + 8] = terrainTiles.get(i).getIndexedImage()[8 * 16 + 8];
            terrainMinimapColorsRgb[i + 16] = terrainTiles.get(i).getIndexedImage()[8 * 16 + 8];
        }

        // artic
        terrainMinimapColorsRgb[17] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x18)).getIndexedImage()[8 * 16 + 8];
        // sea
        terrainMinimapColorsRgb[18] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x19)).getIndexedImage()[8 * 16 + 8];
        // sea lane
        terrainMinimapColorsRgb[19] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x1a)).getIndexedImage()[8 * 16 + 8];
        // unused special tile id 0x21 - maps to desert (0x1)
        terrainMinimapColorsRgb[20] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x21)).getIndexedImage()[8 * 16 + 8];
        // unused special tile id 0x31 - maps to desert (0x1)
        terrainMinimapColorsRgb[21] = terrainTiles.get(gameData.gameMap.getTerrainTileIdByTerrainType(0x31)).getIndexedImage()[8 * 16 + 8];
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_Minimap#FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds
     */
    public void calculateMinimapBounds() {
        int topLeftX = gameData.viewportCenter.x - MINIMAP_MAP_WIDTH / 2;  // 28 pixels left from center
        int topLeftY = gameData.viewportCenter.y - MINIMAP_MAP_HEIGHT / 2;  // 19 pixels up from center

        minimapMin.x = clamp(1, topLeftX, gameData.gameMap.mapSize.width - MINIMAP_MAP_WIDTH - 1);
        minimapMin.y = clamp(1, topLeftY, gameData.gameMap.mapSize.height - MINIMAP_MAP_HEIGHT - 1);
    }


    /**
     * FUN_7f05_048a
     */
    public void renderMinimapPanel(int power) {
        calculateMinimapBounds();
        if (resources.getWoodTile() == null) {
            canvas.fillRect(canvas.getBackscreen(), MINIMAP_PANEL_X, MINIMAP_PANEL_Y, MINIMAP_PANEL_WIDTH, MINIMAP_PANEL_HEIGHT, 0);
        } else {
            canvas.drawSpriteTiled(canvas.getBackscreen(), resources.getWoodTile(), MINIMAP_PANEL_X, MINIMAP_PANEL_Y, MINIMAP_PANEL_WIDTH, MINIMAP_PANEL_HEIGHT, 0, 0);
        }

        canvas.drawRect(canvas.getBackscreen(), MINIMAP_MAP_X, MINIMAP_MAP_Y, MINIMAP_MAP_X + MINIMAP_MAP_WIDTH - 1, MINIMAP_MAP_Y + MINIMAP_MAP_HEIGHT - 1, 6);

        // draw minimap
        renderMinimap(power);


        // draw current visible rectangle
        int x1 = max(gameData.viewportMin.x, minimapMin.x) + MINIMAP_MAP_X + 1;
        int y1 = max(gameData.viewportMin.y, minimapMin.y) + MINIMAP_MAP_Y + 1;

        int x2 = min(gameData.viewportMax.x, minimapMin.x + MINIMAP_MAP_WIDTH - 2) + MINIMAP_MAP_X;
        int y2 = min(gameData.viewportMax.y, minimapMin.y + MINIMAP_MAP_HEIGHT - 2) + MINIMAP_MAP_Y;
        canvas.drawRect(canvas.getBackscreen(), x1, y1, x2, y2, 15);

        // flip to front screen
        // skipped, always draw to front screen
    }

    /**
     * FUN_7f05_0346_module_14_draw_minimap
     */
    private void renderMinimap(int power) {
        renderMinimap(minimapMin.x, minimapMin.y, MINIMAP_MAP_WIDTH - 2, MINIMAP_MAP_HEIGHT - 2, power, false);
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

                canvas.setPixel(canvas.getBackscreen(), x + MINIMAP_MAP_X + 1, y + MINIMAP_MAP_Y + 1, color);
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
                if ((unit.getNationIndex().getId() & playerMask) != 0 || gameData.savegameHeader.field_0x22_maybe_current_turn != 0) {
                    int index = (visitorData >> 4) & 0x0F;
                    color = fractionsColorsTable[index];
                }

                // Check for privateer
                if (unit.getType().getId() == 0x10 && gameData.savegameHeader.field_0x22_maybe_current_turn != 0 && unit.getPrevious() < 0) {
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
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_000c_module_14_102_calculate_viewport()
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


    // draw map stuff, Module14_102_Map

    private int DAT_0180_maybe_colony_vs_map_view;
    private int DAT_a862_power_mask;
    private int DAT_a558;

    private int DAT_a554_draw_map_x_in_pixels;
    private int DAT_a556_draw_map_y_in_pixels;

    //private int DAT_a550_draw_map_x_in_tiles;
    //private int DAT_a552_draw_map_y_in_tiles;

    private int DAT_1e72_sub_tile_x;
    private int DAT_1e73_sub_tile_y;


    private int DAT_a548_terrain_map_pointer_to_current_position;
    private int DAT_a544_surface_map_pointer;
    private int DAT_a54c_visibility_map_pointer;

    private int DAT_a863_value_from_terrain_map;
    private int DAT_a865_current_terrain;
    private int DAT_a864_value_from_visibility_map;
    private int DAT_a866_adjusted_current_terrain_type;
    private int DAT_0184_show_hidden_terrain_state;

    public int[] DAT_00a8_x_directions = new int[] {0, 1, 0, -1, 0, 0};
    public int[] DAT_00ae_y_directions = new int[] {-1, 0, 1, 0, 0, 0};


    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_109c_module_14_102_draw_map_for_type
     */
    public void drawMapForType(int mapViewType, int power) {
        DAT_0180_maybe_colony_vs_map_view = mapViewType;
        FUN_8007_1016_module_14_102_draw_map_viewport(power);
        DAT_0180_maybe_colony_vs_map_view = 0;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_1016_module_14_102_draw_map_viewport
     */
    public void FUN_8007_1016_module_14_102_draw_map_viewport(int power) {
        calculateViewport();

        // if the map is smaller than the viewport (so it does not cover all space), draw wood tiles as background
        if (gameData.viewportOffset.x > 0 || gameData.viewportOffset.y > 0) {
            if (resources.getWoodTile() != null) {
                canvas.drawSpriteTiled(canvas.getScratch(), resources.getWoodTile(), 0, 0,
                        canvas.getScratch().getWidth(), canvas.getScratch().getHeight(), 0, -8);
            } else {
                canvas.clear(canvas.getScratch());
            }
        }

        drawMap(gameData.viewportMin.x, gameData.viewportMin.y, this.viewportTiles.x, this.viewportTiles.y, power);
    }

    /**
     * FUN_1373_0040_should_draw_depending_on_distance_maybe
     */
    public boolean FUN_1373_0040_should_draw_depending_on_distance_maybe(int x, int y, int flag_colony_or_map_view) {
        boolean bVar1;

        x = Math.abs(x);
        y = Math.abs(y);

        bVar1 = (y + x) < 2;
        if (flag_colony_or_map_view != 1) {
            if ((x < 2) && (y < 2)) {
                bVar1 = true;
            }
// not clear what this does
//            if (((param_3_flag_colony_or_map_view != 2) &&
//                    (bVar1 = (bool)(bVar1 | (int)(param_2_y + param_1_x) < 3),
//            param_3_flag_colony_or_map_view != 3)) && (((int)param_1_x < 2 || ((int)param_2_y < 2)))) {
//                bVar1 = true;
        }
        return bVar1;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_0d60_module_14_102_draw_map
     */
    public void drawMap(int x_min, int y_min, int width_in_tiles, int height_in_tiles, int power) {
        if (power < 0) {
            DAT_a862_power_mask = 0;
        } else {
            DAT_a862_power_mask = 1 << (power + 4);
        }

        calculateViewport();

        if (x_min >= gameData.viewportMax.x || y_min >= gameData.viewportMax.y) {
            return;
        }

        int x_max = Math.min(x_min + width_in_tiles, gameData.viewportMax.x);
        int y_max = Math.min(y_min + height_in_tiles, gameData.viewportMax.y);
        x_min = Math.max(x_min, gameData.viewportMin.x);
        y_min = Math.max(y_min, gameData.viewportMin.y);

        int width = x_max - x_min;
        int height = y_max - y_min;

        int mapOffset;
        int map_stride_in_tiles;
        if (gameData.DAT_0150_some_flag) {
            mapOffset = (y_min + 1) * someWidth + x_min + 1;
            map_stride_in_tiles = width;
        } else {
            int x = Math.min(1, x_min);
            int y = Math.min(1, y_min);
            mapOffset = gameData.gameMap.getMapOffset(x, y);
            map_stride_in_tiles = gameData.gameMap.mapSize.width;
        }

        // loop over the map

        DAT_a556_draw_map_y_in_pixels = gameData.viewportOffset.y + gameData.tileSize - 1;

        for (int y = y_min; y <= y_max; y += 1, DAT_a556_draw_map_y_in_pixels += gameData.tileSize) {
            DAT_a548_terrain_map_pointer_to_current_position = mapOffset;
            DAT_a544_surface_map_pointer = mapOffset;
            DAT_a54c_visibility_map_pointer = mapOffset;

            //  don't draw first and last row
            boolean validY = (y > 1) && (y < gameData.gameMap.mapSize.height - 1);

            // Calculate perspective for colony view
            int local_20_something_y = 0;
            if (DAT_0180_maybe_colony_vs_map_view != 0) {
                local_20_something_y = Math.abs(y - gameData.viewportCenter.y);
            }

            DAT_a558 = 0;

            DAT_a554_draw_map_x_in_pixels = gameData.viewportOffset.x + gameData.tileSize / 2;

            for (int x = x_min; x <= x_max; x += 1, DAT_a554_draw_map_x_in_pixels += gameData.tileSize) {
                // don't draw first and last column
                boolean validX = (x > 0) && (x < gameData.gameMap.mapSize.width - 1);

                boolean valid = validX && validY;

                if (DAT_0180_maybe_colony_vs_map_view != 0) {
                    // note: result of the function is inverted
                    valid |= !FUN_1373_0040_should_draw_depending_on_distance_maybe(Math.abs(x - gameData.viewportCenter.x), y, DAT_0180_maybe_colony_vs_map_view);
                }

                FUN_8007_0938_module_14_102_draw_map_tile(x, y, valid);

// draw rectangle around each tiles
//                canvas.drawRect(canvas.getScratch(),
//                        DAT_a554_draw_map_x_in_pixels-8,
//                        DAT_a556_draw_map_y_in_pixels-15,
//                        DAT_a554_draw_map_x_in_pixels + gameData.tileSize - 1-8,
//                        DAT_a556_draw_map_y_in_pixels + gameData.tileSize - 1-15, 15);
// draw x/y of each tile
//                canvas.drawTextSmall(canvas.getScratch(), DAT_a554_draw_map_x_in_pixels-8,DAT_a556_draw_map_y_in_pixels-15,  15,""+x);
//                canvas.drawTextSmall(canvas.getScratch(), DAT_a554_draw_map_x_in_pixels-8,DAT_a556_draw_map_y_in_pixels-15+7,  15,""+y);
// draw the terrain value in hex for each tile
//                canvas.drawTextBig(canvas.getScratch(), DAT_a554_draw_map_x_in_pixels-8,DAT_a556_draw_map_y_in_pixels-15,  15, String.format("%02x", gameData.gameMap.getTerrain(x,y)));


                if (validX && validY) {
                    mapOffset++;
                }

                // negate bit 1 of DAT_a558
                DAT_a558 = DAT_a558 ^ 1;
            }

            mapOffset += map_stride_in_tiles;


        }

    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_0558_module_14_102_draw_surface_sprite
     */
    public void FUN_8007_0558_module_14_102_draw_surface_sprite(int spriteIndex) {
        if (gameData.zoomLevelPercent < 100) {
            // zoom level < 100%
            //FUN_1c3a_000a(DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, gameData.zoomLevelPercent, DAT_016a_phys0_sprite_sheet);
        } else {
            // zoom level == 100%
            canvas.drawSpriteSheetSprite(canvas.getScratch(), resources.getSurface(),
                    DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8,
                    DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf,
                    spriteIndex);
        }
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_067c_module_14_102_draw_terrain_tile
     */
    public void FUN_8007_067c_module_14_102_draw_terrain_tile_only_over_black_pixels(int spriteIndex) {
        if (gameData.zoomLevelPercent < 100) {
            // zoom level < 100%
            //FUN_1101_00b4_blit_terrain_sprite(DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, gameData.zoomLevelPercent, DAT_016a_phys0_sprite_sheet);
        } else {
            // zoom level == 100%
            canvas.drawSpriteSheetSpriteOverBlackPixels(canvas.getScratch(), resources.getTerrain(),
                    DAT_a554_draw_map_x_in_pixels - 8, DAT_a556_draw_map_y_in_pixels - 0xf, spriteIndex);
        }
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_05b8_module_14_102_draw_terrain_tile
     */
    public void FUN_8007_05b8_module_14_102_draw_terrain_tile(int spriteIndex) {
        if (gameData.zoomLevel == 0) {
            canvas.drawSpriteSheetSprite(canvas.getScratch(), resources.getTerrain(),
                    DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8,
                    DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf,
                    gameData.gameMap.getTerrainTileIdByTerrainType(spriteIndex) + 1);
        } else {
            FUN_1101_0126(resources.getTerrain(), spriteIndex, canvas.getScratch(), DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, gameData.zoomLevel);
        }
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Code11#FUN_1101_0126
     */
    public void FUN_1101_0126(List<Ss.Sprite> terrain, int spriteIndex, BufferedImage scratch, int DAT_a554_draw_map_x_in_pixels, int DAT_a556_draw_map_y_in_pixels, int zoomLevel) {
        // TODO
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_0938_module_14_102_draw_map_tile
     */
    public void FUN_8007_0938_module_14_102_draw_map_tile(int x, int y, boolean showFogOfWar) {
        // note: one of the next 2 may be a surface pointer
        DAT_a863_value_from_terrain_map = gameData.gameMap.getTerrain(x, y);
        int currentTerrain = gameData.gameMap.getTerrain(x, y) & 0xff;
        DAT_a865_current_terrain = currentTerrain;

        DAT_a864_value_from_visibility_map = gameData.gameMap.visibility[y * gameData.gameMap.mapSize.width + x];

        DAT_a866_adjusted_current_terrain_type = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(currentTerrain);

        boolean fogOfWar = false;

        if (DAT_a862_power_mask != 0) {
            if ((DAT_a864_value_from_visibility_map & DAT_a862_power_mask) == 0) {
                if (showFogOfWar) {
                    fogOfWar = true;
                }
            }
        }

        int local_14_river = DAT_a865_current_terrain & 0xc0;
        int local_22_base_terrain = DAT_a865_current_terrain & 0x7;

        if (fogOfWar) {
            // draw fog of war tile
            FUN_8007_0558_module_14_102_draw_surface_sprite(0x95);
            if (gameData.zoomLevel != 0) {
                return;
            }
            int local_24_is_sea = 0;
            if (DAT_a866_adjusted_current_terrain_type == 0x19 || DAT_a866_adjusted_current_terrain_type == 0x1a) {
                local_24_is_sea = 1;
            }
            // AX is still 0x95, fog of war
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(x, y, true, local_24_is_sea == 1, false);
            return;
        }
        int local_1e_surrounding_terrain_map = 0;
        int local_6_is_sea_tile = 0;
        int local_4_terrain_type = 0x19;
        if ((DAT_a866_adjusted_current_terrain_type & 0x1f) == 0x19 || DAT_a866_adjusted_current_terrain_type == 0x1a) {
            local_4_terrain_type = DAT_a866_adjusted_current_terrain_type;
            local_1e_surrounding_terrain_map = FUN_8007_01b4_module_14_102_analyze_surrounding_terrain(x, y);
            local_6_is_sea_tile = 1;
        }
        if (local_6_is_sea_tile == 1 && local_1e_surrounding_terrain_map == 0) {

            FUN_8007_05b8_module_14_102_draw_terrain_tile(local_4_terrain_type);
            if (gameData.zoomLevel != 0) {
                return;
            }
            if (DAT_0180_maybe_colony_vs_map_view != 0) {
                // note: prime resources start at 0x59
                int local_20 = FUN_1373_0458_get_prime_resource_at(x, y);
                if (local_20 >= 0) {
                    FUN_8007_0558_module_14_102_draw_surface_sprite(local_20 + 0x5a);
                }
            }
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(x, y, false, true, true);
            return;
        }

        int terrainSpriteId;
        if (DAT_a866_adjusted_current_terrain_type < 0x18) {
            // Regular terrain: use lower 3 bits for base type
            terrainSpriteId = DAT_a866_adjusted_current_terrain_type & 0x07;
        } else {
            // Special terrain: use as-is
            terrainSpriteId = DAT_a866_adjusted_current_terrain_type & 0xFF;
        }

        // Special case: desert (0x01)
        if (terrainSpriteId == 0x01) {  // base desert
            // Check if desert with forest (looks like a really convoluted way of checking "terrain > 0x08".
            if ((DAT_a866_adjusted_current_terrain_type >= 0x08 && DAT_a866_adjusted_current_terrain_type < 0x10) ||
                    (DAT_a866_adjusted_current_terrain_type >= 0x10 && DAT_a866_adjusted_current_terrain_type < 0x18)) {
                terrainSpriteId = 0x11;  // note: desert with trees are sprite id 0x8
            }
        }
        // draw base terrain
        FUN_8007_05b8_module_14_102_draw_terrain_tile(terrainSpriteId);

        if (gameData.zoomLevel == 0) {
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(x, y, false, local_6_is_sea_tile == 1, false);
        }

        // forest (don't draw forest on desert)
        if (terrainSpriteId != 0x11 && local_6_is_sea_tile == 0) {
            if ((DAT_a866_adjusted_current_terrain_type & 0x08) != 0 && (DAT_a866_adjusted_current_terrain_type & 0x1f) < 0x19) {
                int mask = FUN_8007_041e_module_14_102_get_forest_neighbours_mask(x, y, 0x03);
                FUN_8007_0558_module_14_102_draw_surface_sprite(mask + 0x41); // 0x40 = forest
            }
        }
        // plowed field
        if ((gameData.gameMap.getSurfaceAt(x, y) & 0x40) > 0) {
            FUN_8007_0558_module_14_102_draw_surface_sprite(0x96); // 0x95 is plowed field
        }

        // mountains (and not sea)
        if ((DAT_a866_adjusted_current_terrain_type & 0x20) > 0 && local_6_is_sea_tile == 0) {
            int isMajorMountain = DAT_a866_adjusted_current_terrain_type & 0xA0;
            int mask = FUN_8007_0374_module_14_102_get_neighbours_mountains(x, y, 3, isMajorMountain);
            FUN_8007_0558_module_14_102_draw_surface_sprite(mask + (isMajorMountain != 0xA0 ? 0x31 : 0x21));
        }

        // rivers (and not sea)
        if ((DAT_a866_adjusted_current_terrain_type & 0x40) > 0 && local_6_is_sea_tile == 0) {
            int isMajorRiver = DAT_a866_adjusted_current_terrain_type & 0x80;
            int mask = FUN_8007_0314_module_14_102_get_neighbours_by_bitfield(x, y, 0x3, 0x40);
            FUN_8007_0558_module_14_102_draw_surface_sprite(mask + (isMajorRiver != 0x80 ? 0x11 : 0x1));
        }

        if (/*DAT_017a_zoom_level > 0 &&*/ DAT_0184_show_hidden_terrain_state == 0) {
            // prime resources
            int primeResource = FUN_1373_0458_get_prime_resource_at(x, y);
            // note: primeResource == -1 when no prime resource placed
            if (primeResource > 0 /*&& DAT_0180_maybe_colony_vs_map_view == 0*/) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x5a + primeResource);
            }

            // rumor icon
            int hasRumor = gameData.gameMap.FUN_1373_0540_get_rumor_at(x, y);
            if (hasRumor > 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x68);
            }
        }

        // road or colony (and not sea and in max zoom level)?
        if ((gameData.gameMap.getSurfaceAt(x, y) & 0x0A) != 0 && local_6_is_sea_tile == 0 && DAT_0184_show_hidden_terrain_state == 0) {
            int mask = FUN_8007_04e4_module_14_102_get_terrain_neighbours_bitmask_8_directions_first_map_pointer(x, y, 0x1, 0x0a);
            // single road, no adjected roads
            if (mask == 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x51);
            } else {
                // draw each road as a single tile
                int directionMask = 1;
                for (int direction = 0; direction < 8; direction++) {
                    if ((directionMask & mask) != 0) {
                        FUN_8007_0558_module_14_102_draw_surface_sprite(0x52 + direction);
                    }
                    directionMask <<= 1;
                }
            }

        }

        // stop if not a sea tile
        if (local_6_is_sea_tile == 0) {
            return;
        }

        // draw shores
        int local_8 = -1;

        int landMask = DAT_a86a_adjected_land_bitmask;
        // 0xdd = 1101 1101      0xc1 = 1100 0001
        if ((landMask & 0xdd) == 0xc1) {
            local_8 = 0;
        }
        // 0x77 = 0111 0111      0x07 = 0000 0111
        if ((landMask & 0x77) == 0x07) {
            local_8 = 1;
        }
        // 0x77 = 0111 0111      0x07 = 0111 0000
        if ((landMask & 0x77) == 0x70) {
            local_8 = 2;
        }
        // 0xdd = 1101 1101      0x1c = 0001 1100
        if ((landMask & 0xdd) == 0x1c) {
            local_8 = 3;
        }

        if (local_8 < 0) {
            // need half tiles
            for (int direction = 0; direction < 4; direction++) {
                // Calculate next direction with wrap-around (0→1, 1→2, 2→3, 3→0)
                int nextDirection = (direction + 1) & 3;

                // calculate position in a tile, depending on the current direction/quadrant
                DAT_1e72_sub_tile_x = (nextDirection & 0x3E) << 2;
                DAT_1e73_sub_tile_y = (direction & 0xFE) << 2;

                // note: calculated in #FUN_8007_01b4_module_14_102_analyze_surrounding_terrain
                // 0x6d contains 8 sets of tiles. were each tile of the set is for each of the 4 directions/quadrants
                // the first 4 images are empty
                int spriteData = DAT_2cec_adjection_land_stuff[direction];
                int spriteId = (spriteData << 2) + direction + 0x6D; // quarter sprites for each of the corners
                FUN_8007_0558_module_14_102_draw_surface_sprite(spriteId);
            }
            DAT_1e72_sub_tile_x = 0;
            DAT_1e73_sub_tile_y = 0;
        } else {
            // just the corners
            DAT_1e72_sub_tile_x = 0;
            DAT_1e73_sub_tile_y = 0;
            FUN_8007_0558_module_14_102_draw_surface_sprite(0x97 + local_8);
        }

        // draw sea tile or land tile over the black part of the shore tile
        FUN_8007_067c_module_14_102_draw_terrain_tile_only_over_black_pixels(gameData.gameMap.getTerrainTileIdByTerrainType(local_4_terrain_type));

        // river to shore tiles
        if (local_14_river != 0) {
            int local_12_baseSprite = (local_14_river & 0x80) != 0 ? 0x8d : 0x8d + 4;
            for (int local_e_direction = 0; local_e_direction < 4; local_e_direction++) {
                int dy = DAT_00ae_y_directions[local_e_direction];
                int dx = DAT_00a8_x_directions[local_e_direction];
                int terrain = gameData.gameMap.getTerrain(x+dx, y+dy);

                if ((terrain & 0x40) > 0) {
                    int adjustedTerrain2 = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain((byte) terrain);

                    if (adjustedTerrain2 != 0x19 && adjustedTerrain2 != 0x1A) {
                        FUN_8007_0558_module_14_102_draw_surface_sprite(local_12_baseSprite + local_e_direction);
                    }
                }
            }
        }

        // fish prime resource
        if (gameData.zoomLevel == 0 && DAT_0180_maybe_colony_vs_map_view == 1) {
            int prime = FUN_1373_0458_get_prime_resource_at(x, y);
            if (prime >= 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x5a + prime);
            }

        }

    }

    public void FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(int x, int y, boolean centerTileIsFogOfWar, boolean isSea, boolean i2) {
        int savedState = DAT_a558;
        DAT_a558 = 0;

        try {
            // Loop through 4 directions/quadrants
            for (int quadrant = 0; quadrant < 4; quadrant++) {
                processOverlayQuadrant(x, y, quadrant, centerTileIsFogOfWar, isSea, i2);
            }
        } finally {
            DAT_a558 = savedState;
        }
    }

    private void processOverlayQuadrant(int x1, int y1, int quadrant, boolean centerTileIsFogOfWar, boolean isSea, boolean param3) {
        // Calculate target coordinates based on quadrant and offsets
        int yOffset = DAT_00ae_y_directions[quadrant];
        int xOffset = DAT_00a8_x_directions[quadrant];

        int x = x1 + xOffset;
        int y = y1 + yOffset;

        // Check if target tile is within drawable viewport
        boolean inViewport = gameData.gameMap.isTileInDrawableRect(x, y);
        boolean local_a = inViewport; // 8007:07f3

        // For colony view, calculate perspective
        if (DAT_0180_maybe_colony_vs_map_view != 0) {
            int deltaX = Math.abs(x - gameData.viewportCenter.x);
            int deltaY = Math.abs(y - gameData.viewportCenter.y);

            // Check if should draw based on distance
            boolean shouldDraw = FUN_1373_0040_should_draw_depending_on_distance_maybe(deltaX, deltaY, DAT_0180_maybe_colony_vs_map_view);

            local_a |= shouldDraw; // 8007:0728-072f
        }

        // Read terrain type from map
        int terrainType = gameData.gameMap.getTerrain(x, y) & 0xff;
        terrainType = (byte)(terrainType & 0x1F);
        int local_e = terrainType; // 8007:075d

        // remove forest
        if (terrainType < 0x18) {
            terrainType = (byte)(terrainType & 0x07);
        }

        // Adjust for hidden terrain
        int adjustedTerrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(terrainType);
        int local_1e = adjustedTerrain; // 8007:0776

        // Check visibility against fog of war mask
        boolean quadrantIsFogOfWar = false; // 8007:07a2
        if (DAT_a862_power_mask != 0) { // Visibility mask check
            byte visibility = gameData.gameMap.getVisibilityAt(x, y);
            if ((visibility & DAT_a862_power_mask) == 0) {
                quadrantIsFogOfWar = true; // Visible
            }
        }

        // Final rendering decision
        if (centerTileIsFogOfWar && quadrantIsFogOfWar) {
            // Conditions met, this quadrant will render something
            return; // Continue to next quadrant
        }

        // Handle special cases for sea/sea lane (0x19, 0x1A)
        if (adjustedTerrain == 0x19 || adjustedTerrain == 0x1A) { // 8007:0812-081c
            if (isSea) { // 8007:0821
                // Process coastal rendering logic
                // TODO: costal transitions doesn't work right now
                //renderCoastalTransitionSprites(quadrant, x, y, adjustedTerrain);
            }
            return; // Continue to next quadrant
        }

        // Default case - render standard overlay
        if (shouldRenderStandardOverlay(centerTileIsFogOfWar, param3, quadrantIsFogOfWar, adjustedTerrain)) { // 8007:08e3-0916
            int spriteId = quadrant + 0x69; // mask for terrain transitions
            FUN_8007_0558_module_14_102_draw_surface_sprite(spriteId); // clear pixels for terrain transitions
            FUN_8007_067c_module_14_102_draw_terrain_tile_only_over_black_pixels(adjustedTerrain); // write terrain in the cleared pixels
        }
    }

    private void renderCoastalTransitionSprites(int quadrant, int targetX, int targetY, int terrainType) {
        // Determine coastal sprite pattern based on neighbor analysis
        int coastalPattern = determineCoastalPattern(targetX, targetY); // Logic from 8007:0bf8-0c35

        if (coastalPattern >= 0) {
            // Simple coastal pattern - use predefined sprite
            int coastalSprite = 0x97 + coastalPattern; // Base coastal sprite - 8007:0c91
            FUN_8007_0558_module_14_102_draw_surface_sprite(coastalSprite); // 8007:0c94
        } else {
            // Complex coastal pattern - render directional sprites
            renderComplexCoastalEdges();
        }

        // Draw the base terrain tile
        FUN_8007_067c_module_14_102_draw_terrain_tile_only_over_black_pixels(gameData.gameMap.getTerrainTileIdByTerrainType(terrainType));
    }

    private int determineCoastalPattern(int x, int y) {
        // Analyze the 8 surrounding tiles to determine coastal pattern
        // This uses the data collected in DAT_2b4d_a86a_adjected_land_bitmask

        int neighborMask = DAT_a86a_adjected_land_bitmask;
        int pattern = -1; // Default to complex pattern

        // Check for specific coastal patterns using bitmask logic
        if ((neighborMask & 0xDD) == 0xC1) { // 8007:0c00-0c04
            pattern = 0; // Specific coastal configuration
        } else if ((neighborMask & 0x77) == 0x07) { // 8007:0c0e-0c12
            pattern = 1; // Another coastal configuration
        } else if ((neighborMask & 0x77) == 0x70) { // 8007:0c1c-0c20
            pattern = 2; // Different coastal edge
        } else if ((neighborMask & 0xDD) == 0x1C) { // 8007:0c2a-0c2e
            pattern = 3; // Final coastal pattern
        }

        return pattern; // 8007:0c35
    }

    private void renderComplexCoastalEdges() {
        // Reset camera offsets for precise sprite placement
        DAT_1e72_sub_tile_x = 0; // 8007:0c7d
        DAT_1e73_sub_tile_y = 0; // 8007:0c80

        // Loop through 4 primary directions
        for (int direction = 0; direction < 4; direction++) { // 8007:0c3b-0c75
            // Calculate next direction with wrap-around
            int nextDirection = (direction + 1) & 0x3; // 8007:0c43-0c45

            // Calculate precise positioning offsets
            int xOffset = (nextDirection & 0x3E) << 2; // 8007:0c4b-0c4d
            DAT_1e72_sub_tile_x = xOffset; // 8007:0c50

            int yOffset = (direction & 0xFE) << 2; // 8007:0c56-0c58
            DAT_1e73_sub_tile_y = yOffset; // 8007:0c5b

            // Get precomputed coastal sprite data
            int coastalData = DAT_2cec_adjection_land_stuff[direction]; // From terrain analysis - 8007:0c61
            int spriteOffset = (coastalData << 2) + direction; // 8007:0c67-0c6a

            // Calculate final sprite ID and render
            int coastalSprite = 0x6D + spriteOffset; // Base coastal edge sprite - 8007:0c6c
            FUN_8007_0558_module_14_102_draw_surface_sprite(coastalSprite); // 8007:0c6f
        }

        // Reset camera offsets after rendering
        DAT_1e72_sub_tile_x = 0; // 8007:0c7d
        DAT_1e73_sub_tile_y = 0; // 8007:0c80
    }




    private boolean shouldRenderStandardOverlay(boolean centerTileIsFogOfWar, boolean param3, boolean quadrantIsFogOfWar, int terrainType) {
        // this is called when the neighbouring quadrant is not hhh

        // Get and prepare current terrain for comparison
        int currentTerrain = prepareCurrentTerrainForComparison();

        // don't draw transistions between fog of war tiles
        if (centerTileIsFogOfWar && quadrantIsFogOfWar) {
            return false;
        }

        // if the terrain types are different, draw transitions
        if (currentTerrain != terrainType) {
            return true;
        }

        // Terrains match - check special sea/sea lane cases first
        if (terrainType == 0x19 || terrainType == 0x1a) { // 8007:08c2-08cc
            if (param3) { // 8007:08ce
                return false; // Skip rendering for sea with param3 set
            }
            if (centerTileIsFogOfWar) { // 8007:08d4
                return false; // Skip rendering for sea with param1 set
            }
            if (quadrantIsFogOfWar) { // 8007:08da
                return false; // Skip rendering for visible sea
            }
            // If all above false, continue to render
        }

        // Default case for matching non-sea terrain
        return true;
    }

    private int prepareCurrentTerrainForComparison() {
        // Get current adjusted terrain
        int terrain = DAT_a866_adjusted_current_terrain_type; // 8007:08e3
        terrain = (byte)(terrain & 0x1F); // Mask to terrain type - 8007:08e6

        // Normalize if needed
        if (terrain >= 0x18) { // 8007:08ec
            // Keep as-is for special terrain
        } else {
            terrain = (byte)(terrain & 0x07); // Normalize - 8007:08f1
        }

        // Apply hidden terrain adjustment
        terrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(terrain); // 8007:08f5

        return terrain;
    }


    public int DAT_a867_adjected_land_tiles_count;
    public int DAT_a86a_adjected_land_bitmask;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_2cec_adjection_land_stuff
     */
    public int[] DAT_2cec_adjection_land_stuff = new int[4];

    public int[] DAT_00b4_directions_x = new int[]{0, 1, 1, 1, 0, -1, -1, -1, 0, 0};
    public int[] DAT_00be_directions_y = new int[]{-1, -1, 0, 1, 1, 1, 0, -1, 0, 0};

    public int FUN_8007_01b4_module_14_102_analyze_surrounding_terrain(int x, int y) {
        // Clear flags
        DAT_a867_adjected_land_tiles_count = 0;  // Counter for valid surrounding tiles
        DAT_a86a_adjected_land_bitmask = 0;  // Bitmask of visible directions

        // Clear direction flags array (4 bytes at DAT_2cec)
        for (int i = 0; i < 4; i++) {
            DAT_2cec_adjection_land_stuff[i] = 0;
        }

        if (gameData.zoomLevel == 0) {

            for (int directionIndex = 0; directionIndex < 8; directionIndex++) {
                int x_add = DAT_00b4_directions_x[directionIndex];
                int y_add = DAT_00be_directions_y[directionIndex];

                byte terrainType = gameData.gameMap.getTerrain(x + x_add, y + y_add);

                int adjustedTerrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(terrainType) & 0x1f;

                // skip sea and sea lanes
                if (adjustedTerrain != 0x19 && adjustedTerrain != 0x1A) {
                    int bitmask = 1 << directionIndex;
                    // set bit in "adjected land tiles" variable
                    DAT_a86a_adjected_land_bitmask |= bitmask;
                    // increase number of land tiles
                    DAT_a867_adjected_land_tiles_count++;

                    // check for even direction index, these are the non-diagonal directions: N, S, E, W
                    if ((directionIndex & 1) != 0) {
                        // yes, diagonal

                        // calculate index into direction array:
                        //  directionIndex | Direction | +1 | & 6 | >> 1
                        // ----------------|-----------|----|-----|-----
                        //         1       |    NE     | 2  |  2  | 1
                        //         3       |    SE     | 4  |  4  | 2
                        //         5       |    SW     | 6  |  6  | 3
                        //         7       |    NW     | 8  |  0  | 0
                        int Bx = ((directionIndex + 1) & 6) >> 1;
                        DAT_2cec_adjection_land_stuff[Bx] |= 2; // set corner bit for quadrant
                    } else {
                        DAT_a865_current_terrain = adjustedTerrain;
                        int clockwiseToCorner = directionIndex >> 1;
                        int counterClockwiseToCorner = (clockwiseToCorner + 1) & 3;

                        DAT_2cec_adjection_land_stuff[clockwiseToCorner] |= 0x04; // set "there is land clockwise to the corner" bit
                        DAT_2cec_adjection_land_stuff[counterClockwiseToCorner] |= 0x01; // set "there is land counter clockwise to the corner" bit
                    }
                }
            }
        }

        int al = DAT_a865_current_terrain;
        al = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain((byte) al);
        DAT_a866_adjusted_current_terrain_type = al;
        return DAT_a867_adjected_land_tiles_count;
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain
     */
    public int FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(int terrainType) {
        // don't change sea or arctic
        int plainTerrain = terrainType & 0x1f;
        if (plainTerrain >= 0x18) {
            return terrainType;
        }

        if (DAT_0184_show_hidden_terrain_state == 2) {
            // mask off forest, and mountains. for some reason add forest again
            return (terrainType & 7) | 8;
        }
        if (DAT_0184_show_hidden_terrain_state == 3) {
            return terrainType & 7;
        }

        return terrainType;
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0458_get_prime_resource_at
     */
    public int FUN_1373_0458_get_prime_resource_at(int x, int y) {
        // bp 0560:0458
        // check some global flag
        if (gameData.gameMap.mapSeed == 0) {
            return -1;
        }

        int nativeVillageOwner = gameData.gameMap.FUN_1373_0380_visitor_get_native_village_owner(x, y);
        if (nativeVillageOwner > 0) {
            return -1;
        }

        // get terrain type, masking off mountains
        int terrainType = gameData.gameMap.getTerrain(x, y) & 0x3f;

        int isForest;
        // 0x00 - 0x07 - plain terrain
        // 0x08 - 0x0f - terrain with forest
        // 0x10 - 0x18 - unused
        // 0x18 - 0x1a - artic and sea (lanes)
        if (terrainType >= 0x8 && terrainType <= 0x0f) {
            isForest = 1;
        } else {
            isForest = 0;
        }
        int primeResource = -1;

        // calculate stuff with lower 2 bits of coordinate
        int uVar2 = (x & 3) * 4 + (y & 3);

        // calculate stuff with upper bits of coordinate
        int uVar3 = ((y >> 2) * 3 + (x >> 2) - isForest + gameData.gameMap.mapSeed) & 0xf;

        if ((uVar3 == uVar2) || ((uVar3 ^ 0x0a) == uVar2)) {
            int terrain = FUN_13d3_0032_get_terrain_type_stuff(x, y);
            primeResource = primeResourcePerTerrainType[terrain];
            if (primeResource == 0) {
                primeResource = 6;
            }
            int surface = gameData.gameMap.getSurfaceAt(x, y);
            // check if prime resource is depleted
            if ((surface & 4) != 0) {
                // silver mine has a special depleted icon
                if (primeResource == 0xc) {
                    return 0;
                }
                // all others: just remove the prime resource
                primeResource = -1;
            }
        }
        return primeResource;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Code13#FUN_13d3_0032_get_terrain_type_stuff
     */
    public int FUN_13d3_0032_get_terrain_type_stuff(int x, int y) {
        if (gameData.gameMap.isTileInDrawableRect(x, y)) {
            int uVar1 = gameData.gameMap.getTerrain(x, y);
            return FUN_13d3_0006_something_with_mountains(uVar1);
        }
        return 0x19;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Code13#FUN_13d3_0006_something_with_mountains
     */
    public int FUN_13d3_0006_something_with_mountains(int terrain_type) {
        // 0x20 => mountain or hill
        if ((terrain_type & 0x20) != 0) {
            // AL = terrain type
            boolean major = (terrain_type & 0x80) != 0;
            return 0x1b + (major ? 0 : 1);
        }
        return terrain_type & 0x1f;
    }


    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_041e_module_14_102_get_forest_neighbours_mask
     */
    public int FUN_8007_041e_module_14_102_get_forest_neighbours_mask(int x, int y, int zoomLevel) {
        int result = 0;
        if (gameData.zoomLevel > zoomLevel) {
            return 0;
        }

        // check north
        if (FUN_8007_03e4_module_14_102_is_forest(x, y - 1)) {
            result += 8;
        }
        // check south
        if (FUN_8007_03e4_module_14_102_is_forest(x, y + 1)) {
            result += 4;
        }
        // check west
        if (FUN_8007_03e4_module_14_102_is_forest(x - 1, y)) {
            result += 2;
        }
        // check east
        if (FUN_8007_03e4_module_14_102_is_forest(x + 1, y)) {
            result += 1;
        }
        return result;
    }

    /**
     * method is used to determine if the surrounding pieces are forest, so that the forest can be joined.
     */
    public boolean FUN_8007_03e4_module_14_102_is_forest(int x, int y) {
        int terrainType = gameData.gameMap.getTerrain(x, y) & 0x1f;
        // check for arctic, sea and sea lane
        if (terrainType >= 0x18) {
            return false;
        }

        // is it desert (doesn't matter if forest or not)?
        if ((terrainType & 0x7) == 1) {
            // yes, is desert. so no forest
            return false;
        }
        // everything with bit 3 set is forest
        return terrainType > 0x7;
    }

    /**
     * Returns a bitfield of the neighbouring mountains
     * input:
     * AX - mountain type (0xA0 - mountains, 0x20 - hills)
     */
    public int FUN_8007_0374_module_14_102_get_neighbours_mountains(int x, int y, int zoomlevel, int mountainType) {
        int result = 0;
        if (gameData.zoomLevel > zoomlevel) {
            return result;
        }

        // check north
        if ((gameData.gameMap.getTerrain(x, y - 1) & 0xA0) == mountainType) {
            result += 8;
        }
        // check south
        if ((gameData.gameMap.getTerrain(x, y + 1) & 0xA0) == mountainType) {
            result += 4;
        }
        // check west
        if ((gameData.gameMap.getTerrain(x - 1, y) & 0xA0) == mountainType) {
            result += 2;
        }
        // check east
        if ((gameData.gameMap.getTerrain(x + 1, y) & 0xA0) == mountainType) {
            result += 1;
        }
        return result;
    }


    public int FUN_8007_0314_module_14_102_get_neighbours_by_bitfield(int x, int y, int zoomlevel, int bitfield) {
        int result = 0;
        if (gameData.zoomLevel > zoomlevel) {
            return result;
        }

        // check north
        if ((gameData.gameMap.getTerrain(x, y - 1) & bitfield) > 0) {
            result += 8;
        }
        // check south
        if ((gameData.gameMap.getTerrain(x, y + 1) & bitfield) > 0) {
            result += 4;
        }
        // check west
        if ((gameData.gameMap.getTerrain(x - 1, y) & bitfield) > 0) {
            result += 2;
        }
        // check east
        if ((gameData.gameMap.getTerrain(x + 1, y) & bitfield) > 0) {
            result += 1;
        }
        return result;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14_102_Map#FUN_8007_04e4_module_14_102_get_surface_neighbours_bitmask_8_directions_first_map_pointer
     */
    public int FUN_8007_04e4_module_14_102_get_terrain_neighbours_bitmask_8_directions_first_map_pointer(int x, int y, int zoomlevel, int terrainMask) {
        if (gameData.zoomLevel > zoomlevel) {
            return 0;
        }

        int result = 0;
        int bitmask = 1;

        for (int direction = 0; direction < 8; direction++) {
            int x_offset = DAT_00b4_directions_x[direction];
            int y_offset = DAT_00be_directions_y[direction];

            // note: in the assembly there is a check on y_offset != 0 which doesn't make sense. So I removed the check

            // Calculate map position
            int tile_data = gameData.gameMap.getSurfaceAt(x + x_offset, y + y_offset) & 0xff;

            // Test tile properties
            if ((tile_data & terrainMask) > 0) {
                result |= bitmask;
            }

            bitmask <<= 1;  // Move to next direction bit
        }

        return result;
    }
}
