package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module0a.*;
import static com.marf.colonization.decompile.cmodules.Module16.*;
import static com.marf.colonization.decompile.cmodules.Module1d_19.FUN_8e94_00e4_module_1d_19_debug_warning_popup;

/**
 * Base Description: Unit Stuff
 * Segment: 0x15a1
 * Status:
 * - ??
 */
public class Code15 {

    public static int FUN_15a1_000c_get_treaty_type(int sourceNation, int destinationNation) {
        if (sourceNation < 4) {
            return DAT_87e2_europe[sourceNation].treaty[destinationNation];
        }
        return DAT_2b4d_5a8e_tribes_list[sourceNation-4].meetings[destinationNation];
    }

    public static int FUN_15a1_003a_set_treaty_type(int sourceNation, int destinationNation, int treaty_value) {
        if (sourceNation < 4) {
            return DAT_87e2_europe[sourceNation].treaty[destinationNation];
        }
        return DAT_2b4d_5a8e_tribes_list[sourceNation-4].meetings[destinationNation];
    }

    public static int FUN_15a1_00d8_get_treaty(int param_1_tribe_as_nation, int param_2_power, int param_3_some_flag) {
        int uVar1 = FUN_15a1_000c_get_treaty_type(param_1_tribe_as_nation,param_2_power);
        uVar1 = FUN_15a1_003a_set_treaty_type(param_1_tribe_as_nation,param_2_power,uVar1 & ~param_3_some_flag);

        int uVar2 = FUN_15a1_000c_get_treaty_type(param_2_power,param_1_tribe_as_nation);
        uVar2 = FUN_15a1_003a_set_treaty_type(param_2_power,param_1_tribe_as_nation, uVar2 & ~param_3_some_flag);

        if ((uVar2 & param_3_some_flag) != (uVar1 & param_3_some_flag)) {
            FUN_8e94_00e4_module_1d_19_debug_warning_popup("Treaty off error",uVar1,uVar2,param_3_some_flag);
        }
        return uVar1;
    }


