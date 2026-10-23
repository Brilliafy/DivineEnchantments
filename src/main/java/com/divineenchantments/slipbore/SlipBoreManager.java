package com.divineenchantments.slipbore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class SlipBoreManager {

    private static final Set<EntityArrow> ACTIVE_ARROWS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void trackArrow(EntityArrow arrow) {
        if (arrow != null && !arrow.world.isRemote) {
            ACTIVE_ARROWS.add(arrow);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            ACTIVE_ARROWS.removeIf(arrow -> arrow != null && arrow.world == event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE_ARROWS.isEmpty()) {
            return;
        }

        Iterator<EntityArrow> it = ACTIVE_ARROWS.iterator();
        while (it.hasNext()) {
            EntityArrow arrow = it.next();

            if (arrow == null || arrow.isDead || arrow.world.isRemote || arrow.getEntityData().getBoolean("Divine_InGround")
                    || arrow.onGround || (arrow.motionX == 0.0D && arrow.motionY == 0.0D && arrow.motionZ == 0.0D)
                    || !arrow.world.isBlockLoaded(arrow.getPosition())) {
                it.remove();
                continue;
            }

            double r = 3.0D;
            AxisAlignedBB box = new AxisAlignedBB(
                    Math.min(arrow.prevPosX, arrow.posX) - r,
                    Math.min(arrow.prevPosY, arrow.posY) - r,
                    Math.min(arrow.prevPosZ, arrow.posZ) - r,
                    Math.max(arrow.prevPosX, arrow.posX) + r,
                    Math.max(arrow.prevPosY, arrow.posY) + r,
                    Math.max(arrow.prevPosZ, arrow.posZ) + r
            );

            List<EntityLivingBase> mobs = arrow.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
            Vec3d start = new Vec3d(arrow.prevPosX, arrow.prevPosY, arrow.prevPosZ);
            Vec3d end = new Vec3d(arrow.posX, arrow.posY, arrow.posZ);
            Vec3d seg = end.subtract(start);
            double segLenSq = seg.lengthSquared();

            for (EntityLivingBase mob : mobs) {
                if (mob == arrow.shootingEntity || !mob.isEntityAlive()) {
                    continue;
                }

                Vec3d mobPos = new Vec3d(mob.posX, mob.posY + mob.height * 0.5D, mob.posZ);
                Vec3d closest;
                if (segLenSq < 0.0001D) {
                    closest = end;
                } else {
                    double t = Math.max(0.0D, Math.min(1.0D, mobPos.subtract(start).dotProduct(seg) / segLenSq));
                    closest = start.add(seg.scale(t));
                }

                double dist = mobPos.distanceTo(closest);
                if (dist <= r) {
                    double dx = closest.x - mob.posX;
                    double dy = closest.y - (mob.posY + mob.height * 0.5D);
                    double dz = closest.z - mob.posZ;

                    double strength = (1.0D - (dist / r)) * 0.55D + 0.35D;

                    // Yank toward centerline
                    mob.motionX += dx * strength;
                    mob.motionZ += dz * strength;

                    // Drag flying mobs (Cacodemons, Raikos, etc.) crashing to the ground!
                    mob.motionY = Math.min(mob.motionY - 0.35D, dy * strength - 0.22D);
                    mob.velocityChanged = true;

                    if (mob instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) mob).connection.sendPacket(new SPacketEntityVelocity(mob));
                    }
                }
            }

            // Spawn low-pressure vacuum tunnel particles along the arrow trajectory
            if (arrow.world instanceof WorldServer) {
                WorldServer ws = (WorldServer) arrow.world;
                ws.spawnParticle(EnumParticleTypes.CLOUD, arrow.posX, arrow.posY, arrow.posZ, 2, 0.15D, 0.15D, 0.15D, 0.01D);
                ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, arrow.posX, arrow.posY, arrow.posZ, 3, 0.25D, 0.25D, 0.25D, 0.02D);
                ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, arrow.posX, arrow.posY, arrow.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
