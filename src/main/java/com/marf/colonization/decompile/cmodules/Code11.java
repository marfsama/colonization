package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code13.*;
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


    /**
     * param_1 - flags
     * AX - icon index
     * DX - x
     * BX - y
     */
    public static void FUN_112b_015c_draw_icon_maybe(int x, int y, int spriteIndex, int flags) {
        if ((flags & 1) != 0) {
            FUN_1cbc_000a_draw_sprite_silhouette(DAT_2638_backscreen, DAT_082e_icons_sprite_sheet, x,y, spriteIndex, (flags & 0x4) != 0 ? 0x5F : 0x00);
        }
        if ((flags & 2) != 0) {
            FUN_1c1b_0000_draw_compressed_sprite(DAT_2638_backscreen, x + 2, y, spriteIndex, DAT_082e_icons_sprite_sheet);
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
     * <p>
     *     Flags:
     *     0x20 - draw order letter/content for ships
     *     0x40 - maybe unpack transports/show only top most unit. I think this is used when a unit in a stack is
     *            selected or a unit should leave a shop
     *     0x80 - draws some rectangles when the transport has something (or there is a stack of units)
     */
    public static void FUN_112b_01ba_draw_unit(int unit_index, int flags, int screenX, int screenY, int zoom_level_percent, int tile_pixel_size) {
        boolean local_20_flag_privateer_maybe = false;
        flags &= 0xdf; // 1101 1111

        int local_2c_unit_id_for_icon = 0;

        int local_e_unit_icon_index = FUN_112b_010e_get_unit_icon_topmost(unit_index, (flags & 0x40) != 0, local_2c_unit_id_for_icon);

        boolean local_1e_flag_has_stuff_in_transportchain2 = false;
        if ((flags & 0x80) != 0) {
            int unitIndex = FUN_1415_000a_get_transport_chain_head(unit_index);
            int foo = FUN_1415_0052_get_transportchain2(unitIndex);
            if (foo > -1) {
                local_1e_flag_has_stuff_in_transportchain2 = true;
            } else  {
                local_1e_flag_has_stuff_in_transportchain2 = false;
            }
        }

        Unit unit = DAT_30fc_units_list[local_2c_unit_id_for_icon];
        int local_14_icon_size = 1;
        int local_c_unit_order;
        int local_3_order_letter;

        // check if unit is a ship
        // 0xd = 13 "Caravel"
        // 0x12 = 18 "Man-O-War"
        if (local_2c_unit_id_for_icon >= 0xd && local_2c_unit_id_for_icon <= 0x12) {
            switch (local_2c_unit_id_for_icon) {
                case 0xf: // 15 - "Galeon"
                case 0x10: // 16 - "Privateer"
                case 0x11: // 17 - "Frigate"
                case 0x12: // 18 - "Man-O-War",
                    local_14_icon_size = 1;
                    break;
                default:
                    local_14_icon_size = 3;
            }
        } else {
            switch (local_2c_unit_id_for_icon) {
                case 0x4:
                case 0x5:
                case 0x7:
                case 0x8:
                case 0x15:
                case 0x16:
                    local_14_icon_size = 3;
                    break;
                case 0xa:
                case 0xb:
                case 0xc:
                    local_14_icon_size = 2;
                    if (local_2c_unit_id_for_icon == 0xb && (unit.flags_damaged & 0x80) != 0) {
                        local_14_icon_size = 4;
                    }
                    break;
            }
        }


        local_c_unit_order = unit.order;
        int nation = unit.nationIndex;
        if ((nation & 0xf) < 4 ) {
            local_c_unit_order = 0;
        }
        local_3_order_letter = DAT_2b4d_5496_order_letters.charAt(local_c_unit_order);

        if (unit.type >= 0xd && unit.type <= 0x12) {
            if (nation != DAT_5338_savegame_header.maybe_player_controlled_power) {
                // display number of used cargo slots as number
                local_3_order_letter = unit.numCargo + 0x30;
                if (unit.type == 0x10 && DAT_5338_savegame_header.field_0x22_maybe_current_turn != 0) {
                    local_3_order_letter = ':';
                    local_20_flag_privateer_maybe = true;
                }
            }
        }

        // display debug stuff
        if ((nation < 4)
                && DAT_53c6_player_list[nation].control != 0
                && (DAT_5338_savegame_header.field2_0x3 & 0x20) != 0
                && (DAT_0884_debug_info_flags & 0x8) != 0) {
            local_3_order_letter = unit.field_0x7 & 0xff;
            if (local_3_order_letter > 0x80) {
                local_3_order_letter = '-';
            }
        }

        int local_21_power_color_index = DAT_0838_minimap_fractions_colors_table[nation];
        int local_2a_power = nation;
        int local_11_color = local_21_power_color_index;
        if (local_20_flag_privateer_maybe) {
            local_11_color = 0;
        }

        boolean local_1a_flag_artillery_damaged = false;
        if (unit.type == 0xb && (unit.flags_damaged & 0x80) != 0) {
            local_1a_flag_artillery_damaged = true;
        }

        if (local_1a_flag_artillery_damaged) {
            int attack = DAT_51e8_unit_config_array[unit.type].attack - unit.attack_penalty_maybe;
            if (FUN_1373_000e_is_tile_in_drawable_rect(unit.x, unit.y)) {
                int halfAttack = (attack + 1) >> 1;
                if (halfAttack >= 10) {
                    local_3_order_letter = '+';
                } else {
                    local_3_order_letter = '0' + halfAttack;
                }
            }
        }

        String local_34_cargo_digit_string = Character.toString(local_3_order_letter);
        int stringWidth = FUN_1c0e_000c_get_string_width_in_pixels(local_34_cargo_digit_string, DAT_088e_fonttiny_address) + 3;

        int local_26_text_frame_height = DAT_088e_fonttiny_address.height + 3;
        int local_28_sprite_width = DAT_082e_icons_sprite_sheet.field_0x42[local_e_unit_icon_index].field_0x08_width;
        int local_a_sprite_width;
        int local_1c_sprite_height;
        if (zoom_level_percent == 100) {
            local_a_sprite_width = local_28_sprite_width + stringWidth;
            local_1c_sprite_height = DAT_082e_icons_sprite_sheet.field_0x42[local_e_unit_icon_index].field_0x0A_height;
        } else {
            SpriteDimensions spriteDimension = FUN_1c67_0008_calculate_center_offset(zoom_level_percent, DAT_082e_icons_sprite_sheet, local_e_unit_icon_index, screenX, screenY);
            local_a_sprite_width = spriteDimension.x;
            local_1c_sprite_height = spriteDimension.y;
        }

        int x = screenX;
        // center sprite if it does not fit into a tile
        if (local_a_sprite_width > tile_pixel_size) {
            x += (tile_pixel_size - local_a_sprite_width) / 2;
        }


        int local_4_cargo_digit_sprite_x = 0;
        int local_8_sprite_y = 0;
        int local_24_text_frame_width = 0;

        // reuse cargo digit as x
        if (zoom_level_percent != 100) {
            if (zoom_level_percent == 50) {
                int local_10_some_x = x;
                local_4_cargo_digit_sprite_x = screenX + 5;
                local_8_sprite_y = screenY + 5;
                local_24_text_frame_width = 2;
                local_26_text_frame_height = 2;

                FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(DAT_2638_backscreen, local_10_some_x + local_28_sprite_width / 2, screenY + local_1c_sprite_height - 1, zoom_level_percent, local_e_unit_icon_index, DAT_082e_icons_sprite_sheet);

            } else if (zoom_level_percent == 25) {
                local_4_cargo_digit_sprite_x = screenX + 1;
                local_8_sprite_y = screenY + 1;
                local_24_text_frame_width = 2;
                local_26_text_frame_height = 2;
            } else {
                local_4_cargo_digit_sprite_x = screenX;
                local_8_sprite_y = screenY;
                local_24_text_frame_width = 2;
                local_26_text_frame_height = 2;
            }

            FUN_1b83_0000_fill_rectangle(local_4_cargo_digit_sprite_x, local_8_sprite_y, 2, 2, DAT_2638_backscreen, local_11_color);
            return;
        }

        // zoom level == 100%
        int local_10_some_x = x;
        if ((flags & 0x20) != 0 ) {
            local_24_text_frame_width = 4;
            local_26_text_frame_height = 4;
        }

        int dx = x + local_28_sprite_width;
        int local_6_some_x = x;
        int local_48_some_width = local_24_text_frame_width + local_28_sprite_width;
        if (local_48_some_width > 0x10) {
            dx = dx - local_48_some_width - 0x10;
        }

        int local_16_some_x;
        int local_18_some_y;
        switch (local_14_icon_size) {
            case 1:
                local_16_some_x = dx - 2;
                local_8_sprite_y = screenY;
                local_18_some_y = screenY + local_1c_sprite_height - 2;
                local_14_icon_size = 0;
                local_4_cargo_digit_sprite_x = dx;
                break;
            case 2:
            case 4:
                dx = (local_10_some_x - local_24_text_frame_width / 2) + 9;
                local_16_some_x = dx - 2;
                int bx = screenY + (local_14_icon_size == 4 ? 2 : 0);
                local_18_some_y = bx + 2;
                local_14_icon_size = 1;
                local_8_sprite_y = bx;
                break;
            case 3:
                dx = local_10_some_x;
                local_4_cargo_digit_sprite_x = dx;
                local_8_sprite_y = screenY;
                local_16_some_x = dx + 2;
                local_18_some_y = screenY + 2;
                local_14_icon_size = 1;
                local_6_some_x = dx + local_24_text_frame_width;
                int ax = local_48_some_width + 2;
                if (ax > 0x10) {
                    local_6_some_x -= (local_48_some_width - 0xe);
                }

                break;
            default:
                local_16_some_x = dx - 2;
                local_8_sprite_y = screenY - local_26_text_frame_height + local_1c_sprite_height;
                local_18_some_y = screenY + local_1c_sprite_height - 2;
                local_14_icon_size = 0;
                local_4_cargo_digit_sprite_x = dx;
                break;
        }

        if (local_14_icon_size != 0) {
            // draw silhouette
            FUN_112b_015c_draw_icon_maybe(local_6_some_x, screenY, local_e_unit_icon_index, 0x1);
        }

        if (local_1e_flag_has_stuff_in_transportchain2) {
            FUN_1b83_0000_fill_rectangle(local_16_some_x, local_18_some_y, local_24_text_frame_width, local_26_text_frame_height, DAT_2638_backscreen, 0);
            FUN_1b83_0000_fill_rectangle(local_16_some_x + 1, local_18_some_y + 1, local_24_text_frame_width - 2, local_26_text_frame_height - 2, DAT_2638_backscreen, local_11_color);
        }
        FUN_1b83_0000_fill_rectangle(local_4_cargo_digit_sprite_x, local_8_sprite_y, local_24_text_frame_width, local_26_text_frame_height, DAT_2638_backscreen, 0);
        FUN_1b83_0000_fill_rectangle(local_4_cargo_digit_sprite_x+1, local_8_sprite_y+1, local_24_text_frame_width-2, local_26_text_frame_height-2, DAT_2638_backscreen, 0);

        if (local_14_icon_size == 0) {
            // draw silhouette
            FUN_112b_015c_draw_icon_maybe(local_6_some_x, screenY, local_e_unit_icon_index, 0x1);
        }

        // draw unit sprite
        FUN_112b_015c_draw_icon_maybe(local_6_some_x, screenY, local_e_unit_icon_index, 0x2);

        if ((flags & 0x20) == 0) {
            if ((local_c_unit_order == 0x1 || local_c_unit_order == 0x6)) {
                if (local_2a_power < 4) {
                    local_21_power_color_index -= 8;
                } else {
                    local_21_power_color_index = 8;
                }
            } else {
                local_21_power_color_index = 0;
            }

            if (local_20_flag_privateer_maybe) {
                local_21_power_color_index = 0xf;
            }

            if (local_1a_flag_artillery_damaged) {
                local_21_power_color_index = local_2a_power == 2 ? 0xc : 0xf;
            }

            FUN_1c0d_0000_set_text_blit_colors(0xff, local_21_power_color_index, local_21_power_color_index, local_21_power_color_index );
            FUN_1bf6_0002_blit_text_to_bitmap(DAT_2638_backscreen, DAT_088e_fonttiny_address, local_34_cargo_digit_string, local_4_cargo_digit_sprite_x+2, local_8_sprite_y+2, 0);
        }

        if (local_1a_flag_artillery_damaged && zoom_level_percent == 100) {
            FUN_1c1b_0000_draw_compressed_sprite(DAT_2638_backscreen, local_6_some_x+4, screenY+4, 0x38, DAT_082e_icons_sprite_sheet);
        }
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
                FUN_1c0d_0000_set_text_blit_colors(0xf, -1, 0xf, 0xf);
                FUN_1bf6_0002_blit_text_to_bitmap(destination, DAT_088e_fonttiny_address, panicString, x_in_pixels+3, y_in_pixels+3, 0);
            }

        }

        if (zoom_level_percent <= 25) {
            // draw colored rectangles for the two smallest zoom levels
            FUN_1b83_0000_fill_rectangle(x_in_pixels, y_in_pixels, tileSize, tileSize, destination, color);
        }
    }

    /**
     * BX - y_in_pixels
     * DX - x_in_pixels
     * AX - colony index
     * param_1 - display colony name
     * param_2 - display population
     * param_3 - zoom level percent
     * param_4 - destination
     */
    public static void FUN_112b_0c64_draw_colony(Sprite destination, int zoom_level_percent, int screenX, int screenY, boolean displayColonyName, boolean displayPopulation, int colony_index) {
        Colony colony = DAT_5cfe_colonies_list[colony_index];

        int nation = colony.nation;
        int numColonists = colony.num_colonists;

        int fortificationLevel = 0;
        // check for stockade
        if (FUN_15d9_0368_is_building_in_colony(colony_index, 0)) {
            fortificationLevel++;
        }
        // check for fort
        if (FUN_15d9_0368_is_building_in_colony(colony_index, 1)) {
            fortificationLevel++;
        }
        // check for fortress
        if (FUN_15d9_0368_is_building_in_colony(colony_index, 2)) {
            fortificationLevel++;
        }

        if (nation != DAT_5338_savegame_header.maybe_player_controlled_power) {
            int seenColonists = colony.colonists_seen_in_colony[DAT_5338_savegame_header.maybe_player_controlled_power];
            if (seenColonists == 0) {
                seenColonists = 1;
                colony.colonists_seen_in_colony[DAT_5338_savegame_header.maybe_player_controlled_power] = 1;
            }
            numColonists = seenColonists;
            fortificationLevel = colony.seen_fortification_level[DAT_5338_savegame_header.maybe_player_controlled_power];
        }

        // note: 0 wraps around to 0xffff and therefor to 0x3
        int colonySprite = (fortificationLevel - 1) & 0x3;

        if (zoom_level_percent < 100 ) {
            screenX -= 2 >> Data.DAT_017a_zoom_level;
        }


        int local_10_x = screenX + (DAT_82de_tile_pixel_size / 2);
        int local_12_y = screenY + DAT_82de_tile_pixel_size - 1;

        int local_a_another_x = screenX + 6;
        int local_c_another_y = screenY + 1;

        // this looks like some rotation
        int local_e_maybe_some_x = 0;
        if (zoom_level_percent == 100) {
            local_c_another_y = screenX + 6;
            local_e_maybe_some_x = screenY + 4;
        } else {
            local_c_another_y = screenX + 3;
            local_e_maybe_some_x = screenY + 2;
        }

        // draw base icon
        FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(destination, local_10_x, local_12_y, zoom_level_percent, colonySprite+1, DAT_082e_icons_sprite_sheet);
        // draw flag
        int flagOwner = nation;
        int flagSprite = 0x77 + flagOwner;
        if ((DAT_5338_savegame_header.field1_0x2_independence_flag & 1) != 0 &&
                DAT_5338_savegame_header.rebels_nation_maybe == DAT_5338_savegame_header.maybe_current_player) {
            flagOwner = DAT_5338_savegame_header.maybe_current_player;
            flagSprite = 0x83;
        }
        FUN_1c3a_000a_draw_sprite_flippable_centered_zoomed(destination, local_e_maybe_some_x, local_c_another_y, zoom_level_percent, flagSprite, DAT_082e_icons_sprite_sheet);

        // draw population and colony name only in max zoom
        if (zoom_level_percent == 100) {
            int populationColor = 0xf;
            if ((colony.sons_of_liberty_level & 0x2) != 0) {
                populationColor = 0xb;
                if ((colony.sons_of_liberty_level & 0x4) != 0) {
                    populationColor = 0xa;
                }
            }

            FUN_1c0d_0000_set_text_blit_colors(0xff, populationColor, populationColor, populationColor);
            if (displayPopulation) {
                FUN_1bf6_0002_blit_text_to_bitmap(destination, DAT_088e_fonttiny_address, "" + numColonists, screenX + 7, screenY + 7, 0);
            }
            if (displayColonyName) {
                FUN_1c0d_0000_set_text_blit_colors(0xff, 0xf, 0xf, 0xf);
                FUN_1bf6_0002_blit_text_to_bitmap(destination, DAT_2618_fontintr_address, colony.name, screenX + 2, screenY + 0x10, 0);
            }


        }

        if (zoom_level_percent <= 25) {
            // draw colored rectangle in smaller zoom modes
            int color = DAT_0838_minimap_fractions_colors_table[colony.nation];
            FUN_1b83_0000_fill_rectangle(screenX, screenY, DAT_82de_tile_pixel_size, DAT_82de_tile_pixel_size, destination, color);
        }


    }

}
