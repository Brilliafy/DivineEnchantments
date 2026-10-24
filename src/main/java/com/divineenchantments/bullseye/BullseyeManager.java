package com.divineenchantments.bullseye;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.network.ModNetwork;
import com.divineenchantments.network.PacketBullseye;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BullseyeManager {

    
    private static final Map<Integer, BullseyeTarget> TARGETS = new ConcurrentHashMap<>();

    public static void place(EntityLivingBase target) {
        if (target == null || target.world == null) {
            return;
        }

        float spread = Math.max(0.12F, target.width * 0.30F);
        float ox = (target.world.rand.nextFloat() * 2.0F - 1.0F) * spread;
        float oz = (target.world.rand.nextFloat() * 2.0F - 1.0F) * spread;
        float oy = target.height * (0.35F + 0.40F * target.world.rand.nextFloat());
        long expiry = target.world.getTotalWorldTime() + DivineConfig.bullseyeDurationTicks;

        BullseyeTarget spot = new BullseyeTarget(target.getEntityId(), ox, oy, oz, expiry);
        TARGETS.put(target.getEntityId(), spot);

        if (!target.world.isRemote) {
            ModNetwork.sendToAllTracking(new PacketBullseye(target.getEntityId(), ox, oy, oz, expiry, false), target);
        }
    }

    public static BullseyeTarget get(EntityLivingBase target) {
        if (target == null) {
            return null;
        }
        BullseyeTarget spot = TARGETS.get(target.getEntityId());
        if (spot == null) {
            return null;
        }
        if (spot.isExpired(target.world)) {
            TARGETS.remove(target.getEntityId());
            return null;
        }
        return spot;
    }

    public static boolean matches(EntityLivingBase target, Vec3d rayStart, Vec3d rayEnd) {
        BullseyeTarget spot = get(target);
        return spot != null && spot.matchesRay(target, rayStart, rayEnd, DivineConfig.bullseyeHitRadius);
    }

    public static void clear(int entityId) {
        TARGETS.remove(entityId);
    }

    public static void clearAndSync(EntityLivingBase target) {
        if (target == null) {
            return;
        }
        BullseyeTarget removed = TARGETS.remove(target.getEntityId());
        if (removed != null && !target.world.isRemote) {
            ModNetwork.sendToAllTracking(new PacketBullseye(target.getEntityId(), 0, 0, 0, 0, true), target);
        }
    }

    public static void resend(EntityLivingBase target, BullseyeTarget spot) {
        if (target == null || spot == null || target.world.isRemote) {
            return;
        }
        ModNetwork.sendToAllTracking(new PacketBullseye(target.getEntityId(),
                spot.offsetX, spot.offsetY, spot.offsetZ, spot.expiryTime, false), target);
    }

    public static java.util.Collection<BullseyeTarget> getAll() {
        return TARGETS.values();
    }

    public static void clearAll() {
        TARGETS.clear();
    }

    public static void applyNetworkUpdate(int entityId, float ox, float oy, float oz, long expiry, boolean clear) {
        if (clear) {
            TARGETS.remove(entityId);
        } else {
            TARGETS.put(entityId, new BullseyeTarget(entityId, ox, oy, oz, expiry));
        }
    }
}
