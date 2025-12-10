package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code1b.*;
import static com.marf.colonization.decompile.cmodules.Data.*;

public class Module14_5c {

    public static void FUN_7f61_012a_module_14_5c_flip_viewport_backscreen() {
        FUN_1b8e_000c_draw_sprite(DAT_2638_backscreen, DAT_2640_2nd_backscreen, DAT_84f6_viewport_width, DAT_84f8_viewport_height, 8,0,0,0);
    }

    /**
     * Purpose: This function iterates through viewport tiles in a pseudo-random order, calling a rectangle flip
     * function for each tile to create a smooth animation effect, with optional frame timing control.
     * <p>
     * Algorithm Overview:
     * - Initialize PRNG with seed value 1
     * - Loop through all possible 65536 iterations (16-bit counter)
     * - Generate next pseudo-random tile index using LCG:
     *     * Right shift current state
     *     * If LSB was 1, XOR with 0xB400 (taps for maximal period)
     * - Calculate tile coordinates from index:
     *     * tile_x = index % x_tiles
     *     * tile_y = index / x_tiles
     * - Convert to screen coordinates and call rectangle flip function
     * - Optional frame pacing for larger viewports (> 180 tiles)
     *
     */
    public static void FUN_7f61_0160_module_14_5c_flip_all_tiles_in_random_order() {
        // PRNG state (16-bit LFSR)
        short prngState = 1;
        short iteration = 0;
        short[] timerHighLow = new short[2]; // High word, low word

        // Calculate total tiles in viewport
        int totalTiles = DAT_84ec_number_of_y_tiles_in_viewport *
                DAT_84ea_number_of_x_tiles_in_viewport;

        // Loop through all possible 16-bit iterations
        while (true) {
            // Update PRNG using LFSR algorithm
            if ((prngState & 1) != 0) {
                prngState = (short)((prngState >>> 1) ^ 0xB400);
            } else {
                prngState = (short)(prngState >>> 1);
            }

            // Calculate tile index (0-based)
            short tileIndex = (short)(prngState - 1);

            // Check if tile index is within viewport bounds
            if (tileIndex < totalTiles) {
                // Calculate tile coordinates from index
                int tileX = tileIndex % DAT_84ea_number_of_x_tiles_in_viewport;
                int tileY = tileIndex / DAT_84ea_number_of_x_tiles_in_viewport;

                // Convert to screen coordinates
                int screenX = (tileX + DAT_82e0_viewport_x_offset) *
                        DAT_82de_tile_pixel_size;
                int screenY = (tileY + DAT_82e4_viewport_y_offset) *
                        DAT_82de_tile_pixel_size + 8;

                // Call rectangle flip function for this tile
                FUN_1b54_0040_flip_backscreen_rectangle(
                        screenX,                   // x position
                        screenY,                    // y position
                        screenX,                   // x position
                        screenY,                    // y position
                        DAT_82de_tile_pixel_size,  // width
                        DAT_82de_tile_pixel_size  // height
                );

                // Frame pacing for larger viewports
                if (totalTiles > 180) {
                    // Wait if timer indicates we're running too fast
                    if (timerHighLow[0] != 0 || timerHighLow[1] != 0) {
                        long currentTime = FUN_1bf0_0028_get_mocked_timer_ticks();
                        long previousTime = ((long)timerHighLow[0] << 16) | (timerHighLow[1] & 0xFFFF);

                        // Wait until at least 1 tick has passed
                        while (currentTime - previousTime < 1) {
                            currentTime = FUN_1bf0_0028_get_mocked_timer_ticks();
                        }
                    }

                    // Update timer for next frame pacing check
                    long currentTime = FUN_1bf0_0028_get_mocked_timer_ticks();
                    timerHighLow[0] = (short)(currentTime >>> 16);
                    timerHighLow[1] = (short)(currentTime & 0xFFFF);
                }
            }

            // Increment iteration counter (16-bit wrap-around)
            iteration++;
            if (iteration == 0) {
                break; // Wrapped around to 0, exit loop
            }
        }
    }

    public static void FUN_7f61_0214_module_14_flip_viewport_rectangle() {
        FUN_1b54_0040_flip_backscreen_rectangle(0, 8, 0, 8, DAT_84f6_viewport_width, DAT_84f8_viewport_height);
    }

}
