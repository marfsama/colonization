package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code11.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code1b.*;
import static com.marf.colonization.decompile.cmodules.Code1c.*;
import static com.marf.colonization.decompile.cmodules.Data.*;

public class Module14_102_Map {

    public static void FUN_8007_000c_module_14_102_calculate_viewport() {
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
        if (DAT_0150_some_flag != 0) {
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
        DAT_017e_maybe_scroll_amount = (0x5 << DAT_017a_zoom_level) + 0x5;

        // Calculate window maximum bounds
        DAT_87aa_viewport_x_max = DAT_82e2_viewport_x_min + DAT_84ea_number_of_x_tiles_in_viewport - 1;
        DAT_87ac_viewport_y_max = DAT_82e6_viewport_y_min + DAT_84ec_number_of_y_tiles_in_viewport - 1;
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

    public static int FUN_8007_0314_module_14_102_get_neighbours_by_bitfield(int zoomlevel, int bitfield) {
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
    public static int FUN_8007_0374_module_14_102_get_neighbours_mountains(int zoomlevel, int mountainType) {
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

    /**
     * method is used to determine if the surrounding pieces are forest, so that the forest can be joined.
     */
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

    public static int FUN_8007_041e_module_14_102_get_forest_neighbours_mask(int zoomLevel, int terrain) {
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
        if (FUN_8007_03e4_module_14_102_is_forest(-1)) {
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
        if ((DAT_0152_game_map_terrain[DAT_a544_surface_map_pointer - DAT_84ee_some_width] & bitfield) > 0) {
            result += 8;
        }
        // check south
        if ((DAT_0152_game_map_terrain[DAT_a544_surface_map_pointer + DAT_84ee_some_width] & bitfield) > 0) {
            result += 4;
        }
        // check west
        if ((DAT_0152_game_map_terrain[DAT_a544_surface_map_pointer - 1] & bitfield) > 0) {
            result += 2;
        }
        // check east
        if ((DAT_0152_game_map_terrain[DAT_a544_surface_map_pointer + 1] & bitfield) > 0) {
            result += 1;
        }
        return result;
    }

    public static int FUN_8007_04e4_module_14_102_get_surface_neighbours_bitmask_8_directions_first_map_pointer(int zoomlevel, int terrainMask) {
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
            byte tile_data = DAT_0156_game_map_surface[DAT_a544_surface_map_pointer + x_offset + y_offset];

            // Test tile properties
            if ((tile_data & terrainMask) > 0) {
                result |= bitmask;
            }

            bitmask <<= 1;  // Move to next direction bit
        }

        return result;
    }

    public static void FUN_8007_0558_module_14_102_draw_surface_sprite(int spriteIndex) {
        if (DAT_017c_zoom_level_percent < 100) {
            // zoom level < 100%
            FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, DAT_017c_zoom_level_percent, spriteIndex, DAT_016a_phys0_sprite_sheet);
        } else {
            // zoom level == 100%
            FUN_1c1b_0000_draw_compressed_sprite(DAT_2640_2nd_backscreen, DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8, DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf, spriteIndex, DAT_016a_phys0_sprite_sheet);
        }
    }

    public static void FUN_8007_05b8_module_14_102_draw_terrain_tile(int neighboursBitmap) {
        if (DAT_017a_zoom_level == 0) {
            FUN_1101_0050_blit_terrain_tile(DAT_0162_terrain_sprites, neighboursBitmap, DAT_2640_2nd_backscreen, DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8, DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf);
        } else {
            FUN_1101_0126(DAT_0162_terrain_sprites, neighboursBitmap, DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, DAT_017a_zoom_level);
        }
    }

    public static void FUN_8007_061c_module_14_102_draw_surface_sprite() {
        if (DAT_017c_zoom_level_percent < 100) {
            // zoom level < 100%
            FUN_1c8e_000a(DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, DAT_017c_zoom_level_percent, DAT_016a_phys0_sprite_sheet);
        } else {
            // zoom level == 100%
            FUN_1c6d_000c(DAT_2640_2nd_backscreen, DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8, DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf, DAT_016a_phys0_sprite_sheet);
        }
    }

    public static void FUN_8007_067c_module_14_102_draw_terrain_tile(int neighboursBitmap) {
        if (DAT_017a_zoom_level == 0) {
            FUN_1101_00b4_blit_terrain_sprite(DAT_0162_terrain_sprites, neighboursBitmap, DAT_2640_2nd_backscreen, DAT_1e72_sub_tile_x + DAT_a554_draw_map_x_in_pixels - 8, DAT_1e73_sub_tile_y + DAT_a556_draw_map_y_in_pixels - 0xf);
        } else {
            FUN_1101_01dc_blit_sprite_sheet_sprite_only_over_black_pixels(DAT_0162_terrain_sprites, neighboursBitmap, DAT_2640_2nd_backscreen, DAT_a554_draw_map_x_in_pixels, DAT_a556_draw_map_y_in_pixels, DAT_017a_zoom_level);
        }
    }

    /**
     * This function is essentially the "smart border renderer" that makes terrain transitions look natural by analyzing
     * surrounding tiles and drawing appropriate overlay sprites.
     *
     * @param i
     * @param local_24_is_sea
     * @param i1
     */
    public static void FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(int i, int local_24_is_sea, int i1) {
        int savedState = DAT_a558;
        DAT_a558 = 0;

        try {
            // Loop through 4 directions/quadrants
            for (int quadrant = 0; quadrant < 4; quadrant++) {
                if (!processOverlayQuadrant(quadrant, i == 1, local_24_is_sea == 1, i1 == 1)) {
                    break;
                }
            }
        } finally {
            DAT_a558 = savedState;
        }
    }

    private static boolean processOverlayQuadrant(int quadrant, boolean param1, boolean param2, boolean param3) {
        // Calculate target coordinates based on quadrant and offsets
        int yOffset = DAT_00ae_y_directions[quadrant]; // From [BX + 0xae]
        int xOffset = DAT_00a8_x_directions[quadrant]; // From [BX + 0xa8]

        int x = DAT_a550_draw_map_x_in_tiles + xOffset; // 8007:07d4
        int y = DAT_a552_draw_map_y_in_tiles + yOffset; // 8007:07db

        // Check if target tile is within drawable viewport
        boolean inViewport = FUN_1373_000e_is_tile_in_drawable_rect(x, y); // 8007:07e4
        boolean local_a = inViewport; // 8007:07f3

        // For colony view, calculate perspective
        if (DAT_0180_maybe_colony_vs_map_view != 0) {
            int deltaX = Math.abs(x - DAT_0172_viewport_center_x); // 8007:06f6-0700
            int deltaY = Math.abs(y - DAT_0174_viewport_center_y); // 8007:0707-071c

            // Check if should draw based on distance
            boolean shouldDraw = FUN_1373_0040_should_draw_depending_on_distance_maybe(deltaX, deltaY, DAT_0180_maybe_colony_vs_map_view); // 8007:0720

            local_a |= shouldDraw; // 8007:0728-072f
        }

        // Calculate position in terrain map array
        int local_14_offset_in_map_arrays = xOffset;
        if (yOffset < 0) {
            local_14_offset_in_map_arrays -= DAT_84ee_some_width; // 8007:073e
        } else if (yOffset > 0) {
            local_14_offset_in_map_arrays += DAT_84ee_some_width; // 8007:074b
        }

        // Read terrain type from map
        byte terrainType = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position+local_14_offset_in_map_arrays]; // 8007:0751-0758
        terrainType = (byte)(terrainType & 0x1F); // Keep lower 5 bits // 8007:075b
        int local_e = terrainType; // 8007:075d

        // Normalize when not arctic or sea
        if (terrainType < 0x18) {
            terrainType = (byte)(terrainType & 0x07); // Further normalize // 8007:0764
        }

        // Adjust for hidden terrain (e.g., fog of war)
        int adjustedTerrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(terrainType); // 8007:076d
        int local_1e = adjustedTerrain; // 8007:0776

        // Check visibility against fog of war mask
        int local_10 = 0; // 8007:07a2
        if (DAT_a862_power_mask != 0) { // Visibility mask check
            byte visibility = DAT_015e_game_map_visibility[DAT_a54c_visibility_map_pointer+local_14_offset_in_map_arrays]; // 8007:0780-0784
            if ((visibility & DAT_a862_power_mask) != 0) { // 8007:078e
                local_10 = 1; // Visible
            }
        }

        // Final rendering decision
        if (param1 && local_10 != 0) { // 8007:07a7-07b1
            // Conditions met, this quadrant will render something
            return true; // Continue to next quadrant
        }

        // Handle special cases for sea/sea lane (0x19, 0x1A)
        if (adjustedTerrain == 0x19 || adjustedTerrain == 0x1A) { // 8007:0812-081c
            if (param2) { // 8007:0821
                // Process coastal rendering logic
                renderCoastalTransitionSprites(quadrant, x, y, adjustedTerrain);
            }
            return true; // Continue to next quadrant
        }

        // Default case - render standard overlay
        if (shouldRenderStandardOverlay(param1, param3, local_10, adjustedTerrain)) { // 8007:08e3-0916
            int spriteId = quadrant + 0x69; // mask for terrain transitions
            FUN_8007_0558_module_14_102_draw_surface_sprite(spriteId); // 8007:091f
            FUN_8007_067c_module_14_102_draw_terrain_tile(adjustedTerrain); // 8007:0927
        }

        return true; // Continue processing
    }

    private static boolean shouldRenderStandardOverlay(boolean param1, boolean param3, int local_10, int terrainType) {
        // Get and prepare current terrain for comparison
        int currentTerrain = prepareCurrentTerrainForComparison();

        // Compare with target terrain (local_1e)
        if (currentTerrain != terrainType) {
            // Terrains don't match - check rendering conditions
            if (!param1) { // 8007:090a
                return false;
            }
            if (local_10 == 0) {
                return false;
            }
            return true;
        }

        // Terrains match - check special sea/sea lane cases first
        if (terrainType == 0x19 || terrainType == 0x1a) { // 8007:08c2-08cc
            if (param3) { // 8007:08ce
                return false; // Skip rendering for sea with param3 set
            }
            if (param1) { // 8007:08d4
                return false; // Skip rendering for sea with param1 set
            }
            if (local_10 != 0) { // 8007:08da
                return false; // Skip rendering for visible sea
            }
            // If all above false, continue to render
        }

        // Default case for matching non-sea terrain
        return true;
    }

    private static int prepareCurrentTerrainForComparison() {
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

    private static void renderCoastalTransitionSprites(int quadrant, int targetX, int targetY, int terrainType) {
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
        FUN_8007_067c_module_14_102_draw_terrain_tile(terrainType); // 8007:0c9c
    }

    private static int determineCoastalPattern(int x, int y) {
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

    private static void renderComplexCoastalEdges() {
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

    public static void FUN_8007_0938_module_14_102_draw_map_tile(boolean valid) {
        // note: one of the next 2 may be a surface pointer
        DAT_a863_value_from_terrain_map = DAT_0152_game_map_terrain[DAT_a544_surface_map_pointer];
        byte currentTerrain = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position];
        DAT_a865_current_terrain = currentTerrain;

        DAT_a864_value_from_visibility_map = DAT_015e_game_map_visibility[DAT_a54c_visibility_map_pointer];

        int adjustedTerrain = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(currentTerrain);
        DAT_a866_adjusted_current_terrain_type = adjustedTerrain;

        int local_a = 0;

        if (DAT_a862_power_mask != 0) {
            if (DAT_a862_power_mask != DAT_a864_value_from_visibility_map) {
                if (valid) {
                    local_a = 1;
                }
            }
        }

        int local_14_river = DAT_a865_current_terrain & 0xc0;
        int local_22_base_terrain = DAT_a865_current_terrain & 0x7;

        if (local_a != 0) {
            // draw tile 0x95
            // 0x95 = plowed field
            // Note: 0x94 = deep see
            FUN_8007_0558_module_14_102_draw_surface_sprite(0x95);
            if (DAT_017a_zoom_level != 0) {
                return;
            }
            int local_24_is_sea = 0;
            if (DAT_a866_adjusted_current_terrain_type == 0x19 || DAT_a866_adjusted_current_terrain_type == 0x1a) {
                local_24_is_sea = 1;
            }
            // AX is still 0x95, deep sea
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(1, local_24_is_sea, 0);
            return;
        }

        int local_1e_surrounding_terrain_map = 0;
        int local_6_is_sea_tile = 0;
        int local_4_terrain_type = 0x19;
        if (DAT_a866_adjusted_current_terrain_type == 0x19 || DAT_a866_adjusted_current_terrain_type == 0x1a) {
            local_4_terrain_type = DAT_a866_adjusted_current_terrain_type;
            local_1e_surrounding_terrain_map = FUN_8007_01b4_module_14_102_analyze_surrounding_terrain();
            local_6_is_sea_tile = 1;
        }
        if (local_6_is_sea_tile == 1 && local_1e_surrounding_terrain_map != 0) {
            FUN_8007_05b8_module_14_102_draw_terrain_tile(local_4_terrain_type);
            if (DAT_017a_zoom_level != 0) {
                return;
            }
            int x = DAT_a550_draw_map_x_in_tiles;
            int y = DAT_a552_draw_map_y_in_tiles;
            int local_20 = FUN_1373_0458_get_prime_resource_at(x,y) + 1;
            if (DAT_0180_maybe_colony_vs_map_view == 0) {
                // note: prime resources start at 0x59
                FUN_8007_0558_module_14_102_draw_surface_sprite(local_20 + 0x5a);
            }
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(0, 1, 1);
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

        if (DAT_017a_zoom_level == 0){
            FUN_8007_06e0_module_14_102_draw_map_draw_terrain_transitions(0, local_6_is_sea_tile, 0);
        }

        // forest
        if (terrainSpriteId != 0x01) {
            if (DAT_a866_adjusted_current_terrain_type > 7 && DAT_a866_adjusted_current_terrain_type < 0x10 ||
                    DAT_a866_adjusted_current_terrain_type > 0xf && DAT_a866_adjusted_current_terrain_type < 0x18) {
                int mask = FUN_8007_041e_module_14_102_get_forest_neighbours_mask(0x03, local_22_base_terrain);
                FUN_8007_0558_module_14_102_draw_surface_sprite(mask + 0x41); // 0x40 = forst
            }
        }
        // plowed field
        if ((DAT_a863_value_from_terrain_map & 0x40) > 0) {
            FUN_8007_0558_module_14_102_draw_surface_sprite(0x96); // 0x95 is plowed field
        }

        // mountains (and not sea)
        if ((DAT_a866_adjusted_current_terrain_type & 0x20) > 0 && local_6_is_sea_tile == 0) {
            int isMajorMountain = DAT_a866_adjusted_current_terrain_type & 0x80;
            int mask = FUN_8007_0374_module_14_102_get_neighbours_mountains(3, isMajorMountain);
            FUN_8007_0558_module_14_102_draw_surface_sprite(mask + (isMajorMountain > 0 ? 0x31 : 0x21));
        }

        // rivers (and not sea)
        if ((DAT_a866_adjusted_current_terrain_type & 0x40) > 0 && local_6_is_sea_tile == 0) {
            int isMajorRiver = DAT_a866_adjusted_current_terrain_type & 0x80;
            int mask = FUN_8007_0314_module_14_102_get_neighbours_by_bitfield(0x3, 0x40);
            FUN_8007_0558_module_14_102_draw_surface_sprite(mask + (isMajorRiver > 0 ? 0x1 : 0x11));
        }

        if (DAT_017a_zoom_level > 0 && DAT_0184_show_hidden_terrain_state == 0) {
            // prime resources
            int primeResource = FUN_1373_0458_get_prime_resource_at(DAT_a550_draw_map_x_in_tiles, DAT_a552_draw_map_y_in_tiles);
            // note: primeResource == -1 when no prime resource placed
            if (primeResource > 0 && DAT_0180_maybe_colony_vs_map_view == 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x5a + primeResource);
            }

            // rumor icon
            int hasRumor = FUN_1373_0540_get_rumor_at(DAT_a550_draw_map_x_in_tiles, DAT_a552_draw_map_y_in_tiles);
            if (hasRumor > 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x68 + primeResource);
            }
        }

        if ((DAT_a863_value_from_terrain_map & 0xA0) > 0 && local_6_is_sea_tile == 0 && DAT_0184_show_hidden_terrain_state == 0) {
            int mask = FUN_8007_04e4_module_14_102_get_surface_neighbours_bitmask_8_directions_first_map_pointer(0x1, 0xa);
            // single road, no adjected roads
            if (mask == 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x51);
            } else {
                // draw each road as a single tile
                int local_1c = 1;
                for (int local_18 = 0; local_18 < 8; local_18++) {
                    if ((local_1c & mask) == 1) {
                        FUN_8007_0558_module_14_102_draw_surface_sprite(0x52 + local_18);
                    }
                    local_1c <<= 1;
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

        // this is a sea tile (checked above), so draw the tile
        //FUN_8007_067c_module_14_102_draw_terrain_tile(local_4_terrain_type);

        // river to shore tiles
        if (local_14_river != 0) {
            int local_12_baseSprite = (local_14_river & 0x80) != 0 ? 0x8d : 0x8d + 4;
            for (int local_e_direction = 0; local_e_direction < 4; local_e_direction++) {
                int y;
                if (DAT_00ae_y_directions[local_e_direction] < 0) {
                    y = -DAT_84ee_some_width;
                } else if (DAT_00ae_y_directions[local_e_direction] > 0) {
                    y = DAT_84ee_some_width;
                } else {
                    y = 0;
                }
                int x = DAT_00a8_x_directions[local_e_direction];
                int terrain = DAT_0152_game_map_terrain[DAT_a548_terrain_map_pointer_to_current_position + y + x];

                if ((terrain & 0x40) > 0) {
                    int adjustedTerrain2 = FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain((byte) terrain);

                    if (adjustedTerrain2 != 0x19 && adjustedTerrain2 != 0x1A) {
                        FUN_8007_0558_module_14_102_draw_surface_sprite(local_12_baseSprite + local_e_direction);
                    }
                }
            }
        }

        // fish prime resource
        if (DAT_017a_zoom_level == 0 && DAT_0180_maybe_colony_vs_map_view == 0) {
            int prime = FUN_1373_0458_get_prime_resource_at(DAT_a550_draw_map_x_in_tiles, DAT_a552_draw_map_y_in_tiles);
            if (prime >= 0) {
                FUN_8007_0558_module_14_102_draw_surface_sprite(0x5a + prime);
            }

        }
    }



    // AX = x
    // DX = y
    // BX = width
    // param_1 = height
    // param_2 = power
    public static void FUN_8007_0d60_module_14_102_draw_map(int x_min, int y_min, int width_in_tiles, int height_in_tiles, int power) {
        if (power < 0) {
            DAT_a862_power_mask = 0;
        } else {
            DAT_a862_power_mask = 1 << (power + 4);
        }

        FUN_8007_000c_module_14_102_calculate_viewport();

        if (x_min >= DAT_87aa_viewport_x_max || y_min >= DAT_87ac_viewport_y_max) {
            return;
        }

        int x_max = x_min + width_in_tiles;
        int y_max = y_min + height_in_tiles;
        if (x_max >= DAT_87aa_viewport_x_max) {
            x_max = DAT_87aa_viewport_x_max;
        }
        if (y_max >= DAT_87ac_viewport_y_max) {
            y_max = DAT_87ac_viewport_y_max;
        }

        if (x_min < DAT_82e2_viewport_x_min) {
            x_min = DAT_82e2_viewport_x_min;
        }

        if (y_min < DAT_82e6_viewport_y_min) {
            y_min = DAT_82e6_viewport_y_min;
        }

        int width = x_max - x_min;
        int height = y_max - y_min;

        int terrainMapPointer;
        int surfaceMapPointer;
        int visibilityMapPointer;
        int map_stride_in_tiles;
        if (DAT_0150_some_flag > 0) {
            terrainMapPointer = (y_min + 1) * DAT_84ee_some_width + x_min + 1;
            surfaceMapPointer = (y_min + 1) * DAT_84ee_some_width + x_min + 1;
            visibilityMapPointer = (y_min + 1) * DAT_84ee_some_width + x_min + 1;
            map_stride_in_tiles = width;
        } else {
            int x = Math.min(1, x_min);
            int y = Math.min(1, y_min);
            terrainMapPointer = FUN_1373_00fa_get_terrain_type_offset_at(x, y);
            surfaceMapPointer = FUN_1373_012e_get_surface_offset_at(x, y);
            visibilityMapPointer = FUN_1373_02e4_visibility_get_offset_at(x, y);
            map_stride_in_tiles = DAT_84e6_map_width;
        }

        // loop over the map

        DAT_a556_draw_map_y_in_pixels = (DAT_82e4_viewport_y_offset + height + 1) * DAT_82de_tile_pixel_size - 1;

        for (int y = y_min; y < y_max; y += 1, DAT_a556_draw_map_y_in_pixels += DAT_82de_tile_pixel_size) {
            DAT_a552_draw_map_y_in_tiles = y;

            DAT_a548_terrain_map_pointer_to_current_position = terrainMapPointer;
            DAT_a544_surface_map_pointer = surfaceMapPointer;
            DAT_a54c_visibility_map_pointer = visibilityMapPointer;

            //  don't draw first and last row
            boolean validY = (y > 1) && (y < DAT_84e8_map_height - 1);

            // Calculate perspective for colony view
            int local_20_something_y = 0;
            if (DAT_0180_maybe_colony_vs_map_view != 0) {
                local_20_something_y = Math.abs(y - DAT_0174_viewport_center_y);
            }

            DAT_a558 = 0;

            DAT_a554_draw_map_x_in_pixels = (DAT_82e2_viewport_x_min + width) * DAT_82de_tile_pixel_size + DAT_82de_tile_pixel_size / 2;

            for (int x = x_min; x < x_max; x += 1, DAT_a554_draw_map_x_in_pixels += DAT_82de_tile_pixel_size) {
                DAT_a550_draw_map_x_in_tiles = x;
                // don't draw first and last column
                boolean validX = (x > 0) && (x < DAT_84e6_map_width - 1);

                boolean valid = validX && validY;

                if (DAT_0180_maybe_colony_vs_map_view != 0) {
                    // note: result of the function is inverted
                    valid |= !FUN_1373_0040_should_draw_depending_on_distance_maybe(Math.abs(x - DAT_0172_viewport_center_x), y, DAT_0180_maybe_colony_vs_map_view);
                }

                FUN_8007_0938_module_14_102_draw_map_tile(valid);


                if (validX && validY) {
                    terrainMapPointer++;
                    surfaceMapPointer++;
                    visibilityMapPointer++;
                }

                // negate bit 1 of DAT_a558
                DAT_a558 = DAT_a558 ^ 1;
            }

            terrainMapPointer += map_stride_in_tiles;
            surfaceMapPointer += map_stride_in_tiles;
            visibilityMapPointer += map_stride_in_tiles;


        }

    }


    public static void FUN_8007_1016_module_14_102_draw_map_viewport(int power) {
        FUN_8007_000c_module_14_102_calculate_viewport();

        // if the map is smaller than the viewport (so it does not cover all space), draw wood tiles as background
        if (DAT_82e0_viewport_x_offset > 0 || DAT_82e4_viewport_y_offset > 0) {
            if (DAT_081c_address_of_woodtile_sprite_maybe != null) {
                FUN_1bd9_0006_draw_sprite_tiled(DAT_2640_2nd_backscreen, DAT_081c_address_of_woodtile_sprite_maybe, 0, 0,
                        DAT_2640_2nd_backscreen.width, DAT_2640_2nd_backscreen.height, 0, -8);
            } else {
                FUN_1b83_0000_clear_image(DAT_2640_2nd_backscreen, 0);
            }
        }

        FUN_8007_0d60_module_14_102_draw_map(DAT_82e2_viewport_x_min, DAT_82e6_viewport_y_min, DAT_84ea_number_of_x_tiles_in_viewport, DAT_84ec_number_of_y_tiles_in_viewport, power);
    }

    /**
     * input:
     * AX - power
     * DX - maybe: colony_vs_map_view flag
     */
    public static void FUN_8007_109c_module_14_102_draw_map_for_type(int mapViewType, int power) {
        DAT_0180_maybe_colony_vs_map_view = mapViewType;
        FUN_8007_1016_module_14_102_draw_map_viewport(power);
        DAT_0180_maybe_colony_vs_map_view = 0;
    }

    public static void FUN_8007_10ac_module_14_clear_last_visited_for_map() {
        for (int y = 0; y < DAT_84e8_map_height; y = y + 1) {
            for (int x = 0; x < DAT_84e6_map_width; x = x + 1) {
                FUN_1373_022c_visitor_set_last_visited(x, y, -1);
            }
        }
    }
}
