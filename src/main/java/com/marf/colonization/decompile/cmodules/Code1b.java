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
     * Draws a rectangular region sourceX,sourceY - width, height to the destination x,y
     */
    public static void FUN_1b8e_000c_draw_sprite(Sprite destination, Sprite source, int width, int height, int destinationX, int destinationY, int sourceX, int sourceY) {

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
     * BX = dest_x
     * DX = src_y
     * AX = src_x
     * height
     * width
     * dest_y
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
     * Tiles a sprite over a bigger area.
     * The first row and column is offset by offset_x and offset_y
     */
    public static void FUN_1bd9_0006_draw_sprite_tiled(Sprite destination, Sprite source, int x, int y, int width, int height, int offset_x, int offset_y) {
        if ((source.height == 0) || (source.width == 0)) {
            return;
        }
        int tileOffsetX = Math.abs(x - offset_x) % source.height;
        int tileOffsetY = Math.abs(y - offset_y) % source.width;
        int maxX = x + width;
        int maxY = y + height;

        for (int currentY = y; currentY < maxY; y += (currentY - tileOffsetY) + source.width) {
            for (int currentX = x; currentX < maxX; x += (currentX - tileOffsetX) + source.height) {
                // Calculate source position for this tile
                int sourceX = currentX - tileOffsetX;
                int sourceWidth = source.width - tileOffsetX;

                // Calculate how much of this tile we can draw
                int tileWidth = Math.min(sourceWidth, maxX - currentX);

                // Calculate destination Y position and tile height
                int sourceY = currentY - tileOffsetY;
                int sourceHeight = source.height - tileOffsetY;
                int tileHeight = Math.min(sourceHeight, maxY - currentY);

                FUN_1b8e_000c_draw_sprite(
                        destination,
                        source,
                        tileWidth,
                        tileHeight,
                        currentX,
                        currentY,
                        sourceX,
                        sourceY
                );
                tileOffsetX = 0;
            }
            tileOffsetY = 0;
        }

        return;
    }


    public static int FUN_1bf0_000c_get_clock_ticks() {
        return 0;
    }

    public static int FUN_1bf0_0028_get_mocked_timer_ticks() {
        return 0;
    }

    public static void FUN_1bf6_0002_blit_text_to_bitmap(Sprite destination, int fontAddress, String string, int color, int x, int y) {

    }
}
