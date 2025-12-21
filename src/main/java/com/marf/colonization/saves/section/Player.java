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
public class Player extends GameDataSection {
    private String name;
    private String continent;
    private int byte1;
    private int control; // Raw byte value
    private int diplomacy;
}