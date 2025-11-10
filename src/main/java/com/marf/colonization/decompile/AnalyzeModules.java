package com.marf.colonization.decompile;

import com.marf.colonization.reader.BaseReader;
import com.marf.colonization.reader.GameDataSection;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyzeModules extends BaseReader {

    public static void main(String[] args) throws IOException {
//        System.out.println(new Address("0cdd").sub(new Address("01a2")).add(0x1000));
        try (ImageInputStream stream = new FileImageInputStream(new File("src/main/resources/VICEROY.EXE").getAbsoluteFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            new AnalyzeModules(stream).analyze();
        }
    }

    public AnalyzeModules(ImageInputStream stream) {
        super(stream);
    }

    private void analyze() throws IOException {

        var moduleAddress = new Address("20560");
        //var nextModule = new Address("77080");

        List<ModuleHeader> modules = readModules(moduleAddress);

        Address currentAddress = new Address(0x4000);
        for (ModuleHeader module : modules) {
            if (module.getCodeSizeInBytes() > 0) {

                Address nextSegment = currentAddress.add(module.getTotalsize_in_paragraphs());
                System.out.println("Module %02x: %s - %s (%x bytes)".formatted(module.getModuleId(), currentAddress, nextSegment, module.getCodeSizeInBytes()));

                currentAddress = nextSegment;
            }
            //writeModuleFile(module);
        }

        ModuleHeader module = modules.get(0x18);
        List<Relocation> relocations = module.relocations;
        Collections.sort(relocations);
        relocations.forEach(System.out::println);
        List<Address> sortedSegments = relocations.stream().map(Relocation::getSegment)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println(sortedSegments);



    }

    private List<ModuleHeader> readModules(Address moduleAddress) throws IOException {
        List<ModuleHeader> modules = new ArrayList<>();
        for (int i = 1; i < 40; i++) {
            if (stream.getStreamPosition() >= stream.length()-1) {
                break;
            }
            //System.out.println("module "+i+" "+new Address((int) stream.getStreamPosition())+" / "+new Address((int) stream.length()));
            ModuleHeader header = readHeader(moduleAddress);
            header.setModuleId(i);
            header.setModuleAddress(moduleAddress);

            header.relocations = readObjectList(header.number_of_relocation_entries, Relocation::new, this::readRelocation);

            //System.out.println("-------------------");

            modules.add(header);

            moduleAddress = moduleAddress.add(header.totalsize_in_paragraphs * 16);
        }
        return modules;
    }

    private Relocation readRelocation(Relocation relocation, ImageInputStream stream) throws IOException {
        relocation.offset = new Address(stream.readUnsignedShort());
        relocation.segment = new Address(stream.readUnsignedShort());
        return relocation;
    }


    private void writeModuleFile(ModuleHeader header) throws IOException {
        stream.seek(header.getModuleAddress().getValue()+ header.getCode_start_in_paragraphs()*16L);
        byte[] data = new byte[header.getCodeSizeInBytes()];
        stream.readFully(data);

        Files.write(Path.of("module%02d".formatted(header.getModuleId())+".bin"), data);
    }

    public ModuleHeader readHeader(Address moduleAddress) throws IOException {
        //System.out.println("module start: "+moduleAddress+" ("+moduleAddress.getValue()+")");
        stream.seek(moduleAddress.getValue());
        ModuleHeader header = readObject(ModuleHeader::new, this::readModuleHeader);

        //System.out.println(header);

        //System.out.println("start of code: " + moduleAddress.add(header.code_start_in_paragraphs * 16)+" Length: "+new Address((header.totalsize_in_paragraphs-header.code_start_in_paragraphs)*16));
        //System.out.println("end of module: " + moduleAddress.add(header.totalsize_in_paragraphs * 16));
        return header;
    }

    public ModuleHeader readModuleHeader(ModuleHeader header, ImageInputStream stream) throws IOException {
        header.totalsize_in_paragraphs = stream.readUnsignedShort(); // 00
        header.code_start_in_paragraphs = stream.readUnsignedShort(); // 02
        header.unknown = stream.readUnsignedShort();                // 04
        header.offset_of_relocation_table = stream.readUnsignedShort(); // 06
        header.number_of_relocation_entries = stream.readUnsignedShort(); // 08
        header.offset_0a = stream.readUnsignedShort(); // 0a
        header.offset_0c = stream.readUnsignedShort(); // 0c
        header.offset_0e = stream.readUnsignedShort(); // 0e

        return header;
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ModuleHeader extends GameDataSection {
        // 0x00
        private int totalsize_in_paragraphs;
        // 0x02
        private int code_start_in_paragraphs;
        // 0x04
        private int unknown;
        // 0x06
        private int offset_of_relocation_table;
        // 0x08
        private int number_of_relocation_entries;
        // 0x0a
        private int offset_0a;
        // 0x0c
        private int offset_0c;
        // 0x0e
        private int offset_0e;

        private int moduleId;
        private Address moduleAddress;
        private List<Relocation> relocations;

        public int getCodeSizeInBytes() {
            return (totalsize_in_paragraphs - code_start_in_paragraphs) * 16;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Relocation extends GameDataSection implements Comparable<Relocation> {
        private Address offset;
        private Address segment;

        private int getLinearAddress() {
            return segment.getValue() * 16 + offset.getValue();
        }

        @Override
        public String toString() {
            return "Relocation{" +
                    "offset=" + offset +
                    ", segment=" + segment +
                    ", linearAddress="+ getLinearAddress() +
                    '}';
        }

        @Override
        public int compareTo(Relocation o) {
            return getLinearAddress() - o.getLinearAddress();
        }
    }

}
