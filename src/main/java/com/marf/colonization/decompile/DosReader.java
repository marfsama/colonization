package com.marf.colonization.decompile;

import com.marf.colonization.reader.ReadDataSectionFunction;
import com.marf.colonization.reader.GameDataSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.function.Supplier;

public class DosReader {

    private final ImageInputStream stream;

    public DosReader(ImageInputStream stream) {
        this.stream = stream;
    }

    public static void main(String[] args) throws IOException {
        try (ImageInputStream stream = new FileImageInputStream(new File("src/main/resources/VICEROY.EXE").getAbsoluteFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            stream.seek(0);

            DosReader reader = new DosReader(stream);
            reader.read();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void read() throws IOException {
        Header header = readObject(Header::new, this::readHeader);

        stream.seek(header.relocationTableOffset);

        System.out.println("items: "+header.numRelocationItems);
        for (int i = 0; i < header.numRelocationItems; i++) {
            Address offset = new Address(stream.readUnsignedShort());
            Address segment = new Address(stream.readUnsignedShort());
            Address totalAddress = offset.add(segment.getValue() * 16);
            System.out.println(i+": "+segment+":"+offset+" => "+totalAddress+" in file: "+totalAddress.add(header.headerParagraphs*16));
        }
        System.out.println(header);

        System.out.print("initial cs: ");
        String line = new Scanner(System.in).next();
        Address dosboxAddress = new Address(line);
        Address diff = dosboxAddress.sub(header.initialCodeSegment);
        Address ghidraAddress = dosboxAddress.sub(diff).add(0x1000);
        System.out.println("Dosbox Address: "+dosboxAddress+":21db");
        System.out.println("Diff: "+diff);
        System.out.println("Ghidra: "+ghidraAddress+":21db");

    }

    private Header readHeader(Header header, ImageInputStream stream) throws IOException {
        header.signature = readFixedString(stream, 2);
        header.lastPageSize = stream.readUnsignedShort();
        header.filePages = stream.readUnsignedShort();
        header.numRelocationItems = stream.readUnsignedShort();
        header.headerParagraphs = stream.readUnsignedShort();
        header.minAlloc = stream.readUnsignedShort();
        header.maxAlloc = stream.readUnsignedShort();
        header.initialStackSegment = stream.readUnsignedShort();
        header.initialStackPointer = stream.readUnsignedShort();
        header.checksum = stream.readUnsignedShort();
        header.initialInstructionPointer = stream.readUnsignedShort();
        header.initialCodeSegment = stream.readUnsignedShort();
        header.relocationTableOffset = stream.readUnsignedShort();
        header.overlayNumber = stream.readUnsignedShort();
        return header;
    }

    private String readFixedString(ImageInputStream stream, int size) throws IOException {
        byte[] data = new byte[size];
        stream.readFully(data);
        return new String(data);
    }


    /**
     * Generic method for reading any game data section
     * @param constructor Supplier to create the object instance
     * @param reader BiFunction that takes the object and stream, returns the populated object
     * @return The read object with error handling
     */
    private <E extends GameDataSection> E readObject(Supplier<E> constructor,
                                                     ReadDataSectionFunction<E> reader) {
        E object = constructor.get();
        long startPos = getCurrentPosition();
        object.setStartPosition(startPos);

        try {
            E result = reader.readGameDataSection(object, stream);
            object.setReadSuccessfully(result != null);
        } catch (Exception e) {
            object.setErrorMessage("Failed to read section: " + e.getMessage());
            object.setReadSuccessfully(false);
        }

        object.setEndPosition(getCurrentPosition());
        return object;
    }

    private long getCurrentPosition() {
        try {
            return stream.getStreamPosition();
        } catch (IOException e) {
            return -1;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Header extends GameDataSection {
        /** "MZ" */
        private String signature;
        /** number of bytes used in the last 512 bytes page. 0 means 512 bytes used. */
        private int lastPageSize;
        /** number of pages required to hold the file */
        private int filePages;
        /** number of items in the relocation table */
        private int numRelocationItems;
        private int headerParagraphs;
        private int minAlloc;
        private int maxAlloc;
        private int initialStackSegment;
        private int initialStackPointer;
        private int checksum;
        private int initialInstructionPointer;
        /** Before relocation */
        private int initialCodeSegment;
        private int relocationTableOffset;
        private int overlayNumber;

        @Override
        public String toString() {
            return ("Header{" +
                    "signature='%s'%n " +
                    "lastPageSize=%d%n " +
                    "filePages=%d%n " +
                    "totalSizeInBytes=%d%n " +
                    "numRelocationItems=%d (%d bytes)%n " +
                    "headerParagraphs=%d (%d bytes)%n " +
                    "minAlloc=%d (%d bytes)%n " +
                    "maxAlloc=%d (%d bytes)%n " +
                    "initialStackSegment=0x%x%n " +
                    "initialStackPointer=0x%x%n " +
                    "checksum=%d%n " +
                    "initialInstructionPointer=0x%x%n " +
                    "initialCodeSegment=0x%x%n " +
                    "relocationTableOffset=%d%n " +
                    "overlayNumber=%d}").formatted(signature, lastPageSize, filePages,
                    filePages * 512 + lastPageSize,
                    numRelocationItems,numRelocationItems * 4,
                    headerParagraphs,headerParagraphs * 16,
                    minAlloc, minAlloc* 16,
                    maxAlloc,maxAlloc* 16,
                    initialStackSegment, initialStackPointer, checksum, initialInstructionPointer, initialCodeSegment, relocationTableOffset, overlayNumber);
        }
    }
}