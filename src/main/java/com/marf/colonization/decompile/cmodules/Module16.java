package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code0.FUN_1000_0062_get_string_from_table;
import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: Text with placeholders, Text drawing
 * Status:
 * - ??
 *
 */
public class Module16 {

    /**
     * stores an int value as text placeholder value.
     */
    public static void FUN_83d9_042c_module_16_store_int_parameter(int value, int slot) {
        DAT_9c60_int_placeholder[slot] = value;
    }


    /**
     * copies the string to a slot in the string parameter list.
     * These strings will be used for placeholders %STRING0 - %STRING4 (5 slots)
     * Each parameter can be 0x40 (64) bytes inclusive null terminator.
     */
    public static void FUN_83d9_03d0_copy_string_to_string_parameter(int slot, String string) {
        DAT_9c82_string_placeholder_array[slot] = string;
    }

    public static void FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(int slot, int string_index) {
        String string = FUN_1000_0062_get_string_from_table(string_index);
        FUN_83d9_03d0_copy_string_to_string_parameter(slot, string);
    }


    /**
     * Draws some text. Maybe: display message box centered on screen
     * <p>
     * Params:
     * BX = offset to Text File (in DS)
     * AX = offset to section name (in DS)
     * DX = unkwnon
     * Return:
     * AX = next X Pos after the text
     */
    public static void FUN_83d9_36b6_module_16_draw_text_in_current_font(String textFile, String section, int dxUnknown) {
        // todo
    }

    public static void FUN_83d9_3730_module_16_draw_text_from_gametxt(String section) {
        FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", section, 0);
    }
}
