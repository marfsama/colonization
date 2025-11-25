package com.marf.colonization.saves.section;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marf.colonization.reader.GameDataSection;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Header extends GameDataSection {

    private String magic;
    @JsonIgnore
    private byte[] padding1;
    private Position mapSize;
    @JsonIgnore
    private byte[] padding2;
    private int year;
    private int autumn;
    private int turn;
    @JsonIgnore
    private byte[] padding3;
    private int activeUnit;

    private int viewportPower;
    private int playerControlledPower;
    private int maybe_current_player;
    private int numTribes;
    private int numUnits;
    private int numColonies;
    @JsonIgnore
    private byte[] padding5;
    private int difficulty; // Will use Lookup enum later
    @JsonIgnore
    private byte[] padding6;
}
