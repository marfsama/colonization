package com.marf.colonization.decompile;

import com.marf.colonization.reader.GameDataSection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ModuleStackEntry extends GameDataSection {
    /**
     * 0x0 0x4: some segment:offset pair
     */
    public SegmentOffset caller;
    /**
     * 0x4 0x1 module index, 0-based
     */
    public int moduleIndex;
    /**
     * 0x5 0x1 some flag
     */
    public int flags;
    /**
     * 0x6 0x2 stack pointer pointing to caller return address
     */
    public Address stackPointerToCallerReturnAddress;
    /**
     * 0x8 0x2 code segment for the module.
     */
    public Address codeSegment;
    // stuff read from SS:[field_3_0x6]
    public SegmentOffset stackStuff;
}
