package com.marf.colonization.decompile.cmodules;

public class SavegameHeader {
    /** 0x02	0x1	byte	*/
    public int field1_0x2;
    // [..]
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
    public int field27_0x50;
    // [..]
}
