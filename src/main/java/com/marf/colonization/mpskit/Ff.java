package com.marf.colonization.mpskit;

import com.marf.colonization.util.ByteArrayImageInputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.stream.IntStream;

/** read font file */
public class Ff {

    private final Madspack madspack;
    private int height;
    private int maxWidth;
    private int[] charWidth;
    private int[] charOffsets;
    private BufferedImage image;


    public Ff(Madspack madspack) {
        this.madspack = madspack;
    }

    public void read() throws IOException {
        Section section = madspack.getSection(0);

        ImageInputStream stream = new ByteArrayImageInputStream(section.getData());
        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        this.height = stream.read();
        this.maxWidth = stream.read();
        this.charWidth = new int[128];
        this.charOffsets = new int[128];

        for (int i = 0; i < charWidth.length; i++) {
            this.charWidth[i] = stream.read();
        }
        for (int i = 0; i < charOffsets.length; i++) {
            this.charOffsets[i] = stream.readUnsignedShort();
        }

        System.out.printf("height %s, width: %s%n", height, maxWidth);
        System.out.println(IntStream.of(charWidth).boxed().toList());
        System.out.println(IntStream.of(charOffsets).mapToObj(Integer::toHexString).toList());

        int imgWidth = IntStream.of(charWidth).sum();

        this.image = new BufferedImage(imgWidth, height, BufferedImage.TYPE_INT_ARGB);

        int[] pal = new int [] { 0x000000, 0xffffffff, 0xffffffff, 0xffffffff };
        int currentX = 0;
        for (int i = 0; i < charWidth.length; i++) {
            int width = charWidth[i];

            if (width == 0) {
                continue;
            }

            stream.seek(charOffsets[i]);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; ) {
                    int b = stream.read();
                    int c = (b >> 6) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    x++;
                    if (x == width)
                        break;

                    c = (b >> 4) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    x++;
                    if (x == width)
                        break;
                    c = (b >> 2) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    x++;
                    if (x == width)
                        break;
                    c = (b) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    x++;
                    if (x == width)
                        break;
                }
            }
            currentX += width;
        }
    }

    public BufferedImage getAll() {
        return image;
    }

    public static void main(String[] args) throws IOException {
        Path path = Path.of("src/main/resources/FONTSMAL.FF");
        String filename = path.getFileName().toString().toLowerCase().replace(".ff", "");
        System.out.println(filename);
        try (FileImageInputStream stream = new FileImageInputStream(path.toFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            Ff ff = new Ff(madspack);
            ff.read();
            Path destinationFolder = Path.of(".");
            ImageIO.write(ff.getAll(), "png", destinationFolder.resolve("%s.png".formatted(filename)).toFile());

        } catch (IOException e) {
            throw new IllegalStateException(path.toString(), e);
        }

    }
}
