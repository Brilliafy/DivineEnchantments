package com.divineenchantments.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OreGlowClient {

    private static final Map<BlockPos, Long> GLOWING = new ConcurrentHashMap<>();

    private OreGlowClient() {
    }

    public static void add(long[] packed, int durationTicks) {
        if (Minecraft.getMinecraft().world == null) {
            return;
        }
        long expiry = Minecraft.getMinecraft().world.getTotalWorldTime() + Math.max(1, durationTicks);
        for (long value : packed) {
            GLOWING.put(BlockPos.fromLong(value), expiry);
        }
    }

    public static List<BlockPos> getActive() {
        if (Minecraft.getMinecraft().world == null) {
            return java.util.Collections.emptyList();
        }
        long now = Minecraft.getMinecraft().world.getTotalWorldTime();
        List<BlockPos> active = new ArrayList<>();
        for (Map.Entry<BlockPos, Long> entry : GLOWING.entrySet()) {
            if (entry.getValue() < now) {
                GLOWING.remove(entry.getKey());
            } else {
                active.add(entry.getKey());
            }
        }
        return active;
    }

    public static void clear() {
        GLOWING.clear();
    }
}
