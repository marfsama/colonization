package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module19_be.*;
import static com.marf.colonization.decompile.cmodules.Module1c.*;
import static com.marf.colonization.decompile.cmodules.Module1d_19.*;
import static com.marf.colonization.decompile.cmodules.Code16.*;

/**
 * Base Description: Working with map layers, terrain types and ownerships
 * Status:
 * - all functions decompiled
 * - 1 function unclear
 *
 */
public class Code15 {
    /**
     * The first and last row and column in the grid are not drawn and are not accessible.
     * This function returns true when the tile at (x,y) is drawable and false otherwise.
     */
    public static boolean FUN_1373_000e_is_tile_in_drawable_rect(int x, int y) {
        if (
                (x < 1) ||
                        (y < 1) ||
                        (DAT_84e6_map_width - 1 <= x) ||
                        (DAT_84e8_map_height - 1 <= y)) {
            return false;
        }
        return true;
    }

    /**
     * unknown yet
     */
    public static boolean FUN_1373_0040(int param_1, int param_2, int param_3) {
        boolean bVar1;

        if (param_1 < 1) {
            param_1 = ~param_1 + 1;
        }

        if (param_2 < 1) {
            param_2 = ~param_2 + 1;
        }
        bVar1 = (param_2 + param_1) < 2;
        if (param_3 != 1) {
            if ((param_1 < 2) && (param_2 < 2)) {
                bVar1 = true;
            }
// not clear what this does
//            if (((param_3 != 2) && (bVar1 = (bVar1 | (int) (param_2 + param_1) < 3),param_3 != 3)) &&
//            ((param_1 < 2 || (param_2 < 2)))){
//                bVar1 = true;
//            }
        }
        return bVar1;
    }

    /**
     * checks if the coordinate is in the currently visible game window
     */
    public static boolean FUN_1373_00c4_is_in_visible_window(int x, int y) {
        if ((x < DAT_82e2_game_window_x_min) || (DAT_87aa_game_window_x_max < x)) {
            return false;
        }
        if ((y < DAT_82e6_game_window_y_min) || (DAT_87ac_game_window_y_max < y)) {
            return false;
        }
        return true;
    }

    /**
     * returns the offset to the terrain type at position X,y in DX:AX
     */
    public static int FUN_1373_00fa_get_terrain_type_offset_at(int x, int y) {
        // Note: the real function returns a pointer into the DAT_0152_game_map_terrain array
        return y * DAT_84e6_map_width + x;
    }

    /* returns the terrain type at pos (x,y) */
    public static int FUN_1373_0112_get_terrain_type_at(int x, int y) {
        return DAT_0152_game_map_terrain[y * DAT_84e6_map_width + x] & 0xff;
    }

    public static int FUN_1373_012e_get_surface_offset_at(int x, int y) {
        return y * DAT_84e6_map_width + x;
    }


    public static int FUN_1373_0146_get_surface_type_at(int x, int y) {
        return DAT_0156_game_map_surface[y * DAT_84e6_map_width + x] & 0xff;
    }


    public static void FUN_1373_0162_change_surface_type_at(int x, int y, byte bitmask, int set_or_remove) {
        int index = FUN_1373_012e_get_surface_offset_at(x, y);
        if (set_or_remove != 0) {
            DAT_0156_game_map_surface[index] |= bitmask;
            return;
        }

        DAT_0156_game_map_surface[index] &= (byte) ~bitmask;
    }


    public static int FUN_1373_0198_get_visitor_offset_at(int x, int y) {
        return y * DAT_84e6_map_width + x;
    }


    public static int FUN_1373_01b0_get_visitor_at(int x, int y) {
        return DAT_015a_game_map_visitor[y * DAT_84e6_map_width + x];
    }


    public static int FUN_1373_01ce_visitor_get_owner(int x, int y) {
        int bVar1 = FUN_1373_01b0_get_visitor_at(x, y);
        return bVar1 & 0xf;
    }


    // TODO: this decompilation is not guaranteed to be correct.
    //      the interpretation  is not guaranteed to be correct.
    public static void FUN_1373_01e0_visitor_set_visited_by(int x, int y, byte bitmask) {
        int oldValue = FUN_1373_01b0_get_visitor_at(x, y);

        int newValue = (oldValue ^ bitmask) & 0xf;

        DAT_015a_game_map_visitor[y * DAT_84e6_map_width + x] ^= (byte) newValue;
    }


