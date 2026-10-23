package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class PerpetualCrucibleEventHandler {

    private static Potion EXTENSION_POTION = null;
    private static boolean CHECKED_POTION = false;

    private static Potion getExtensionPotion() {
        if (!CHECKED_POTION) {
            CHECKED_POTION = true;
            EXTENSION_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "extension"));
        }
        return EXTENSION_POTION;
    }

    /**
     * Landing critical hits grants the player Extension for 3 seconds (60 ticks).
     */
    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (event.getEntityPlayer().world.isRemote) {
            return;
        }

        if (event.getResult() == Event.Result.ALLOW || (event.getResult() == Event.Result.DEFAULT && event.isVanillaCritical())) {
            EntityPlayer player = event.getEntityPlayer();
            triggerPerpetualCrucible(player);
        }
    }

    /**
     * Killing a mob grants the player Extension for 3 seconds.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntityLiving() != event.getSource().getTrueSource()) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            triggerPerpetualCrucible(player);
        }
    }

    private static void triggerPerpetualCrucible(EntityPlayer player) {
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty() || ModEnchantments.getLevel(ModEnchantments.PERPETUAL_CRUCIBLE, chest) == 0) {
            return;
        }

        Potion extension = getExtensionPotion();
        if (extension != null) {
            // Extension II (amplifier 1) for 60 ticks (3 seconds)
            player.addPotionEffect(new PotionEffect(extension, 60, 1, false, true));
        } else {
            // Standalone fallback: Freeze active positive potion timers by adding 60 ticks
            List<PotionEffect> positiveEffects = new ArrayList<>();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (!effect.getPotion().isBadEffect()) {
                    positiveEffects.add(effect);
                }
            }

            for (PotionEffect effect : positiveEffects) {
                player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration() + 60, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
            }
        }

        // Visual and auditory feedback
        if (player.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) player.world;
            ws.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, player.posX, player.posY + 1.0D, player.posZ, 6, 0.25D, 0.25D, 0.25D, 0.05D);
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.PLAYERS, 0.5F, 1.5F);
    }
}
