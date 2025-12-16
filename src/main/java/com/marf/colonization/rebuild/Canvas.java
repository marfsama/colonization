package com.marf.colonization.rebuild;

import com.marf.colonization.mpskit.Ff;
import com.marf.colonization.mpskit.Ss;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Getter
public class Canvas {
    private final Resources resources;
    private BufferedImage backscreen;
    // scratch screen
    private BufferedImage scratch;

    private int[] textColors = new int[] {0xf,0xf,0xf,0xf};

    public Canvas(Resources resources) {
        System.out.println("create canvas with "+resources);
        this.resources = resources;
        this.backscreen = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
        this.scratch = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
    }

    public void clear(BufferedImage screen) {
        fillRect(screen, 0, 0, 320, 200, 0);
    }

    /**
     * FUN_1b83_0000_fill_rectangle
     */
    public void fillRect(BufferedImage screen, int x, int y, int width, int height, int color) {
        Graphics graphics = screen.getGraphics();
        graphics.setColor(resources.getPalette()[color]);
        graphics.fillRect(x, y, width, height);
        graphics.dispose();
    }

    /**
     * FUN_1bae_0008_draw_rectangle
     */
    public void drawRect(BufferedImage screen, int x1, int y1, int x2, int y2, int color) {
        Graphics graphics = screen.getGraphics();
        graphics.setColor(resources.getPalette()[color]);
        graphics.drawRect(x1, y1, x2-x1, y2-y1);
        graphics.dispose();
    }

    /** @see com.marf.colonization.decompile.cmodules.Code1c#FUN_1c1b_0000_draw_compressed_sprite */
    public void drawSpriteSheetSprite(BufferedImage destination, List<Ss.Sprite> spriteSheet, int x, int y, int spriteIndex) {
        Graphics2D graphics = destination.createGraphics();
        graphics.drawImage(spriteSheet.get(spriteIndex-1).getImage(), x,y,null);
        graphics.dispose();
    }

    /** @see com.marf.colonization.decompile.cmodules.Code11#FUN_1101_01dc_blit_sprite_sheet_sprite_only_over_black_pixels */
    public void drawSpriteSheetSpriteOverBlackPixels(BufferedImage destination, List<Ss.Sprite> spriteSheet, int x, int y, int spriteIndex) {
        // overwrites only the pixels which are black
        BufferedImage source = spriteSheet.get(spriteIndex).getImage();
        for (int y1 = 0; y1 < source.getHeight(); y1++) {
            for (int x1 = 0; x1 < source.getWidth(); x1++) {
                if (x1 + x < destination.getWidth() && y1 + y < destination.getHeight()) {
                    // get color of destination (mask out alpha)
                    int destinationColor = destination.getRGB(x1 + x, y1 + y) & 0xffffff;
                    if (destinationColor == 0) {
                        destination.setRGB(x1+x, y1+y, source.getRGB(x1, y1));
                    }
                }
            }
        }


    }

    /** @see com.marf.colonization.decompile.cmodules.Code1b#FUN_1b8e_000c_draw_sprite */
    public void drawSprite(BufferedImage destination, BufferedImage source, int width, int height, int destinationX, int destinationY, int sourceX, int sourceY) {
        Graphics2D graphics = destination.createGraphics();
        graphics.drawImage(source, destinationX, destinationY, destinationX + width, destinationY + height, sourceX, sourceY, sourceX + width, sourceY + height, null);
        graphics.dispose();
    }

    /**
     * Draws the Sprite at x,y, where the x coordinate is centered. spriteIndex < 0 means the sprite is mirrored along the vertical axis.
     * @see com.marf.colonization.decompile.cmodules.Code1c#FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed
     */
    public void drawSpriteFlippableCenteredZoomed(BufferedImage destination, int x, int y, int zoomLevelPercent, int spriteIndex, List<Ss.Sprite> spriteSheet) {
        if (zoomLevelPercent != 100) {
            throw new IllegalStateException("scaling not implemented yet");
        }
        Graphics2D graphics = destination.createGraphics();
        BufferedImage sprite = spriteSheet.get(spriteIndex - 1).getImage();
        graphics.drawImage(sprite, x - sprite.getWidth() / 2, y-16, null);
        graphics.dispose();
    }


