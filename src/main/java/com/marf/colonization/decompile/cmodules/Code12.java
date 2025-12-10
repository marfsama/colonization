package com.marf.colonization.decompile.cmodules;

public class Code12 {
    public static int FUN_124c_0000_clamp(int min_value,int value,int max_value) {
        if (value < min_value) {
            value = min_value;
        }
        if (max_value < value) {
            value = max_value;
        }
        return value;
    }

    public static int FUN_1261_00de_wait_for_keystroke() {
        return 0;
    }

    public static int FUN_1261_0064_maybe_wait_for_keystroke_or_click() {
        // TODO
        return 0;
    }

    public static void FUN_12e8_0092_copy_sprite_to_sprite
            (Sprite param_1_dest,Sprite param_2_src,int param_3_src_x,int param_4_src_y,
             int param_5_dest_x,int param_6_dest_y,int param_7_width,int param_8_height)
    {

    }
}
