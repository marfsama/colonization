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
    /** 0x0 0x1 */
    private int x;
    /** 0x1 0x1 */
    private int y;
    /**0x2	0x18*/
    private String name;
    /** 0x1a 0x1 */
    private int nation;
    /** 0x1b 0x1 */
    private byte field_0x1b;
    /** 0x1c 0x4 */
    private byte sonsOfLibertyLevel;
    /** 0x1d 0x2 */
    private short field_0x1d;
    /** 0x1f 0x1 */
    private int numColonists;
    /** 0x20 0x20 */
    private List<Integer> colonistOccupation;
    /** 0x40 0x20 */
    private List<Integer> colonistSpecialization;
    /** 0x60 0x10 */
    private List<Integer> colonistTime;
    /** 0x70 0x8 */
    private List<Integer> tileUsage;
    /** 0x78 0xc */
    private List<Integer> field0x78;
    /** 0x84 0x6 */
    private List<Integer> buildings;
    /** 0x8a 0x2 */
    private int customsHouse;
    /** 0x8c 0x6 */
    private List<Integer> field0x8c;
    /** 0x92 0x2 */
    private int hammers;
    /** 0x94 0x1 */
    private int	currentProduction;
    /** 0x95 0x5 */
    private List<Integer> field0x95;
    /** 0x9a 0x20 */
    private List<Integer> storage;
    /** 0xba 0x4
     * number of colonists the player has seen in the colony (so this is a snapshot)
     * */
    private List<Integer> colonistsSeenInColony;
    /** 0xbe 0x4 */
    private List<Integer> seenFortificationLevel;
    /** 0xc2 0x8 */
    private long bells;
    // total size: 0xca
}
