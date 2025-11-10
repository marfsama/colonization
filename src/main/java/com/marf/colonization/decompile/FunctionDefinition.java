package com.marf.colonization.decompile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FunctionDefinition {
    private String name;
    /** The not relocated address of the function. In modules this is the intra-module segment, which might be 0x0000 */
    private Address segment;
    private Address from;
    private Address to;
    /** moduleId, 0-based */
    private Address module;
    /** read bp from stack? */
    @JsonProperty("stack-frame")
    private boolean stackFrame = true;
    /** don't read cs from stack */
    @JsonProperty("call-near")
    private boolean callNear = false;
    @JsonProperty("local-vars")
    private List<String> localVars;
    /** size in ENTER instruction*/
    @JsonProperty("local-stack-size")
    private Address localStackSize = new Address(0);
    private List<Parameter> parameters;

    public boolean contains(Address segment, Address address) {
        return segment.compareTo(this.segment) == 0 && from.compareTo(address) <= 0 && address.compareTo(to) <= 0;
    }

    public boolean contains(SegmentOffset segmentOffset) {
        return contains(segmentOffset.getSegment(), segmentOffset.getOffset());
    }

    @Data
    public static class Parameter {
        private String name;
        private String type = "int";
    }
}
