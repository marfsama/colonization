package com.marf.colonization.decompile.cmodules;

public class Code12 {
    public static int FUN_1261_00de_wait_for_keystroke() {
        return 0;
    }

    public static int FUN_124c_0000_clamp(int min_value,int value,int max_value) {
        if (value < min_value) {
            value = min_value;
        }
        if (max_value < value) {
            value = max_value;
        }
        return value;
    }
}
