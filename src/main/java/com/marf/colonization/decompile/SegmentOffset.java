package com.marf.colonization.decompile;

import com.marf.colonization.reader.GameDataSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Segment:Offset pair.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SegmentOffset extends GameDataSection {
    private final Address segment;
    private final Address offset;

    public SegmentOffset() {
        this(new Address(0));
    }

    public SegmentOffset(Address segment) {
        this(segment, new Address(0));
    }

    public int toLinearAddress() {
        return segment.getValue() * 16 + offset.getValue();
    }

    public SegmentOffset withOffset(int newOffset) {
        return withOffset(new Address(newOffset));
    }

    public SegmentOffset withOffset(Address newOffset) {
        return new SegmentOffset(segment, newOffset);
    }

    @Override
    public String toString() {
        return segment + ":" + offset;
    }
}
