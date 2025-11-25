package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code11.*;
import static com.marf.colonization.decompile.cmodules.Code12.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code1a.*;
import static com.marf.colonization.decompile.cmodules.Code1b.*;
import static com.marf.colonization.decompile.cmodules.Code1c.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Module14 {
    public static int FUN_7f05_0000_module_14_get_center_pixel_of_compressed_sprite(int spriteIndex) {
        Sprite tempSprite = new Sprite();
        tempSprite.width = 0x10;
        tempSprite.height = 0x10;

        FUN_1c1b_0000_draw_compressed_sprite(tempSprite, 0, 0, spriteIndex, DAT_016a_phys0_sprite_sheet);
        return 0; // return center pixel of temp sprite
    }

    public static int FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(int terrain_type) {
        Sprite tempSprite = new Sprite();

        tempSprite.width = 0x10;
        tempSprite.height = 0x10;
        FUN_1101_0050_blit_terrain_tile(DAT_0162_terrain_sprites, terrain_type, tempSprite, 0, 0);
        return 0; // return center pixel of temp sprite
    }

    public static void FUN_7f05_0068_module_14_precalculate_minimap_terrain_pixels() {
        for (int terrainType = 0; terrainType < 8; terrainType++) {
            DAT_a526_terrain_minimap_pixel[terrainType] = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(terrainType);
            // note: both below are unused
            DAT_a52e_terrain_minimap_pixel[terrainType] = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(terrainType);
            DAT_a536_terrain_minimap_pixel[terrainType] = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(terrainType);
        }
        // note: all these are unused
        DAT_a53e_terrain_minimap_pixel_arctic = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(0x18);
        DAT_a53f_terrain_minimap_pixel_sea = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(0x19);
        DAT_a540_terrain_minimap_pixel_sealane = FUN_7f05_0034_module_14_get_center_pixel_of_terrain_sprite(0x1a);
        DAT_a541_terrain_minimap_pixel_0x21 = FUN_7f05_0000_module_14_get_center_pixel_of_compressed_sprite(0x21);
        DAT_a542_terrain_minimap_pixel_0x31 = FUN_7f05_0000_module_14_get_center_pixel_of_compressed_sprite(0x31);
    }

    public static void FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds() {
        // Calculate initial centered position with offsets
        int initialY = DAT_0174_viewport_center_y - 0x13;  // 19 pixels up from center
        int initialX = DAT_0172_viewport_center_x - 0x1C;  // 28 pixels left from center

        // Store initial values
        DAT_9c7a_minimap_min_y = initialY;
        DAT_9c7c_minimap_min_x = initialX;

        // Clamp X coordinate to stay within map bounds
        // Min X = 1, Max X = map_width - 57
        int maxX = DAT_84e6_map_width - 0x39;
        DAT_9c7c_minimap_min_x = FUN_124c_0000_clamp(1, initialX, maxX);

        // Clamp Y coordinate to stay within map bounds
        // Min Y = 1, Max Y = map_height - 40
        int maxY = DAT_84e8_map_height - 0x28;
        DAT_9c7a_minimap_min_y = FUN_124c_0000_clamp(1, initialY, maxY);
    }


    public static void FUN_7f05_0118_module_14_draw_minimap(int x, int y, int width, int height, int player, int param_6) {
        int playerMask = 0;
        if (player != 0) {
            playerMask = 0x10 << player;
        }

        int terrainOffset = FUN_1373_00fa_get_terrain_type_offset_at(x,y);
        int surfaceOffset = FUN_1373_012e_get_surface_offset_at(x,y);
        int visibilityOffset = FUN_1373_02e4_visibility_get_offset_at(x,y);
        int visitorOffset = FUN_1373_0198_get_visitor_offset_at(x,y);

        int pixelAddress = FUN_1a32_000e_get_pixel_address(DAT_2638_backscreen, x + 252, y + 9);

        // TODO...complete the function

    }

    public static void FUN_7f05_0346_module_14_draw_minimap(int power) {
        FUN_7f05_0118_module_14_draw_minimap(DAT_9c7c_minimap_min_x, DAT_9c7a_minimap_min_y, 70, 39, power, 0);
    }

    public static void FUN_7f05_0360_module_14_draw_minimap(int param_1_x, int param_2_y, int param_width, int param_height, boolean param_5_flush_to_screen, int power, int some_flag) {
        FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds();

        int min_y = DAT_9c7a_minimap_min_y;
        int min_x = DAT_9c7c_minimap_min_x;

        int y = max(min_y, param_2_y);
        int height = max(0, min(min_y + 0x26, param_height - 1) - y);

        // note: x and height needs to be chacked against assembly (just copied the part from y)
        int x = max(min_x, param_1_x);
        int width = max(0, min(min_x + 0x37, param_width - 1) - x);

        if (width == 0 || height == 0) {
            return;
        }

        // draw the minimap
        FUN_7f05_0118_module_14_draw_minimap(x,y,width, height, power, some_flag);

        // DAT_2638_backscreen
        // max(min_y + 0x26, game_window_y_max) - min_y + 9
        // 15
        // AX = max(viewport_min_x,  min_x) - min_x + 252
        // BX = max(min_x + 0x37, game_window_x_max) - min_x + 252
        // DX = max(viewport_min_y, min_y) - min_y + 9
        // draws the current viewport as a rectangle in the minimap
        FUN_1bae_0008_draw_rectangle(DAT_2638_backscreen, 15,
                /* AX */ max(DAT_82e2_viewport_x_min,  min_x) - min_x + 252,
                /* param_1 */ max(min_y + 0x26, DAT_87ac_viewport_y_max) - min_y + 9,
                /* BX */ max(min_x + 0x37, DAT_87aa_viewport_x_max) - min_x + 252,
                /* DX */ max(DAT_82e6_viewport_y_min, min_y) - min_y + 9
                );

        if (param_5_flush_to_screen) {
            FUN_1b54_0040_flip_backscreen_rectangle(x - min_x + 252, y - min_y + 9, x - min_x + 252, y - min_y + 9, width, height);
        }

    }

    /**
     * draws the minimap (wood) panel, the border around the minimap, the minimap itself and the current viewport as a
     * rectangle over the minimap.
     * */
    public static void FUN_7f05_048a_module_14_draw_minimap_panel(boolean param_1_flush_to_screen,int param_2_power) {
        FUN_7f05_00d8_module_14_maybe_calculate_minimap_bounds();
        if (DAT_081c_address_of_woodtile_sprite_maybe != 0) {
            FUN_1bd9_0006_draw_sprite_sheet_entry(DAT_2638_backscreen, DAT_081c_address_of_woodtile_sprite_maybe, 241, 8, 79, 41, 0, 0);
        } else {
            // param_1_color
            // param_2_height
            // param_3_destination
            // bx - width
            // dx - y
            // ax - x
            FUN_1b83_0000_fill_rectangle(241, 8, 79, 41, DAT_2638_backscreen, 0);
        }

        // draw orange border around minimap
        FUN_1bae_0008_draw_rectangle (DAT_2638_backscreen, 6, 251,8,308,48);

        // draw minimap
        FUN_7f05_0346_module_14_draw_minimap(param_2_power);

        // draw current viewport
        int y1 = max(DAT_82e6_viewport_y_min, DAT_9c7a_minimap_min_y) + 9;
        int y2 = min(DAT_87ac_viewport_y_max, DAT_9c7a_minimap_min_y + 0x26) - DAT_9c7a_minimap_min_y + 9;
        int x1 = max(DAT_82e2_viewport_x_min, DAT_9c7c_minimap_min_x) + 0xfc;
        int x2 = min(DAT_9c7c_minimap_min_x + 0x37, DAT_87aa_viewport_x_max) + 0xfc;
        FUN_1bae_0008_draw_rectangle(DAT_2638_backscreen, 15, x1, y1, x2, y2);

        // flip minimap part of the screen when requested
        if (param_1_flush_to_screen) {
            FUN_1b54_0040_flip_backscreen_rectangle(241, 8, 241, 8, 79, 41);
        }
    }

    /** Note: the parameters are all pointers (word*), so they are all output parameters as well. */
    public static void FUN_7f61_0004_module_14_5c_clamp_to_viewport(int x1, int y1, int x2, int y2) {
        x1 = max(x1, DAT_82e2_viewport_x_min);
        y1 = max(y1, DAT_82e6_viewport_y_min);
        x2 = min(x2, DAT_87aa_viewport_x_max);
        y2 = min(y2, DAT_87ac_viewport_y_max);
        // note: return x1, y1, x2, y2
    }

    public static void FUN_7f61_004a_module_14_5c_clamp_to_viewport(int x, int y, int width, int height) {
        // Calculate right and bottom edges
        int rightEdge = x + width - 1;
        int bottomEdge = y + height - 1;

        // Clip X-axis
        // Clamp right edge to game window maximum X
        int clampedRight = Math.min(rightEdge, DAT_87aa_viewport_x_max);

        // Clamp left edge to viewport minimum X
        int clampedLeft = Math.max(x, DAT_82e2_viewport_x_min);
        x = clampedLeft;

        // Recalculate width
        int newWidth = Math.max(clampedRight - clampedLeft + 1, 0);
        width = newWidth;

        // Clip Y-axis
        // Clamp bottom edge to game window maximum Y
        int clampedBottom = Math.min(bottomEdge, DAT_87ac_viewport_y_max);

        // Clamp top edge to viewport minimum Y
        int clampedTop = Math.max(y, DAT_82e6_viewport_y_min);
        y = clampedTop;

        // Recalculate height
        int newHeight = Math.max(clampedBottom - clampedTop + 1, 0);
        height = newHeight;

        // return x,y,width,height
    }





}
