package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code11.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code14.*;
import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module14_Minimap.*;

public class Module14_83 {

    /**
     *
     * @param x map viewport x in tiles
     * @param y map viewport y in tiles
     * @param width map viewport width in tiles
     * @param height map viewport height in tiles
     */
    public static void FUN_7f88_0002_module_14_maybe_render_tribes(int x, int y, int width, int height) {
        int playerMask = 0x10 << DAT_5338_savegame_header.maybe_player_controlled_power;
        int x2 = x + width - 1;
        int y2 = y + height - 1;

        FUN_7f61_0004_module_14_5c_clamp_to_viewport(x, y, x2, y2);

        for (int villageIndex = 0; villageIndex < DAT_5338_savegame_header.num_indians; villageIndex++) {
            IndianVillage village = DAT_54a4_indian_village_list[villageIndex];
            // check if the cillage is in the visible area
            if (village.x >= x && village.y >= y && village.x <= x2 && village.y <= y2) {
                // check if the tile is visible to the current player or the debug flag "reveal whole map" is active
                if ((FUN_1373_02fc_get_visibility_at(village.x, village.y) & playerMask) > 0 || DAT_5338_savegame_header.field_0x22_maybe_current_turn > 0) {

                    int screenX = (village.x - DAT_82e2_viewport_x_min + DAT_82e0_viewport_x_offset) * DAT_82de_tile_pixel_size;
                    int screenY = (village.y - DAT_82e6_viewport_y_min + DAT_82e4_viewport_y_offset) * DAT_82de_tile_pixel_size;
                    FUN_112b_0790_draw_indian_village(DAT_2640_2nd_backscreen, DAT_017c_zoom_level_percent, screenX, screenY, villageIndex);
                }
            }
        }
    }


    public static void FUN_7f88_00ea_module_14_draw_tribes_viewport() {
        FUN_7f88_0002_module_14_maybe_render_tribes(DAT_82e2_viewport_x_min, DAT_82e6_viewport_y_min, DAT_84ea_number_of_x_tiles_in_viewport, DAT_84ec_number_of_y_tiles_in_viewport);
    }
    public static void FUN_7f88_0248_module_14() {

    }

    public static void FUN_7f88_034c_module_14_83_draw_unit(int unit_index, boolean param2_maybe_is_active, boolean param3) {
        // Early exit: param2 != 0 AND param3 == 0
        if (param2_maybe_is_active == true && param3 == false) {
            return;
        }

        // If param2 == 0, check if unit is on a colony
        if (param2_maybe_is_active == false) {
            // Get unit coordinates
            Unit unit = DAT_30fc_units_list[unit_index];
            int x = unit.x;
            int y = unit.y;

            // Check if there's a colony at this location
            int colonyId = FUN_15d9_0a80_find_colony_at(x, y);
            if (colonyId >= 0) {
                return; // Unit is in a colony, don't draw separately
            }
        }

        int flags;

        // Base flags based on param2
        if (param2_maybe_is_active == true) {
            flags = 0x80;  // Some special drawing mode
        } else {
            flags = 0xC0;  // Default drawing mode (0x40 + 0x80)
        }

        // Check if unit is owned by current player
        Unit unit = DAT_30fc_units_list[unit_index];
        int unitOwner = unit.nationIndex & 0x0F;

        if (unitOwner != DAT_5338_savegame_header.maybe_player_controlled_power) {
            flags |= 0x20;  // Add flag for non-player-owned units
        }

        // Convert to screen coordinates
        int screenX = (unit.x - DAT_82e2_viewport_x_min + DAT_82e0_viewport_x_offset) * DAT_82de_tile_pixel_size;
        int screenY = (unit.y - DAT_82e6_viewport_y_min + DAT_82e4_viewport_y_offset) * DAT_82de_tile_pixel_size + 8;

        // Draw the unit
        // param_1 - zoom_level_percent
        // param_2 - tile_pixel_size
        // param_3 - y
        // BX - x
        // DX - flags
        // AX - unit index
        FUN_112b_01ba_draw_unit(
                unit_index,
                flags,
                screenX,
                screenY,
                DAT_017c_zoom_level_percent,
                DAT_82de_tile_pixel_size
        );
    }

