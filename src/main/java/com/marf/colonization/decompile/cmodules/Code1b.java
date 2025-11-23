package com.marf.colonization.decompile.cmodules;

/**
 * Base Description: Timer stuff
 * Segment: 0x1bf0
 * Status:
 * - ??
 */
public class Code1b {
    /**
     * fills the rectangle from 0,0 - width,height
     */
    public static void FUN_1b83_0000_clear_image(Sprite destination, int color) {

    }

    /**
     * params:
     * - param_1_color
     * - param_2_y2
     * - param_3_destination
     * - AX - x1
     * - DX - y1
     * - BX - x2
     */
    public static void FUN_1bae_0008_draw_rectangle(Sprite destination, int color, int x1, int y1, int x2, int y2) {
        // todo
    }

    /**
     * BX = x
     * DX = y
     * AX = x
     * height
     * width
     * y
     */
    public static void FUN_1b54_0040_flip_backscreen_rectangle(int src_x, int src_y, int dest_x, int dest_y, int width, int height) {
        /*
        FUN_1a3c_02d4_init_something();
        iVar1 = FUN_1a3c_05c4();
        FUN_1cf5_0022_draw_sprite_to_vga_memory_0xA000(Data.DAT_2638_backscreen, src_x, src_y, dest_x, dest_y, width, height);
        if (iVar1 != 0) {
            FUN_1a3c_0703(var1);
        }
        FUN_1a3c_02e6();

         */
    }

    /**
     * param_1_color
     * param_2_height
     * param_3_destination
     * bx - width
     * dx - y
     * ax - x
     */
    public static void FUN_1b83_0000_fill_rectangle(int x, int y, int width, int height, Sprite destination, int color) {

    }

    /**
     * Draws a sprite (as in "sprite sheet"). This seems to support tile repeating to fill the drawing area.
     */
    public static void FUN_1bd9_0006_draw_sprite_sheet_entry(Sprite destination, int source, int x, int y, int width, int height, int maybe_tile_index, int maybe_flags) {

    }


    public static int FUN_1bf0_000c_get_clock_ticks() {
        return 0;
    }


}
