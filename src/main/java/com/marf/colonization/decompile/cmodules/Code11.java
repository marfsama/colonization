package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code14.*;
import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Code1b.*;
import static com.marf.colonization.decompile.cmodules.Code1c.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Modulea.*;

public class Code11 {

    public static void FUN_1101_0050_blit_terrain_tile(Module1a.SpriteSheetSomeStructure terrainSprites, int spriteIndex, Sprite destination, int x, int y) {

    }

    public static void FUN_1101_00b4_blit_terrain_sprite(Module1a.SpriteSheetSomeStructure terrainSprites, int neighboursBitmap, Sprite destination, int x, int y) {

    }

    public static void FUN_1101_0126(Module1a.SpriteSheetSomeStructure terrainSprites, int neighboursBitmap, Sprite destination, int x, int y, int zoomLevel) {

    }


    public static void FUN_1101_01dc_blit_sprite_sheet_sprite_only_over_black_pixels(Module1a.SpriteSheetSomeStructure terrainSptites, int neighboursBitmap, Sprite destination, int x, int y, int zoomLevel) {

    }

    /**
     * param:
     * AX - profession
     * return
     * AX - profession icon for the unit
     */
    public static int FUN_112b_0002_get_profession_icon_index(int profession) {
        return switch (profession) {
            case 0x13 -> 0x65;   // 0x13 -> 0x65
            case 0x14 -> 0x3B;   // 0x14 -> 0x3B
            case 0x15 -> 0x3C;   // 0x15 -> 0x3C
            case 0x16 -> 0x3D;   // 0x16 -> 0x3D
            case 0x17 -> profession + 0x52; // 0x17 -> default
            case 0x18 -> 0x3E;   // 0x18 -> 0x3E
            case 0x19 -> 0x6B;   // 0x19 -> 0x6B
            case 0x1a -> 0x6C;   // 0x1A -> 0x6C
            case 0x1b -> 0x43;   // 0x1B -> 0x43
            case 0x1c -> 0x65;   // 0x1C -> 0x65 (same as 0x13)
            default -> profession + 0x52;
        };
    }

    /**
     *
     */
    public static int FUN_112b_0060_get_unit_icon(int unitIndex) {
        Unit unit = DAT_30fc_units_list[unitIndex];

        int icon = DAT_51e8_unit_config_array[unit.type].icon;

        // colonist
        if (unit.type== 0) {
            icon = FUN_112b_0002_get_profession_icon_index(unit.profession);
        }

        // hardy pioneer
        if (unit.type == 2 && unit.profession == 0x14 ) {
            icon = 0x4a;
        }

        // veteran soldier
        if (unit.type == 1 && unit.profession == 0x15 ) {
            icon = 0x4b;
        }

        // veteran dragoon
        if (unit.type == 4 && unit.profession == 0x15 ) {
            icon = 0x4d;
        }

        // seasoned scout
        if (unit.type == 5 && unit.profession == 0x16 ) {
            icon = 0x4c;
        }

        // jesuit missionar
        if (unit.type == 3 && unit.profession == 0x18 ) {
            icon = 0x4e;
        }

        // damaged artillery
        if (unit.type == 11 && ((unit.flags_damaged & 0x80) != 0) ) {
            icon = 0x42;
        }
        return icon;
    }

    /**
     * parms:
     *   AX - unit index
     *   DX - flag - unpack transports
     *   BX - ptr to store the final unit id (output parameter)
     * return:
     *   AX - unit icon
     */
    public static int FUN_112b_010e_get_unit_icon_topmost(int unitIndex, boolean showTopmostInStack, int out_unitId) {
        // notes: why are only transports checked for "transportchain2"?

        int topmost = unitIndex;
        if (showTopmostInStack) {
            // first unpack transports
            int head = FUN_1415_000a_get_transport_chain_head(unitIndex);
            if (head >= 0) {
                Unit unit = DAT_30fc_units_list[head];

                // it seems that this loops searches the last unit from the chain/stack
                do {
                    // is the transport a ship?
                    // 0xd = caravel, 0x12 = man-o-war
                    if (unit.type >= 0xd && unit.type <= 0x12 ) {
                        head = FUN_1415_0052_get_transportchain2(head);
                        if (head >= 0) {
                            topmost = head;
                        }
                    }
                } while (head >= 0);
            }

        }

        // note: out_unitId is an out parameter
        out_unitId = topmost;
        return FUN_112b_0060_get_unit_icon(topmost);

    }

