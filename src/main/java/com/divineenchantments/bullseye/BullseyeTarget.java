package com.divineenchantments.bullseye;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BullseyeTarget {

    public final int entityId;
    public final float offsetX;
    public final float offsetY;
    public final float offsetZ;
    public final long expiryTime;

    public BullseyeTarget(int entityId, float offsetX, float offsetY, float offsetZ, long expiryTime) {
        this.entityId = entityId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired(World world) {
        return world.getTotalWorldTime() > this.expiryTime;
    }

    public Vec3d getWorldPosition(EntityLivingBase entity) {
        return new Vec3d(entity.posX + this.offsetX, entity.posY + this.offsetY, entity.posZ + this.offsetZ);
    }

    public boolean matchesRay(EntityLivingBase entity, Vec3d start, Vec3d end, double radius) {
        Vec3d center = this.getWorldPosition(entity);
        Vec3d segment = end.subtract(start);
        double lengthSq = segment.lengthSquared();
        if (lengthSq < 1.0E-8D) {
            return center.squareDistanceTo(start) <= radius * radius;
        }
        double t = center.subtract(start).dotProduct(segment) / lengthSq;
        t = Math.max(0.0D, Math.min(1.0D, t));
        Vec3d closest = start.add(segment.scale(t));
        return closest.squareDistanceTo(center) <= radius * radius;
    }
}
