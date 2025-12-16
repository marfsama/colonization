package com.marf.colonization.decompile.cmodules;

public class SavegameHeader {
    public int field0_0x0;
    /**
     * Bit 0 - set when the independence is declared
     * <p>
     * 0x02	0x1	byte
     */
    public int field1_0x2_independence_flag;
    /** 0x10	0x2	byte	*/
    public int field9_0x10;
    /** 0x12	0x2	byte	*/
    public int active_unit;
    /** 0x14	0x2	short	*/
    public int viewport_power;
    /** 0x16	0x2	short	*/
    public int maybe_player_controlled_power;
    /** 0x18	0x2	short	*/
    public int maybe_current_player;
    /** 0x1a	0x2	short	*/
    public int num_indians;
    /** 0x1c	0x2	short */
    public int num_units;
    /** 0x1e	0x2	short */
    public int num_colonies;
    /** 0x22	0x2	short */
    public int field_0x22_maybe_current_turn;
    // [..]
    /** 0x50	0x2	short */
    public int tories_nation_maybe;
    /** 0x52	0x2	short */
    public int rebels_nation_maybe;
    /** 0x54	0x2	short */
    public int[] field_0x54 = new int[20];
    // [..]
}
