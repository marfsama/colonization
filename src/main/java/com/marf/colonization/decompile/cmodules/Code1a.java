package com.marf.colonization.decompile.cmodules;

public class Code1a {

    public static int FUN_1a3c_0391_get_mouse_status() {
        return 0;
    }

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

    /** checks if a keystroke is available, returns true (!= 0)/false (1) */
    public static boolean FUN_1acb_0008_check_for_keystroke_available()  {
        return true;
    }

    public static int FUN_1ae6_0004_load_stuff_from_file(int filehandle, int size, Object buffer) {
        // return number of bytes read
        return 0;
    }

    public static int FUN_1a32_000e_get_pixel_address(Sprite sprite, int x, int y) {
        // return sprite.address + y * sprite.height + x;
        return 0;
    }
}
