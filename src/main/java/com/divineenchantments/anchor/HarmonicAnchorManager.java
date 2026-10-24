package com.divineenchantments.anchor;

import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class HarmonicAnchorManager {

    public static class AnchorData {
        public final EntityLivingBase entity;
        public final double anchorX;
        public final double anchorY;
        public final double anchorZ;
        public int ticksRemaining;

        public AnchorData(EntityLivingBase entity, double x, double y, double z, int durationTicks) {
            this.entity = entity;
            this.anchorX = x;
            this.anchorY = y;
            this.anchorZ = z;
            this.ticksRemaining = durationTicks;
        }
    }

    private static final Map<UUID, AnchorData> ANCHORS = new ConcurrentHashMap<>();

    public static boolean applyAnchor(EntityLivingBase target, int durationTicks) {
        if (target == null || target.world.isRemote) {
            return false;
        }

        // Bosses, boss-bar entities, and very high-health mobs are immune.
        if (ItemCheckHelper.isBossEntity(target) || target.getMaxHealth() > 150.0F) {
            return false;
        }

        // Only place a new anchor stone if not already anchored
        AnchorData existing = ANCHORS.get(target.getUniqueID());
        if (existing != null && existing.ticksRemaining > 0) {
            existing.ticksRemaining = Math.max(existing.ticksRemaining, durationTicks);
            return true;
        }

        double ax = target.posX;
        double ay = target.posY;
        double az = target.posZ;
        ANCHORS.put(target.getUniqueID(), new AnchorData(target, ax, ay, az, durationTicks));

        // Visual and sound cue on anchor placement
        if (target.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) target.world;
            ws.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, ax, ay + 0.5D, az, 30, 0.4D, 0.4D, 0.4D, 0.1D);
            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, ax, ay + 0.2D, az, 20, 0.3D, 0.1D, 0.3D, 0.05D);
        }
        target.world.playSound(null, ax, ay, az, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.8F, 1.4F);
        return true;
    }

    @SubscribeEvent
    public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            ANCHORS.entrySet().removeIf(entry -> entry.getValue().entity != null && entry.getValue().entity.world == event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ANCHORS.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, AnchorData>> it = ANCHORS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, AnchorData> entry = it.next();
            AnchorData data = entry.getValue();

            if (data.entity == null || !data.entity.isEntityAlive() || data.ticksRemaining <= 0
                    || !data.entity.world.isBlockLoaded(new net.minecraft.util.math.BlockPos(data.anchorX, data.anchorY, data.anchorZ))
                    || !data.entity.world.isBlockLoaded(data.entity.getPosition())) {
                it.remove();
                continue;
            }

            data.ticksRemaining--;

            EntityLivingBase mob = data.entity;
            double dist = mob.getDistance(data.anchorX, data.anchorY, data.anchorZ);

            // Tether check: Hard cap at 5 blocks
            if (dist > 5.0D) {
                double dx = data.anchorX - mob.posX;
                double dy = data.anchorY - mob.posY;
                double dz = data.anchorZ - mob.posZ;

                double normX = dx / dist;
                double normY = dy / dist;
                double normZ = dz / dist;

                // Slingshot backward towards the center point
                double pullStrength = Math.min(1.2D, (dist - 4.5D) * 0.9D);
                mob.motionX = normX * pullStrength;
                mob.motionY = Math.max(-0.4D, normY * pullStrength);
                mob.motionZ = normZ * pullStrength;
                mob.velocityChanged = true;

                if (mob instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) mob).connection.sendPacket(new SPacketEntityVelocity(mob));
                }

                if (mob.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) mob.world;
                    // Draw spectral line from mob to anchor point
                    int particles = 8;
                    for (int i = 0; i < particles; i++) {
                        double f = (double) i / (double) particles;
                        double px = mob.posX + dx * f;
                        double py = mob.posY + mob.height * 0.5D + dy * f;
                        double pz = mob.posZ + dz * f;
                        ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                    ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, data.anchorX, data.anchorY + 0.2D, data.anchorZ, 4, 0.1D, 0.1D, 0.1D, 0.02D);
                }

                if (data.ticksRemaining % 8 == 0) {
                    mob.world.playSound(null, mob.posX, mob.posY, mob.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 0.8F, 1.6F);
                }
            } else if (mob.world instanceof WorldServer && data.ticksRemaining % 10 == 0) {
                // Subtle anchor pulse at center
                WorldServer ws = (WorldServer) mob.world;
                ws.spawnParticle(EnumParticleTypes.TOWN_AURA, data.anchorX, data.anchorY + 0.1D, data.anchorZ, 3, 0.2D, 0.1D, 0.2D, 0.01D);
            }
        }
    }

    /**
     * Actively watches players fishing: when a mob is hooked by a Harmonic Anchor rod, anchor it immediately!
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.fishEntity != null && player.fishEntity.caughtEntity instanceof EntityLivingBase) {
            ItemStack rod = player.getHeldItemMainhand();
            if (!ItemCheckHelper.isFishingRod(rod)) {
                rod = player.getHeldItemOffhand();
            }

            if (!rod.isEmpty() && ModEnchantments.getLevel(ModEnchantments.HARMONIC_ANCHOR, rod) > 0) {
                EntityLivingBase caught = (EntityLivingBase) player.fishEntity.caughtEntity;
                if (applyAnchor(caught, 160)) { // 8 seconds = 160 ticks
                    // Nullify pull velocity so mob does not fly to player
                    caught.motionX = 0.0D;
                    caught.motionY = 0.0D;
                    caught.motionZ = 0.0D;
                    caught.velocityChanged = true;
                }
            }
        }
    }

    /**
     * Intercepts reel-in click to ensure hooked mob stays anchored instead of flying to player.
     */
    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || player.fishEntity == null) {
            return;
        }

        ItemStack held = event.getItemStack();
        if (ItemCheckHelper.isFishingRod(held) && ModEnchantments.getLevel(ModEnchantments.HARMONIC_ANCHOR, held) > 0) {
            Entity caught = player.fishEntity.caughtEntity;
            if (caught instanceof EntityLivingBase) {
                if (applyAnchor((EntityLivingBase) caught, 160)) {
                    caught.motionX = 0.0D;
                    caught.motionY = 0.0D;
                    caught.motionZ = 0.0D;
                    caught.velocityChanged = true;
                }
            }
        }
    }
}
