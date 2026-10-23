package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class ApertureEventHandler {

    /**
     * Intercepts projectile impacts before hit logic fires.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }

        RayTraceResult ray = event.getRayTraceResult();
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.ENTITY || !(ray.entityHit instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) ray.entityHit;
        Entity proj = event.getEntity();

        ItemStack shield = getApertureShield(player);
        if (shield.isEmpty()) {
            return;
        }

        if (isShooterOrAllied(proj, player)) {
            return;
        }

        // Cancel hit logic completely and re-fire as allied projectile!
        event.setCanceled(true);
        captureAndReflectProjectile(proj, player, shield);
    }

    /**
     * Fallback for custom modded projectiles that directly deal damage via LivingAttackEvent.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack shield = getApertureShield(player);
        if (shield.isEmpty()) {
            return;
        }

        Entity proj = event.getSource().getImmediateSource();
        if (proj != null && proj != player && !proj.isDead && !proj.getEntityData().getBoolean("Divine_Aperture_Captured")) {
            if (!isShooterOrAllied(proj, player)) {
                event.setCanceled(true);
                captureAndReflectProjectile(proj, player, shield);
            }
        }
    }

    private static ItemStack getApertureShield(EntityPlayer player) {
        ItemStack offhand = player.getHeldItemOffhand();
        if (ItemCheckHelper.isShield(offhand) && ModEnchantments.getLevel(ModEnchantments.APERTURE, offhand) > 0) {
            return offhand;
        }

        ItemStack mainhand = player.getHeldItemMainhand();
        if (ItemCheckHelper.isShield(mainhand) && ModEnchantments.getLevel(ModEnchantments.APERTURE, mainhand) > 0) {
            return mainhand;
        }

        return ItemStack.EMPTY;
    }

    private static boolean isShooterOrAllied(Entity proj, EntityPlayer player) {
        if (proj.getEntityData().getBoolean("Divine_Aperture_Captured")) {
            return true;
        }
        if (proj instanceof EntityArrow && ((EntityArrow) proj).shootingEntity == player) {
            return true;
        }
        if (proj instanceof EntityFireball && ((EntityFireball) proj).shootingEntity == player) {
            return true;
        }
        if (proj instanceof EntityThrowable && ((EntityThrowable) proj).getThrower() == player) {
            return true;
        }
        String alliedUUID = proj.getEntityData().getString("Divine_AlliedOwner");
        return player.getUniqueID().toString().equals(alliedUUID);
    }

    private static void captureAndReflectProjectile(Entity proj, EntityPlayer player, ItemStack shield) {
        proj.getEntityData().setBoolean("Divine_Aperture_Captured", true);
        proj.getEntityData().setString("Divine_AlliedOwner", player.getUniqueID().toString());

        // Boost incoming speed by +50%
        double curSpeed = Math.sqrt(proj.motionX * proj.motionX + proj.motionY * proj.motionY + proj.motionZ * proj.motionZ);
        double speed = Math.max(1.3D, curSpeed * 1.5D);

        Vec3d look = player.getLookVec();
        double eyeX = player.posX + look.x * 0.9D;
        double eyeY = player.posY + player.getEyeHeight() + look.y * 0.9D;
        double eyeZ = player.posZ + look.z * 0.9D;

        proj.setPosition(eyeX, eyeY, eyeZ);
        proj.motionX = look.x * speed;
        proj.motionY = look.y * speed;
        proj.motionZ = look.z * speed;
        proj.velocityChanged = true;

        float f = (float) Math.sqrt(look.x * look.x + look.z * look.z);
        proj.prevRotationYaw = proj.rotationYaw = (float) (MathHelper.atan2(look.x, look.z) * (180D / Math.PI));
        proj.prevRotationPitch = proj.rotationPitch = (float) (MathHelper.atan2(look.y, (double) f) * (180D / Math.PI));

        // Re-assign ownership
        if (proj instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) proj;
            arrow.shootingEntity = player;
            arrow.setDamage(arrow.getDamage() * 1.5D);
            arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
        } else if (proj instanceof EntityFireball) {
            EntityFireball fb = (EntityFireball) proj;
            fb.shootingEntity = player;
            fb.accelerationX = look.x * 0.12D;
            fb.accelerationY = look.y * 0.12D;
            fb.accelerationZ = look.z * 0.12D;
        } else if (proj instanceof EntityThrowable) {
            EntityThrowable thr = (EntityThrowable) proj;
            setFieldIfExists(thr, "thrower", player);
        }

        // Generic reflection support for modded projectiles (e.g. Cacodemon balls, Dragon breath orbs)
        setFieldIfExists(proj, "shootingEntity", player);
        setFieldIfExists(proj, "owner", player);
        setFieldIfExists(proj, "caster", player);
        setFieldIfExists(proj, "target", null);
        setFieldIfExists(proj, "attackTarget", null);
        setFieldIfExists(proj, "homingTarget", null);

        // Sound and visual feedback
        player.world.playSound(null, eyeX, eyeY, eyeZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.2F, 1.8F);
        player.world.playSound(null, eyeX, eyeY, eyeZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.9F, 1.5F);

        if (player.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) player.world;
            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, eyeX, eyeY, eyeZ, 25, 0.25D, 0.25D, 0.25D, 0.15D);
            ws.spawnParticle(EnumParticleTypes.PORTAL, eyeX, eyeY, eyeZ, 20, 0.3D, 0.3D, 0.3D, 0.1D);
        }

        // Shield durability
        shield.damageItem(1, player);
    }

    private static void setFieldIfExists(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Throwable ignored) {
            // Also check superclasses
            Class<?> clz = obj.getClass().getSuperclass();
            while (clz != null && clz != Object.class) {
                try {
                    Field f = clz.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(obj, value);
                    return;
                } catch (Throwable t2) {
                    clz = clz.getSuperclass();
                }
            }
        }
    }
}
