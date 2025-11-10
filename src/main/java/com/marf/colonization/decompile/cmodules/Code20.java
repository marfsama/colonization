package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: Unit Stuff
 * Status:
 * - ??
 *
 */
public class Code20 {
    /** returns the power name text index of a unit (including indians) in multiple. */
    public static int FUN_15a1_01a0_get_power_name_textindex_multiple(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner-4].tribe_name_multiple;
        }
        if (DAT_5338_savegame_header.field1_0x2 != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2e1e; // rebels
            }
            if (DAT_5338_savegame_header.field27_0x50 == param_1_unit_owner){
                return DAT_2e20; // tories
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

    /** returns the power name text index of a unit (including indians) in singular */
    public static int FUN_15a1_01e8_get_power_name_textindex_singular(int param_1_unit_owner) {
        if (param_1_unit_owner > 3) {
            return DAT_8cb8_tribes_names[param_1_unit_owner-4].tribe_name_singular;
        }
        if (DAT_5338_savegame_header.field1_0x2 != 0) {
            if (DAT_5338_savegame_header.maybe_current_player == param_1_unit_owner) {
                return DAT_2dfc; // rebel
            }
            if (DAT_5338_savegame_header.field27_0x50 == param_1_unit_owner){
                return DAT_2dfe; // tory
            }
        }
        return DAT_8cb0_power_names[param_1_unit_owner];
    }

}
