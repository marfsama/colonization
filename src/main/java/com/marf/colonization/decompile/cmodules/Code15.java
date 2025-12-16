package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code13.FUN_1373_000e_is_tile_in_drawable_rect;
import static com.marf.colonization.decompile.cmodules.Code13.FUN_1373_0346_surface_get_european_unit_owner;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module16.FUN_83d9_3730_module_16_draw_text_from_gametxt;

/**
 * Base Description: Unit Stuff
 * Segment: 0x15a1
 * Status:
 * - ??
 *
 */
public class Code15 {
    /** returns the power name text index of a unit (including indians) in multiple. */
    public static int FUN_15a1_01a0_get_power_name_textindex_multiple(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner-4].tribe_name_multiple;
        }
        if (DAT_5338_savegame_header.field1_0x2_independence_flag != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2e1e; // rebels
            }
            if (DAT_5338_savegame_header.tories_nation_maybe == param_1_unit_owner){
                return DAT_2e20; // tories
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

    /** returns the power name text index of a unit (including indians) in singular */
    public static int FUN_15a1_01e8_get_power_name_textindex_singular(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner-4].tribe_name_singular;
        }
        if (DAT_5338_savegame_header.field1_0x2_independence_flag != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2dfc; // rebel
            }
            if (DAT_5338_savegame_header.tories_nation_maybe == param_1_unit_owner){
                return DAT_2dfe; // tory
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

    /**
     * Base Description: Something with colonies
     * Segment: 0x15d9
     * Status:
     * - ??
     */
    public static void FUN_15d9_0036_check_if_colony_is_player_controlled(int colony_index) {
        // Validate colony index range
        boolean was_invalid = false;
        if (colony_index < 0 || colony_index >= DAT_5338_savegame_header.num_colonies) {
            // Index out of bounds, default to colony 0
            colony_index = 0;
            was_invalid = true;
        }

        Colony current_colony = DAT_5cfe_colonies_list[colony_index];
        DAT_8d6c_current_colony_ptr = current_colony;

        // Check if colony belongs to current player and is valid for interaction
        boolean is_player_colony = false;

        if (current_colony.nation == DAT_5338_savegame_header.maybe_player_controlled_power) {
            // Colony belongs to current player's nation
            is_player_colony = true;

            // Additional checks for European colonies controlled by human player
            if (current_colony.nation < 4) {  // European power (0-3)
                Player colony_owner = DAT_53c6_player_list[current_colony.nation];
                if (colony_owner.control == 0) {  // Controlled by AI, not human
                    is_player_colony = false;
                }
            } else {
                // Native colony (nation >= 4), not player-controlled
                is_player_colony = false;
            }
        }

        // Set the colony validity flag
        if (is_player_colony && !was_invalid) {
            DAT_a85b_colony_valid_flag = 1;  // Valid player colony selected
        } else {
            DAT_a85b_colony_valid_flag = 0;  // Invalid or not player colony
        }

        // Reset UI state variables
        DAT_033e_some_flag = 0;
        DAT_0342_some_flag = 0;
    }


    public static boolean FUN_15d9_0368_is_building_in_colony(int colonyIndex, int buildingIndex) {
        if (buildingIndex < 0) {
            return false;
        }

        // building / 8
        int building = buildingIndex >> 3;
        int mask = 0x1 << (buildingIndex & 7);

        return (DAT_5cfe_colonies_list[colonyIndex].buildings[building] & mask) > 0;
    }

    public static int FUN_15d9_03e0_get_building_level(int colony_index, int building) {
        int local_4_result = 0;
        do {
            if (FUN_15d9_0368_is_building_in_colony(colony_index, building)) {
                local_4_result = local_4_result + 1;
            }
            building = DAT_8f2c_buildings_table[building].next;
        } while (building > -1);
        return local_4_result;
    }


    /**
     * returns the colony index at pos x,y or -1 if there is no colony
     */
    public static int FUN_15d9_0a80_find_colony_at(int x, int y) {
        int result = -1;
        if (FUN_1373_000e_is_tile_in_drawable_rect(x, y)) {
            int owner = FUN_1373_0346_surface_get_european_unit_owner(x, y);
            if (owner > -1) {
                for (int colonyIndex = 0; colonyIndex < DAT_5338_savegame_header.num_colonies; colonyIndex++) {
                    Colony colony = DAT_5cfe_colonies_list[colonyIndex];
                    if (colony.x == x && colony.y == y) {
                        result = colonyIndex;
                        break;
                    }
                }
                if (result < 0) {
                    // colony not found: display error message "Colony Flags Error."
                    FUN_83d9_3730_module_16_draw_text_from_gametxt("COLONYFLAG");
                }
            }
        }
        return result;
    }

    /**
     * updates the building in the currently selected colony.
     * params:
     * - building_id - building to set
     * - new_value - true - set bit, false, remove bit
     */
    public static void FUN_15d9_0ffe_update_building_bitset(int param_1_building, boolean param_2_new_value) {
        byte buildingByte = DAT_8d6c_current_colony_ptr.buildings[param_1_building >> 3];
        int mask = 1 << (param_1_building & 7);

        if (param_2_new_value) {
            buildingByte |= mask;
        } else {
            buildingByte = (byte) (buildingByte & ~mask);
        }
        DAT_8d6c_current_colony_ptr.buildings[param_1_building >> 3] = buildingByte;
    }

    public static int FUN_15cb_00da_get_tribe_aggression_for_power(int tribeIndex, int aggressor) {
        return DAT_2b4d_5a8e_tribes_list[tribeIndex].aggressions[aggressor];
    }

}
