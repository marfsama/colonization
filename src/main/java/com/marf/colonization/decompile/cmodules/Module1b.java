package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code19.*;
import static com.marf.colonization.decompile.cmodules.Code1a.*;
import static com.marf.colonization.decompile.cmodules.Code1d.*;

public class Module1b {

    /**
     *    Opens a madspack file, returning the header without the magic
     *
     *    Input:
     *    - far_pointer filename
     *    - far_pointer buffer
     *    - BX = offset to access mode (ie. "rb")
     *
     *    Output:
     *    - AX = 0 = sucess, else failure
     *
     * The java version returns the header buffer.
     *
     * @param filename
     * @param accessmode
     *
     * @return the buffer structure
     */
    public static MadspackHeader FUN_8d90_0000_module_1b_open_madspack_archive(String filename, String accessmode) {
        String lowercaseFilename = filename.toUpperCase();
        MadspackHeader header = new MadspackHeader();

        int readOnly = accessmode.contains("r") ? 1 : 0;

        header.field_0x00 = 0;
        // something with "search for 'r' in access mode"
        header.field_0x04 = 0;
        header.field_0x08 = -1; // 0xffff

        int fileHandle = FUN_19da_0100_stdio_fopen(filename, accessmode);
        header.field_0x06_file_handle = fileHandle;
        if (fileHandle == 0) {
            // jump to label: exitFunction
            return null;
        }
        header.current_section = 0;
        header.field_0x02_read_only_flag = readOnly;

        if (readOnly == 1) {

            int startFilePositon = FUN_1d01_09a8_ftell_buffer(header.field_0x06_file_handle);

            // note: this reads the fields magicString, field_0x26 and sectionCount
            int bytesRead = FUN_1ae6_0004_load_stuff_from_file(header.field_0x06_file_handle, 0x10, header.magicString);
            if (bytesRead == 0) {
                // jump to label: exitFunction
                return null;
            }
            if (!new String(header.magicString).equals("MADSPACK 2.0")) {
                // jump to label: exitFunction
                return null;
            }
            // load section list
            int size = header.sectionCount * 10;
            bytesRead = FUN_1ae6_0004_load_stuff_from_file(header.field_0x06_file_handle, size, header.sections);
            if (bytesRead == 0) {
                // jump to label: exitFunction
                return null;
            }

            // seek to just after the section list
            FUN_1d01_0ac4_seek_start(header.field_0x06_file_handle, startFilePositon + 0xB0);

            header.total_uncompressed_size = 0;
            readOnly = 0;
            for (MadspackSection section : header.sections) {
                header.total_uncompressed_size += section.uncompressed_size;
            }
        } else {
            // not readonly
            // initialize madspack header
            // write header from offset 0x1a (magic string) to file
            // set total_uncompressed_size = 0
        }

        // label: exitFunction
        if (fileHandle != 0) {
            FUN_1d01_03fa_fclose(fileHandle);
        }

        return header;
    }


    /** closes the madspack and releases the memory. */
    public static void FUN_8d90_021c_close_madspack(MadspackHeader madspackHeader) {

    }

    public static void FUN_8dbb_0000_module_1b_read_entry_from_madspack(int size, MadspackHeader madspackHeader, Object destination, int unknown1, int unknown2) {
        // note: it seems that the sections are read sequentially and not in random order. So there is no need to
        // have a "sectionId" parameter.


        madspackHeader.current_section++;
    }





    public static class MadspackHeader {
        /** 0x00 - word */
        public int field_0x00;
        /** 0x02 - boolean/word */
        public int field_0x02_read_only_flag;
        /** 0x04 - byte */
        public int field_0x04;
        /** 0x04 - byte (unused) */
        public int field_0x05;
        /** 0x06 - word */
        public int field_0x06_file_handle;
        /** 0x08 - word */
        public int field_0x08;
        // [..]
        /** 0x14 - dword */
        public int total_uncompressed_size;
        /** 0x18 - word - current section at which the file is currently positioned */
        public int current_section;
        /** 0x1a - byte[12] */
        public byte[] magicString = new byte[12];
        /** 0x26 - word */
        public int field_0x26;
        /** 0x28 - word */
        public int sectionCount;
        /** 0x2a - 16 * 10 (0x10 * 0xA) = 0xA0 */
        public MadspackSection[] sections;
        // size: 0xCA (202 bytes)
    }

    public static class MadspackSection {
        /** 0x00 - word */
        int flags;
        /** 0x02 - word */
        int uncompressed_size;
        /** 0x06 - word */
        int compressed_size;
        // size: 0xA (10 bytes)
    }
}
