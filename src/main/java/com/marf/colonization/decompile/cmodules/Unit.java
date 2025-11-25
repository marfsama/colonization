package com.marf.colonization.decompile.cmodules;

public class Unit {
    public int x;
    public int y;
    public int type;
    public int nationIndex;
    public int dummy0;
    public int dummy1;
    public int usedMoves;
    public byte[] dummy2 = new byte[2];
    public int order;
    public int gotoX;
    public int gotoY;
    public byte[] dummy3;
    public int numCargo;
    public byte[] cargoTypes = new byte[3];
    public byte[] cargoAmount = new byte[6];
    public byte dummy4;
    public int profession;
    /** Unit id. maybe fist unit in transport? */
    public int transportChain1;
    /** Unit id. maybe last unit in transport? */
    public int transportChain2;

}
