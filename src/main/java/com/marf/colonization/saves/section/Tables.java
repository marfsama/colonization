package com.marf.colonization.saves.section;

import lombok.Data;

import java.util.List;

@Data
public class Tables {
    private List<String> orders;
    private List<String> controls;
    private List<String> difficulties;
    private List<String> occupations;
    private List<String> directions;
    private List<String> buildings;
    private List<String> goods;
    private List<String> nations;
    private List<String> units;
    private List<String> foundingFathers;
    private List<String> terrain;
}
