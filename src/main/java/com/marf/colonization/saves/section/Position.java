package com.marf.colonization.saves.section;

import com.marf.colonization.reader.GameDataSection;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Position extends GameDataSection {
    private int x;
    private int y;
}
