package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code11.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code1c.*;
import static com.marf.colonization.decompile.cmodules.Data.*;

public class Module14 {

    public static void FUN_8007_000c_module_14_102_calculateViewport() {
        DAT_84ea_number_of_x_tiles_in_viewport = 15 << DAT_017a_zoom_level;
        DAT_84ec_number_of_y_tiles_in_viewport = 12 << DAT_017a_zoom_level;

        if (DAT_0180_maybe_colony_vs_map_view != 0) {
            DAT_84ea_number_of_x_tiles_in_viewport = 5;
            DAT_84ec_number_of_y_tiles_in_viewport = 5;
            DAT_017a_zoom_level = 0;
        }

        // each tile is 0x10 (16) pixel square. Calculate the size of the tiles in the current zoom level
        int tileSize = 0x10 >> DAT_017a_zoom_level;
        DAT_5a8c_tile_pixel_size = tileSize;
        DAT_82de_tile_pixel_size = tileSize;

        // calculate viewport top left coordinates to place x,y in center if the viewport
        DAT_82e2_viewport_x_min = -(DAT_84ea_number_of_x_tiles_in_viewport / 2 - DAT_0172_viewport_center_x);
        DAT_82e6_viewport_y_min = -(DAT_84ec_number_of_y_tiles_in_viewport / 2 - DAT_0174_viewport_center_y);

        // clamp viewport to the map size
        if (DAT_0180_maybe_colony_vs_map_view == 0) {
            DAT_82e2_viewport_x_min = Math.max(1, Math.min(DAT_82e2_viewport_x_min, DAT_84e6_map_width - DAT_84ea_number_of_x_tiles_in_viewport - 1));
            DAT_82e6_viewport_y_min = Math.max(1, Math.min(DAT_82e6_viewport_y_min, DAT_84e8_map_height - DAT_84ec_number_of_y_tiles_in_viewport - 1));
        }


        // when the viewport is bigger than the map tiles, there are gaps left and right and top and botton
        // this calculates the offset of the map drawing into the viewport
        DAT_82e0_viewport_x_offset = 0;
        DAT_82e4_viewport_y_offset = 0;
        // Handle maps smaller than viewport (X-axis)
        if (DAT_84e6_map_width - 2 < DAT_84ea_number_of_x_tiles_in_viewport) {
            DAT_82e2_viewport_x_min = 1;
            DAT_82e0_viewport_x_offset = (DAT_84ea_number_of_x_tiles_in_viewport - DAT_84e6_map_width + 2) / 2;
            ;
            DAT_84ea_number_of_x_tiles_in_viewport = DAT_84e6_map_width - 2;
        }

        // Handle maps smaller than viewport (Y-axis)
        if (DAT_84e8_map_height - 2 < DAT_84ec_number_of_y_tiles_in_viewport) {
            DAT_82e6_viewport_y_min = 1;
            DAT_82e4_viewport_y_offset = (DAT_84ec_number_of_y_tiles_in_viewport - DAT_84e8_map_height + 2) / 2;
            DAT_84ec_number_of_y_tiles_in_viewport = DAT_84e8_map_height - 2;
        }

        // Store previous frame positions (with -1 adjustment)
        DAT_84f2_some_x = DAT_82e2_viewport_x_min - 1;
        DAT_84f4_some_y = DAT_82e6_viewport_y_min - 1;

        // Calculate display dimensions based on DAT_0150 flag
        if (DAT_0150 != 0) {
            if (DAT_0180_maybe_colony_vs_map_view == 0) {
                // Normal zoomed display
                DAT_84ee_some_width = (0xF << DAT_017a_zoom_level) + 2;
                DAT_84f0_some_height = (0xC << DAT_017a_zoom_level) + 2;
            } else {
                // Special display mode calculations
                calculateSpecialDisplayDimensions();
            }
        } else {
            // Full map display
            DAT_84ee_some_width = DAT_84e6_map_width;
            DAT_84f0_some_height = DAT_84e8_map_height;
        }

        // Final zoom-dependent calculations
        DAT_017c_zoom_level_percent = 0x64 >> DAT_017a_zoom_level;  // 100 >> zoomLevel
        DAT_017e = (0x5 << DAT_017a_zoom_level) + 0x5;

        // Calculate window maximum bounds
        DAT_87aa_game_window_x_max = DAT_82e2_viewport_x_min + DAT_84ea_number_of_x_tiles_in_viewport - 1;
        DAT_87ac_game_window_y_max = DAT_82e6_viewport_y_min + DAT_84ec_number_of_y_tiles_in_viewport - 1;
    }

