package com.marf.colonization.decompile.cmodules;

public class Unit {
    /** 0x00 */
    public int x;
    /** 0x01 */
    public int y;
    /** 0x02 */
    public int type;
    /** 0x03 */
    public int nationIndex;
    /** 0x04 (bit 7 = 1 => damaged artillery) */
    public int flags_damaged;
    /** 0x05 */
    public int usedMoves;
    /** 0x06 */
    public byte field_0x6;
    /** 0x06 */
    public byte field_0x7;
    /** 0x08 */
    public int order;
    /** 0x09 */
    public int gotoX;
    /** 0x0a */
    public int gotoY;
    /** 0x0b */
    public byte[] dummy3;
    /** 0x0c */
    public int numCargo;
    /** 0x0d */
    public byte[] cargoTypes = new byte[3];
    /** 0x10 */
    public byte[] cargoAmount = new byte[6];
    /** 0x16 */
    public byte attack_penalty_maybe;
    /** 0x17 */
    public int profession;
    /** 0x18 - Unit id. maybe fist unit in transport? */
    public int transportChain1;
    /** 0x1a - Unit id. this. might be the next unit in a stack of units (on the map or in colony) */
    public int transportChain2;
    // total size: 0x1c
}