    // TODO: this decompilation is not guaranteed to be correct
    public static int FUN_1373_0204_visitor_get_last_visitor(int x, int y) {
        int oldValue = FUN_1373_01b0_get_visitor_at(x, y);

        // get upper nibble (clear AH in AX)
        int upperNibble = (oldValue >> 4) & 0xf;

        // AX = 0x000f ?
        if (upperNibble == 0xf) {
            // yes, seems to return AH (so return 0)
            return 0;
        }

        // else return -1
        return -1;
    }

    public static void FUN_1373_022c_visitor_set_last_visited(int x,int y,int new_owner) {
        // only check if the player want to occupy a tile
        if (new_owner < 4) {
            int tribe = FUN_1373_0380_visitor_get_native_village_owner(x,y);
            // check if the tile is an indian village
            if (tribe > -1) {
                // yes, not allowed.
                // show debug message
                FUN_8e94_00e4_module_1d_19_debug_warning_popup("Illegal entry into village",new_owner,x,y);
                // dump savegame to analyze
                FUN_89c4_0034_module_19_be_save_game(5);
                // show game error and exit
                FUN_8e33_03d6_module_1c_show_game_error(y,y >> 0xf,x,x >> 0xf);
            }
        }

        int tileOffset = FUN_1373_0198_get_visitor_offset_at(x,y);
        int value = DAT_015a_game_map_visitor[tileOffset] & 0xff;
        int newValue = (value & 0xf) | (new_owner << 4);
        DAT_015a_game_map_visitor[tileOffset] = (byte) newValue;
    }


    public static int FUN_1373_02a4_get_tile_owner(int x,int y) {
        if (FUN_1373_000e_is_tile_in_drawable_rect(x,y)) {
            if (FUN_13d3_006c_is_sea(x,y) == false) {
                int owner = FUN_1373_01ce_visitor_get_owner(x,y);
                return (owner & 0xff);
            }
        }
        return -1;
    }

    public static int FUN_1373_02e4_visibility_get_offset_at(int x,int y) {
        return y * DAT_84e6_map_width + x;
    }

    /** returns the visibility at pos (x,y) */
    public static int FUN_1373_02fc_get_visibility_at(int x,int y) {
        return DAT_015e_game_map_visibility[y * DAT_84e6_map_width + x] & 0xff;
    }

    public static int FUN_1373_0318_surface_get_unit_owner(int x,int y) {
        int surfaceType = FUN_1373_0146_get_surface_type_at(x,y);
        // bit 0 set means there is a unit
        if ((surfaceType & 1) != 0) {
            return FUN_1373_0204_visitor_get_last_visitor(x,y);
        }
        return -1;
    }

    public static int FUN_1373_0346_surface_get_european_unit_owner(int x,int y) {
        int surfaceType = FUN_1373_0146_get_surface_type_at(x,y);
        // bit 0 set means there is a unit
        if ((surfaceType & 1) != 0) {
            int lastVisitor = FUN_1373_0204_visitor_get_last_visitor(x,y);
            // only return when the last visitor of the tile is european
            if (lastVisitor <= 3) {
                return lastVisitor;
            }
        }
        return -1;
    }

    public static int FUN_1373_0380_visitor_get_native_village_owner(int x,int y) {
        var terrainType = FUN_1373_0146_get_surface_type_at(x,y);
        if ((terrainType & 2) != 0) {
            int lastVisitor = FUN_1373_0204_visitor_get_last_visitor(x, y);
            if (lastVisitor < 4) {
                return -1;
            }
            return lastVisitor;
        }
        return -1;
    }

    public static int FUN_1373_03ba_surface_get_colony_owner(int x,int y) {
        int surfaceType = FUN_1373_0146_get_surface_type_at(x,y);
        // bit 1 set means there is a colony/village
        if ((surfaceType & 2) != 0) {
            return FUN_1373_0204_visitor_get_last_visitor(x,y);
        }
        return -1;
    }


