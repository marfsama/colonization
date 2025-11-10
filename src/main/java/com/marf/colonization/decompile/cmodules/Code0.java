package com.marf.colonization.decompile.cmodules;


import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: Text management
 * Status:
 * - all relevant functions decompiled. the remaining is low level memory management
 *
 */

public class Code0 {
    public static void FUN_1000_002c_add_string_to_table(String string) {
        DAT_2d00_string_table.add(string);
        DAT_2d12_string_count++;
    }

    /**
     * This function navigates through an array of null-terminated strings to find the string at a
     * specified index.
     */
    public static String FUN_1000_0062_get_string_from_table(int string_index) {
        return DAT_2d00_string_table.get(string_index);
    }
}
