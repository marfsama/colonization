package com.marf.colonization.saves.section;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveFile {
    private Header header;

    private List<Integer> royalForce;
    @JsonIgnore
    private byte[] padding7;
    private List<Player> players;
    private List<Viewport> viewports;
    private List<Colony> colonies;
    private List<Unit> units;
    private List<Europe> europe;
    private List<IndianVillage> indianVillages;
    private List<IndianTribe> indianTribes;
    @JsonIgnore
    private byte[] padding9;
    private Position cursorPos;
    @JsonIgnore
    private byte[] padding10;
    private Position viewport;
    private GameMap map;
    @JsonIgnore
    private byte[] paddingFinal;


    private List<String> globalErrors = new ArrayList<>();
    private boolean hasErrors;
}
