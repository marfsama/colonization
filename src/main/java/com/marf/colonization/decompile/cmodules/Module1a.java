package com.marf.colonization.decompile.cmodules;

import java.awt.image.BufferedImage;

import static com.marf.colonization.decompile.cmodules.Code1d.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module1b.*;
import static com.marf.colonization.decompile.cmodules.Module1c.*;

/**
 * Base Description: ??
 * Segment: 0x8d06
 * Status:
 * -
 *
 */
public class Module1a {
    public SpriteSheetSomeStructure FUN_8d06_0054_module1a_load_sprite_sheet(String spriteSheetName, int inAX) {

        DAT_238c = null; // DAT_a5d6_sprite_sheet_next_free;
        DAT_a5de = DAT_2360_available_sprite_sheet_memory;


        SpriteSheetSomeStructure spriteSheetAddress = FUN_8d1c_000a_module_1a_16_load_ss_sprite_sheet(spriteSheetName, inAX);

        if (spriteSheetAddress != null) {
            if (DAT_a5da_sprite_sheet_size < DAT_2360_available_sprite_sheet_memory) {
                DAT_2364_sprite_sheets_loaded_counter++;
                DAT_a5d6_sprite_sheet_next_free += DAT_a5da_sprite_sheet_size; // note: this only adds the lower word
                DAT_2360_available_sprite_sheet_memory -= DAT_a5da_sprite_sheet_size;
            }
        }
        DAT_238c = null;
        // return far pointer to sprite sheet data
        return spriteSheetAddress;
    }



    // Module 1a, Segment 0016
    ///////////////////////////////

    public static SpriteSheetSomeStructure FUN_8d1c_000a_module_1a_16_load_ss_sprite_sheet(String spritesheetName, int inAX) {
        // bp 1BFD:034d        2a10:034d
        // bp 9D5E:0211
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

        // read sprite sheet header
        SpriteSheetHeader spriteSheetHeader = new SpriteSheetHeader();
        FUN_8dbb_0000_module_1b_read_entry_from_madspack(0x98, madspackHeader, spriteSheetHeader, 1, 0);

        int local_4_sprite_entry_size = spriteSheetHeader.number_sprites * 16;

        // DX:AX = number of sprites * 12 + 0x42
        int local_11a_pointer_sprite_count = spriteSheetHeader.number_sprites * 12 + 0x42;
        int local_6a_pointer_sprite_count = spriteSheetHeader.number_sprites * 12 + 0x42;

        int local_20e_data_size = spriteSheetHeader.number_sprites * 12 + 0x42;

        // 8d1c:013d TEST byte ptr [BP + local_212],0x2 ; BP-0x210 = saved AX
        // The check is AX from the caller, which might or might not be compressed.
        if (/*isCompressed*/ false) {
            local_20e_data_size = spriteSheetHeader.data_size;
        }

        SpriteSheetSomeStructure local_62_some_structure_ptr = null;

        if (DAT_238c != null) {
            if (DAT_a5de > local_20e_data_size) {
                local_62_some_structure_ptr = new SpriteSheetSomeStructure();// DAT_238c;
            }
        }

        DAT_a5da_sprite_sheet_size = local_20e_data_size;

        if (local_62_some_structure_ptr == null) {
            // allocate memory
            // FUN_8f08_02e4_module_1e_21_get_sys_memory_block(local_20e_data_size, cleanedFileName, )
            local_62_some_structure_ptr = new SpriteSheetSomeStructure();
            // when the allocation fails
            if (local_62_some_structure_ptr == null) {
                DAT_2386_load_sprite_sheet_last_error = -4;
                // jump to LAB_8d1c_0441_close_madspack
                return null;
            }
        }

        // allocate memory for actual sprite sheet data
        // local_66_sprite_sheet_destination = new byte[local_4_sprite_entry_size];
        // this seems to be an array of elements with size 0x10
        Sprite[] local_66_sprite_sheet_destination = new Sprite[spriteSheetHeader.number_sprites];
        // return -4 when the allocation failed:
        // * DAT_2386_load_sprite_sheet_last_error = -4;
        // * jump to LAB_8d1c_0441_close_madspack

        // initialize somestructure to zero
        for (int index = 0; index < local_62_some_structure_ptr.field_0x2e.length; index++) {
            local_62_some_structure_ptr.field_0x2e[index] = 0;
        }

        FUN_8dbb_0000_module_1b_read_entry_from_madspack(local_4_sprite_entry_size, madspackHeader, local_66_sprite_sheet_destination, 1, 0);
        // return error -2 on failure

        if (spriteSheetHeader.pflag == 0) {
            // show error and I think stop programm
            // AX = -7
            // DX = 0x2
            // BX = 0xD
            FUN_8e33_03d6_module_1c_show_game_error(0,0,0,0);
        }

        //
        int paletteSize = madspackHeader.sections[madspackHeader.current_section].uncompressed_size;

        int local_11e;
        if (DAT_238a != DAT_2388_palette_destination) {
            local_11e = 0;
            FUN_8dbb_0000_module_1b_read_entry_from_madspack(0x300, madspackHeader, DAT_2388_palette_destination, 1, 0);
            // fail when not successfull
        } else {
            // skip section and increase the current section in the madspack header
            int filePosition = FUN_1d01_09a8_ftell_buffer(madspackHeader.field_0x06_file_handle);
            FUN_1d01_0a44_stdio_fseek(madspackHeader.field_0x06_file_handle, filePosition);
            madspackHeader.current_section++;
        }

        local_62_some_structure_ptr.mode = spriteSheetHeader.mode;
        if (spriteSheetHeader.type1 != 0 && spriteSheetHeader.type2 < 4) {
            local_62_some_structure_ptr.field_0x00 = 1;
        } else {
            local_62_some_structure_ptr.field_0x00 = 0;
        }

        local_62_some_structure_ptr.type2 = spriteSheetHeader.type2;
        local_62_some_structure_ptr.num_sprites = spriteSheetHeader.number_sprites;
        local_62_some_structure_ptr.field_0x28 = spriteSheetHeader.field_0x90;
        local_62_some_structure_ptr.field_0x2a = spriteSheetHeader.field_0x92;

        // copy a list from sprite sheet header to some_structure
        for (int i = 0; i < 0x10; i++) {
            // I don't really know what the source is
            local_62_some_structure_ptr.field_0x08[i] = 0; // spriteSheetHeader.
        }

        // pointer to end of somestructure
        int local_6e_end_of_somestructure = local_11a_pointer_sprite_count + 0; // &local_62_some_structure_ptr;

        local_62_some_structure_ptr.field_0x42 = new SomeStructureItem[spriteSheetHeader.number_sprites];
        for (int i = 0; i < spriteSheetHeader.number_sprites; i++) {
            SomeStructureItem item = new SomeStructureItem();
            local_62_some_structure_ptr.field_0x42[i] = item;

            item.field_0x04_width_padded = local_66_sprite_sheet_destination[i].width_padded;
            item.field_0x06_height_padded = local_66_sprite_sheet_destination[i].height_padded;
            item.field_0x08_width = local_66_sprite_sheet_destination[i].width;
            item.field_0x0A_height = local_66_sprite_sheet_destination[i].height;

            if (inAX == 2 || spriteSheetHeader.mode == 0) {
                item.field_0x00 = 0;
                continue;
            }

            item.field_0x00 = local_6e_end_of_somestructure;
            local_6e_end_of_somestructure += local_66_sprite_sheet_destination[i].length;
        }

        SpriteSheetSomeStructure local_20a_some_structure_to_return = null;
        if (inAX != 2 && spriteSheetHeader.mode != 0) {
            FUN_8dbb_0000_module_1b_read_entry_from_madspack(spriteSheetHeader.data_size, madspackHeader, local_6e_end_of_somestructure, 1, 0);
        } else {
            local_20a_some_structure_to_return = local_62_some_structure_ptr;
        }

        // label LAB_8d1c_0441_close_madspack
        if (madspackHeader.field_0x00 != 0) {
            FUN_8d90_021c_close_madspack(madspackHeader);
        }
        // free memory
        local_66_sprite_sheet_destination = null;
        local_11e = 0; // somehow this is always 0

        if (local_62_some_structure_ptr != null && local_62_some_structure_ptr != DAT_238c) {
            if (local_20a_some_structure_to_return == null) {
                // release memory
                local_62_some_structure_ptr = null;
            }
        }
        return local_20a_some_structure_to_return;

    }

