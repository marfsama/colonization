package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code12.FUN_1261_0064_maybe_wait_for_keystroke_or_click;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module14_102_Map.*;
import static com.marf.colonization.decompile.cmodules.Module14_5c.*;
import static com.marf.colonization.decompile.cmodules.Module14_df.FUN_7fe4_00c0_draw_map_and_minimap;

/**
 * Base Description: ??
 * Segment: 0x4af1
 * Status:
 * - all functions decompiled
 *
 */
public class Module00 {
    public static void FUN_4000_1f26_module_0_process_hide_terrain() {

        // slowly remove terrain layers
        for (int local4 = 0; local4 < 4; local4++) {
            DAT_0184_show_hidden_terrain_state = local4;

            int power = DAT_5338_savegame_header.field_0x22_maybe_current_turn == 0x0
                    ? DAT_5338_savegame_header.maybe_player_controlled_power
                    : -1;
            FUN_8007_1016_module_14_102_draw_map_viewport(power);

            FUN_7f61_012a_module_14_5c_flip_viewport_backscreen();
            FUN_7f61_0160_module_14_5c_flip_all_tiles_in_random_order();
        }

        DAT_0184_show_hidden_terrain_state = 0;

        int keyPressed = FUN_1261_0064_maybe_wait_for_keystroke_or_click();
        if ((DAT_27b9_maybe_key_modifier_state[keyPressed] & 0x2) != 2) {
            keyPressed -= 0x20; // maybe convert some character to uppercase
        }

        // if the user pressed 'h', slowly add terrain layers again
        if (keyPressed == 0x48) { // 0x48 = 'H'
            for (int local_4 = 2; local_4 > -1; local_4 = local_4 - 1) {
                DAT_0184_show_hidden_terrain_state = local_4;

                int power = DAT_5338_savegame_header.field_0x22_maybe_current_turn == 0x0
                        ? DAT_5338_savegame_header.maybe_player_controlled_power
                        : -1;

                FUN_8007_1016_module_14_102_draw_map_viewport(power);
                FUN_7f61_012a_module_14_5c_flip_viewport_backscreen();
                FUN_7f61_0160_module_14_5c_flip_all_tiles_in_random_order();
            }
        }
        FUN_7fe4_00c0_draw_map_and_minimap(true);
    }
}
