package com.divineenchantments.compat;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityAegis;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class LycanitesIntegration {

    private static Potion DECAY_POTION = null;
    private static boolean CHECKED_DECAY = false;

    private static Potion APHAGIA_POTION = null;
    private static boolean CHECKED_APHAGIA = false;

    public static Potion getDecayPotion() {
        if (!CHECKED_DECAY) {
            CHECKED_DECAY = true;
            DECAY_POTION = Potion.REGISTRY.getObject(new ResourceLocation("lycanitesmobs", "decay"));
        }
        return DECAY_POTION;
    }

    public static Potion getAphagiaPotion() {
        if (!CHECKED_APHAGIA) {
            CHECKED_APHAGIA = true;
            APHAGIA_POTION = Potion.REGISTRY.getObject(new ResourceLocation("lycanitesmobs", "aphagia"));
        }
        return APHAGIA_POTION;
    }

    /**
     * Checks if a living entity is a Lycanites Mobs creature classified as a Demon or having the Shadow element.
     */
    public static boolean isDemonOrShadowCreature(EntityLivingBase entity) {
        if (entity == null) {
            return false;
        }

        try {
            if (entity instanceof BaseCreatureEntity) {
                BaseCreatureEntity creature = (BaseCreatureEntity) entity;
                if (creature.creatureInfo != null) {
                    if (creature.creatureInfo.creatureType != null && "demon".equalsIgnoreCase(creature.creatureInfo.creatureType.getName())) {
                        return true;
                    }
                    if (creature.creatureInfo.elements != null) {
                        for (ElementInfo element : creature.creatureInfo.elements) {
                            if (element != null && "shadow".equalsIgnoreCase(element.name)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        ResourceLocation key = EntityList.getKey(entity);
        if (key != null && "lycanitesmobs".equals(key.getNamespace())) {
            String path = key.getPath().toLowerCase();
            return path.contains("belph") || path.contains("cacodemon") || path.contains("rahovart")
                    || path.contains("behemoth") || path.contains("archvile") || path.contains("pinky")
                    || path.contains("vorach") || path.contains("grue") || path.contains("epion")
                    || path.contains("darkling") || path.contains("geist") || path.contains("amalgalich")
                    || path.contains("warg") || path.contains("asmodeus");
        }

        return false;
    }

    public static boolean isBoss(net.minecraft.entity.Entity entity) {
        if (entity instanceof BaseCreatureEntity) {
            try {
                return ((BaseCreatureEntity) entity).isBoss();
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    /**
     * Spawns a friendly ephemeral Aegis minion owned by the player,
     * configured in Assist mode (follows player and attacks what hits the player or what the player hits).
     */
    public static void spawnAegis(EntityPlayer player, EntityLivingBase target) {
        if (player == null || player.world.isRemote) {
            return;
        }

        World world = player.world;

        try {
            EntityAegis aegis = new EntityAegis(world);

            // Spawn near player
            double angle = world.rand.nextDouble() * Math.PI * 2.0D;
            double distance = 1.0D + world.rand.nextDouble() * 1.5D;
            double spawnX = player.posX + Math.cos(angle) * distance;
            double spawnY = player.posY;
            double spawnZ = player.posZ + Math.sin(angle) * distance;

            aegis.setPosition(spawnX, spawnY, spawnZ);

            // Configure minion and ephemeral duration (1200 ticks = 60s, same as Lycanites summoning staff)
            aegis.setMinion(true);
            aegis.setTemporary(1200);

            // Set ownership
            aegis.setPlayerOwner(player);

            // Mode: Assist (follows player, attacks what hits player or what player hits)
            aegis.setSitting(false);
            aegis.setFollowing(true);
            aegis.setPassive(false);
            aegis.setAssist(true);
            aegis.setAggressive(false);
            aegis.setPVP(true);

            aegis.onFirstSpawn();

            // Direct attention to current target if available
            if (target != null && target.isEntityAlive() && target != player) {
                aegis.setAttackTarget(target);
            }

            // Spawn particles and sound
            if (world instanceof WorldServer) {
                WorldServer ws = (WorldServer) world;
                ws.spawnParticle(EnumParticleTypes.SPELL_WITCH, spawnX, spawnY + 1.0D, spawnZ, 30, 0.4D, 0.6D, 0.4D, 0.1D);
                ws.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, spawnX, spawnY + 1.0D, spawnZ, 20, 0.3D, 0.5D, 0.3D, 0.05D);
            }

            world.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, SoundCategory.PLAYERS, 1.0F, 1.3F);

            // Spawn into world
            world.spawnEntity(aegis);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
