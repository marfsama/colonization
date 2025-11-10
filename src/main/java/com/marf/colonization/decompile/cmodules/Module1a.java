package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module1b.*;

/**
 * Base Description: ??
 * Segment: 0x8d06
 * Status:
 * -
 *
 */
public class Module1a {
    public int FUN_8d06_0054_module1a_load_sprite_sheet(String spriteSheetName, int inAX) {

        DAT_238c = DAT_a5d6_sprite_sheet_next_free;
        DAT_a5de = DAT_2360_available_sprite_sheet_memory;


        int spriteSheetAddress = FUN_8d1c_000a_module_1a_16_load_ss_sprite_sheet(spriteSheetName, inAX);

        if (spriteSheetAddress != 0) {
            if (DAT_a5da_sprite_sheet_size < DAT_2360_available_sprite_sheet_memory) {
                DAT_2364_sprite_sheets_loaded_counter++;
                DAT_a5d6_sprite_sheet_next_free += DAT_a5da_sprite_sheet_size; // note: this only adds the lower word
                DAT_2360_available_sprite_sheet_memory -= DAT_a5da_sprite_sheet_size;

            }
        }
        DAT_238c = 0;
        // return far pointer to sprite sheet data
        return spriteSheetAddress;
    }



    // Module 1a, Segment 0016
    ///////////////////////////////

    public static int FUN_8d1c_000a_module_1a_16_load_ss_sprite_sheet(String spritesheetName, int inAX) {
        // Block 1: Initialization & Setup
        // Block 2: Filename Processing
        // add extension .SS when the requested filename does not contain an extension
        String cleanedFileName = spritesheetName;
        if (!cleanedFileName.contains(".")) {
            cleanedFileName = cleanedFileName + ".SS";
        }
        cleanedFileName = cleanedFileName.toUpperCase();

        // Block 4: Archive Header Processing
        Module1b.MadspackHeader madspackHeader = FUN_8d90_0000_module_1b_open_madspack_archive(cleanedFileName, "rb");



        // Block 5: Open MADSPACK Archive
        // Block 6: Read Archive Header
        // Block 7: Memory Allocation & Size Calculation
        // Block 8: Memory Management Decision
        // Block 9: Memory Allocation
        // Block 10: Read Sprite Sheet Data
        // Block 11: Sprite Sheet Header Processing
        // Block 12: Sprite Frame Processing Loop
        // Block 13: Cleanup & Error Handling
        // Block 14: Return Result
        return 0;

    }

}
