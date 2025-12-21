package com.marf.colonization.rebuild;

import lombok.Data;

@Data
public class UnitTypeConfig {
    /**
     * 0x0 0x2
     * Note: in c this is the name index.
     */
    private String name;
    /** 0x2 0x2	*/
    private int icon;
    /** 0x4 0x1	*/
    private int movement;
    /** 0x5 0x1	*/
    private int attack;
    /** 0x6 0x1	*/
    private int combat;
    /** 0x7 0x1	*/
    private int cargo;
    /** 0x8 0x1	*/
    private int size;
    /** 0x9 0x1	*/
    private int cost;
    /** 0xa 0x1	*/
    private int tools;
    /** 0xb 0x1	*/
    private int guns;
    /** 0xc 0x1	*/
    private int hull;
    /** 0xd 0x1	*/
    private  int role;
}