    public static void FUN_112b_015c_draw_icon_maybe(int drawFlags) {
        if ((drawFlags & 1) != 0) {
            // FUN_1cbc_000a(-((param_1_flag_skip_first_stuff & 4) != 0) & 0x5f,in_BX,DAT_082e_icons_sprite_sheet);
        }
        if ((drawFlags & 2) != 0) {
            // FUN_1c1b_0000_draw_compressed_sprite(DAT_2638_backscreen, x,y, iconIndex ,DAT_082e_icons_sprite_sheet);
        }
    }

    /**
     * Draw the unit
     * param_1 - zoom_level_percent
     * param_2 - tile_pixel_size
     * param_3 - y
     * BX - x
     * DX - flags
     * AX - unit index
     */
    public static void FUN_112b_01ba_draw_unit(int unit_index, int flags, int screenX, int screenY, int zoom_level_percent, int tile_pixel_size) {


    }

    /**
     * Params:
     * - param_1 - zoom_level
     * - param_2 - backscreen
     * - DX - x in pixel
     * - BX - y in pixels
     * - AX - current village index
     */
    public static void FUN_112b_0790_draw_indian_village(Sprite destination, int zoom_level_percent, int x_in_pixels, int y_in_pixels, int village_index) {
        IndianVillage village = DAT_54a4_indian_village_list[village_index];
        int tribeIndex = village.nation - 4;
        Tribe tribe = DAT_2b4d_5a8e_tribes_list[tribeIndex];
        int tribeLevel = tribe.level;

        int tileSize = 16;
        if (zoom_level_percent < 100) {
            x_in_pixels -= (2 >> DAT_017a_zoom_level) & 0x3;
            tileSize = DAT_82de_tile_pixel_size;
        }

        // draw base icon
        int screen_y = y_in_pixels + tileSize - 1;
        int screen_x = x_in_pixels + tileSize >> 1;
        FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(destination, screen_x, screen_y, zoom_level_percent, Math.min(tribeLevel, 3)+0xb, DAT_082e_icons_sprite_sheet);

        int color = DAT_0838_minimap_fractions_colors_table[tribeIndex+4];
        if (zoom_level_percent == 100) {
            if (tribeLevel == 0) {
                FUN_1b83_0000_fill_rectangle(x_in_pixels+3, y_in_pixels+4, 1, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+12, y_in_pixels+4, 1, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+9, y_in_pixels+6, 1, 1, destination, color);
            } else if (tribeLevel == 1) {
                FUN_1b83_0000_fill_rectangle(x_in_pixels+4, y_in_pixels+9, 2, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+9, y_in_pixels+11, 3, 1, destination, color);
            }
            // no tribe colors for level 2 and 3
        }
        if (zoom_level_percent == 50) {
            if (tribeLevel == 0) {
                FUN_1b83_0000_fill_rectangle(x_in_pixels+2, y_in_pixels+2, 1, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+6, y_in_pixels+2, 1, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+5, y_in_pixels+3, 1, 1, destination, color);
            } else if (tribeLevel == 1) {
                FUN_1b83_0000_fill_rectangle(x_in_pixels+2, y_in_pixels+4, 1, 1, destination, color);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+5, y_in_pixels+5, 3, 1, destination, color);
            }
            // no tribe colors for level 2 and 3
        }

        // is the village a capital?
        if ((village.state & 4) != 0) {
            //  draw the capital icon
            FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(destination, screen_x, screen_y, zoom_level_percent, 0x12, DAT_082e_icons_sprite_sheet);
        }

