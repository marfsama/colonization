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
public class Colony extends GameDataSection {
    private int x;
    private int y;
    private String name;
    private int nation; // Will use Lookup enum later
    private byte[] dummy1;
    private int colonistsNum;
    private List<TableValue> colonistsOccupation;
    private List<TableValue> colonistsSpecialization;
    private List<Integer> colonistsTime;
    private List<Integer> tileUsage;
    private byte[] dummy2;
    private byte[] buildingsBitset;
    private int customsHouse;
    private byte[] dummy3;
    private int hammers;
    private int currentProduction; // Will use Lookup enum later
    private byte[] dummy4;
    private List<Integer> storage;
    private byte[] dummy5;
    private int bells;
    private int data;
}
