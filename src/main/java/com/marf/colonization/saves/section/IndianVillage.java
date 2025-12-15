package com.marf.colonization.saves.section;

import com.marf.colonization.reader.GameDataSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IndianVillage extends GameDataSection {
    private int x;
    private int y;
    private TableValue nation; // Will use Lookup enum later
    private int state;
    private int population;
    private int mission; // Will use Lookup enum later
    private byte[] padding1;
    private int[] panic;
}
