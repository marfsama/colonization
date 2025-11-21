package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code14.*;
import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module02.*;
import static com.marf.colonization.decompile.cmodules.Module19_be.*;
import static com.marf.colonization.decompile.cmodules.Module1c.*;
import static com.marf.colonization.decompile.cmodules.Module1d_19.*;

/**
 * Base Description: Working with map layers, terrain types and ownerships
 * Status:
 * - all functions decompiled
 * - 1 function unclear
 *
 */
public class Code13 {
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
     * unknown yet. The function seems to check distance from the center and returns true or false depending on the distance
     * and the flag.
     */
    public static boolean FUN_1373_0040_should_draw_depending_on_distance_maybe(int x, int y, int flag_colony_or_map_view) {
        boolean bVar1;

        x = Math.abs(x);
        y = Math.abs(y);

        bVar1 = (y + x) < 2;
        if (flag_colony_or_map_view != 1) {
            if ((x < 2) && (y < 2)) {
                bVar1 = true;
            }
// not clear what this does
//            if (((param_3_flag_colony_or_map_view != 2) &&
//                    (bVar1 = (bool)(bVar1 | (int)(param_2_y + param_1_x) < 3),
//            param_3_flag_colony_or_map_view != 3)) && (((int)param_1_x < 2 || ((int)param_2_y < 2)))) {
//                bVar1 = true;
        }
        return bVar1;
    }

    /**
     * checks if the coordinate is in the currently visible game window
     */
    public static boolean FUN_1373_00c4_is_in_visible_window(int x, int y) {
        if ((x < DAT_82e2_viewport_x_min) || (DAT_87aa_game_window_x_max < x)) {
            return false;
        }
        if ((y < DAT_82e6_viewport_y_min) || (DAT_87ac_game_window_y_max < y)) {
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


    /** returns the prime resource at this position or -1 if there should not be a prime resource. */
    public static int FUN_1373_0458_get_prime_resource_at(int x, int y) {
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

    /** returns 1 when a rumor is at this position, else 0 */
    public static int FUN_1373_0540_get_rumor_at(int x, int y) {
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

    public static int FUN_1373_05bc_adjust_terrain_type_with_show_hidden_terrain(int terrainType) {
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

    /**
     * Base Description: Terrain type queries
     * Segment: 0x13d3
     * Status:
     * - all functions decompiled
     *
     */

    public static int FUN_13d3_0006_something_with_mountains(int terrain_type) {
        // 0x20 => mountain or hill
        if ((terrain_type & 0x20) != 0) {
            //
            // AL = terrain type
            boolean major = (terrain_type & 0x80) != 0;
            return 0x1b + (major ? 1 : 0);
        }
        return terrain_type & 0x1f;
    }

    public static int FUN_13d3_0032_get_terrain_type_stuff(int x,int y) {
        if (FUN_1373_000e_is_tile_in_drawable_rect(x,y)) {
            int uVar1 = FUN_1373_0112_get_terrain_type_at(x,y);
            return FUN_13d3_0006_something_with_mountains(uVar1);
        }
        return 0x19;
    }

    public static boolean FUN_13d3_006c_is_sea(int x, int y) {
        int terrainType = FUN_1373_0112_get_terrain_type_at(x,y);
        int baseTerrain = terrainType & 0x1f;
        // 0x19 = sea, 0x1a = sea lane
        if ((baseTerrain != 0x19) && (baseTerrain != 0x1a)) {
            // neither sea nor sea lane
            return false;
        }
        return true;
    }


    public static boolean FUN_13d3_009a_is_forest(int x,int y) {
        int terrainType = FUN_1373_0112_get_terrain_type_at(x,y);
        int baseTerrain = terrainType & 0x1f;

        // check if >= 8 && < 0x10. This these are the forest terrain types
        if ((baseTerrain >= 8) && (baseTerrain <= 0xf)) {
            return true;
        }
        return false;
    }


    /* params:
     AX - x
     DX - y
     BL - power to visit the tile */

    public static void FUN_13e0_0002(int param_1, int x, int y, int power) {

        // update visited (fog of war) bits for the power
        int visibilityOffset = FUN_1373_02e4_visibility_get_offset_at(x,y);
        int visibility = DAT_015e_game_map_visibility[visibilityOffset];
        int mask = 1 << (power + 4);
        DAT_015e_game_map_visibility[visibilityOffset] = (byte) (visibility | mask);

        // update last visitor of field
        int lastVisitor = FUN_1373_0204_visitor_get_last_visitor(x,y);
        if (lastVisitor < 0) {
            int maybeMountain = FUN_1373_0540_get_rumor_at(x,y);
            if (maybeMountain == 0) {
                FUN_1373_022c_visitor_set_last_visited(x, y, power);
            }
        }


        int unitIndex = FUN_1415_0064_get_unit_at(x,y);
        if ((param_1 == 0) || (DAT_30fc_units_list[unitIndex].nationIndex < 4)) {
            FUN_1415_09b4_set_power_bit_in_unit_stack(unitIndex, power);
        }
        int colonyIndex = FUN_15d9_0a80_find_colony_at(x, y);
        if (-1 < colonyIndex) {
            FUN_4af1_1b4c_update_defender(colonyIndex,power);
        }

    }

}