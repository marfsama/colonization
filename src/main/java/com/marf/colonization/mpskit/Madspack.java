package com.marf.colonization.mpskit;

import com.marf.colonization.reader.BaseReader;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

/**
 * MADSPACK format
 * <p>
 * size repeat content
 * ---------------------------------------------
 * 12          magic           MADSPACK 2.0
 * 2           ykhm            0x1A
 * 2           count (max 16)
 * 10   16     section_header
 * count  section
 * <p>
 * section_header
 * size repeat content
 * ---------------------------------------------
 * 2			flags -- bit 0 set => fab compression
 * 4			size -- uncompressed size
 * 4			csize -- compressed size
 */
public class Madspack extends BaseReader {

    private short unknown;
    private short sectionCount;
    private List<Section> sections;
    private int _bits_left;
    private int _bit_buffer;

    public Madspack(ImageInputStream stream) {
        super(stream);
    }

    public Section getSection(int index) {
        return sections.get(index);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void read() throws IOException {
        // read header
        String magic = readString(stream, 12);
        if (!magic.equals("MADSPACK 2.0")) {
            throw new IllegalStateException("invalid madspack version; expected=MADSPACK 2.0; got=%s".formatted(magic));
        }

        this.unknown = stream.readShort();
        this.sectionCount = stream.readShort();
        this.sections = readObjectList(sectionCount, Section::new, this::readSection);

        // seek to start of data
        stream.seek(0xb0);

        for (Section section : sections) {
            if ((section.flags & 1) == 1) {
                // compressed
                long start = stream.getStreamPosition();
                section.setData(readCompressedData(section));
                //stream.seek(start+section.uncompressedSize);
            } else {
                // uncompressed
                section.setData(new byte[section.uncompressedSize]);
                stream.read(section.getData());

            }
        }


    }

    private byte[] readCompressedData(Section section) throws IOException {
        byte[] destination = new byte[section.uncompressedSize];

        String fabMagic = readString(stream, 3);
        if (!fabMagic.equals("FAB")) {
            throw new IllegalStateException("compressed section does not start with 'FAB', but with '%s'".formatted(fabMagic));
        }

        int shift_val = stream.readUnsignedByte();

        if (shift_val < 10 || shift_val > 14) {
            throw new IllegalStateException("fab_decode: invalid shift_val: %s".formatted(shift_val));
        }

        int copy_adr_shift = 16 - shift_val;
        int copy_adr_fill = 0xFF << (shift_val - 8);
        int copy_len_mask = (1 << copy_adr_shift) - 1;

        this._bits_left = 16;
        this._bit_buffer = stream.readUnsignedShort();

        int j = 0;

        while (true) {
            // 1 - take next byte literal
            // 00 - copy earlier pattern
            // 01
            if (get_bit() == 1) {
                byte b = stream.readByte();
                destination[j] = b;
                j += 1;
            } else {
                int copy_len = 0;
                int copy_adr = 0xFFFF0000;

                // first bit == 0
                // check next bit
                if (get_bit() == 0) {
                    // 00, bit, bit, byte
                    int b1 = get_bit();
                    int b2 = get_bit();

                    // use 2 bits for copy_len
                    // copy_len in range [2,5]
                    copy_len = ((b1 << 1) | b2) + 2;

                    // read negative num in range [255 -> -1, 0 -> -256]
                    int raw_copy_adr = stream.read();
                    copy_adr = raw_copy_adr | 0xFFFFFF00;
                } else {
                    // 01, byte A, byte B
                    int A = stream.read();
                    int B = stream.read();

                    copy_adr = (((B >> copy_adr_shift) | copy_adr_fill) << 8) | A;
                    copy_adr |= 0xFFFF0000;

                    // use [3 to 7] (usually 4) bits for copy_len
                    copy_len = B & copy_len_mask;

                    if (copy_len == 0) {
                        // use 8 bits for copy len
                        copy_len = stream.read();

                        if (copy_len == 0) {
                            break;
                        } else if (copy_len == 1) {
                            // NOP
                            continue;
                        } else {
                            copy_len += 1;
                        }
                    } else {
                        copy_len += 2;
                    }
                }

                while (copy_len > 0) {
                    byte v = destination[j + copy_adr];
                    destination[j] = v;
                    j += 1;
                    copy_len -= 1;
                }
            }
        }
        return destination;
    }

    public int get_bit() throws IOException {
        _bits_left -= 1;
        if (_bits_left == 0) {
            _bit_buffer = (stream.readUnsignedShort() << 1) | (_bit_buffer & 1);
            _bits_left = 16;
        }
        int bit = _bit_buffer & 1;
        _bit_buffer >>= 1;

        return bit;
    }


    private Section readSection(Section section, ImageInputStream stream) throws IOException {
        section.flags = stream.readShort();
        section.uncompressedSize = stream.readInt();
        section.compressedSize = stream.readInt();
        return section;

    }



    public static void main(String[] args) throws IOException {
        try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/BUILDING.SS").getAbsoluteFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            madspack.sections.forEach(System.out::println);
        }
    }
}
