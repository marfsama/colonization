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
public class Indian extends GameDataSection {
    private int unk0;
    private int unk1;
    private int level;
    private byte[] unk2;
    private int armedBraves;
    private int horseHerds;
    private byte[] unk3;
    private List<Integer> stock;
    private byte[] unk4;
    private List<Integer> meetings;
    private byte[] unk5;
    private List<Aggression> aggressions;

    @Data
    public static class Aggression {
        private int aggr;
        private int aggrHigh;
    }
}
