package com.marf.colonization.rebuild;

import lombok.Data;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Getter
public class Canvas {
    private final Resources resources;
    private BufferedImage backscreen;
    private BufferedImage frontscreen;

    public Canvas(Resources resources) {
        System.out.println("create canvas with "+resources);
        this.resources = resources;
        this.backscreen = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
    }

    public void clear() {
        fillRect(0, 0, 320, 200, 1);
    }

    /**
     * FUN_1b83_0000_fill_rectangle
     */
    public void fillRect(int x, int y, int width, int height, int color) {
        Graphics graphics = backscreen.getGraphics();
        graphics.setColor(resources.getPalette()[color]);
        graphics.fillRect(x, y, width, height);
        graphics.dispose();
    }

    /**
     * FUN_1bae_0008_draw_rectangle
     */
    public void drawRect(int x1, int y1, int x2, int y2, int color) {
        Graphics graphics = backscreen.getGraphics();
        graphics.setColor(resources.getPalette()[color]);
        graphics.drawRect(x1, y1, x2-x1, y2-y1);
        graphics.dispose();
    }

    /**
     * FUN_1bd9_0006_draw_sprite_sheet_entry
     */
    public void drawSpriteSheetEntry(List<BufferedImage> spriteSheet, int x, int y, int width, int height, int spriteIndex, int maybeFlags) {
        Graphics graphics = backscreen.getGraphics();
        graphics.setClip(x, y, width, height);

        BufferedImage sprite = spriteSheet.get(spriteIndex);
        int cols = (width + sprite.getWidth() - 1) / sprite.getWidth();
        int rows = (height + sprite.getHeight() - 1) / sprite.getHeight();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                graphics.drawImage(sprite, x + col * sprite.getWidth(), y + row * sprite.getHeight(), null);
            }
        }

        graphics.dispose();

    }
}
