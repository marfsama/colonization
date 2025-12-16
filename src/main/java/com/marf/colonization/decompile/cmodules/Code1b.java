package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;

/**
 * Base Description: Timer stuff
 * Segment: 0x1bf0
 * Status:
 * - ??
 */
public class Code1b {
    /**
     * fills the rectangle from 0,0 - width,height
     */
    public static void FUN_1b83_0000_clear_image(Sprite destination, int color) {

    }

    /**
     * Draws a rectangular region sourceX,sourceY - width, height to the destination x,y
     */
    public static void FUN_1b8e_000c_draw_sprite(Sprite destination, Sprite source, int width, int height, int destinationX, int destinationY, int sourceX, int sourceY) {

    }

    /**
     * params:
     * - param_1_color
     * - param_2_y2
     * - param_3_destination
     * - AX - x1
     * - DX - y1
     * - BX - x2
     */
    public static void FUN_1bae_0008_draw_rectangle(Sprite destination, int color, int x1, int y1, int x2, int y2) {
        // todo
    }

    /**
     * BX = dest_x
     * DX = src_y
     * AX = src_x
     * height
     * width
     * dest_y
     */
    public static void FUN_1b54_0040_flip_backscreen_rectangle(int src_x, int src_y, int dest_x, int dest_y, int width, int height) {
        /*
        FUN_1a3c_02d4_init_something();
        iVar1 = FUN_1a3c_05c4();
        FUN_1cf5_0022_draw_sprite_to_vga_memory_0xA000(Data.DAT_2638_backscreen, src_x, src_y, dest_x, dest_y, width, height);
        if (iVar1 != 0) {
            FUN_1a3c_0703(var1);
        }
        FUN_1a3c_02e6();
         */
    }

    /**
     * param_1_color
     * param_2_height
     * param_3_destination
     * bx - width
     * dx - y
     * ax - x
     */
    public static void FUN_1b83_0000_fill_rectangle(int x, int y, int width, int height, Sprite destination, int color) {

    }

    /**
     * Tiles a sprite over a bigger area.
     * The first row and column is offset by offset_x and offset_y
     */
    public static void FUN_1bd9_0006_draw_sprite_tiled(Sprite destination, Sprite source, int x, int y, int width, int height, int offset_x, int offset_y) {
        if ((source.height == 0) || (source.width == 0)) {
            return;
        }
        int tileOffsetX = Math.abs(x - offset_x) % source.height;
        int tileOffsetY = Math.abs(y - offset_y) % source.width;
        int maxX = x + width;
        int maxY = y + height;

        for (int currentY = y; currentY < maxY; currentY += (currentY - tileOffsetY) + source.width) {
            for (int currentX = x; currentX < maxX; currentX += (currentX - tileOffsetX) + source.height) {
                // Calculate source position for this tile
                int sourceX = currentX - tileOffsetX;
                int sourceWidth = source.width - tileOffsetX;

                // Calculate how much of this tile we can draw
                int tileWidth = Math.min(sourceWidth, maxX - currentX);

                // Calculate destination Y position and tile height
                int sourceY = currentY - tileOffsetY;
                int sourceHeight = source.height - tileOffsetY;
                int tileHeight = Math.min(sourceHeight, maxY - currentY);

                FUN_1b8e_000c_draw_sprite(
                        destination,
                        source,
                        tileWidth,
                        tileHeight,
                        currentX,
                        currentY,
                        sourceX,
                        sourceY
                );
                tileOffsetX = 0;
            }
            tileOffsetY = 0;
        }

        return;
    }


    public static int FUN_1bf0_000c_get_clock_ticks() {
        return 0;
    }

    public static int FUN_1bf0_0028_get_mocked_timer_ticks() {
        return 0;
    }

    /**
     * This function renders text using a custom bitmap font format that appears to use some form of compression
     * or packing for the glyph data.
     * <p>
     * Additional parameter:
     * <p>
     * BX - ptr to backscreen sprite
     * AX - x pos
     * DX - y pos
     */
    public static int FUN_1bf6_0002_blit_text_to_bitmap(Sprite destination, Font font, String string, int x, int y, int additionalSpacing) {
        int local_5e_y = 0;
        int local_66 = 0;

        // copy string to local buffer (size: 0x50 (80) bytes)
        String text = string;

        if (y < 0) {
            local_5e_y = -y;
            y = 0;
        }

        // clip upper edge
        int fontHeight = font.height & 0xFF;
        int visibleHeight = fontHeight - local_5e_y;  // Clip top
        if (visibleHeight < 0) {
            visibleHeight = 0;
        }

        int local_60_visibleFontHeight = fontHeight;
        // clip lower edge
        int destinationHeight = destination.height;
        if ((y + visibleHeight - 1) > destinationHeight) {
            local_60_visibleFontHeight = (y + visibleHeight - 1) - destinationHeight;
            if (local_60_visibleFontHeight > fontHeight) {
                local_60_visibleFontHeight = fontHeight;
            }
            visibleHeight = fontHeight - local_60_visibleFontHeight;
        }


        if (local_60_visibleFontHeight < 0) {
            return x;
        }

        // Create color lookup (2-bit pixels to actual colors)
        int[] colorLookup = DAT_262c_text_blit_colors;

        // 5. Calculate starting position in buffer
        int currentX = x;
        int currentY = y;

        // 6. Render each character
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int charIndex = ch & 0xFF;  // ASCII/8-bit chars

            // Get glyph width
            int glyphWidth = font.glyphWidths[charIndex] & 0xFF;
            if (glyphWidth == 0) {
                continue;
            }

            // Check if glyph fits
            if (currentX + glyphWidth > destination.width) {
                break;  // Horizontal clipping
            }

            // Get glyph data offset (offset 0x82 has word offsets)
            int glyphDataOffset = font.glyphOffsets[charIndex];

            // Adjust for vertical clipping
            if (local_5e_y > 0) {
                int bytesPerRow = (glyphWidth + 3) / 4;  // 4 pixels per byte, +3 to round up
                glyphDataOffset += local_5e_y * bytesPerRow;
            }

            // Render the glyph
            renderGlyph(destination, glyphDataOffset,
                    currentX, currentY,
                    font, glyphDataOffset,
                    glyphWidth, visibleHeight,
                    colorLookup);

            // Advance position
            currentX += glyphWidth + additionalSpacing;
        }

        // Return final x position (for chaining/measurement)
        return currentX;
    }

    private static void renderGlyph(Sprite destination, int dataOffset, int x, int y, Font font, int glyphDataOffset, int glyphWidth, int height, int[] colorLookup) {

        int destIndex = y * destination.width + x;
        int srcOffset = dataOffset;

        for (int row = 0; row < height; row++) {
            int col = 0;
            int srcByte = 0;
            int bitShift = 6;  // Start with bits 7-6

            while (col < glyphWidth) {
                if (bitShift == 6) {
                    // Need new source byte
                    srcByte = font.glyphData[srcOffset++] & 0xFF;
                }

                // Extract 2-bit pixel value
                int pixelIndex = (srcByte >> bitShift) & 0x03;
                int color = colorLookup[pixelIndex];

                // Draw pixel (skip if transparent/0xFF)
                if (color != 0xFF) {
                    destination.data[destIndex + col] = (byte) color;
                }

                col++;
                bitShift -= 2;
                if (bitShift < 0) {
                    bitShift = 6;
                }
            }

            destIndex += destination.width;  // Next line
        }
    }
}
