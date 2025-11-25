package com.marf.colonization.mpskit;

import com.marf.colonization.util.ByteArrayImageInputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Read Madspack *.pik files
 */
public class Pik {

    private final Madspack madspack;

    public Pik(Madspack madspack) {
        this.madspack = madspack;
    }

    public BufferedImage getImage() throws IOException {
        Header header = getHeader(madspack.getSection(0));
        Color[] palette = getPalette();
        System.out.println(header);

        BufferedImage image = new BufferedImage(header.width, header.height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayInputStream stream = new ByteArrayInputStream(madspack.getSection(1).getData());
        for (int y = 0; y < header.height; y++) {
            for (int x = 0; x < header.width; x++) {
                int c = stream.read();
                image.setRGB(x, y, palette[c].getRGB());
            }
        }
        return image;
    }

    private Color[] getPalette() throws IOException {
        byte[] colorData;
        // if the pik has a palette, return it
        if (madspack.getSections().size() > 2) {
            colorData = madspack.getSection(2).getData();
        } else {
            // pik doesn't have a palette...load default palette
            colorData = Files.readAllBytes(Path.of("src/main/resources/VICEROY.PAL"));
        }

        Color[] palette = new Color[colorData.length / 3];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = new Color((colorData[i * 3] & 0xff) / 64.0f, (colorData[i * 3 + 1] & 0xff) / 64.0f, (colorData[i * 3 + 2] & 0xff) / 64.0f);
        }
        return palette;
    }

    private Header getHeader(Section section) throws IOException {
        ImageInputStream stream = new ByteArrayImageInputStream(section.getData());
        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        Header header = new Header();
        header.height = stream.readUnsignedShort();
        header.width = stream.readUnsignedShort();
        header.segment = stream.readUnsignedShort();
        header.offset = stream.readUnsignedShort();
        return header;
    }


    public class Header {
        int width;
        int height;
        int segment;
        int offset;

        @Override
        public String toString() {
            return "Header{width=%d, height=%d, segment=0x%x, offset=0x%x}".formatted(width, height, segment, offset);
        }
    }

    public static void main(String[] args) throws IOException {
        try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/COLONY.PIK").getAbsoluteFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            Pik pik = new Pik(madspack);
            BufferedImage image = pik.getImage();
            ImageIO.write(image, "png", new File("test.png"));
        }
    }

}
