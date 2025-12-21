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
public class Unit extends GameDataSection {
    /** 0x00 */
    private int x;
    /** 0x01 */
    private int y;
    /** 0x02 */
    private int type;
    /** 0x03 */
    private int nationIndex;
    /** 0x04 (bit 7 = 1 => damaged artillery) */
    private int flags_damaged;
    /** 0x05 */
    private int usedMoves;
    /** 0x06 */
    private byte field_0x6;
    /** 0x06 */
    private byte field_0x7;
    /** 0x08 */
    private int order;
    /** 0x09 */
    private int gotoX;
    /** 0x0a */
    private int gotoY;
    /** 0x0b */
    private int dummy3;
    /** 0x0c */
    private int numCargo;
    /** 0x0d */
    private List<Integer> cargoTypes;
    /** 0x10 */
    private List<Integer> cargoAmount;
    /** 0x16 */
    private int attack_penalty_maybe;
    /** 0x17 */
    private int profession;
    /** 0x18 - Unit id. maybe fist unit in transport? */
    private int previous;
    /** 0x1a - Unit id. this. might be the next unit in a stack of units (on the map or in colony) */
    private int next;
    // total size: 0x1c
}
