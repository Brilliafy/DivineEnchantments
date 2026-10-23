package com.divineenchantments.client;

import net.minecraft.entity.EntityLivingBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnlargementClientData {

    private static final Map<Integer, Entry> ENTRIES = new ConcurrentHashMap<>();

    public static void apply(int entityId, int amplifier, long expiryTime, boolean clear) {
        if (clear) {
            ENTRIES.remove(entityId);
        } else {
            ENTRIES.put(entityId, new Entry(amplifier, expiryTime));
        }
    }

    public static int getAmplifier(EntityLivingBase entity, long worldTime) {
        Entry entry = ENTRIES.get(entity.getEntityId());
        if (entry == null) {
            return -1;
        }
        if (worldTime > entry.expiryTime) {
            ENTRIES.remove(entity.getEntityId());
            return -1;
        }
        return entry.amplifier;
    }

    public static void clearAll() {
        ENTRIES.clear();
    }

    private static class Entry {
        final int amplifier;
        final long expiryTime;

        Entry(int amplifier, long expiryTime) {
            this.amplifier = amplifier;
            this.expiryTime = expiryTime;
        }
    }
}
