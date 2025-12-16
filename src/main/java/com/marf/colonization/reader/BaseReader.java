package com.marf.colonization.reader;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class BaseReader {
    protected final ImageInputStream stream;

    public BaseReader(ImageInputStream stream) {
        this.stream = stream;
    }

    public long getCurrentPosition() {
        try {
            return stream.getStreamPosition();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Generic method for reading any game data section
     * @param constructor Supplier to create the object instance
     * @param reader BiFunction that takes the object and stream, returns the populated object
     * @return The read object with error handling
     */
    public <E extends GameDataSection> E readObject(Supplier<E> constructor,
                                                     ReadDataSectionFunction<E> reader) {
        E object = constructor.get();
        long startPos = getCurrentPosition();
        object.setStartPosition(startPos);

        try {
            E result = reader.readGameDataSection(object, stream);
            if (result != null) {
                result.setStartPosition(startPos);
                object = result;
            }
            object.setReadSuccessfully(result != null);
        } catch (Exception e) {
            object.setErrorMessage("Failed to read section: " + e.getMessage());
            object.setReadSuccessfully(false);
        }

        object.setEndPosition(getCurrentPosition());
        return object;
    }

    // Helper methods
    public String readString(ImageInputStream stream, int length) throws IOException {
        byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return new String(bytes).trim();
    }

    public String readNullTerminatedString(ImageInputStream stream, int length) throws IOException {
        String s = readString(stream, length);
        int terminator = s.indexOf('\0');
        if (terminator > -1) {
            return s.substring(0, terminator);
        }
        return s;
    }

    public String readNullTerminatedString(ImageInputStream stream) throws IOException {
        StringBuilder s = new StringBuilder();
        int b;
        do {
            b = stream.read();
            if (b > 0) {
                s.append((char) b);
            }
        } while (b > 0);
        return s.toString();
    }

    public byte[] readBytes(ImageInputStream stream, int length) throws IOException {
        byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return bytes;
    }

    /**
     * Helper method to read a list of bytes
     */
    public List<Integer> readByteList(int count, ImageInputStream stream) throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(stream.readUnsignedByte());
        }
        return list;
    }

    /**
     * Helper method to read a list of words (unsigned shorts)
     */
    public List<Integer> readShortList(int count, ImageInputStream stream) throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add((int)stream.readShort());
        }
        return list;
    }

    /**
     * Helper method to read a list of words (unsigned shorts)
     */
    public List<Integer> readIntList(int count, ImageInputStream stream) throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(stream.readInt());
        }
        return list;
    }

    /**
     * Generic method for reading a list of GameDataSection objects
     * @param count Number of items to read
     * @param constructor Supplier to create each object instance
     * @param reader BiFunction that reads each object from the stream
     * @return List of read objects with error handling
     */
    public <E extends GameDataSection> List<E> readObjectList(int count,
                                                               Supplier<E> constructor,
                                                               ReadDataSectionFunction<E> reader) {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(readObject(constructor, reader));
        }
        return list;
    }

    public <E extends GameDataSection> List<E> readObjectList(IntSupplier countSupplier,
                                                               Supplier<E> constructor,
                                                               ReadDataSectionFunction<E> reader) {
        return readObjectList(countSupplier.getAsInt(), constructor, reader);
    }

}
