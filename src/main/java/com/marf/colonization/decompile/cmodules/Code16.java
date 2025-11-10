package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Code18.*;
import static com.marf.colonization.decompile.cmodules.Code22.FUN_15d9_0a80_find_colony_at;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module02.FUN_4af1_1b4c_update_defender;

/**
 * Base Description: Terrain type queries
 * Status:
 * - all functions decompiled
 *
 */
public class Code16 {


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
            int maybeMountain = FUN_1373_0540_maybe_map_generation_stuff(x,y);
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
