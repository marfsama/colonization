package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Code15.*;
import static com.marf.colonization.decompile.cmodules.Data.*;
import static com.marf.colonization.decompile.cmodules.Module16.*;

public class Code18 {


    /**
     * Traverses the transport chain of the unit until the head and returns the head.
     * <p>
     * param:
     * ax = unit index
     * return
     * ax = unit index at the head of the transport chain
     */
    public static int FUN_1415_000a_get_transport_chain_head(int unitIndex) {
        if (unitIndex < 0) {
            return -1;
        }

        Unit unit = DAT_30fc_units_list[unitIndex];
        while (unit.transportChain1 > 0) {
            unitIndex = unit.transportChain1;
            unit = DAT_30fc_units_list[unitIndex];
        }
        return unitIndex;
    }

    /**
     * Traverses the tranport chain of the unit until the tail and returns the tail.
     * <p>
     * param:
     * ax = unit index
     * return
     * ax = unit index at the head of the transport chain
     */
    public static int FUN_1415_002e_get_transport_chain_tail(int unitIndex) {
        if (unitIndex < 0) {
            return -1;
        }

        Unit unit = DAT_30fc_units_list[unitIndex];
        while (unit.transportChain2 > 0) {
            unitIndex = unit.transportChain2;
            unit = DAT_30fc_units_list[unitIndex];
        }
        return unitIndex;
    }

    /**
     * param:
     * ax = unit index
     * return
     * ax = transport chain 2
     */
    public static int FUN_1415_0052_get_transportchain2(int unitIndex) {
        if (unitIndex < 0) {
            return -1;
        }

        return DAT_30fc_units_list[unitIndex].transportChain2;
    }


    /**
     * Params:
     * AX = x
     * DX = y
     */
    public static int FUN_1415_0064_get_unit_at(int x, int y) {

        boolean inDrawableArea = FUN_1373_000e_is_tile_in_drawable_rect(x, y);
        if (inDrawableArea) {
            int owner = FUN_1373_0318_surface_get_unit_owner(x, y);
            if (owner < 0) {
                return -1;
            }
        }


        // scan for unit at the current tile
        int currentUnit = 0;
        var result = -1;
        do {
            Unit unit = DAT_30fc_units_list[currentUnit];
            if (unit.x == x && unit.y == y) {
                result = currentUnit;
            }
            currentUnit = currentUnit + 1;
        } while (result < 0 && currentUnit < DAT_5338_savegame_header.num_units);


        int headUnit = FUN_1415_000a_get_transport_chain_head(currentUnit);
        if (headUnit > -1 && inDrawableArea) {
            FUN_83d9_042c_module_16_store_int_parameter(x, 0);
            FUN_83d9_042c_module_16_store_int_parameter(y, 1);
            int unitOwner = FUN_1373_0318_surface_get_unit_owner(x, y);
            int powerTextIndex = Code20.FUN_15a1_01e8_get_power_name_textindex_singular(unitOwner);
            // prepare placeholder
            FUN_83d9_03ec_copy_string_from_string_table_to_string_parameter(0, powerTextIndex);
            // display text: Unit Flags Error (%NUMBER0, %NUMBER1) (%STRING0).
            FUN_83d9_36b6_module_16_draw_text_in_current_font("GAME", "UNITFLAG", 0);
        }
        return currentUnit;
    }

    public static void FUN_1415_099a_set_power_bit_in_unit(int unitIndex,int power) {
        if (unitIndex > -1) {
            int mask = 0x10 << power;
            // set power bit in nation index
            DAT_30fc_units_list[unitIndex].nationIndex |= mask;
        }
    }

    public static void FUN_1415_09b4_set_power_bit_in_unit_stack(int startUnitIndex,int power) {
        int currentUnitIndex = FUN_1415_000a_get_transport_chain_head(startUnitIndex);
        if (currentUnitIndex < 0) {
            return;
        }

        while (currentUnitIndex > -1) {
            FUN_1415_099a_set_power_bit_in_unit(currentUnitIndex,power);
            currentUnitIndex = FUN_1415_0052_get_transportchain2(currentUnitIndex);
        }
    }

}
