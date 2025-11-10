package com.marf.colonization.decompile.cmodules;

public class Code1a {

    /**
     * AL: Width in bytes top copy from source to destination
     * AH: Height (number of rows to copy)
     * BX: width of source image
     * DX: width of destionation image
     * DS:SI: Source pointer (already placed at the correct source x,y)
     * ES:DI: Destination pointer (already placed at the correct destination x,y)
     */
    public static void FUN_1a3c_051e_copy_rectangle(Object source, Object destination, int sourceWidth, int destinationWidth, int width, int height) {

    }


    public static int FUN_1ae6_0004_load_stuff_from_file(int filehandle, int size, Object buffer) {
        // return number of bytes read
        return 0;

    }
}
