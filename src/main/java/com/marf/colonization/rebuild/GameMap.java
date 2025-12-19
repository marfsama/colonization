package com.marf.colonization.rebuild;

import com.marf.colonization.saves.section.IndianTribe;
import com.marf.colonization.saves.section.Unit;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84e6_map_width
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84e8_map_height
     */
    public Dimension mapSize = new Dimension();

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0152_game_map_terrain
     */
    public byte[] terrain;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0156_game_map_surface
     */
    public byte[] surface;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_015a_game_map_visitor
     */
    public byte[] visitor;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_015e_game_map_visibility
     */
    public byte[] visibility;

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_30fc_units_list
     */
    public List<Unit> units = new ArrayList<>();
    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_54a4_indian_village_list */
    public List<IndianVillage> indianVillages = new ArrayList<>();
    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_2b4d_5a8e_tribes_list */
    public List<IndianTribe> indianTribes = new ArrayList<>();
    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_5cfe_colonies_list */
    public List<Colony> colonies = new ArrayList<>();

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_0186_map_seed */
    public int mapSeed = 0x4C4D; // todo: read from save file

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_00fa_get_terrain_type_offset_at */
    public int getMapOffset(int x, int y) {
        return y * mapSize.width + x;
    }

    /**
     * The first and last row and column in the grid are not drawn and are not accessible.
     * This function returns true when the tile at (x,y) is drawable and false otherwise.
     * @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_000e_is_tile_in_drawable_rect
     */
    public boolean isTileInDrawableRect(int x, int y) {
        if ((x >= 1) && (y >= 1) && (x < mapSize.width - 1) && (y < mapSize.height - 1)) {
            return true;
        }
        return false;
    }



    /**
     * @see com.marf.colonization.decompile.cmodules.Code14#FUN_1415_0064_get_unit_at
     */
    public Unit getUnitAt(int x, int y) {
        boolean inDrawableArea = isTileInDrawableRect(x, y);
        if (inDrawableArea) {
            int owner = surfaceGetUnitOwner(x, y);
            if (owner < 0) {
                return null;
            }
        }


        // scan for unit at the current tile
        Unit result = null;
        for (Unit unit : units) {
            if (unit.getX() == x && unit.getY() == y) {
                result = unit;
                break;
            }
        }

/*
        int headUnit = FUN_1415_000a_get_transport_chain_head(result);
        if (headUnit > -1 && inDrawableArea) {
            FUN_83d9_042c_module_16_store_int_parameter(x, 0);
            FUN_83d9_042c_module_16_store_int_parameter(y, 1);
            int unitOwner = FUN_1373_0318_surface_get_unit_owner(x, y);
            int powerTextIndex = Code15.FUN_15a1_01e8_get_power_name_textindex_singular(unitOwner);
            // prepare placeholder
            FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(0, powerTextIndex);
            // display text: Unit Flags Error (%NUMBER0, %NUMBER1) (%STRING0).
            FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", "UNITFLAG", 0);
        }

 */
        return result;

    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0146_get_surface_type_at */
    public int FUN_1373_0146_get_surface_type_at(int x, int y) {
        return surface[y * mapSize.width + x] & 0xff;
    }


    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0318_surface_get_unit_owner */
    public int surfaceGetUnitOwner(int x, int y) {
        int surfaceType = FUN_1373_0146_get_surface_type_at(x,y);

        // bit 0 set means there is a unit
        if ((surfaceType & 1) != 0) {
            return visitorGetLastVisitor(x,y);
        }
        return -1;
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_01b0_get_visitor_at */
    public int getVisitorAt(int x, int y) {
        return visitor[y * mapSize.width + x] & 0xff;
    }


    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0204_visitor_get_last_visitor */
    public int visitorGetLastVisitor(int x, int y) {
        int visitor = getVisitorAt(x,y);

        // get upper nibble (clear AH in AX)
        int upperNibble = (visitor >> 4) & 0xf;

        // AX = 0x000f ?
        if (upperNibble == 0xf) {
            return -1;
        }

        // else return -1
        return upperNibble;
    }


    /** @see "Code4#FUN_1101_0026_get_terrain_tile_id_by_terrain_type" */
    public int getTerrainTileIdByTerrainType(int terrainType) {
        // note: modified function

        // remove mountains, rivers
        int baseTerrain = terrainType & 0x1f;
        // is it arctic, sea or sea lane?
        if (baseTerrain >= 0x18) {
            // artic starts at index 9
            return (baseTerrain & 0x7) + 9;
        }

        // is it desert with forest (Scrub Forest) ?
        // note: this way 0x9 previously, but in FUN_8007_0938_module_14_102_draw_map_tile desert with forest is set to 0x11
        if (baseTerrain == 0x11) {
            // scrub forest is index 8
            return 8;
        }
        // else return the base terrain tiles starting from index 0
        return baseTerrain & 7;

        // original code, which may or may not be decompiled correctly
//        if ((terrainType != 0x11) && (terrainType != 9)) {
//            if (terrainType > 7) {
//                terrainType = terrainType - 0xf;
//            }
//            return terrainType;
//        }
    }


    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0112_get_terrain_type_at */
    public byte getTerrain(int x, int y) {
        return terrain[y * mapSize.width + x];
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0146_get_surface_type_at */
    public byte getSurfaceAt(int x, int y) {
        return surface[y * mapSize.width + x];
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_02fc_get_visibility_at */
    public byte getVisibilityAt(int x, int y) {
        return visibility[y * mapSize.width + x];
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0380_visitor_get_native_village_owner */
    public int FUN_1373_0380_visitor_get_native_village_owner(int x, int y) {
        var terrainType = FUN_1373_0146_get_surface_type_at(x,y);
        if ((terrainType & 2) != 0) {
            int lastVisitor = visitorGetLastVisitor(x, y);
            if (lastVisitor < 4) {
                return -1;
            }
            return lastVisitor;
        }
        return -1;
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_13d3_0006_something_with_mountains */
    public int FUN_13d3_0006_something_with_mountains(int terrain_type) {
        // 0x20 => mountain or hill
        if ((terrain_type & 0x20) != 0) {
            //
            // AL = terrain type
            boolean major = (terrain_type & 0x80) != 0;
            return 0x1b + (major ? 1 : 0);
        }
        return terrain_type & 0x1f;
    }

    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_13d3_0032_get_terrain_type_stuff */
    public int FUN_13d3_0032_get_terrain_type_stuff(int x,int y) {
        if (isTileInDrawableRect(x,y)) {
            int uVar1 = getTerrain(x,y);
            return FUN_13d3_0006_something_with_mountains(uVar1);
        }
        return 0x19;
    }



    /** @see com.marf.colonization.decompile.cmodules.Code13#FUN_1373_0540_get_rumor_at */
    public int FUN_1373_0540_get_rumor_at(int x, int y) {
        if (mapSeed == 0) {
            return 0;
        }

        int some_terrain_value = FUN_13d3_0032_get_terrain_type_stuff(x,y);
        // 0x18 = arctic, 0x19 = sea, 0x1a = sea lane
        if (((some_terrain_value != 0x19) && (some_terrain_value != 0x1a)) && (some_terrain_value != 0x18)) {
            // don't do stuff on sea or arctic
            int cVar1 = visitorGetLastVisitor(x,y);
            if ((cVar1 < 0) &&
                    ((((y >> 2) * 0x13 + (x >> 2) * 0x11 + mapSeed + 8) & 0x1f) + (x & 3) * -4 == (y & 3))) {
                return  1;
            }
        }

        return 0;

    }

    public int getTribeAggressionForPower(int tribeIndex, int aggressor) {
        return indianTribes.get(tribeIndex).getAggressions().get(aggressor);
    }

}
