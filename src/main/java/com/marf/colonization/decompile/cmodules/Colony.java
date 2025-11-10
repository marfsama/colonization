package com.marf.colonization.decompile.cmodules;

public class Colony {
    /** 0x0 0x1 */
    public byte x;
    /** 0x1 0x1 */
    public byte y;
    /**0x2	0x18*/
    public String name;
    /** 0x1a 0x1 */
    public byte	nation;
    /** 0x1b 0x4 */
    public byte[] field_4_0x1b = new byte[4];
    /** 0x1f 0x1 */
    public byte	num_colonists;
    /** 0x20 0x20 */
    public byte[] colonist_occupation = new byte[32];
    /** 0x40 0x20 */
    public byte[] colonist_specialization = new byte[32];
    /** 0x60 0x10 */
    public byte[] colonist_time = new byte[16];
    /** 0x70 0x8 */
    public byte[] tile_usage = new byte[8];
    /** 0x78 0xc */
    public byte[] field_x_0x78 = new byte[12];
    /** 0x84 0x6 */
    public byte[] buildings = new byte[6];
    /** 0x8a 0x2 */
    public short customs_house;
    /** 0x8c 0x6 */
    public byte[] field_x_0x8c = new byte[6];
    /** 0x92 0x2 */
    public short hammers;
    /** 0x94 0x1 */
    public byte	current_production;
    /** 0x95 0x5 */
    public byte[] field_x_0x95 = new byte[5];
    /** 0x9a 0x10 */
    public byte[] storage = new byte[16];
    /** 0xaa 0x8 */
    public byte[] field_x_0xaa = new byte[8];
    /** 0xb2 0x4 */
    public int bells;
    /** 0xb6 0x4 */
    public int field_x_0xb6;
    /** 0xba 0x10 */
    public byte[] field_x_0xba = new byte[16];
}