    /**
     * Tiles a sprite over a bigger area.
     * The first row and column is offset by offsetX and offsetY
     * @see com.marf.colonization.decompile.cmodules.Code1b#FUN_1bd9_0006_draw_sprite_tiled
     */
    public void drawSpriteTiled(BufferedImage screen, BufferedImage source, int x, int y, int width, int height, int offsetX, int offsetY) {
        Graphics graphics = screen.getGraphics();
        graphics.setClip(x, y, width, height);

        // note: this normally calls #drawSprite() (Code1b#FUN_1b8e_000c_draw_sprite) for a single tile. This is simplified here
        // as we can use the clip rectangle of the graphics
        BufferedImage sprite = source;
        // note: add one to accomodate for offsets
        int cols = (width + sprite.getWidth() - 1) / sprite.getWidth() + (offsetX != 0 ? 1 : 0);
        int rows = (height + sprite.getHeight() - 1) / sprite.getHeight() + (offsetY != 0 ? 1 : 0);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                graphics.drawImage(sprite, x + col * sprite.getWidth() + offsetX, y + row * sprite.getHeight() + offsetY, null);
            }
        }

        graphics.dispose();

    }

    public void setPixel(BufferedImage screen, int x, int y, int color) {
        if (x >= screen.getWidth()) {
            System.out.println("x > width-1: "+x+" > "+screen.getWidth());
            return;
        }
        if (y >= screen.getHeight()) {
            System.out.println("y > height-1: "+y+" > "+screen.getHeight());
            return;
        }
        screen.setRGB(x,y, resources.getPalette()[color].getRGB());
    }

    public void drawTextBig(BufferedImage destination, int x, int y, int color, String text) {
        drawText(destination,x,y,color,14.0f,text);
    }
    public void drawTextSmall(BufferedImage destination, int x, int y, int color, String text) {
        drawText(destination,x,y,color,7.0f,text);
    }

    public void drawText(BufferedImage destination, int x, int y, int color, float size, String text) {
        Graphics2D graphic = destination.createGraphics();

        Font font = graphic.getFont();
        font = font.deriveFont(size);
        graphic.setFont(font);

        int height = font.getSize();

        graphic.setColor(resources.getPalette()[color]);
        graphic.drawString(text, x, y+height);

        graphic.dispose();
    }

    /** @see com.marf.colonization.decompile.cmodules.Code1c#FUN_1c0d_0000_set_text_blit_colors */
    public void setTextColors(int a, int b, int c, int d) {
        textColors[0] = a;
        textColors[1] = b;
        textColors[2] = c;
        textColors[3] = d;
    }

    /** @see com.marf.colonization.decompile.cmodules.Code1b#FUN_1bf6_0002_blit_text_to_bitmap */
    public void drawString(BufferedImage destination, Ff.Font font, String text, int x, int y, int letterSpacing) {
        int currentX = x;
        for (int charIndex : text.toCharArray()) {
            if (charIndex < 128) {
                Ff.FontCharacter character = font.getChars().get(charIndex-1);
                for (int y1 = 0; y1 < font.getHeight(); y1++) {
                    for (int x1 = 0; x1 < character.getWidth(); x1++) {
                        int pixel = character.getPixel(x1, y1);
                        int color = textColors[pixel];
                        if (color != 0xff) {
                            int px = x1 + currentX;
                            int py = y1 + y + font.getHeight();
                            if (px >= 0 && py >= 0 && px < destination.getWidth() && py < destination.getHeight()) {
                                setPixel(destination, px, py, color);
                            }
                        }
                    }
                }
                currentX += character.getWidth() + letterSpacing;
            }
        }

    }
}
