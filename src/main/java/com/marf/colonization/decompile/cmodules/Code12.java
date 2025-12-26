package com.marf.colonization.decompile.cmodules;


import static com.marf.colonization.decompile.cmodules.Data.*;

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

    public static int FUN_124c_0034_distance(int dx, int dy) {
        // Take absolute values
        int abs_dx = (dx < 0) ? -dx : dx;
        int abs_dy = (dy < 0) ? -dy : dy;

        if (abs_dy < abs_dx) {
            // dy is the smaller component
            return abs_dy / 2 + abs_dx;
        } else {
            // dx is the smaller component (or equal)
            return abs_dx / 2 + abs_dy;
        }
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

    public static boolean FUN_1261_00fa_is_mouse_in_rect(int x1,int y1,int x2,int y2) {
        if (x1 < DAT_07d6_mouse_x && x2  > DAT_07d8_mouse_y  && y1 < DAT_07d8_mouse_y && y2 > DAT_07d8_mouse_y) {
            return true;
        }
        return false;
    }

}
