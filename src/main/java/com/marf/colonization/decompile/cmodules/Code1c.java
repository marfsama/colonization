package com.marf.colonization.decompile.cmodules;

public class Code1c {
    /** return the width of the string in the selected font. */
    public static int FUN_1c0e_000c_get_string_width_in_pixels(String s, Font font) {
        return 0;
    }

    /**
     * Params:
     * AL - col1
     * DL - col2
     * BL - col3
     * param1 - col4
     */
    public static void FUN_1c0d_0000_set_text_blit_colors(int col1, int col2, int col3, int col4) {

    }

    /**
     * - param_1 - spritesheet (far ptr)
     * - param_2 - y
     * - AX - x
     * - DX - sprite index
     * - BX - destination
     */
    public static void FUN_1c1b_0000_draw_compressed_sprite(Sprite destination, int x, int y, int spriteIndex, Module1a.SpriteSheetSomeStructure surfaceSpriteSheet) {


    }

    /**
     * Draws the Sprite at x,y, where the x coordinate is centered. spriteIndex < 0 means the sprite is mirrored along the vertical axis.
     * <p>
     * params
     * - param_1 - destination
     * - param_2 - zoom level percent
     * - param_3 - y
     * - param_4 - sprite sheet
     * - AX - sprite index
     * - DX - x
     * - BX - destination
     *
     */
    public static void FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(Sprite destination, int x, int y, int zoomLevelPercent, int spriteIndex, Module1a.SpriteSheetSomeStructure spriteSheet) {

    }


    public static class SpriteDimensions {
        public int x;
        public int y;
        public int width;
        public int height;
    }

    public static SpriteDimensions FUN_1c67_0008_calculate_center_offset(int zoomPercent,
                                                              Module1a.SpriteSheetSomeStructure spriteSheet,
                                                              int iconIndex,
                                                              int x,
                                                              int y) {
        // 1. Calculate pointer to sprite data
        // Each sprite entry is 12 bytes, with data starting at offset 0x36

        // 2. Read original dimensions from sprite data
        int originalWidth = spriteSheet.field_0x42[iconIndex].field_0x08_width;
        int originalHeight = spriteSheet.field_0x42[iconIndex].field_0x0A_height;

        // 3. Apply zoom with rounding: ((dimension * zoom%) + 50) / 100
        int zoomedWidth = ((originalWidth * zoomPercent) + 50) / 100;
        int zoomedHeight = ((originalHeight * zoomPercent) + 50) / 100;

        SpriteDimensions outParam = new SpriteDimensions();
        // 4. Store zoomed dimensions
        outParam.width = zoomedWidth;
        outParam.height = zoomedHeight;

        // 5. Calculate centering offsets
        outParam.x = x - (zoomedWidth / 2);
        outParam.y = y - zoomedHeight + 1;
        return outParam;
    }


    public static void FUN_1c6d_000c(Sprite destination, int x, int y, Module1a.SpriteSheetSomeStructure surfaceSpriteSheet) {

    }

    public static void FUN_1c8e_000a(Sprite destination, int x, int y, int zoomLevelPercent, Module1a.SpriteSheetSomeStructure surfaceSpriteSheet) {

    }

    /**
     * Draws the sprite to the vga memory at 0xA000
     * */
    public static void FUN_1cf5_0022_draw_sprite_to_vga_memory_0xA000 (Sprite pointer_to_sprite,int src_x,int src_y,int dest_x,int dest_y,int width, int height) {
    }


    /**
     * param_1 - color
     * param_2 - y
     * param_3 - sprite sheet
     * AX = icon index
     * BX = destination
     * DX = x
     */
    public static void FUN_1cbc_000a_draw_sprite_silhouette(Sprite destination, Module1a.SpriteSheetSomeStructure spriteSheet, int x, int y, int spriteIndex, int color) {

    }
}
