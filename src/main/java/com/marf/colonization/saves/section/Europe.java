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
public class Europe extends GameDataSection {
    private int flags;
    private int taxRate;
    private List<TableValue> nextRecruits;
    private byte[] padding2;
    private byte[] foundingFathersBitset;
    private byte[] padding3;
    private int currentBells;
    private byte[] padding4;
    private int currentFoundingFather; // Will use Lookup enum later
    private byte[] padding5;
    private int boughtArtillery;
    private byte[] padding6;
    private int gold;
    private int currentCrosses;
    private int neededCrosses;
    private byte[] padding8;
    private List<Integer> goodsPrice;
    private List<Integer> goodsUnknown;
    private List<Integer> goodsBalance;
    private List<Integer> goodsDemand;
    private List<Integer> goodsDemand2;
}
