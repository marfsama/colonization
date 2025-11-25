package com.marf.colonization.decompile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.marf.colonization.reader.BaseReader;
import com.marf.colonization.reader.IoFunction;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeMemoryDump extends BaseReader {

    /** Address of the code segment with the main module. This is also the segment with the entry point. */
    private static final Address MAIN_CODE_SEGMENT = new Address(0x20FE);
    public static final int SEGMENT_DIFF = 0xe13;
    private final CpuState cpuState;

    private static SegmentOffset DYNAMIC_CALL_RETURN = new SegmentOffset(new Address(0x12eb), new Address(0x1554));

    private List<FunctionDefinition> functions;
    private List<ModuleConfig> moduleConfigs;

    public AnalyzeMemoryDump(ImageInputStream stream, CpuState cpuState, List<FunctionDefinition> functions) {
        super(stream);
        this.cpuState = cpuState;
        this.functions = functions;
    }

    public static void main(String[] args) throws IOException {
        // memdumpbin 0:0 100000
        // log 1
        String baseFolder = "dumps/11";

        String description = Files.readString(Path.of(baseFolder,  "desc.txt"));
        System.out.println("-----------------");
        System.out.println(description);
        CpuState cpuState = CpuState.fromString(Files.readString(Path.of(baseFolder,  "LOGCPU.TXT")).trim());
        System.out.println("----- CPU -------");
        System.out.println(cpuState);
        System.out.println("-----------------");

        try (ImageInputStream stream = new FileImageInputStream(Path.of(baseFolder,  "MEMDUMP.BIN").toFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            List<FunctionDefinition> functions = readFunctionDefinitions(mapper);
            var analyzer = new AnalyzeMemoryDump(stream, cpuState, functions);
            // read module config
            SegmentOffset moduleConfigPosition = new SegmentOffset(new Address(0x26dc).sub(0xe13), new Address(0x40));
            stream.seek(moduleConfigPosition.toLinearAddress());
            analyzer.moduleConfigs = analyzer.readObjectList(31, ModuleConfig::new, analyzer::readModuleConfig);
            System.out.printf("Module Config (%s)%n", moduleConfigPosition);
            System.out.println(new MarkdownTableConverter()
                    .withFormatter("flags", Integer::toHexString)
                    .withFormatter("moduleIndex", Integer::toHexString)
                    .toMarkdownTable(analyzer.moduleConfigs));

            // read module stack
            stream.seek(new SegmentOffset(MAIN_CODE_SEGMENT.sub(0xe13), new Address(0x3952)).toLinearAddress());
            SegmentOffset moduleStackPtr = analyzer.readObject(SegmentOffset::new, analyzer::readSegmentOffset);

            System.out.println("moduleStackPtr: "+moduleStackPtr+" "+moduleStackPtr.toLinearAddress());

            int stackSize = (moduleStackPtr.getOffset().getValue() / 10);
            stream.seek(moduleStackPtr.withOffset(0).toLinearAddress());
            List<ModuleStackEntry> entries = analyzer.readObjectList(stackSize, ModuleStackEntry::new, analyzer::readModuleStackEntry);

            for (ModuleStackEntry entry : entries) {
                if (entry.stackPointerToCallerReturnAddress.getValue() != 0xffff) {
                    SegmentOffset stackPtr = new SegmentOffset(cpuState.getSs(), entry.stackPointerToCallerReturnAddress);
                    stream.seek(stackPtr.toLinearAddress());
                    entry.stackStuff = analyzer.readSegmentOffset(new SegmentOffset(), stream);
                }
            }

            System.out.println(new MarkdownTableConverter()
                            .withFormatter("flags", Integer::toHexString)
                            .withFormatter("moduleIndex", Integer::toHexString)
                    .toMarkdownTable(entries));


            analyzer.printVariousProperties();

            //analyzer.readStack(entries);
            //analyzer.readStringTable();

            SegmentOffset foo1 = new SegmentOffset(cpuState.getDs(), new Address(0x8d6c));
            System.out.println("foo1: "+foo1+" "+foo1.toLinearAddress());
            stream.seek(foo1.toLinearAddress());
            Address foo2 = analyzer.readAddress(new Address(), stream);
            System.out.println("foo2: "+foo2+" "+new SegmentOffset(cpuState.getDs(), foo2).toLinearAddress());
            System.out.println("foo3: "+new SegmentOffset(cpuState.getDs(), foo2.add(0x9a)).toLinearAddress());

        }
    }

    private void readStringTable() throws IOException {
        SegmentOffset segmentOffset = new SegmentOffset(new Address(0x2b4d).sub(SEGMENT_DIFF), new Address(0x2d00));
        stream.seek(segmentOffset.toLinearAddress());
        System.out.println(segmentOffset.toLinearAddress());

        if (segmentOffset.getSegment().getValue() == 0) {
            return;
        }

        var foo = readAddress(new Address(), stream);
        var textLocation = readSegmentOffset(new SegmentOffset(), stream);
        var loc2 = readSegmentOffset(new SegmentOffset(), stream);
        var loc3 = readSegmentOffset(new SegmentOffset(), stream);
        var loc4 = readSegmentOffset(new SegmentOffset(), stream);
        var loc5 = readSegmentOffset(new SegmentOffset(), stream);
        stream.seek(textLocation.toLinearAddress());

        System.out.println("flags: "+foo+" "+foo.getValue());
        System.out.println("start: "+textLocation+" "+textLocation.toLinearAddress());
        System.out.println("end: "+loc2+" "+loc2.toLinearAddress());
        System.out.println("total: "+loc3+" "+loc3.toLinearAddress());
        System.out.println("remaining: "+loc4+" "+loc4.toLinearAddress());
        System.out.println("number of text entries: "+loc5+" "+loc5.toLinearAddress());

        List<String> strings = new ArrayList<>();
        while (true) {
            String s = readNullTerminatedString(stream);
            if (s.isEmpty()) {
                break;
            }
            strings.add(s);
        }

        for (int i = 0; i < strings.size(); i++) {
            System.out.println(i+" (0x"+Integer.toHexString(i)+"): "+strings.get(i));
        }
        System.out.println("number of Strings: "+strings.size()+" 0x"+Integer.toHexString(strings.size()));

        SegmentOffset textIndicesOffset = new SegmentOffset(new Address(0x2b4d).sub(SEGMENT_DIFF), new Address(0x9772));
        System.out.println(textIndicesOffset.toLinearAddress());
        stream.seek(textIndicesOffset.toLinearAddress());
    }

    private static List<FunctionDefinition> readFunctionDefinitions(ObjectMapper mapper) throws IOException {
        List<FunctionDefinition> functionDefinitions = mapper.readValue(AnalyzeStackTrace.class.getResource("/functions.yaml"), new TypeReference<List<FunctionDefinition>>() {
        });
        // relocate function definitions to local address space
        for (FunctionDefinition definition : functionDefinitions) {
            if (definition.getModule() == null) {
                definition.setSegment(definition.getSegment().sub(SEGMENT_DIFF).add(0x1000)); // add 0xe13
            }
        }

        return functionDefinitions;
    }

    private void readStack(List<ModuleStackEntry> moduleStack) throws IOException {
        System.out.println("--- Stack Trace --- ");

        SegmentOffset currentIp = new SegmentOffset(cpuState.getCs(), cpuState.getIp());
        System.out.println("start cs:ip: "+currentIp);

        FunctionDefinition currentFunction = getCallingFunction(currentIp, null, moduleStack);

        SegmentOffset stack = new SegmentOffset(cpuState.getSs(), cpuState.getSp());
        int stackStartOffset = stack.toLinearAddress();
        stream.seek(stackStartOffset);

        List<String> stackTrace = new ArrayList<>();

        while (currentFunction != null) {
            StackElement stackElement = readStackTraceElement(currentFunction, stream);
            stackElement.module = findModule(stackElement.callerCs);

            System.out.println("stack: " + stack);
            System.out.println(stackElement);
            stackTrace.add(stackElement.toStringSmall());
            int stackDiff = (int) (stream.getStreamPosition() - stackStartOffset);
            stack = new SegmentOffset(cpuState.getSs(), cpuState.getSp().add(stackDiff));

            SegmentOffset callerAddress = new SegmentOffset(stackElement.callerCs, stackElement.callerIp);
            currentFunction = getCallingFunction(callerAddress, stackElement.module, moduleStack);
        }

        System.out.println("remaining stuff on stack");
        int rowLength = 0;
        for (int i = 0; i < 32; i++) {
            int value = stream.readUnsignedShort();
            System.out.print(new Address(value)+" ");
            rowLength++;
            if (rowLength == 16) {
                System.out.println();
                rowLength = 0;
            }
        }

        System.out.println(String.join("\n", stackTrace));

    }

    private ModuleConfig findModule(Address callerCs) {
        for (ModuleConfig config : moduleConfigs){
            // check if module is loaded
            if ((config.getFlags() & 0x8000) > 0) {
                Address firstSegment = config.getCode_segment();
                Address lastSegment = firstSegment.add(config.getSize_in_paragraphs());
                if (firstSegment.compareTo(callerCs) <= 0 && callerCs.compareTo(lastSegment) < 0) {
                    return config;
                }
            }
        }
        return null;
    }

    private StackElement readStackTraceElement(FunctionDefinition function, ImageInputStream stackStream) throws IOException {
        StackElement stackElement = new StackElement();

        stackElement.functionDefinition = function;

        if (function.getLocalStackSize().getValue() != 0) {
            stackElement.savedBp = new Address(stackStream.readUnsignedShort());
            stackElement.localStackFrame = new byte[function.getLocalStackSize().getValue()];
            stackStream.readFully(stackElement.localStackFrame);
        }

        for (var localVar : function.getLocalVars()) {
            stackElement.localVars.put(localVar, new Address(stackStream.readUnsignedShort()));
        }
        if (function.isStackFrame() && function.getLocalStackSize().getValue() == 0) {
            stackElement.savedBp = new Address(stackStream.readUnsignedShort());
        }

        stackElement.callerIp = new Address(stackStream.readUnsignedShort());
        if (!function.isCallNear()) {
            stackElement.callerCs = new Address(stackStream.readUnsignedShort());
        } else {
            stackElement.callerCs = function.getSegment();
        }

        for (var localVar : function.getParameters())
            if ("string".equals(localVar.getType())) {
                int offset = stackStream.readUnsignedShort();
                String s = readRandom(new SegmentOffset(cpuState.getDs(), new Address(offset)), this::readNullTerminatedString);
                stackElement.parameters.put(localVar.getName(), "\"%s\" (DS:%04x)".formatted(s, offset));
            } else if ("farstring".equals(localVar.getType())) {
                int offset = stackStream.readUnsignedShort();
                int segment = stackStream.readUnsignedShort();
                String s = readRandom(new SegmentOffset(new Address(segment), new Address(offset)), this::readNullTerminatedString);
                stackElement.parameters.put(localVar.getName(), "\"%s\" (%04x:%04x)".formatted(s, segment, offset));
            } else if ("SegmentOffset".equals(localVar.getType())) {
                stackElement.parameters.put(localVar.getName(), readSegmentOffset(new SegmentOffset(), stream));
            } else {
                stackElement.parameters.put(localVar.getName(), new Address(stackStream.readUnsignedShort()));
            }
        return stackElement;
    }

    public <E> E readRandom(SegmentOffset segmentOffset, IoFunction<ImageInputStream, E> reader) throws IOException {
        long currentPos = stream.getStreamPosition();
        stream.seek(segmentOffset.toLinearAddress());
        try {
            return reader.apply(stream);
        } finally {
            stream.seek(currentPos);
        }
    }


    private void printVariousProperties() throws IOException {
        readShortValue(stream, 0x3958, "stackelement[4], current module index");
        readShortValue(stream, 0x395a, "segment(paragraph) to current module config");
        readShortValue(stream, 0x395c, "stackelement[6], is a stack offset");
        System.out.println("---");
        readShortValue(stream, 0x3973, "pointer to viceroy.exe");
        readShortValue(stream, 0x3975, "int 21 handle zum viceroy");
        readShortValue(stream, 0x3977, "unbekannt");
        readShortValue(stream, 0x3979, "segment wo der erste paragraph hingeladen wird");
        readShortValue(stream, 0x397b, "segment wo der Rest hingeladen wird (irgendwo recht hoch)");
        readShortValue(stream, 0x397d, "FUN_20fe_0dab_module_loader_resolve_function_call: caller return offset");
        readShortValue(stream, 0x397f, "FUN_20fe_0dab_module_loader_resolve_function_call: caller return segment");
        readShortValue(stream, 0x3981, "FUN_20fe_2fd2_some_stuff: offset of the call (caller return address - size of the call instuction)");
        readShortValue(stream, 0x3983, "FUN_20fe_0dab_module_loader_resolve_function_call: SP pointing to the caller return address");
        System.out.println("----");
        readShortValue(stream, 0x399b, "segment (paragraph) to list of module headers?");
        readShortValue(stream, 0x399d, "stackelement[8]");

        System.out.println("----");
        readShortValue(stream, 0x0172, "viewport_center_x");
        readShortValue(stream, 0x0174, "viewport_center_y");
        readShortValue(stream, 0x82e2, "viewport_min_x");
        readShortValue(stream, 0x82e6, "viewport_min_y");
        readShortValue(stream, 0x84e6, "map_size_width");
        readShortValue(stream, 0x84e6, "map_size_height");


        System.out.println("--- Module Loader ---");
        readByteValue(stream, 0x39e1, "set to -1 after a relocated module function is called, maybe busy flag");
        readByteValue(stream, 0x39e2, "Module Loader Flag, maybe global error");
        readSegmentOffsetValue(stream, 0x3a07, "offset where the file position is standing");
    }

    private void readSegmentOffsetValue(ImageInputStream stream, int value, String desc2) throws IOException {
        stream.seek(new SegmentOffset(MAIN_CODE_SEGMENT.sub(0xe13), new Address(value)).toLinearAddress());

        System.out.printf("0x%04x - %s: %s%n", value, desc2, readSegmentOffset(new SegmentOffset(), stream));
    }

    private void readShortValue(ImageInputStream stream, int value, String desc2) throws IOException {
        stream.seek(new SegmentOffset(MAIN_CODE_SEGMENT.sub(0xe13), new Address(value)).toLinearAddress());
        System.out.printf("0x%04x - %s: %04x%n", value, desc2, stream.readUnsignedShort());
    }

    private void readByteValue(ImageInputStream stream, int value, String desc2) throws IOException {
        stream.seek(new SegmentOffset(MAIN_CODE_SEGMENT.sub(0xe13), new Address(value)).toLinearAddress());
        System.out.printf("0x%04x - %s: %04x%n", value, desc2, stream.readUnsignedByte());
    }

    private ModuleStackEntry readModuleStackEntry(ModuleStackEntry stackEntry, ImageInputStream stream) throws IOException {
        stackEntry.caller = readObject(SegmentOffset::new, this::readSegmentOffset);
        stackEntry.moduleIndex = stream.readUnsignedByte();
        stackEntry.flags = stream.readUnsignedByte();
        stackEntry.stackPointerToCallerReturnAddress = readObject(Address::new, this::readAddress);
        stackEntry.codeSegment = readObject(Address::new, this::readAddress);
        return stackEntry;
    }

    private ModuleConfig readModuleConfig(ModuleConfig config, ImageInputStream stream) throws IOException {
        config.flags = stream.readUnsignedShort();
        config.code_segment = readObject(Address::new, this::readAddress);
        config.size_in_paragraphs = readObject(Address::new, this::readAddress);
        config.field_3_0x6 = stream.readUnsignedShort();
        config.offset_in_exe = stream.readInt();
        config.field_5_0xc = stream.readUnsignedShort();
        config.moduleIndex = stream.readUnsignedShort();
        config.field_7_0x10 = stream.readInt();
        config.field_8_0x14 = stream.readInt();
        config.memoryControlBlock = readObject(Address::new, this::readAddress);
        config.field_10_0x1a = stream.readUnsignedShort();
        config.field_11_0x1c = stream.readInt();
        return config;
    }

    private SegmentOffset readSegmentOffset(SegmentOffset segmentOffset, ImageInputStream stream) {
        Address offset = readObject(Address::new, this::readAddress);
        Address segment = readObject(Address::new, this::readAddress);
        return new SegmentOffset(segment, offset);
    }

    private Address readAddress(Address address, ImageInputStream stream) throws IOException {
        return new Address(stream.readUnsignedShort());
    }

    private FunctionDefinition getCallingFunction(SegmentOffset callerAddress, ModuleConfig module, List<ModuleStackEntry> moduleStack) {
        if (callerAddress.equals(DYNAMIC_CALL_RETURN)) {
            // get correct caller address from module stack
            ModuleStackEntry moduleEntry = moduleStack.remove(moduleStack.size() - 1);
            callerAddress = moduleEntry.caller;
            if (moduleEntry.getModuleIndex() > 0) {
                module = moduleConfigs.get(moduleEntry.getModuleIndex() - 1);
                Address moduleSegment = callerAddress.getSegment().sub(module.getCode_segment());
                System.out.printf("resolving dynamic call to %s at module %x (with module segment %s)%n", callerAddress, module.getModuleIndex() - 2, moduleSegment);
            } else {
                System.out.printf("resolving dynamic call to %s at main module%n", callerAddress);
            }
        }

        ModuleConfig finalModule = module;
        if (module != null) {
            Address moduleSegment = callerAddress.getSegment().sub(module.getCode_segment());
            SegmentOffset moduleAddress = new SegmentOffset(moduleSegment, callerAddress.getOffset());
            return functions.stream()
                    .filter(function -> function.getModule() != null)
                    .filter(function -> function.getModule().getValue() == finalModule.moduleIndex-2)
                    .filter(function -> function.contains(moduleAddress))
                    .findFirst().orElse(null);
        }

        SegmentOffset callerAddressFinal = callerAddress;
        return functions.stream()
                .filter(function -> function.contains(callerAddressFinal))
                .findFirst().orElse(null);
    }


}
