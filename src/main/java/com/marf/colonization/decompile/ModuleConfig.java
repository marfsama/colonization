package com.marf.colonization.decompile;

import com.marf.colonization.reader.GameDataSection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ModuleConfig extends GameDataSection {
    /**
     * 0x0 0x2:
     * bit 15 - loaded
     * bit 0 - skip loading module (already loaded?)
     */
    public int flags;
    /**
     * 0x2 0x2
     */
    public Address code_segment;
    /**
     * 0x4 0x2
     */
    public Address size_in_paragraphs;
    /**
     * 0x6 0x2
     */
    public int field_3_0x6;
    /**
     * 0x8 0x4
     */
    public int offset_in_exe;
    /**
     * 0xc 0x2
     */
    public int field_5_0xc;
    /**
     * 0xe 0x2 module index, 1 based
     */
    public int moduleIndex;
    /**
     * 0x10 0x4
     */
    public int field_7_0x10;
    /**
     * 0x14 0x4
     */
    public int field_8_0x14;
    /**
     * 0x18 0x2
     */
    public Address memoryControlBlock;
    /**
     * 0x1a 0x2
     */
    public int field_10_0x1a;
    /**
     * 0x1c 0x4
     */
    public int field_11_0x1c;
}
