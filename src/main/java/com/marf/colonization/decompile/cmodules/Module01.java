package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code15.FUN_15d9_3904;
import static com.marf.colonization.decompile.cmodules.Code1b.FUN_1b54_0040_flip_backscreen_rectangle;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module1a_61.*;

public class Module01 {
    public static void FUN_43d0_05b6_module_01_read_colony_pik_file() {
        FUN_8d67_0004_module_1a_61_madspack_get_pik_file("COLONY", DAT_2640_2nd_backscreen);
    }

    public static void FUN_43d0_2c92(boolean param_1_flip_buffer) {
        FUN_15d9_392a();
        FUN_43d0_0332();
        FUN_43d0_0a74_module_01_draw_map_for_colony();
        FUN_43d0_0a3e_blit_bitmap(0,0,320,200);
        FUN_43d0_0fce(false);
        FUN_43d0_0ba8(false);
        FUN_43d0_17d0(false);
        FUN_43d0_28d4(false);
        FUN_43d0_2c3c(false,false);
        FUN_43d0_24b0(false);
        FUN_43d0_284a(false);
        FUN_43d0_171c(false);
        if (param_1_flip_buffer) {
            FUN_1b54_0040_flip_backscreen_rectangle(0, 0, 0, 0, 200, 0x140);
        }
        return;
    }

    public static void FUN_43d0_2d0e() {
        FUN_43d0_05b6_module_01_read_colony_pik_file();
        FUN_43d0_2c92(true);
    }



    public static void FUN_15d9_392a() {
        FUN_15d9_3904();
        FUN_15d9_3920();
    }

}
