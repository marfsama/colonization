package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code12.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Code15.FUN_15cb_009c_get_aggression_level;
import static com.marf.colonization.decompile.cmodules.Code19.*;
import static com.marf.colonization.decompile.cmodules.Code20.FUN_2071_00be_random_next_value_between;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module16.FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter;
import static com.marf.colonization.decompile.cmodules.Module16.FUN_83d9_378e_show_text_box;

public class Module0a {


    public static void FUN_6158_0000_show_indian_burn_mission_textbox(int tribeIndex, int power) {

        // find tribe village with a mission
        for (int villageIndex = 0;villageIndex < DAT_5338_savegame_header.num_indians; villageIndex++) {
            IndianVillage village = DAT_54a4_indian_village_list[villageIndex];
            if (village.nation == tribeIndex+4 && (village.mission & 0xf) == power) {
                village.mission = 0xff;

                int tribeNameIndex = FUN_15a1_01a0_get_power_name_textindex_multiple(tribeIndex + 4);
                FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(0,tribeNameIndex);
                int powerNameIndex = FUN_15a1_01e8_get_power_name_textindex_singular(power);
                FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(1,powerNameIndex);
                if ((power < 4) && DAT_53c6_player_list[power].control != 0) {
                    FUN_83d9_378e_show_text_box("INDIANBURN",1);
                }
                return;
            }
        }
    }

    public static void FUN_6158_00f2_adjust_tribe_aggression(int tribe_index, int power, int aggression_delta) {
        int aggression = FUN_15cb_00da_get_tribe_aggression_for_power(tribe_index, power);
        int local_5e_clamped_aggression = FUN_124c_0000_clamp(aggression_delta, 0, 100);
        int local_6_aggression_level = FUN_15cb_009c_get_aggression_level(local_5e_clamped_aggression);

        // spanish gets aggression twice as fast
        if (power == 1 && aggression_delta > 0) {
            aggression_delta >>= 1;
        }

        // check pocahontas
        if (FUN_196c_0004_check_founding_fathers( power, 0x10) && aggression_delta > 0) {
            aggression_delta <<= 1;
        }

        int currentAggression = DAT_2b4d_5a8e_tribes_list[tribe_index].aggressions[power];
        currentAggression += aggression_delta;
        int local_64_updated_aggression = FUN_124c_0000_clamp(currentAggression, 0, 100);
        DAT_2b4d_5a8e_tribes_list[tribe_index].aggressions[power] = local_64_updated_aggression;

        int local_8_updated_aggression_level = FUN_15cb_009c_get_aggression_level(local_64_updated_aggression);

        local_6_aggression_level >>= 1;
        local_8_updated_aggression_level >>= 1;

        if (aggression_delta >= 0) {
            FUN_15a1_00d8_get_treaty(tribe_index + 4, power, 4);

            if (local_64_updated_aggression < 75) {
                FUN_15a1_00d8_get_treaty(tribe_index + 4, power, 2);
            }
        }

        // when the aggression is 100, there is a chance (depending on the difficulty) that the indians burn
        // a mission in one of the villages.
        if (local_64_updated_aggression == 100) {
            int treatyType = FUN_15a1_000c_get_treaty_type(tribe_index+4, power);
            if ((treatyType & 0x40) != 0) {
                int local_66_difficulty;
                if (power < 4 && DAT_53c6_player_list[power].control == 0) {
                    local_66_difficulty = DAT_5338_savegame_header.difficulty;
                } else {
                    local_66_difficulty = 1;
                }
                int random = FUN_2071_00be_random_next_value_between(0, 10);
                int cx = local_66_difficulty+1;
                if (random < cx) {
                    FUN_6158_0000_show_indian_burn_mission_textbox(tribe_index, power);
                }
                return;
            }
        }

        int bx = local_5e_clamped_aggression / -5;
        int ax = Math.min(local_64_updated_aggression, 99) / -5;
        if (ax == bx) {
            return;
        }
        FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(0, FUN_15a1_01e8_get_power_name_textindex_singular(power));
        FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(1, FUN_15a1_01e8_get_power_name_textindex_singular(tribe_index));

        if (aggression_delta >= 0) {
            return;
        }

        for (int villageIndex = 0; villageIndex < DAT_5338_savegame_header.num_indians; villageIndex++) {
            IndianVillage village = DAT_54a4_indian_village_list[villageIndex];
            if (village.nation != tribe_index+4) {
                // if the aggression level decreases
                if (local_6_aggression_level > local_8_updated_aggression_level)  {
                    // set panic of all villages to 0
                    village.panic[power] = 0;
                } else {
                    // aggression level is increased.

                    // new aggression level is 0?
                    if (local_8_updated_aggression_level == 0)  {
                        // set panic leve to at least 0x20
                        village.panic[power] = Math.min(0x20, village.panic[power]);
                    } else {
                        // else set panic level to at least 0x60
                        village.panic[power] = Math.min(0x60, village.panic[power]);
                    }
                }
            }
        }
    }

