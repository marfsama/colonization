package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;

public class Module14 {

    public static void FUN_8007_000c_module_14_102() {
        DAT_84ea_number_of_x_tiles_in_viewport = 15 << DAT_017a_maybe_zoom_level;
        DAT_84ec_number_of_y_tiles_in_viewport = 12 << DAT_017a_maybe_zoom_level;

        if (DAT_0180_maybe_colony_vs_map_view != 0) {
            DAT_84ea_number_of_x_tiles_in_viewport = 5;
            DAT_84ec_number_of_y_tiles_in_viewport = 5;
            DAT_017a_maybe_zoom_level = 0;
        }

        // each tile is 0x10 (16) pixel square. Calculate the size of the tiles in the current zoom level
        int tileSize = 0x10 >> DAT_017a_maybe_zoom_level;
        DAT_5a8c_tile_pixel_size = tileSize;
        DAT_82de_tile_pixel_size = tileSize;

        // calculate viewport top left coordinates to place x,y in center if the viewport
        DAT_82e2_game_window_x_min = -(DAT_84ea_number_of_x_tiles_in_viewport / 2 - DAT_0172_some_x);
        DAT_82e6_game_window_y_min = -(DAT_84ec_number_of_y_tiles_in_viewport / 2 - DAT_0174_some_y);

        // clamp viewport to the map size
        if (DAT_0180_maybe_colony_vs_map_view == 0) {
            DAT_82e2_game_window_x_min = Math.max(1, Math.min(DAT_82e2_game_window_x_min, DAT_84e6_map_width - DAT_84ea_number_of_x_tiles_in_viewport - 1));
            DAT_82e6_game_window_y_min = Math.max(1, Math.min(DAT_82e6_game_window_y_min, DAT_84e8_map_height - DAT_84ec_number_of_y_tiles_in_viewport - 1));
        }


        // when the viewport is bigger than the map tiles, there are gaps left and right and top and botton
        // this calculates the offset of the map drawing into the viewport
        DAT_82e0 = 0;
        DAT_82e4 = 0;
        // Handle maps smaller than viewport (X-axis)
        if (DAT_84e6_map_width - 2 < DAT_84ea_number_of_x_tiles_in_viewport) {
            DAT_82e2_game_window_x_min = 1;
            DAT_82e0 = (DAT_84ea_number_of_x_tiles_in_viewport - DAT_84e6_map_width + 2) / 2;;
            DAT_84ea_number_of_x_tiles_in_viewport = DAT_84e6_map_width - 2;
        }

        // Handle maps smaller than viewport (Y-axis)
        if (DAT_84e8_map_height - 2 < DAT_84ec_number_of_y_tiles_in_viewport) {
            DAT_82e6_game_window_y_min = 1;
            DAT_82e4 = (DAT_84ec_number_of_y_tiles_in_viewport - DAT_84e8_map_height + 2) / 2;
            DAT_84ec_number_of_y_tiles_in_viewport = DAT_84e8_map_height - 2;
        }

    }

    public static void FUN_8007_1016_module_14_102() {
        // TODO
    }

    public static void FUN_8007_109c_module_14_102_something_with_map_view(int mapViewType) {
        DAT_0180_maybe_colony_vs_map_view = mapViewType;
        FUN_8007_1016_module_14_102();
        DAT_0180_maybe_colony_vs_map_view = 0;
    }
}
