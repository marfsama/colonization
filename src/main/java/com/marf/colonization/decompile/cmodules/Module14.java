package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code11.*;
import static com.marf.colonization.decompile.cmodules.Code12.*;
import static com.marf.colonization.decompile.cmodules.Code13.*;
import static com.marf.colonization.decompile.cmodules.Code1a.FUN_1a32_000e_get_pixel_address;
import static com.marf.colonization.decompile.cmodules.Code1c.*;
import static com.marf.colonization.decompile.cmodules.Data.*;

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



    }
}