    public static void FUN_7f88_03f6(boolean b) {

    }

    public static void FUN_7f88_0428_module_14_83_draw_unit(int unitIndex) {
        // Early exit conditions
        if (DAT_5338_savegame_header.field9_0x10 != 0 ||
                Data.DAT_0816 != 0 ||
                Data.DAT_0818 != 0) {

            FUN_7f88_034c_module_14_83_draw_unit(unitIndex, false, false);
            return;
        }

        Unit activeUnit = DAT_30fc_units_list[DAT_5338_savegame_header.active_unit];
        if (activeUnit.nationIndex >= 4) {
            FUN_7f88_034c_module_14_83_draw_unit(unitIndex, false, false);
            return;
        }

        Player player = DAT_53c6_player_list[activeUnit.nationIndex];
        if (player.control != 0) {
            FUN_7f88_034c_module_14_83_draw_unit(unitIndex, false, false);
            return;
        }

        if (FUN_1415_1338_unit_is_selectable(DAT_5338_savegame_header.active_unit) == false) {
            FUN_7f88_034c_module_14_83_draw_unit(unitIndex, false, false);
            return;
        }

        Unit unit = DAT_30fc_units_list[unitIndex];

        if (unit.x != activeUnit.x || unit.y != activeUnit.y) {
            FUN_7f88_034c_module_14_83_draw_unit(unitIndex, false, false);
            return;
        }

        FUN_7f88_03f6(true);
    }


    public static void FUN_7f88_04bc_module_14_render_units(int x1, int y1, int width, int height) {
        int x2 = x1 + width - 1;
        int y2 = y1 + height - 1;
        FUN_7f61_0004_module_14_5c_clamp_to_viewport(x1, y1, x2, y2);

        int player_id = DAT_5338_savegame_header.maybe_player_controlled_power;
        int visibilityMask = 1 << (player_id + 4);

        if (DAT_5338_savegame_header.num_units > 0) {
            for (int unitIndex = 0; unitIndex < DAT_5338_savegame_header.num_units; unitIndex++) {
                Unit unit = DAT_30fc_units_list[unitIndex];

                // only unit not in a transport
                if (unit.transportChain1 >= 0) {
                    continue;
                }
                int x = unit.x;
                int y = unit.y;

                if (x < x1 || x > x2 || y < y1 || y > y2) {
                    continue;
                }

                boolean shouldDraw = false;
                if (unit.nationIndex == player_id)  {
                    // Unit owned by current player - check fog of war
                    int visibility = FUN_1373_02fc_get_visibility_at(x,y);
                    if ((visibility & visibilityMask) != 0) {
                        shouldDraw = true;
                    }
                }
                 else {
                    // Unit owned by other player - check if visible to current player
                    // Check if unit has visibility flag for current player
                    byte playerMask = (byte)(0x10 << player_id);
                    if ((unit.nationIndex & playerMask) != 0) {
                        shouldDraw = true;
                    }
                }

                // Special case: observer/debug mode shows all units
                if (DAT_5338_savegame_header.field_0x22_maybe_current_turn != 0) {
                    shouldDraw = true;
                }

                // Draw the unit if visible
                if (shouldDraw) {
                    FUN_7f88_0428_module_14_83_draw_unit(unitIndex);
                }
            }
        }
    }

    public static void FUN_7f88_058e_module_14_render_units_in_viewport() {
        FUN_7f88_04bc_module_14_render_units(DAT_82e2_viewport_x_min, DAT_82e6_viewport_y_min, DAT_84ea_number_of_x_tiles_in_viewport, DAT_84ec_number_of_y_tiles_in_viewport);
    }
}
