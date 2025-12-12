package com.marf.colonization.decompile.cmodules;

import com.marf.colonization.saves.section.IndianTribe;
import lombok.Data;

import java.util.List;

public class Tribe {
    /** 0x00 db */
    public int unk0;
    /** 0x01 db */
    public int unk1;
    /** 0x02 db
     * 0 - tents
     * more: incas
     */
    public int level;
    /** 0x03  db[4] */
    public int[] unk2 = new int[4];
    /** 0x07 db */
    public int armedBraves;
    /** 0x08 db */
    public int horseHerds;
    /** 0x09 db[5] */
    public int[] unk3 = new int[5];
    /** 0x0e dw[16] */
    public int[] stock = new int[16];
    /** 0x2e db[12] */
    public int[] unk4 = new int[12];
    /** 0x3a db[4] */
    public int[] meetings = new int[4];
    /** 0x3e db[8] */
    public byte[] unk5 = new byte[8];
    /** 0x46 dw[4] */
    public IndianTribe.Aggression[] aggressions = new IndianTribe.Aggression[4];
    // size: 0x4e

    @Data
    public static class Aggression {
        /** byte */
        public int aggr;
        /** byte */
        public int aggrHigh;
    }
}
