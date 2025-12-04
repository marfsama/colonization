package com.marf.colonization.rebuild;

import com.marf.colonization.decompile.cmodules.Module1a;
import com.marf.colonization.decompile.cmodules.Sprite;
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

    public Canvas(Resources resources) {
        System.out.println("create canvas with "+resources);
        this.resources = resources;
        this.backscreen = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
        this.scratch = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
    }

    public void clear(BufferedImage screen) {
        fillRect(screen, 0, 0, 320, 200, 1);
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

    /** @see com.marf.colonization.decompile.cmodules.Code1b#FUN_1b8e_000c_draw_sprite */
    public void drawSprite(BufferedImage destination, BufferedImage source, int width, int height, int destinationX, int destinationY, int sourceX, int sourceY) {
        Graphics2D graphics = destination.createGraphics();
        graphics.drawImage(source, destinationX, destinationY, destinationX + width, destinationY + height, sourceX, sourceY, sourceX + width, sourceY + height, null);
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
        if (x >= backscreen.getWidth()) {
            System.out.println("x > width-1: "+x+" > "+backscreen.getWidth());
            return;
        }
        if (y >= backscreen.getHeight()) {
            System.out.println("y > height-1: "+y+" > "+backscreen.getHeight());
            return;
        }
        backscreen.setRGB(x,y, resources.getPalette()[color].getRGB());
    }
}
