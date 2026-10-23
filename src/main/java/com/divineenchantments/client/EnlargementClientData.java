package com.divineenchantments.client;

import net.minecraft.entity.EntityLivingBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnlargementClientData {

    private static final Map<Integer, Entry> ENTRIES = new ConcurrentHashMap<>();

    public static void apply(int entityId, float scaleFactor, long expiryTime, boolean clear) {
        if (clear) {
            ENTRIES.remove(entityId);
        } else {
            ENTRIES.put(entityId, new Entry(scaleFactor, expiryTime));
        }
    }

    public static float getFactor(EntityLivingBase entity, long worldTime) {
        Entry entry = ENTRIES.get(entity.getEntityId());
        if (entry == null) {
            return -1.0F;
        }
        if (worldTime > entry.expiryTime) {
            ENTRIES.remove(entity.getEntityId());
            return -1.0F;
        }
        return entry.scaleFactor;
    }

    public static void clearAll() {
        ENTRIES.clear();
    }

    private static class Entry {
        final float scaleFactor;
        final long expiryTime;

        Entry(float scaleFactor, long expiryTime) {
            this.scaleFactor = scaleFactor;
            this.expiryTime = expiryTime;
        }
    }
}
