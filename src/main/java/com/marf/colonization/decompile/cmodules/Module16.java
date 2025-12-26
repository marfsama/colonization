package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code0.FUN_1000_0062_get_string_from_table;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module17.FUN_8778_001a_module_17_text_file_prepare_read_section;
import static com.marf.colonization.decompile.cmodules.Module1e_21.FUN_8f08_0312_module_1e_21_free_memory;

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

    public static int FUN_83d9_06d0(Font DAT_1f6c_current_font_address, int DAT_1f70_current_font_data_size) {
        return 0;
    }

    public static int FUN_83d9_2580(int somePointer) {
        return 0;
    }

    /**
     * Draws some text. Maybe: display message box centered on screen
     * <p>
     * Params:
     * BX = offset to Text File (in DS)
     * AX = offset to section name (in DS)
     * DX = unknown, maybe Dialog type
     * Return:
     * AX = result code
     */
    public static int FUN_83d9_32a4_show_dialog_box_impl(String textFile, String section, int maybeDialogType) {
        int local_6 = 1;
        int local_164 = 1;
        int local_12 = 0;
        int local_e_image_maybe = 0;
        if (FUN_8778_001a_module_17_text_file_prepare_read_section(textFile, section) != 0) {
            DAT_1f2c_show_text_box_boolean_flag = -1;
            DAT_1f2a = -1;
            DAT_1f2e = -1;
            DAT_1f34 = 0;
            return -1;
        }

        int foo = FUN_83d9_06d0(DAT_1f6c_current_font_address, DAT_1f70_current_font_data_size);

        // TODO
        // this function shows a dialog box.
        // - parses text configuration options like with, checkbox/options
        // - parses text placeholder
        // - shows the dialog box





        return 0;
    }


    /**
     * Draws some text. Maybe: display message box centered on screen
     * <p>
     * Params:
     * BX = offset to Text File (in DS)
     * AX = offset to section name (in DS)
     * DX = unknown
     * Return:
     * AX = result code
     */
    public static int FUN_83d9_36b6_module_16_draw_text_in_current_font(String textFile, String section, int dxUnknown) {
        int result = 0;
        int somePointer = FUN_83d9_32a4_show_dialog_box_impl(textFile, section, dxUnknown);
        if (somePointer != 0) {
            result = FUN_83d9_2580(somePointer);
            FUN_8f08_0312_module_1e_21_free_memory(somePointer);
        }
        return result;
    }

    /**
     * BX - section name inn GAME.TXT
     */
    public static void FUN_83d9_3730_module_16_draw_text_from_gametxt(String section) {
        FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", section, 0);
    }

    /**
     * DX = some global flag
     * AX = inDX for {@link #FUN_83d9_36b6_module_16_draw_text_in_current_font}
     * BX = section name
     * */
    public static void FUN_83d9_3764_show_message_box_maybe(String sectionName, int someGlobalFlag, int inDX) {
        DAT_1f2a = inDX;
        FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", sectionName, someGlobalFlag);
    }

    public static void FUN_83d9_3776_show_message_box_maybe(String section) {
        DAT_1f2a = 8;
        FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", section, 0);
    }

    public static void FUN_83d9_378e_show_text_box(String gameTxtSection, int b)  {
        DAT_1f2c_show_text_box_boolean_flag = b;
        FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", gameTxtSection, 0);
    }
}
