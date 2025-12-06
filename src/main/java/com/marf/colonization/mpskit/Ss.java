package com.marf.colonization.mpskit;

import com.marf.colonization.util.ByteArrayImageInputStream;
import lombok.Getter;
import lombok.ToString;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ss {

    private static final int TRANSPARENT_PIXEL_INDEX = 0xFD;

    private final Madspack madspack;

    public Ss(Madspack madspack) {
        this.madspack = madspack;
    }

    public List<Sprite> getSprites() throws IOException {
        Header header = readHeader(madspack.getSection(0));
        List<Sprite> sprites = getSpriteHeaders(header);
        Color[] palette = getPalette(header.pflag);

        ImageInputStream stream = new ByteArrayImageInputStream(madspack.getSections().get(3).getData());
        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

        for (Sprite sprite : sprites) {
            sprite.indexedImage = readSprite(sprite, stream);
            sprite.image = indexedToImage(sprite, sprite.indexedImage, palette);
        }
        return sprites;
    }

    public List<BufferedImage> getImages() throws IOException {
        return getSprites().stream().map(Sprite::getImage).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private BufferedImage indexedToImage(Sprite sprite, byte[] indexedImage, Color[] palette) {
        BufferedImage image = new BufferedImage(sprite.width, sprite.height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorIndex = (indexedImage[y * sprite.getWidth() + x]) & 0xff;
                if (colorIndex != TRANSPARENT_PIXEL_INDEX) {
                    image.setRGB(x, y, palette[colorIndex].getRGB());
                }
            }

        }

        return image;
    }

    /**
     * ti -- sprite header
     * rdata -- file-like object
     * pal -- array of (R,G,B)
     * mode
     * 0 -- data isn't compressed
     * 1 -- data is fab compressed
     * <p>
     * <p>
     * Tiles are compressed with linemode|command encoding. Colors are stored
     * in indexed mode. Each pixel line begins with linemode.
     * <p>
     * len -- length
     * col -- color index
     * lm -- linemode
     * cm -- command
     * <p>
     * Linemodes/commands:
     * <pre>
     * lm cm             effect
     * ------------------------------------------------------------------------
     * FF                fill rest of the line with bg color, read lm
     *
     * FE                enter FE mode (pixel) - usual mode
     *     FF            finish line
     *     FE len col    produce len * [col] pixels
     *     col           produce [col] pixel
     *
     * FD                enter FD mode (multipixel) - used to fill entire line with one color
     *     FF            finish line
     *     len col       produce len * [col] pixels
     *
     * FC                stop (end of image)
     *
     * Notes marfsama:
     *
     * Each line starts with the line mode (only 0xFC - 0xFF allowed). The line contains pixel commands until the line
     * is terminated with 0xff. Each line needs to be terminated, the line is *not* terminated automatically once x
     * reaches width.
     *  0xFF - terminates the line
     *  0xFE - in single pixel mode: start run length, in multi pixel mode this is already the length of the run length.
     *  everything other - either part of the command or a single pixel color index.
     * </pre>
     */
    private byte[] readSprite(Sprite sprite, ImageInputStream stream) throws IOException {
        stream.seek(sprite.start_offset);

        if (sprite.width < 1 || sprite.height < 1) {
            return new byte[1];
        }

        byte[] indexedImage = new byte[sprite.width * sprite.height];
        Arrays.fill(indexedImage, (byte) TRANSPARENT_PIXEL_INDEX);


        int x = 0;
        int y = 0;
        while (true) {
            // read mode for this line
            int lineMode = stream.read();
            if (lineMode == 0xFC) {
                // finish image
                break;
            }
            if (lineMode == 0xFF) {
                // skip line
                y++;
                continue;
            }
            if (lineMode < 0xfc) {
                throw new IllegalStateException("illegal line mode at offset %d: 0x%02x".formatted(stream.getStreamPosition(), lineMode));
            }

            // read all commands of the line
            while (true) {
                int command = stream.read();
                if (command == 0xff) {
                    // finish line
                    y++;
                    x = 0;
                    break;
                }
                // pixel mode
                if (lineMode == 0xFE) {
                    // run length?
                    if (command == 0xFE) {
                        int count = stream.read();
                        int color = stream.read();
                        fill(indexedImage, x, y, count, color, sprite);
                        x += count;
                    } else {
                        // single pixel (skip transparant pixels)
                        indexedImage[y * sprite.width + x] = (byte) command;
                        x++;
                    }
                } else {
                    // multipixel mode, always runlength encoded
                    int count = command;
                    int color = stream.read();
                    fill(indexedImage, x, y, count, color, sprite);
                    x += count;
                }

            }
        }


        return indexedImage;
    }

    private void fill(BufferedImage img, int x, int y, int count, int color, Color[] palette) {
        // skip transparent pixels
        for (int i = 0; i < count; i++) {
            img.setRGB(x + i, y, palette[color].getRGB());
        }
    }

    private void fill(byte[] img, int x, int y, int count, int color, Sprite sprite) {
        // skip transparent pixels
        if (color != TRANSPARENT_PIXEL_INDEX) {
            for (int i = 0; i < count; i++) {
                img[y * sprite.width + x + i] = (byte) color;
            }
        }
    }

    private Color[] getPalette(int pflag) throws IOException {
        byte[] colorData;
        // if the pik has a palette, return it
        if (pflag == 1) {
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


    private List<Sprite> getSpriteHeaders(Header header) throws IOException {
        ImageInputStream stream = new ByteArrayImageInputStream(madspack.getSections().get(1).getData());
        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        List<Sprite> sprites = new ArrayList<>();

        for (int i = 0; i < header.nsprites; i++) {
            Sprite sprite = new Sprite();
            sprite.start_offset = stream.readInt();
            sprite.length = stream.readInt();
            sprite.width_padded = stream.readUnsignedShort();
            sprite.height_padded = stream.readUnsignedShort();
            sprite.width = stream.readUnsignedShort();
            sprite.height = stream.readUnsignedShort();
            sprites.add(sprite);
        }
        return sprites;
    }

    private Header readHeader(Section section) throws IOException {
        // size: 152 (98)
        ImageInputStream stream = new ByteArrayImageInputStream(section.getData());
        stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);


        Header h = new Header();
        h.mode = stream.read();
        h.unk1 = stream.read();
        h.type1 = stream.readUnsignedShort();
        h.type2 = stream.readUnsignedShort();

        h.unk2a = new int[3];
        for (int i = 0; i < 3; i++) {
            h.unk2a[i] = stream.readUnsignedShort();
        }
        h.pflag = stream.read();
        h.unk2b = new byte[25];
        for (int i = 0; i < h.unk2b.length; i++) {
            h.unk2b[i] = stream.readByte();
        }

        h.nsprites = stream.readUnsignedShort();
        h.unk3 = new byte[100];
        for (int i = 0; i < h.unk3.length; i++) {
            h.unk3[i] = stream.readByte();
        }
        h.field_0x8c = stream.readUnsignedShort();
        h.field_0x8e = stream.readUnsignedShort();
        h.field_0x90 = stream.readUnsignedShort();
        h.field_0x92 = stream.readUnsignedShort();
        h.data_size = stream.readInt();
        return h;
    }

    @ToString
    @Getter
    public static class Sprite {
        /**
         * 0x00 - dword
         */
        int start_offset;
        /**
         * 0x04 - dword
         */
        int length;
        /**
         * 0x08 - word
         */
        int width_padded;
        /**
         * 0x0a - word
         */
        int height_padded;
        /**
         * 0x0c - word
         */
        int width;
        /**
         * 0x0e - word
         */
        int height;
        byte[] indexedImage;
        BufferedImage image;

    }

    @ToString
    private static class Header {
        int mode;
        int unk1;
        int type1;
        int type2;
        int[] unk2a;
        int pflag;
        byte[] unk2b;
        int nsprites;
        byte[] unk3;
        public int field_0x8c;
        public int field_0x8e;
        public int field_0x90;
        public int field_0x92;
        int data_size;

        @Override
        public String toString() {
            return "Header{" +
                    "mode=" + mode +
                    "\n unk1=" + unk1 +
                    "\n type1=" + type1 +
                    "\n type2=" + type2 +
                    "\n unk2a=" + Arrays.toString(unk2a) +
                    "\n pflag=" + pflag +
                    "\n unk2b=" + Arrays.toString(unk2b) +
                    "\n nsprites=" + nsprites +
                    "\n unk3=" + Arrays.toString(unk3) +
                    "\n field_0x8c=0x" + Integer.toHexString(field_0x8c) +
                    "\n field_0x8e=0x" + Integer.toHexString(field_0x8e) +
                    "\n field_0x90=" + field_0x90 +
                    "\n field_0x92=" + field_0x92 +
                    "\n data_size=" + data_size +
                    '}';
        }
    }

    /**
     * Places the images in a 16x? grid.
     */
    private static BufferedImage toGrid(List<BufferedImage> images) {
        // calculate size of final image
        int[] columnWidth = new int[16];
        int rows = (images.size() + 15) / 16;
        int[] rowHeights = new int[rows];
        for (int i = 0; i < images.size(); i++) {
            BufferedImage image = images.get(i);
            int row = i / 16;
            int column = i % 16;
            columnWidth[column] = Math.max(columnWidth[column], image.getWidth());
            rowHeights[row] = Math.max(rowHeights[row], image.getHeight());
        }

        // 2 pixel spacing
        int spacing = 2;
        // fontsize is the size of the lowest row, but at least 10 pixel
        int fontSize = Math.max(10, IntStream.of(rowHeights).min().getAsInt());

        Font font = new Font("Helvetica", Font.PLAIN, fontSize);
        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(font);

        // get max width of a single hex letter
        int maxSingleLetterWidth = "01234567890ABCDEF".chars().map(i -> fm.charWidth((char) i)).max().getAsInt();
        // get width the text with the max number of rows
        int maxRowsTextWidth = Integer.toHexString(rows * 16).length() * maxSingleLetterWidth;


        int width = IntStream.of(columnWidth).sum() + 16 * spacing + maxRowsTextWidth;
        int height = IntStream.of(rowHeights).sum() + rows * spacing + fontSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(font);
        g.setColor(Color.BLACK);
        int y = fontSize;
        for (int row = 0; row < rows; row++) {
            int x = maxRowsTextWidth;
            g.drawString(Integer.toHexString(row * 16), 0, y + fontSize);
            for (int col = 0; col < 16; col++) {
                if (row == 0) {
                    g.drawString(Integer.toHexString(col), x, fontSize);
                }
                int tileId = row * 16 + col;
                if (tileId < images.size()) {
                    BufferedImage tile = images.get(tileId);
                    g.drawImage(tile, x, y, null);
                    g.drawRect(x - spacing, y - spacing, image.getWidth() + spacing * 2, image.getHeight() * 2);
                }
                x += columnWidth[col] + spacing;
            }
            y += rowHeights[row] + spacing;
        }

        return image;
    }


    public static void main(String[] args) throws IOException {
        Path path = Path.of("src/main/resources/ICONS.SS");
        String filename = path.getFileName().toString().toLowerCase().replace(".ss", "");
        System.out.println(filename);
        try (FileImageInputStream stream = new FileImageInputStream(path.toFile())) {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            Madspack madspack = new Madspack(stream);
            madspack.read();
            Ss ss = new Ss(madspack);
            List<BufferedImage> images = ss.getImages();
            Path destinationFolder = Path.of(filename);
            Files.createDirectories(destinationFolder);
            for (int i = 0; i < images.size(); i++) {
                ImageIO.write(images.get(i), "png", destinationFolder.resolve("%s_%02x.png".formatted(filename, i)).toFile());
            }
            ImageIO.write(toGrid(images), "png", destinationFolder.resolve("%s.png".formatted(filename)).toFile());

        } catch (IOException e) {
            throw new IllegalStateException(path.toString(), e);
        }

    }

}
