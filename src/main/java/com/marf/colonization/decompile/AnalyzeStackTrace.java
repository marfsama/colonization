package com.marf.colonization.decompile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.List;

public class AnalyzeStackTrace {

    private List<FunctionDefinition> functions;

    public static void main(String[] args) throws IOException {
//        System.out.println(new Address("0cdd").sub(new Address("01a2")).add(0x1000));
        new  AnalyzeStackTrace().analyze();
    }

    private void analyze() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        this.functions = mapper.readValue(AnalyzeStackTrace.class.getResource("/functions.yaml"), new TypeReference<List<FunctionDefinition>>() {});

        String home = System.getProperty("user.home");
        System.out.println(home);

        var address = new Address("2720");
        var segmentAbsolute = new Address("0d01");
        var dosboxcs = new Address("0eee");
        var segmentDiff = dosboxcs.sub(segmentAbsolute);

        System.out.println("segment diff: "+segmentDiff);

        ImageInputStream stackStream = new FileImageInputStream(Path.of(home+"/MEMDUMP.BIN").toFile());
        stackStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);


        while (true) {
            StackElement stackElement = readStackTraceElement(segmentAbsolute, address, stackStream);
            if (stackElement == null) {
                break;
            }
            System.out.println(stackElement);
            segmentAbsolute = stackElement.callerCs.sub(segmentDiff);
            Address segmentGhidra = segmentAbsolute.add(0x1000);
            System.out.println("segment: "+segmentAbsolute+" dosbox address: "+stackElement.callerCs+":"+stackElement.callerIp+" ghidra address: "+segmentGhidra+":"+stackElement.callerIp+" stackStream pos: "+new Address((int) stackStream.getStreamPosition()));


            address = stackElement.callerIp;
        }

        System.out.println();
        int rowLength = 0;
        while (stackStream.getStreamPosition() < stackStream.length()) {
            int value = stackStream.readUnsignedShort();
            System.out.print(new Address(value)+" ");
            rowLength++;
            if (rowLength == 16) {
                System.out.println();
                rowLength = 0;
            }
        }


        stackStream.close();
    }

    private StackElement readStackTraceElement(Address segment, Address address, ImageInputStream stackStream) throws IOException {
        FunctionDefinition function = getFunction(segment, address);
        if (function == null) {
            return null;
        }

        StackElement stackElement = new StackElement();

        stackElement.functionDefinition = function;

        if (function.getLocalStackSize().getValue() != 0) {
            stackElement.localStackFrame = new byte[function.getLocalStackSize().getValue()];
            stackStream.readFully(stackElement.localStackFrame);
        }

        for (var localVar : function.getLocalVars()) {
            stackElement.localVars.put(localVar, new Address(stackStream.readUnsignedShort()));
        }
        stackElement.savedBp = new Address(stackStream.readUnsignedShort());
        stackElement.callerIp = new Address(stackStream.readUnsignedShort());
        stackElement.callerCs = new Address(stackStream.readUnsignedShort());

        for (var localVar : function.getParameters()) {
            stackElement.parameters.put(localVar.getName(), new Address(stackStream.readUnsignedShort()));
        }
        return stackElement;
    }

    private FunctionDefinition getFunction(Address segment, Address address) {
        return functions.stream().filter(
                function -> function.contains(segment, address)
        ).findFirst().orElse(null);
    }
}
