package com.marf.colonization.decompile.cmodules;


public class Europe {
    /** 0x00 0x1 */
    public int flags;
    /** 0x01 0x1 */
    public int taxRate;
    /** 0x02 0x3  */
    public int[] nextRecruits = new int[3];
    /** 0x05 0x2  */
    public int padding2;
    /** 0x07 0x4  */
    public byte[] foundingFathersBitset = new byte[4];
    /** 0x0b 0x1  */
    public byte padding3;
    /** 0x0c 0x2  */
    public int currentBells;
    /** 0x0e 0x4  */
    public byte[] padding4;
    /** 0x12 0x2  */
    public int currentFoundingFather;
    /** 0x14 0xa  */
    public byte[] padding5;
    /** 0x1e 0x1  */
    public int boughtArtillery;
    /** 0x1f 0xb  */
    public byte[] padding6;
    /** 0x2a 0x4  */
    public int gold;
    /** 0x2e 0x2  */
    public int currentCrosses;
    /** 0x30 0x2  */
    public int neededCrosses;
    /** 0x32 0x2  */
    public short padding8;
    /** 0x34 0x10  */
    public int[] treaty = new int[0x10];
    /** 0x44 0x8  */
    public byte padding9;
    /** 0x4c 0x10  */
    public int[] goodsPrice;
    /** 0x5c 0x20  */
    public int[] goodsUnknown;
    /** 0x7c 0x40  */
    public int[] goodsBalance;
    /** 0xbc 0x40  */
    public int[] goodsDemand;
    /** 0xfc 0x40  */
    public int[] goodsDemand2;
    // total size: 0x13c
}