    private static void calculateSpecialDisplayDimensions() {
        // Lines 0126-0175 - complex calculations for special display mode
        int tempX = DAT_84f2_some_x + 6;
        int tempY = DAT_84f4_some_y + 6;

        // X-dimension calculation
        int maxX = DAT_84e6_map_width - 1;
        int displayWidth = Math.min(maxX, tempX);
        DAT_84f2_some_x = Math.max(0, DAT_84f2_some_x);  // Clamp to 0
        DAT_84ee_some_width = displayWidth - DAT_84f2_some_x + 1;

        // Y-dimension calculation
        int maxY = DAT_84e8_map_height - 1;
        int displayHeight = Math.min(maxY, tempY);
        DAT_84f4_some_y = Math.max(0, DAT_84f4_some_y);  // Clamp to 0
        DAT_84f0_some_height = displayHeight - DAT_84f4_some_y + 1;
    }

    public static int FUN_8007_01b4_module_14_102_analyze_surrounding_terrain() {
        // Clear flags
        DAT_a867_adjected_land_tiles_count = 0;  // Counter for valid surrounding tiles
        DAT_a86a_adjected_land_bitmask = 0;  // Bitmask of visible directions

        // Clear direction flags array (4 bytes at DAT_2cec)
        for (int i = 0; i < 4; i++) {
            DAT_2cec_adjection_land_stuff[i] = 0;
        }

        if (DAT_017a_zoom_level == 0) {

            for (int directionIndex = 0; directionIndex < 8; directionIndex++) {
                int direction_y = DAT_00be_directions_y[directionIndex];
                int y_add = 0;
                if (direction_y < 0) {
                    y_add = -DAT_84ee_some_width;
                } else if (direction_y > 0) {
                    y_add = DAT_84ee_some_width;
                }

                int x_add = DAT_00b4_directions_x[directionIndex];

                byte terrainType = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + y_add + x_add];

                int adjustedTerrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(terrainType);

                // skip sea and sea lanes
                if (adjustedTerrain != 0x19 && adjustedTerrain != 0x1A) {
                    int bitmask = 1 << directionIndex;
                    // set bit in "adjected land tiles" variable
                    DAT_a86a_adjected_land_bitmask |= bitmask;
                    // increase number of land tiles
                    DAT_a867_adjected_land_tiles_count++;
                }

                // check for even direction index, these are the non-diagonal directions: N, S, E, W
                if ((directionIndex & 1) == 0) {
                    // yes, diagonal

                    // calculate index into direction array:
                    // Direction | directionIndex | +1 | & 6 | >> 1
                    // NW | 1 | 2 | 2 | 1
                    // NE | 3 | 4 | 4 | 2
                    // SE | 5 | 6 | 6 | 3
                    // SW | 7 | 8 | 0 | 0
                    int Bx = ((directionIndex + 1) & 6) >> 1;
                    DAT_2cec_adjection_land_stuff[Bx] |= 2; // set bit 1 in array
                } else {
                    DAT_a865_current_terrain = adjustedTerrain;

                    int cardinalIndex = directionIndex >> 1;
                    int oppositeIndex = (cardinalIndex + 1) & 3;

                    DAT_2cec_adjection_land_stuff[cardinalIndex] |= 0x04;
                    DAT_2cec_adjection_land_stuff[oppositeIndex] |= 0x01;
                }
            }
        }