    /**
     * returns the power name text index of a unit (including indians) in multiple.
     */
    public static int FUN_15a1_01a0_get_power_name_textindex_multiple(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner - 4].tribe_name_multiple;
        }
        if (DAT_5338_savegame_header.field1_0x2_independence_flag != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2e1e; // rebels
            }
            if (DAT_5338_savegame_header.tories_nation_maybe == param_1_unit_owner) {
                return DAT_2e20; // tories
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

    /**
     * returns the power name text index of a unit (including indians) in singular
     */
    public static int FUN_15a1_01e8_get_power_name_textindex_singular(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner - 4].tribe_name_singular;
        }
        if (DAT_5338_savegame_header.field1_0x2_independence_flag != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2dfc; // rebel
            }
            if (DAT_5338_savegame_header.tories_nation_maybe == param_1_unit_owner) {
                return DAT_2dfe; // tory
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

    public static void FUN_15cb_0000(int tribeIndex) {
        DAT_8cf8_tribe_index = tribeIndex;
        if (tribeIndex < 0 || tribeIndex >= 8) {
            tribeIndex = 0;
        }
        DAT_8cf6_tribe_index_plus_4 = tribeIndex + 4;
        DAT_8cf4_current_tribe = DAT_2b4d_5a8e_tribes_list[tribeIndex];
    }

    public static void FUN_15cb_002c_set_current_indian_village(int villageIndex) {
        DAT_8cf2_village_index = villageIndex;
        if (villageIndex < 0) {
            return;
        }
        if (villageIndex >= DAT_5338_savegame_header.num_indians) {
            villageIndex = 0;
        }
        IndianVillage village = DAT_54a4_indian_village_list[villageIndex];
        DAT_8cf0_current_village = village;
        FUN_15cb_0000(village.nation);
    }

    public static int FUN_15cb_009c_get_aggression_level(int param_1_aggression) {
        if (param_1_aggression < 25) {
            return 0;
        }
        if (param_1_aggression < 50) {
            return 1;
        }
        if (param_1_aggression < 75) {
            return 2;
        }
        return 3;
    }

    public static int FUN_15cb_00da_get_tribe_aggression_for_power(int tribeIndex, int aggressor) {
        return DAT_2b4d_5a8e_tribes_list[tribeIndex].aggressions[aggressor];
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
        DAT_0342_flag_colony_tiles_analyzed = false;
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

    /**
     * This is a colony tile assessment function that determines what's happening on tiles relative to a colony.
     * It returns a bitmask of flags indicating various conditions on the tile. Let me analyze it:
     * Purpose Analysis
     * <p>
     * This function examines a tile position relative to a colony and returns flags indicating:
     * <p>
     * Visibility status (fog of war)
     * Unit presence and threat level
     * Rumors/special events
     * Indian villages
     * Other colonies
     * Adjacency to colony center
     * <p>
     * Key Return Value (Bitmask in local_8)
     * <p>
     * The function builds a bitmask in local_8 with these flags:
     * <p>
     * Bit 0x10 (0x10): Tile not visible/fog of war
     * Bit 0x80 (0x80): Enemy combat unit present (threat)
     * Bit 0x02 (0x02): Rumor/special event present
     * Bit 0x04 (0x04): Indian village present
     * Bit 0x40 (0x40): Another colony's tile usage conflict
     * Bit 0x20 (0x20): Another colony present at same location
     * Bit 0x08 (0x08): Tile is adjacent to colony center (distance 0 or 1 in both axes)
     * <p>
     * param_1 and param_2 appear to be offsets from the colony center (likely -2 to +2 range).
     */
    public static int FUN_15d9_23c6_get_colony_tile_type_bitfield(int x, int y) {
        // TODO
        return 0;
    }


    public static void FUN_15d9_2660_analyze_colony_tiles() {
        if (!DAT_0342_flag_colony_tiles_analyzed) {
            for (int y = 0; y < 5; y = y + 1) {
                for (int x = 0; x < 5; x = x + 1) {
                    int uVar1 = FUN_15d9_23c6_get_colony_tile_type_bitfield(x, y);
                    DAT_8d98_colony_tile_assessments[y + x * 5] = uVar1;
                }
            }
        }
        DAT_0342_flag_colony_tiles_analyzed = true;
    }

    public static int FUN_15d9_054e_get_gold(int power)  {
        return DAT_87e2_europe[power].gold;
    }

    public static void FUN_15d9_0560_add_gold(int power, int goldDelta) {
        int gold = FUN_15d9_054e_get_gold(power);
        gold += goldDelta;
        if (gold > 99_999) {
            gold = 99_999;
        }
        DAT_87e2_europe[power].gold = gold;
    }

    public static void FUN_15d9_05a0_deduct_gold(int power,int gold) {
        FUN_15d9_0560_add_gold(power, -gold);
    }




    public static int FUN_15d9_05ec_get_slot_index(int x, int y) {
        for (int i = 0; i < 20; i++) {
            if (x == DAT_00c8_directions_x[i] && y == DAT_00de_directions_y[i]) {
                return i;
            }
        }
        return -1;
    }

    public static void FUN_15d9_0672_change_purchased_land_flag(int x, int y, boolean set_or_remove) {
        int colonySlot = FUN_15d9_05ec_get_slot_index(x,y);
        if (colonySlot < 0 ) {
            return;
        }
        Colony colony = DAT_8d6c_current_colony_ptr;

        FUN_1373_0162_change_surface_type_at(colony.x + x - 2, colony.y + y - 2, 0x10, set_or_remove);
    }

    public static int FUN_15d9_06b0_get_colonist_at_tile_slot(int x, int y) {
        int slotIndex = FUN_15d9_05ec_get_slot_index(x,y);
        if (slotIndex < 0) {
            return -1;
        }
        Colony colony = DAT_8d6c_current_colony_ptr;
        return colony.tile_usage[slotIndex];
    }

    public static void FUN_15d9_06dc_maybe_grab_colony_tile(int x, int y, int param_3) {
        Colony colony = DAT_8d6c_current_colony_ptr;
        int local_e_slot_index = FUN_15d9_05ec_get_slot_index(x, y);
        int local_8_colonyOwner = FUN_1373_02a4_get_tile_owner(colony.x, colony.y);

        if (local_e_slot_index < 0) {
            return;
        }
        int tileUsage = colony.tile_usage[local_e_slot_index];
        if (tileUsage <= 0) {
            return;
        }

        if (DAT_0343_some_flag == true) {
            return;
        }

        int local_4_x = colony.x + x - 2;
        int local_6_y = colony.y + y - 2;
        int unitOwner = FUN_1373_0318_surface_get_unit_owner(local_4_x, local_6_y);

        // is there a unit on the tile?
        if (unitOwner < 0) {
            // nope. is there some other owner of the tile?
            int colonyOwner = FUN_1373_03ba_surface_get_colony_owner(local_4_x, local_6_y);
            if (colonyOwner < 0) {
                // nope. set the tile to be owned by the nation of the colony
                FUN_1373_022c_visitor_set_last_visited(local_4_x, local_6_y, colony.nation);
            }
        }

        int local_14 = DAT_8d44_something_with_colony_tiles[local_4_x * 5 + local_6_y];

        if (local_14 < 0) {
            // set "already purchased" flag
            FUN_15d9_0672_change_purchased_land_flag(local_4_x, local_6_y, true);
            return;
        }

        // note:this function sets some global variables
        FUN_6158_0358_module_a_find_closest_indian_village(local_4_x, local_6_y, -1, local_8_colonyOwner);
        int local_a_village_tile_cost = 0;

        if (colony.nation >= 4 && DAT_53c6_player_list[colony.nation].control != 0) {
            local_a_village_tile_cost = FUN_6158_07c4_maybe_calculate_village_tile_cost(DAT_8cf2_village_index, colony.nation, local_4_x, local_6_y);

            int gold = FUN_15d9_054e_get_gold(colony.nation);
            if (gold >= (local_a_village_tile_cost / 2)) {
                DAT_8cf4_current_tribe.tiles_purchased++;
                FUN_15d9_05a0_deduct_gold(DAT_8d6c_current_colony_ptr.nation, local_a_village_tile_cost);
            } else {
                local_a_village_tile_cost = 0;
            }
        }
        // if no tribal land was purchased
        if (local_a_village_tile_cost != 0) {
            int local_16_difficulty = 0;
            if (DAT_5338_savegame_header.viewport_power < 4 && DAT_53c6_player_list[DAT_5338_savegame_header.viewport_power].control == 1)  {
                local_16_difficulty = DAT_5338_savegame_header.difficulty;
            }
            int local_12 = local_16_difficulty;
            int local_10 = local_16_difficulty;
            if (DAT_8d5e_min_distance_to_indian_village <= 2) {
                local_10 = local_16_difficulty * 2;
            }
            if (DAT_8d5e_min_distance_to_indian_village <= 1) {
                local_10 = local_10 + local_12;
            }
            int aggression_delta = local_10;
            int primeResource = FUN_1373_0458_get_prime_resource_at(local_4_x, local_6_y);
            if (primeResource > -1) {
                aggression_delta = aggression_delta * 2;
            }
            FUN_6158_00f2_adjust_tribe_aggression(local_14 - 4 , DAT_8d6c_current_colony_ptr.nation, aggression_delta);


        }
        DAT_8d44_something_with_colony_tiles[local_4_x*5+local_6_y] = -1;
        FUN_15d9_0672_change_purchased_land_flag(local_4_x, local_6_y, true);
    }

    public static void FUN_15d9_2850_maybe_grab_all_colony_tiles() {
        for (int y = 0; y < 5; y = y + 1) {
            for (int x = 0; x < 5; x = x + 1) {
                if (DAT_8d98_colony_tile_assessments[y + x * 5] != 0) {
                    FUN_15d9_06dc_maybe_grab_colony_tile(x, y, -1);
                }
            }
        }
    }

    public static void FUN_15d9_3904() {
        FUN_15d9_2660_analyze_colony_tiles();
        FUN_15d9_2850_maybe_grab_all_colony_tiles();
        FUN_15d9_2e74();
        int uVar1 = FUN_15d9_2f10();
        DAT_0332 = uVar1;
        DAT_0334 = 0;
        DAT_0336 = 0;
    }
}
