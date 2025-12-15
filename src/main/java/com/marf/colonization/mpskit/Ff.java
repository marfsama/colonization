package com.marf.colonization.mpskit;

import com.marf.colonization.util.ByteArrayImageInputStream;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    public Font read() throws IOException {
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

        int imgWidth = IntStream.of(charWidth).sum();

        this.image = new BufferedImage(imgWidth, height, BufferedImage.TYPE_INT_ARGB);
        List<FontCharacter> fontCharacters = new ArrayList<>();

        int[] pal = new int [] { 0x000000, 0xffffffff, 0xffffffff, 0xffffffff };
        int currentX = 0;
        for (int i = 0; i < charWidth.length; i++) {
            int width = charWidth[i];
            FontCharacter fontCharacter = new FontCharacter(width, height);
            fontCharacters.add(fontCharacter);

            if (width == 0) {
                continue;
            }

            stream.seek(charOffsets[i]);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; ) {
                    int b = stream.read();

                    int c = (b >> 6) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    fontCharacter.setPixel(x,y,c);
                    x++;
                    if (x == width)
                        break;

                    c = (b >> 4) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    fontCharacter.setPixel(x,y,c);
                    x++;
                    if (x == width)
                        break;

                    c = (b >> 2) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    fontCharacter.setPixel(x,y,c);
                    x++;
                    if (x == width)
                        break;

                    c = (b) & 0x3;
                    image.setRGB(currentX+x,y,pal[c]);
                    fontCharacter.setPixel(x,y,c);
                    x++;
                    if (x == width)
                        break;
                }
            }
            currentX += width;
        }
        return new Font(height, fontCharacters);
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

    @Data
    public static class Font {
        private final int height;
        private final List<FontCharacter> chars;

        /** @see com.marf.colonization.decompile.cmodules.Code1c#FUN_1c0e_000c_get_string_width_in_pixels */
        public int getStringWidth(String text) {
            return text.chars().map(character -> {
                if (character < 128) {
                    return chars.get(character).width;
                }
                return 0;
            }
            ).sum();
        }
    }

    @Data
    public static class FontCharacter {
        private int width;
        private int[] colorIndices;

        public FontCharacter(int width, int height) {
            this.width = width;
            this.colorIndices = new int[width*height];
        }

        public int getPixel(int x, int y) {
            return colorIndices[y * width + x];
        }

        public void setPixel(int x, int y, int color) {
            colorIndices[y * width + x] = color;
        }
    }
}
