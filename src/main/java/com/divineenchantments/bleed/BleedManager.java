package com.divineenchantments.bleed;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class BleedManager {

    private static final List<BleedEntry> ACTIVE_BLEEDS = new ArrayList<>();
    private static final List<BleedEntry> PENDING_ADD = new ArrayList<>();

    public static class BleedEntry {
        public final EntityLivingBase target;
        public final EntityPlayer sourcePlayer;
        public final float damagePerTick;
        public int remainingBleeds;
        public int ticksUntilNext;

        public BleedEntry(EntityLivingBase target, EntityPlayer sourcePlayer, float damagePerTick, int totalBleeds) {
            this.target = target;
            this.sourcePlayer = sourcePlayer;
            this.damagePerTick = damagePerTick;
            this.remainingBleeds = totalBleeds;
            this.ticksUntilNext = 6; // 0.3s = 6 ticks (20 ticks/sec * 0.3 = 6)
        }
    }

    /**
     * Adds a new bleeding effect to the target entity.
     *
     * @param target         The target living entity.
     * @param sourcePlayer   The player dealing the direct hit.
     * @param damagePerTick  (4 + n)% of weapon damage.
     * @param totalBleeds    3 + n times.
     */
    public static synchronized void addBleed(EntityLivingBase target, EntityPlayer sourcePlayer, float damagePerTick, int totalBleeds) {
        if (target == null || !target.isEntityAlive() || target.world.isRemote) {
            return;
        }
        PENDING_ADD.add(new BleedEntry(target, sourcePlayer, damagePerTick, totalBleeds));
    }

    @SubscribeEvent
    public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            synchronized (BleedManager.class) {
                ACTIVE_BLEEDS.removeIf(entry -> entry.target != null && entry.target.world == event.getWorld());
                PENDING_ADD.removeIf(entry -> entry.target != null && entry.target.world == event.getWorld());
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        synchronized (BleedManager.class) {
            if (!PENDING_ADD.isEmpty()) {
                ACTIVE_BLEEDS.addAll(PENDING_ADD);
                PENDING_ADD.clear();
            }

            if (ACTIVE_BLEEDS.isEmpty()) {
                return;
            }

            Iterator<BleedEntry> it = ACTIVE_BLEEDS.iterator();
            while (it.hasNext()) {
                BleedEntry entry = it.next();

                if (entry.target == null || !entry.target.isEntityAlive() || entry.target.isDead
                        || !entry.target.world.isBlockLoaded(entry.target.getPosition())) {
                    it.remove();
                    continue;
                }

                entry.ticksUntilNext--;
                if (entry.ticksUntilNext <= 0) {
                    // Trigger a bleed tick
                    applyBleedDamage(entry.target, entry.sourcePlayer, entry.damagePerTick);
                    entry.remainingBleeds--;

                    if (entry.remainingBleeds > 0) {
                        entry.ticksUntilNext = 6; // Next bleed in 0.3s
                    } else {
                        it.remove();
                    }
                }
            }
        }
    }

    private static void applyBleedDamage(EntityLivingBase target, EntityPlayer sourcePlayer, float damage) {
        if (target == null || !target.isEntityAlive() || target.isDead) {
            return;
        }

        DamageSource bleedSource;
        if (sourcePlayer != null && sourcePlayer.isEntityAlive()) {
            bleedSource = new EntityDamageSource("bleed", sourcePlayer).setDamageBypassesArmor();
        } else {
            bleedSource = new DamageSource("bleed").setDamageBypassesArmor();
        }

        // Preserve previous hurt resistance so bleed tick does not trigger/increase player invincibility
        int prevHurtResistantTime = target.hurtResistantTime;

        // Reset hurt resistance so bleed damage can apply
        target.hurtResistantTime = 0;
        target.attackEntityFrom(bleedSource, damage);

        // Requirement: "does not trigger player invincibility so other attacks can land fine"
        // Ensure hurtResistantTime is never increased by the bleed tick
        if (prevHurtResistantTime <= 0) {
            target.hurtResistantTime = 0;
        } else {
            target.hurtResistantTime = Math.min(prevHurtResistantTime, target.hurtResistantTime);
        }

        // Spawn bleeding particles: Redstone block crack particles
        if (target.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) target.world;
            int redstoneBlockState = Block.getStateId(Blocks.REDSTONE_BLOCK.getDefaultState());
            ws.spawnParticle(
                    EnumParticleTypes.BLOCK_CRACK,
                    target.posX,
                    target.posY + target.height * 0.5D,
                    target.posZ,
                    16,
                    0.25D,
                    target.height * 0.25D,
                    0.25D,
                    0.05D,
                    redstoneBlockState
            );
        }

        // Subtle squish/blood sound
        target.world.playSound(
                null,
                target.posX,
                target.posY,
                target.posZ,
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                0.35F,
                1.8F
        );
    }
}
