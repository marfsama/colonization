package com.marf.colonization.decompile.cmodules;

import static com.marf.colonization.decompile.cmodules.Data.*;

public class Code20 {

    public static void FUN_2071_0008_random_set_slot(int newSlot) {
        int currentContextId = Data.DAT_26b6_current_random_slot;

        // If switching to same context, do nothing
        if (newSlot == currentContextId) {
            return;
        }

        // Save current random state if current context is valid (≥ 0)
        if (currentContextId >= 0) {
            Data.DAT_a5c6_random_slot_seeds[currentContextId] = FUN_20f6_0064_random_get_seed();
        }

        // Update current context
        Data.DAT_26b6_current_random_slot = newSlot;

        // Restore new random state if new context is valid (≥ 0)
        if (newSlot >= 0) {
            // Load saved state from array
            FUN_20f6_0051_random_set_seed(Data.DAT_a5c6_random_slot_seeds[newSlot]);
        }
    }

    public static int FUN_2071_004e_random_find_free_slot() {
        for (int slotIndex = 0; slotIndex < DAT_a62a_random_slot_availability.length; slotIndex++) {
            if (DAT_a62a_random_slot_availability[slotIndex]  == 0) {
                // mark as used
                DAT_a62a_random_slot_availability[slotIndex] = -1;
            }
            // Initialize slot with zero seed
            Data.DAT_a5c6_random_slot_seeds[slotIndex] = 0;
            // select slot
            FUN_2071_0008_random_set_slot(slotIndex);
            // return slot index
            return slotIndex;
        }
        return -1;
    }

    public static void FUN_2071_008a_random_update_slot_seed(long seed) {
        int currentSlot = DAT_26b6_current_random_slot;

        // Only update if we have an active slot (slot ID >= 0)
        if (currentSlot >= 0) {
            // Temporarily switch to no slot context
            FUN_2071_0008_random_set_slot(-1);

            // Update the seed in the state array
            DAT_a5c6_random_slot_seeds[currentSlot] = seed;
            // Switch back to original slot
            FUN_2071_0008_random_set_slot(currentSlot);
        }
    }

    /** returns a random value between min and max-1 */
    public static int FUN_2071_00be_random_next_value_between(int min,int max) {
        return FUN_20f6_003a_random_next_value(max - min) + min;
    }

    /** returns the next 16 bit random value. */
    public static int FUN_20f6_0008_random_next_value() {
        long multiplier = 0x4E6D;
        long increment = 0x3039;
        DAT_273a_random_seed = (DAT_273a_random_seed * multiplier + increment) % 0xffff_ffffL;
        return (int) (DAT_273a_random_seed >> 16) & 0xffff;
    }

    /** returns a random value between 0 and max-1 */
    public static int FUN_20f6_003a_random_next_value(int max) {
        if (max != 0) {
            int uVar1 = FUN_20f6_0008_random_next_value();
            return uVar1 % max;
        }
        return max;
    }

    public static void FUN_20f6_0051_random_set_seed(long seed) {
        DAT_273a_random_seed = seed;
    }

    public static long FUN_20f6_0064_random_get_seed() {
        return DAT_273a_random_seed;
    }

}
