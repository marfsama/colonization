package com.marf.colonization.saves.section;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Tile {
    private final int tileValue;
    private final int tileId;
    private final boolean forest;
    private final boolean sea;
    private final boolean mountains;
    private final boolean river;
    /**
     * major mountain or major river
     */
    private final boolean major;

    /** Bitfield of forest neighbours. This is also the index into the forest sprites. */
    private int forestMask;
    /** Bitfield of river neighbours. This is also the index into the river sprites. */
    private int riverMask;
    /** Bitfield of mountain neighbours. This is also the index into the mountains sprites. */
    private int mountainMask;
    /** Bitfield of sea neighbours.  */
    private int seaMask;

    public Tile(int value) {
        this.tileValue = value & 0xff;
        this.tileId = (value) & 7;
        this.forest = ((value >> 3) & 1) == 1;
        this.sea = ((value >> 4) & 0x1) == 1;
        this.mountains = ((value >> 5) & 0x1) == 1;
        this.river = ((value >> 6) & 0x1) == 1;
        this.major = ((value >> 7) & 0x1) == 1; // major mountain or major river
    }

    public boolean isForest() {
        return forest && !sea;
    }

    public boolean isRiver() {
        return river && !sea;
    }

}
