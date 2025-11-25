package com.marf.colonization.saves.section;

import com.marf.colonization.reader.GameDataSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Unit extends GameDataSection {
    private int x;
    private int y;
    private TableValue type; // Will use Lookup enum later
    private TableValue nationIndex;
    private int dummy0;
    private int dummy1;
    private int usedMoves;
    private byte[] dummy2;
    private TableValue order;
    private int gotoX;
    private int gotoY;
    private byte[] dummy3;
    private int numCargo;
    private List<TableValue> cargoTypes;
    private List<Integer> cargoAmount;
    private byte[] dummy4;
    private TableValue profession;
    /** Unit id. maybe fist unit in transport? */
    private int previous;
    /** Unit id. maybe last unit in transport? */
    private int next;
}