        int al = DAT_a865_current_terrain;
        al = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain((byte) al);
        DAT_a866_adjusted_current_terrain_type = al;
        return DAT_a867_adjected_land_tiles_count;
    }

    public static void FUN_8007_02ba_add_to_land_tiles(int offset, int bitfield) {
        int terrainType = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + offset];
        if (terrainType >= DAT_a868_lower_bound && terrainType <= DAT_a869_upper_bound) {
            DAT_a867_adjected_land_tiles_count += bitfield;
        }
    }

    // note: unused

    /**
     * checks if the horizontal and vertical neighbours are between some bounds and returns a bitfield.
     */
    public static int FUN_8007_02d6_get_neighbours_by_range(int lowerBound, int upperBound) {
        DAT_a867_adjected_land_tiles_count = 0;
        if (DAT_017a_zoom_level > 0) {
            return lowerBound;
        }
        DAT_a868_lower_bound = lowerBound;
        DAT_a869_upper_bound = upperBound;

        FUN_8007_02ba_add_to_land_tiles(-DAT_84ee_some_width, 8);
        FUN_8007_02ba_add_to_land_tiles(DAT_84ee_some_width, 4);
        FUN_8007_02ba_add_to_land_tiles(-1, 2);
        FUN_8007_02ba_add_to_land_tiles(1, 1);
        return DAT_a867_adjected_land_tiles_count;
    }

    public static int FUN_8007_0314_module_14_102_get_neighbours_by_bitfield(int zoomlevel, byte bitfield) {
        int result = 0;
        if (DAT_017a_zoom_level > zoomlevel) {
            return result;
        }

        // check north
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position - DAT_84ee_some_width] & bitfield) > 0) {
            result += 8;
        }
        // check south
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + DAT_84ee_some_width] & bitfield) > 0) {
            result += 4;
        }
        // check west
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position - 1] & bitfield) > 0) {
            result += 2;
        }
        // check east
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + 1] & bitfield) > 0) {
            result += 1;
        }
        return result;
    }

    /**
     * Returns a bitfield of the neighbouring mountains
     * input:
     * AX - mountain type (0xA0 - mountains, 0x20 - hills)
     */
    public static int FUN_8007_0374_module_14_102_get_neighbours_mountains(int zoomlevel, byte mountainType) {
        int result = 0;
        if (DAT_017a_zoom_level > zoomlevel) {
            return result;
        }

        // check north
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position - DAT_84ee_some_width] & 0xA0) > 0) {
            result += 8;
        }
        // check south
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + DAT_84ee_some_width] & 0xA0) > 0) {
            result += 4;
        }
        // check west
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position - 1] & 0xA0) > 0) {
            result += 2;
        }
        // check east
        if ((DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + 1] & 0xA0) > 0) {
            result += 1;
        }
        return result;
    }

    /** method is used to determine if the surrounding pieces are forest, so that the forest can be joined.  */
    public static boolean FUN_8007_03e4_module_14_102_is_forest(int offset) {
        int terrainType = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + offset] & 0x1f;
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

    public static int FUN_8007_041e_module_14_102_get_forest_neighbours_mask(int zoomLevel) {
        int result = 0;
        if (DAT_017a_zoom_level > zoomLevel) {
            return 0;
        }

        // check north
        if (FUN_8007_03e4_module_14_102_is_forest(-DAT_84ee_some_width)) {
            result += 8;
        }
        // check south
        if (FUN_8007_03e4_module_14_102_is_forest(DAT_84ee_some_width)) {
            result += 4;
        }
        // check west
        if (FUN_8007_03e4_module_14_102_is_forest(- 1)) {
            result += 2;
        }
        // check east
        if (FUN_8007_03e4_module_14_102_is_forest(1)) {
            result += 1;
        }
        return result;
    }

    public static int FUN_8007_0484_get_neighbours_by_bitfield_first_map_pointer(int zoomlevel, byte bitfield) {
        int result = 0;
        if (DAT_017a_zoom_level > zoomlevel) {
            return result;
        }

        // check north
        if ((DAT_0152_game_map_terrain[DAT_a544_first_map_pointer - DAT_84ee_some_width] & bitfield) > 0) {
            result += 8;
        }
        // check south
        if ((DAT_0152_game_map_terrain[DAT_a544_first_map_pointer + DAT_84ee_some_width] & bitfield) > 0) {
            result += 4;
        }
        // check west
        if ((DAT_0152_game_map_terrain[DAT_a544_first_map_pointer - 1] & bitfield) > 0) {
            result += 2;
        }
        // check east
        if ((DAT_0152_game_map_terrain[DAT_a544_first_map_pointer + 1] & bitfield) > 0) {
            result += 1;
        }
        return result;
    }

    public static int FUN_8007_04e4_module_14_102_get_terrain_neighbours_bitmask_8_directions_first_map_pointer(int zoomlevel, int terrainMask) {
        if (DAT_017a_zoom_level > zoomlevel) {
            return 0;
        }

        int result = 0;
        int bitmask = 1;

        for (int direction = 0; direction < 8; direction++) {
            int x_offset = DAT_00b4_directions_x[direction];
            int y_offset = DAT_00be_directions_y[direction] * DAT_84e6_map_width;

            // note: in the assembly there is a check on y_offset != 0 which doesn't make sense. So I removed the check

            // Calculate map position
            byte tile_data = DAT_0152_game_map_terrain[DAT_a544_first_map_pointer + x_offset + y_offset];

            // Test tile properties
            if ((tile_data & terrainMask) > 0) {
                result |= bitmask;
            }

            bitmask <<= 1;  // Move to next direction bit
        }

        return result;
    }

    public static void FUN_8007_0558_module_14_102() {
        if (DAT_017c_zoom_level_percent < 100) {
            // zoom level < 100%
            FUN_1c3a_000a(DAT_2640_2nd_backscreen, DAT_a554_some_x, DAT_a556_some_y, DAT_017c_zoom_level_percent, DAT_016a_phys0_sprite_sheet);
        } else {
            // zoom level == 100%
            FUN_1c1b_0000_maybe_draw_compressed_sprite(DAT_2640_2nd_backscreen, DAT_1e72_camera_x_maybe + DAT_a554_some_x - 8, DAT_1e73_camera_y_maybe + DAT_a556_some_y - 0xf, DAT_016a_phys0_sprite_sheet);
        }
    }

    public static void FUN_8007_05b8_module_14_102_draw_pedia_terrain_tile(int inAX) {
        if (DAT_017a_zoom_level == 0) {
            FUN_1101_0050_blit_terrain_tile(DAT_0162_terrain_sprites, inAX, DAT_2640_2nd_backscreen, DAT_1e72_camera_x_maybe + DAT_a554_some_x - 8, DAT_1e73_camera_y_maybe + DAT_a556_some_y - 0xf);
        } else {
            FUN_1101_0126(DAT_0162_terrain_sprites, inAX, DAT_2640_2nd_backscreen, DAT_a554_some_x, DAT_a556_some_y, DAT_017a_zoom_level);
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
