package com.marf.colonization.rebuild;


import static com.marf.colonization.decompile.cmodules.Data.*;
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

    public Minimap(Resources resources, Canvas canvas, GameData gameData) {
        System.out.println("create minimap with " + resources + " " + canvas);
        this.resources = resources;
        this.canvas = canvas;
        this.gameData = gameData;
    }

    /**
     * @see com.marf.colonization.decompile.cmodules.Module14#FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds
     */
    public void calculateMinimapBounds() {
        int initialX = gameData.viewportCenter.x - 28;  // 28 pixels left from center
        int initialY = gameData.viewportCenter.y - 19;  // 19 pixels up from center

        minimapMin.y = clamp(1, initialX, gameData.mapSize.width - 57);
        minimapMin.x = clamp(1, initialY, gameData.mapSize.height - 40);
    }


    /**
     * FUN_7f05_048a
     */
    public void renderMinimap() {
        calculateMinimapBounds();
        if (resources.getWoodTile() == null) {
            canvas.fillRect(241, 8, 79, 41, 0);
        } else {
            canvas.drawSpriteSheetEntry(resources.getWoodTile(), 241, 8, 79, 41, 0, 0);
        }

        canvas.drawRect(251, 8, 308, 48, 6);

        // draw minimap


        // draw current visible rectangle
        int y1 = max(gameData.viewportMin.getY(), minimapMin.y) + 9;
        int x1 = max(gameData.viewportMin.getX(), minimapMin.x) + 252;

        int y2 = min(gameData.viewportMax.y, minimapMin.y + 38) - minimapMin.y + 9;
        int x2 = min(gameData.viewportMax.y, minimapMin.x + 55) + 252;
        canvas.drawRect(x1, y1, x2, y2, 15);

        // flip to front screen


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

        gameData.tileSize = 0x10 >> DAT_017a_zoom_level;

        // calculate viewport top left of the viewport
        gameData.viewportMin.y = gameData.viewportCenter.y - (viewportTiles.y / 2);
        gameData.viewportMin.x = gameData.viewportCenter.x - (viewportTiles.x / 2);

        // clamp viewport to the map size (if not in colony view)
        if (!gameData.colonyView) {
            // adjust the top left corner so that it is not < 1 and that all viewport tiles can be displayed.
            // Note that the first and last column and row are not drawn
            gameData.viewportMin.x = clamp(1, gameData.viewportMin.x, gameData.mapSize.width - viewportTiles.x - 1);
            gameData.viewportMin.y = clamp(1, gameData.viewportMin.y, gameData.mapSize.height - viewportTiles.y - 1);
        }

        // when the viewport is bigger than the map (in smalled and 2nd smallest zoom level),
        // there are gaps left and right and top and bottom.
        // This calculates the offset of the map drawing into the viewport (from top left)
        gameData.viewportOffset.x = 0;
        gameData.viewportOffset.y = 0;
        // Handle maps smaller than viewport (X-axis)
        if (gameData.mapSize.width - 2 < viewportTiles.x) {
            gameData.viewportMin.x = 1;
            gameData.viewportOffset.x = (viewportTiles.x - gameData.mapSize.width + 2) / 2;
            viewportTiles.x = gameData.mapSize.width - 2;
        }

        // Handle maps smaller than viewport (Y-axis)
        if (gameData.mapSize.height - 2 < viewportTiles.y) {
            gameData.viewportMin.y = 1;
            gameData.viewportOffset.x = (viewportTiles.y - gameData.mapSize.height + 2) / 2;
            viewportTiles.y = gameData.mapSize.height - 2;
        }

        // Calculate display dimensions based on DAT_0150 flag
        if (DAT_0150_some_flag != 0) {
            if (!gameData.colonyView) {
                // Normal zoomed display
                DAT_84ee_some_width = (15 << gameData.zoomLevel) + 2;
                DAT_84f0_some_height = (12 << gameData.zoomLevel) + 2;
            } else {
                // Special display mode calculations
                calculateSpecialDisplayDimensions();
            }
        } else {
            // Full map display
            DAT_84ee_some_width = gameData.mapSize.width;
            DAT_84f0_some_height = gameData.mapSize.height;
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
        int maxX = Math.min(gameData.viewportMin.x + 5, gameData.mapSize.width - 1);
        int minX = Math.max(0, gameData.viewportMin.x - 1);  // Clamp to 0
        DAT_84ee_some_width = maxX - minX + 1;

        // Y-dimension calculation
        int maxY = Math.min(gameData.viewportMin.y + 5, gameData.mapSize.height - 1);
        int minY = Math.max(0, gameData.viewportMin.y - 1);  // Clamp to 0
        DAT_84f0_some_height = maxY - minY + 1;
    }

}
