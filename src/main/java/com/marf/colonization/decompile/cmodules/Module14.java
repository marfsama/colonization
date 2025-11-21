package com.marf.colonization.decompile.cmodules;

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

        local_a.width = 0x10;
        local_a.height = 0x10;
        local_a.address_offset = (short)local_10a;
        FUN_1101_0050_blit_terrain_tile
                (*(undefined2 *)0x162,*(undefined2 *)0x164,param_1_terrain_type,&local_a,0,0);
        return local_82;
}
