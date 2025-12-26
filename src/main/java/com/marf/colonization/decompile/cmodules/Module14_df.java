package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code1b.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module14_102_Map.*;
import static com.marf.colonization.decompile.cmodules.Module14_5c.*;
import static com.marf.colonization.decompile.cmodules.Module14_83.*;
import static com.marf.colonization.decompile.cmodules.Module14_Minimap.*;

public class Module14_df {

    /** draws a selected portion of the map. */
    public static void FUN_7fe4_0004_module_14_df_draw_map_section(int x, int y, int width,
                                                                   int height, boolean flushToScreen) {
        FUN_7f61_004a_module_14_5c_clamp_to_viewport(x, y, width, height);

        int power;
        if (DAT_5338_savegame_header.field_0x22_maybe_current_turn == 0) {
            power = DAT_5338_savegame_header.maybe_player_controlled_power;
        }
        else {
            power = 0xffff;
        }
        FUN_8007_0d60_module_14_102_draw_map(x,y,width, height, power);
        FUN_7f88_00ea_module_14_draw_tribes_viewport();
        FUN_7f88_0248_module_14_83_render_colonies_viewport();
        FUN_7f61_00c0_module_14_5c_flip_backscreen_in_tile_coordinates(x,y,width, height);
        FUN_7f88_04bc_module_14_83_draw_units(x,y,width, height);

        FUN_7f05_0360_module_14_draw_minimap(x,y,width, height, false, power, false);
        if (flushToScreen) {
            FUN_7f61_022c_flip_backscreen_in_tiles(x,y,width, height);
        }
    }

    public static void FUN_7fe4_00c0_module_14_df_draw_map_and_minimap(boolean flushToScreen) {
        FUN_1bae_0008_draw_rectangle(DAT_2638_backscreen, 0, -1, 7, DAT_84f6_viewport_width, 0x8);

        int power = DAT_5338_savegame_header.field_0x22_maybe_current_turn == 0
                ? DAT_5338_savegame_header.maybe_player_controlled_power
                : -1;

        FUN_8007_1016_module_14_102_draw_map_viewport(power);
        FUN_7f88_00ea_module_14_draw_tribes_viewport();
        FUN_7f88_0248_module_14_83_render_colonies_viewport();
        FUN_7f61_012a_module_14_5c_flip_viewport_backscreen();
        FUN_7f88_058e_module_14_render_units_in_viewport();
        if (DAT_017a_zoom_level == 3) {
            // draw continent name
            //local_52[0] = 0;
            //FUN_1d01_07ea_strcpy_near(local_52,*(int *)0x534e * 0x34 + 0x53de);
            //FUN_104b_0318_draw_tiny_string_centered(local_52);
        }

        FUN_7f05_048a_module_14_draw_minimap_panel(flushToScreen,power);
        if (flushToScreen) {
            FUN_7f61_0214_module_14_flip_viewport_rectangle();
        }
    }


}