        if (zoom_level_percent == 100) {
            screen_x = x_in_pixels + 6;

            // draw threat level
            int out_thread_level = 0;
            int aggressor = FUN_6158_03fa_module_a_maybe_get_max_european_threat(village_index, out_thread_level);

            int threatLevel = out_thread_level;
            if (aggressor >= 0) {
                int panicBackgroundColor;
                int panicColor;
                if (DAT_5338_savegame_header.maybe_current_player == aggressor) {
                    int panic = village.panic[aggressor];
                    if (panic < 0) {
                        village.panic[aggressor] = 0;
                    }
                    int panicLevel = panic >> 5;
                    if (panicLevel > 3) {
                        panicLevel = 3;
                    }
                    int tribeAggression = FUN_15cb_00da_get_tribe_aggression_for_power(tribeIndex, aggressor);
                    if (tribeAggression >= 0x4b) {
                        panicLevel = 3;
                    }

                    panicColor = switch (panicLevel) {
                        case 0 -> 0xa;
                        case 1 -> 0xb;
                        case 2 -> 0xe;
                        default -> 0xc;
                    };
                    panicBackgroundColor = 0;
                } else {
                    threatLevel = 1;
                    panicColor = DAT_0838_minimap_fractions_colors_table[aggressor];
                    // TODO: check this. the panic color cannot be the same as the background color
                    panicBackgroundColor = panicColor;
                }

                if (threatLevel > 0) {
                    screen_y += 4;
                    do {
                        // for half an aggression level show darker color
                        if (threatLevel < 3) {
                            panicColor -= 8;
                        }
                        // draw background: for the player black, for other powers the color of the power
                        FUN_1b83_0000_fill_rectangle(screen_x, screen_y, 3, 7, destination, panicBackgroundColor);
                        // long part of the exclamation mark
                        FUN_1b83_0000_fill_rectangle(screen_x+1, screen_y+1, 1, 5, destination, panicColor);
                        // dot of the exclamation mark
                        FUN_1b83_0000_fill_rectangle(screen_x+1, screen_y+4, 1, 1, destination, panicColor);

                        screen_x += 2;
                        threatLevel -= 4;
                    } while (threatLevel > 0);
                }
                screen_x += 2;
            }
            // draw mission
            if (village.mission > -1) {
                int missionPower = village.mission & 0x7;
                int upperNibble = village.mission & 0xf8;

                int missionColor = DAT_0838_minimap_fractions_colors_table[missionPower] + (upperNibble != 0 ? 0xf8 : 0);
                // draw background
                FUN_1b83_0000_fill_rectangle(screen_x+1, screen_y+5, 1, 1, destination, 0);
                // draw cross
                FUN_1b83_0000_fill_rectangle(screen_x+2, screen_y+6, 1, 4, destination, 0);

                FUN_1b83_0000_fill_rectangle(screen_x+1, screen_y+7, 3, 1, destination, 0);
            }

            if ((DAT_0884_debug_info_flags & 1) > 0) {
                int panic = village.panic[aggressor];
                // draw panic value to screen
                String panicString = String.valueOf(panic);
                int stringWidth = FUN_1c0e_000c_get_string_width_in_pixels(panicString, DAT_088e_fonttiny_address);
                FUN_1b83_0000_fill_rectangle(x_in_pixels+2, y_in_pixels+9, stringWidth+2, 0 /*DAT_088e_fonttiny_address.height*/, destination, 0);
                FUN_1c0d_0000_set_text_blit_parameters(0xf, -1, 0xf, 0xf);
                FUN_1bf6_0002_blit_text_to_bitmap(destination, DAT_088e_fonttiny_address, panicString, 0, x_in_pixels+3, y_in_pixels+3);
            }

        }

        if (zoom_level_percent <= 25) {
            // draw colored rectangles for the two smallest zoom levels
            FUN_1b83_0000_fill_rectangle(x_in_pixels, y_in_pixels, tileSize, tileSize, destination, color);
        }
    }
}
