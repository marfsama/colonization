package com.marf.colonization.decompile.cmodules;

public class Code19 {
    public static int FUN_19da_0100_stdio_fopen(String filename, String access_mode) {
        // byte *in_BX;
        // byte local_52 [80];

        // FUN_19da_00b6_maybe_resolve_wildcards(local_52);
        // FUN_1d01_04e0_stdio_fopen(local_52,in_BX);
        return 0;
    }

    /** Returns true when the founding father is present. */
    public static boolean FUN_196c_0004_check_founding_fathers(int power, int founding_father_index) {
        if (founding_father_index < 0) {
            return true;
        }
        if (power > 3) {
            return false;
        }

        int byteIndex = founding_father_index >> 3; // divide by 8 (bits)
        int bitPosition = founding_father_index & 7;
        int mask = 1 << bitPosition;

        byte b = Data.DAT_87e2_europe[power].foundingFathersBitset[byteIndex];

        return (b & mask) != 0;
    }
}