    /** returns the owner of the village or unit on the tile or -1 if there is no colony or unit */
    public static int FUN_1373_03e8_get_owner_of_stuff_on_tile(int x,int y) {
        int iVar1 = FUN_1373_03ba_surface_get_colony_owner(x, y);
        if (iVar1 < 0) {
            iVar1 = FUN_1373_0318_surface_get_unit_owner(x,y);
        }
        return iVar1;
    }

    public static int FUN_1373_040a_surface_is_influenced_by_player(int x, int y, int player_index) {
        int result = -1;
        int surfaceType = FUN_1373_0146_get_surface_type_at(x,y);
        // check if the tile is either road or plowed
        if ((surfaceType & 0x48) != 0) {
            int lastVisitor = FUN_1373_0204_visitor_get_last_visitor(x,y);
            // check player bit. maybe check if player has left the game
            if ((lastVisitor > -1) && (lastVisitor < 4) && (lastVisitor != player_index) && (( DAT_87e2_europe[player_index].flags & 0x40) != 0)) {
                result = lastVisitor;
            }
        }
        return result;
    }


    public static int FUN_1373_0458_maybe_place_land_prime_resources(int x,int y) {
        // check some global flag
        if (DAT_0186 == 0) {
            return -1;
        }

        int nativeVillageOwner = FUN_1373_0380_visitor_get_native_village_owner(x,y);
        if (nativeVillageOwner > 0) {
            return -1;
        }

        // get terrain type, masking off mountains
        int terrainType = FUN_1373_0112_get_terrain_type_at(x,y) & 0x3f;

        int isForest;
        // 0x00 - 0x07 - plain terrain
        // 0x08 - 0x0f - terrain with forest
        // 0x10 - 0x18 - unused
        // 0x18 - 0x1a - artic and sea (lanes)
        if (terrainType >= 0x8 && terrainType <= 0x0f ) {
            isForest = 1;
        }
        else {
            isForest = 0;
        }
        int local_8 = -1;

        int uVar2 = (x & 3) * 4 + (y & 3);

        int uVar3 = ((y >> 2) * 3 + (x >> 2) - isForest + DAT_0186) & 0xf;

        if ((uVar3 == uVar2) || ((uVar3 ^ 10) == uVar2)) {
            int iVar1 = FUN_13d3_0032_get_terrain_type_stuff(x,y);
            local_8 = DAT_0188_maybe_prime_resource_per_terrain_type[iVar1];
            if (local_8 == 0) {
                local_8 = 6;
            }
            uVar2 = FUN_1373_0146_get_surface_type_at(x,y);
            if ((uVar2 & 4) != 0) {
                if (local_8 == 0xc) {
                    return 0;
                }
                local_8 = -1;
            }
        }
        return local_8;
    }

    public static int FUN_1373_0540_maybe_map_generation_stuff(int x,int y) {
        if (DAT_0186 == 0) {
            return 0;
        }

        int some_terrain_value = FUN_13d3_0032_get_terrain_type_stuff(x,y);
        // 0x18 = arctic, 0x19 = sea, 0x1a = sea lane
        if (((some_terrain_value != 0x19) && (some_terrain_value != 0x1a)) && (some_terrain_value != 0x18)) {
            // don't do stuff on sea or arctic
            int cVar1 = FUN_1373_0204_visitor_get_last_visitor(x,y);
            if ((cVar1 < 0) &&
                    ((((y >> 2) * 0x13 + (x >> 2) * 0x11 + DAT_0186 + 8) & 0x1f) + (x & 3) * -4 == (y & 3))) {
                return  1;
            }
        }

        return 0;
    }

    public static void FUN_1373_05ba_empty() {
        // empty. maybe debug stuff removed by preprocessor flags
    }

    public static int FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(byte terrainType) {
        // don't change sea or arctic
        int plainTerrain = terrainType & 0x1f;
        if (plainTerrain >= 0x18) {
            return terrainType;
        }

        if (DAT_0184_show_hidden_terrain_state == 2) {
            // mask off forest, and mountains. for some reason add forest again
            return (terrainType & 7) | 8;
        }
        if (DAT_0184_show_hidden_terrain_state == 3) {
          return terrainType & 7;
        }

        return terrainType;
    }

    // last address: 0x0605
}