    public static class SpriteSheetSomeStructure {
        /** 0x00 0x2 dw	short */
        public int field_0x00;
        /** 0x02 0x2 dw	short */
        public int type2;
        /** 0x04 0x2 dw	short */
        public int num_sprites;
        // [..]
        /** 0x08 0x20 dw[0x10] short */
        public int[] field_0x08;
        /** 0x28 0x2 dw	short */
        public int field_0x28;
        /** 0x2a 0x2 dw	short */
        public int field_0x2a;
        /** 0x2c 0x1 db	byte - mode from sprite sheet header */
        public int mode;
        /** 0x2e - word[0x0A] */
        int[] field_0x2e = new int[0xa];
        /** 0x42 - structure of size 0xc (12 bytes)  */
        SomeStructureItem[] field_0x42 = new SomeStructureItem[0]; // for each sprite one entry
    }

    public static class SomeStructureItem {
        /** 0x00 0x4 ddw int - some pointer */
        public int field_0x00;
        /** 0x04 0x2 dw	short */
        public int field_0x04_width_padded;
        /** 0x06 0x2 dw	short */
        public int field_0x06_height_padded;
        /** 0x08 0x2 dw	short */
        public int field_0x08_width;
        /** 0x0A 0x2 dw	short */
        public int field_0x0A_height;
        // size: 0xC (12 bytes)
    }

    public static class SpriteSheetHeader {
        /**
         * 0x0	0x1	db	byte
         * 0 = data isn't compressed
         * 1 = data is compressed
         */
        public int mode;
        /** 0x1	0x1	db	byte */
        public int field_0x01;
        /** 0x2	0x2	dw	word */
        public int type1;
        /** 0x4	0x2	dw	word */
        public int type2;
        /** 0x6	0x6	db[6]	*/
        public byte[] field_0x06 = new byte[6];
        /** 0xc	0x1	db	byte */
        public int pflag;
        /** 0xd	0x19	db[25] */
        public byte[] field_0x0d = new byte[25];
        /** 0x26	0x2	dw	word	*/
        public int number_sprites;
        /** 0x28	0x6c	db[108]	*/
        public byte[] field_0x28 = new byte[104];
        /** 0x90	0x2	dw	word	*/
        public int field_0x90;
        /** 0x92	0x2	dw	word	*/
        public int field_0x92;
        /** 0x94	0x4	ddw	dword	*/
        public int data_size;
    }

    private static class Sprite {
        /** 0x00 - dword */
        int start_offset;
        /** 0x04 - dword */
        int length;
        /** 0x08 - word */
        int width_padded;
        /** 0x0a - word */
        int height_padded;
        /** 0x0c - word */
        int width;
        /** 0x0e - word */
        int height;
    }

}