    public static int FUN_6158_0358_module_a_find_closest_indian_village(int x, int y, int nation_filter, int power_filter) {
        int closest_idx = -1;
        int min_distance = 9999;

        for (int i = 0; i < DAT_5338_savegame_header.num_indians; i++) {
            IndianVillage village = DAT_54a4_indian_village_list[i];
            if (nation_filter >= 0 && village.nation != nation_filter)
                continue;

            if (power_filter >= 0 && FUN_1373_02a4_get_tile_owner(village.x, village.y) != power_filter)
                continue;

            int distance = FUN_124c_0034_distance(x - village.x, y - village.y);
            if (distance < min_distance) {
                min_distance = distance;
                closest_idx = i;
            }
        }
        DAT_8d5e_min_distance_to_indian_village = min_distance;
        if (closest_idx >= 0) {
            FUN_15cb_002c_set_current_indian_village(closest_idx);
        }

        return closest_idx;
    }

    /**
     * This is a complex AI scoring function for Indian villages - likely calculating a "desirability" or "threat" score for a target location from a specific power's perspective. Let me break it down:
     *
     * Algorithm Steps:
     * 1. Setup (lines 07c4-07ee):
     *
     *     Set current village context
     *
     *     Calculate distance between village and target using FUN_124c_0070 (likely the weighted distance function we saw earlier)
     *
     * 2. Base Score Calculation (two branches):
     * Branch A: Human Player (07f1-0827):
     *
     *     If power < 4 (European power) AND player-controlled (control == 0)
     *
     *     Formula: base_score = (difficulty + 3) * 2 + tribe_field_0x02 + tribe_field_0x05 - distance
     *
     *     Multiplier: 0x41 (65 decimal)
     *
     * Branch B: AI/Other (082a-0849):
     *
     *     Formula: base_score = tribe_field_0x02 + tribe_field_0x05 - difficulty - distance + 0xC
     *
     *     Multiplier: 0x32 (50 decimal)
     *
     * 3. Modifiers:
     * Power Reputation Modifier (084e-0864):
     *
     *     Adjust based on DAT_2b4d_938a[power] (likely reputation/relations)
     *
     *     Formula: max(0, (10 - reputation) / 2), subtracted from base score
     *
     * Resource Bonus (0867-087b):
     *
     *     If target has prime resource: base_score *= 2
     *
     *     Check using FUN_1373_0458_get_prime_resource_at
     *
     * Clamp Minimum (087b-0886):
     *
     *     Ensure base_score >= 1
     *
     * Apply Multiplier (0889-088f):
     *
     *     base_score *= multiplier (65 or 50 from step 2)
     *
     * 4. Aggression Modifier (0892-08c0):
     *
     *     Only for human players (power < 4 and control == 0)
     *
     *     Get tribe aggression towards viewport power
     *
     *     Apply aggression factor: base_score *= (aggression + 1)
     *
     * 5. Final Adjustments:
     * Village Flag Check (08c3-08cf):
     *
     *     If village has flag 0x04 in byte 3: base_score += base_score/2 (50% bonus)
     *
     * Peace Treaty Check (08d2-08e3):
     *
     *     Check if peace treaty exists (thunk_FUN_196c_0004(power, 2))
     *
     *     If peace exists: base_score = 0
     *
     * Final Halving (08e8-08ef):
     *
     *     base_score /= 2 before returning
     *
     * Key Data Structures:
     *
     *     Indian Village: Has x, y coordinates, flags (byte 3 has 0x04 flag)
     *
     *     Tribe Data: Has fields at offsets 0x02 and 0x05 (likely aggression/strength values)
     *
     *     Player List: DAT_2b4d_53c6_player_list with control field (0 = human)
     *
     *     Reputation: DAT_2b4d_938a array indexed by power
     *
     * Decompiled Pseudocode:
     * c
     *
     * int16_t FUN_6158_07c4(int16_t village_idx, int16_t power, int16_t target_x, int16_t target_y) {
     *     // Setup
     *     set_current_village(village_idx);
     *     int16_t distance = calculate_distance(village, target_x, target_y);
     *
     *     // Base score calculation
     *     int16_t base_score, multiplier;
     *
     *     if (power < 4 && players[power].control == 0) {  // Human player
     *         base_score = (difficulty + 3) * 2 +
     *                      current_tribe->field_0x02 +
     *                      current_tribe->field_0x05 -
     *                      distance;
     *         multiplier = 65;
     *     } else {  // AI or other
     *         base_score = current_tribe->field_0x02 +
     *                      current_tribe->field_0x05 -
     *                      difficulty -
     *                      distance + 12;
     *         multiplier = 50;
     *     }
     *
     *     // Reputation modifier
     *     int16_t rep_mod = max(0, (10 - reputation[power]) / 2);
     *     base_score -= rep_mod;
     *
     *     // Resource bonus
     *     if (has_prime_resource(target_x, target_y)) {
     *         base_score *= 2;
     *     }
     *
     *     // Clamp minimum
     *     if (base_score < 1) base_score = 1;
     *
     *     // Apply multiplier
     *     base_score *= multiplier;
     *
     *     // Aggression modifier (human players only)
     *     if (power < 4 && players[power].control == 0) {
     *         int16_t aggression = get_tribe_aggression_for_power(current_tribe_idx, viewport_power);
     *         base_score *= (aggression + 1);
     *     }
     *
     *     // Village flag bonus
     *     if (current_village->flags & 0x04) {
     *         base_score += base_score / 2;  // 50% bonus
     *     }
     *
     *     // Peace treaty check
     *     if (has_peace_treaty(power, 2)) {
     *         base_score = 0;
     *     }
     *
     *     // Final adjustment
     *     return base_score / 2;
     * }
     * */
    public static int FUN_6158_07c4_maybe_calculate_village_tile_cost(int village, int nation, int x, int y) {
        // TODO
        return 0;
    }

}
