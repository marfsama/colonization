package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: ??
 * Segment: 0x4af1
 * Status:
 * - all functions decompiled
 *
 */
public class Module02 {

    public static boolean FUN_4af1_1b76_module_2_is_colony_visible(int playerIndex, int colonyIndex) {
        Colony colony = DAT_5cfe_colonies_list[colonyIndex];
        if (colony.nation == 0) {
            return true;
        }

        if (DAT_5338_savegame_header.field_0x22_maybe_current_turn != 0) {
            return true;
        }

        if (colony.colonists_seen_in_colony[playerIndex] > 0) {
            return true;
        }

        return false;
    }

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
        Colony colony = DAT_5cfe_colonies_list[colonyIndex];

        colony.colonists_seen_in_colony[power] = colony.num_colonists;
        colony.seen_fortification_level[4] = (byte) FUN_15d9_03e0_get_building_level(colonyIndex, 0);
    }
}
