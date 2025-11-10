package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: ??
 * Segment: 0x4af1
 * Status:
 * - all functions decompiled
 *
 */
public class Module02 {

    public static void FUN_4af1_0688_module_02_maybe_update_colony_per_round(int param_1) {
        // end: 4af1:1aeb
    }

    public static void FUN_4af1_1aec(int param_1,int param_2) {
        /*
        FUN_15d9_0036(param_1);
        FUN_15d9_26b6();
        FUN_15d9_392a();
        int uVar1 = FUN_15d9_0978(param_2,0);
        FUN_43d0_2f3e(uVar1);
         */
    }

    public static void FUN_4af1_1b1a(int param_1,int param_2) {
        /*
        undefined2 uVar1;

        FUN_15d9_0036(param_1);
        FUN_15d9_26b6();
        FUN_15d9_392a();
        int  uVar1 = FUN_15d9_0978(param_2,0);
        FUN_15d9_1036(uVar1);
        // AX,word ptr [BP + local_6]
        // local_6 is never filled, so it must be something still on the stack
        return 0;

         */
    }


    /** updates the number of colonists who defends the colony and the fortification level */
    public static void FUN_4af1_1b4c_update_defender(int colonyIndex, int power) {
        Colony colony = DAT_5cfe_colonies_list.get(colonyIndex);

        colony.field_x_0xba[power] = colony.num_colonists;
        colony.field_x_0xba[4] = (byte) Code22.FUN_15d9_03e0_get_building_level(colonyIndex, 0);
    }
}
