package com.marf.colonization.decompile;

import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StackElement {
    FunctionDefinition functionDefinition;
    Map<String, Object> localVars = new LinkedHashMap<>();
    byte[] localStackFrame;
    Address savedBp;
    Address callerIp;
    Address callerCs;
    Map<String, Object> parameters = new LinkedHashMap<>();
    ModuleConfig module;

    public SegmentOffset getCallerAddress() {
        return new SegmentOffset(callerCs, callerIp);
    }

    @Override
    public String toString() {
        String moduleInfo = null;
        if (module != null) {
            Address moduleSegment = callerCs.sub(module.code_segment);
            moduleInfo ="Module %x, Segment %s".formatted(module.moduleIndex-2, moduleSegment);
        }


        return "StackElement " + functionDefinition.getName() + "\n" +
                "  localFrame: " + (localStackFrame != null ? new Address(localStackFrame.length) : "none") + "\n" +
                "  localVars=" + localVars + "\n" +
                "  savedBp=" + savedBp + "\n" +
                "  callerIp=" + callerIp + "\n" +
                "  callerCs=" + callerCs + " (" + (module == null ? callerCs.add(0xe13) : moduleInfo) + ")\n" +
                "  parameters={\n" + parameters.entrySet()
                .stream()
                .map(entry -> "    "+entry.getKey()+" = "+entry.getValue()+"\n")
                .collect(Collectors.joining()) + "  }\n";
    }

    public String toStringSmall() {
        return functionDefinition.getName()+"("+parameters.entrySet()
                .stream()
                .map(entry -> entry.getKey()+" = "+entry.getValue())
                .collect(Collectors.joining(", ")) + ")";
    }
}
