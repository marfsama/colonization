package com.marf.colonization.rebuild;

public class Util {
    /** FUN_124c_0000_clamp */
    public static int clamp(int min_value,int value,int max_value) {
        if (value < min_value) {
            value = min_value;
        }
        if (max_value < value) {
            value = max_value;
        }
        return value;
    }

}
