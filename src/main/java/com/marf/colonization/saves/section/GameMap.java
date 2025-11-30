package com.marf.colonization.saves.section;

import com.marf.colonization.reader.GameDataSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameMap extends GameDataSection {
    private int width;
    private int height;
    private byte[] terrain;
    private byte[] surface;
    private byte[] visitor;
    private byte[] visibility;

    private List<Tile> tiles = new ArrayList<>();

    public Tile getTile(int x, int y) {
        return tiles.get(x + y * width);
    }

    public void postProcess() {
        // skip first and last row and column
        for (int x = 1; x < width-1; x++) {
            for (int y = 1; y < height-1; y++) {
                Tile tile = getTile(x, y);
                tile.setForestMask(calculateMask(x,y,Tile::isForest));
                tile.setRiverMask(calculateMask(x,y,Tile::isRiver));

                tile.setSeaMask(calculateSeaMask(x,y));

            }
        }
    }

    private int calculateSeaMask(int x, int y) {
        Tile tile = getTile(x, y);
        int bitmask = 0;
        if (tile.isSea()) {
            // bits are places clockwise, msb to lsb, starting west
            // each direction which is land gets the bit set
            bitmask = (bitmask << 1) | (getTile(x-1,y).isSea() ? 0 : 1); // west
            bitmask = (bitmask << 1) | (getTile(x - 1, y - 1).isSea() ? 0 : 1); // NW
            bitmask = (bitmask << 1) | (getTile(x,y-1).isSea() ? 0 : 1); // north
            bitmask = (bitmask << 1) | (getTile(x + 1, y - 1).isSea() ? 0 : 1); // NE
            bitmask = (bitmask << 1) | (getTile(x+1,y).isSea() ? 0 : 1); // east
            bitmask = (bitmask << 1) | (getTile(x + 1, y + 1).isSea() ? 0 : 1); // SE
            bitmask = (bitmask << 1) | (getTile(x,y+1).isSea() ? 0 : 1); // south
            bitmask = (bitmask << 1) | (getTile(x - 1, y + 1).isSea() ? 0 : 1); // SW

            tile.setSeaMask(bitmask);
        }
        return bitmask;
    }

    private int calculateMask(int x, int y, Function<Tile, Boolean> getter) {
        Tile tile = getTile(x, y);
        int bitmask = 0;
        if (getter.apply(tile)) {
            bitmask |= getter.apply(getTile(x+1,y)) ? 1 : 0; // east
            bitmask |= getter.apply(getTile(x-1,y)) ? 2 : 0; // west
            bitmask |= getter.apply(getTile(x,y+1)) ? 4 : 0; // south
            bitmask |= getter.apply(getTile(x,y-1)) ? 8 : 0; // north
        }
        return bitmask;
    }
}
