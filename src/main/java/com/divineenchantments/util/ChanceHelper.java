package com.divineenchantments.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Average-accurate chance rolls.
 *
 * Each roll adds its chance to an accumulator. When the accumulator reaches
 * 1.0 the roll succeeds and the remainder carries over. A random initial
 * offset avoids a fixed cadence while preserving the exact long-term average.
 *
 * Example: a 0.4% roll (0.004) is guaranteed within 250 attempts.
 */
public final class ChanceHelper {

    private ChanceHelper() {
    }

    public static boolean roll(Entity entity, String key, float chance) {
        if (entity == null || chance <= 0.0F) {
            return false;
        }
        if (chance >= 1.0F) {
            return true;
        }

        NBTTagCompound data = entity.getEntityData();
        String tag = "DE_Chance_" + key;
        float progress;
        if (data.hasKey(tag)) {
            progress = data.getFloat(tag);
        } else {
            progress = entity.world.rand.nextFloat();
        }

        progress += chance;
        if (progress >= 1.0F) {
            data.setFloat(tag, progress - 1.0F);
            return true;
        }
        data.setFloat(tag, progress);
        return false;
    }

    public static boolean roll(Entity entity, String key, double chance) {
        return roll(entity, key, (float) chance);
    }

    public static void reset(Entity entity, String key) {
        if (entity != null) {
            entity.getEntityData().removeTag("DE_Chance_" + key);
        }
    }
}
