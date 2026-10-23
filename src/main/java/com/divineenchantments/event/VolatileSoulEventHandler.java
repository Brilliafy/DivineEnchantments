package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class VolatileSoulEventHandler {

    /**
     * Acts as a Totem of Undying upon death: consumes the chestplate, revives the player,
     * and detonates a colossal power 20 explosion that obliterates terrain and mobs while leaving the player unharmed.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (!chest.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_VOLATILE_SOUL, chest) > 0) {
            // Cancel death
            event.setCanceled(true);

            // Revive and restore health
            player.setHealth(4.0F); // 2 full hearts
            player.clearActivePotions();

            // Totem buffs & explosion protection buffs
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 4, false, false)); // Resistance V

            // Trigger official Totem of Undying pop animation & sound on client
            player.world.setEntityState(player, (byte) 35);
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Consume the chestplate safely (preventing Tether recursion if both are present)
            player.getEntityData().setBoolean("Divine_VolatileSoul_Consuming", true);
            try {
                player.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
                player.inventoryContainer.detectAndSendChanges();
            } finally {
                player.getEntityData().removeTag("Divine_VolatileSoul_Consuming");
            }

            // Set explosion immunity flag so the power 20 detonation deals zero damage to the player
            player.getEntityData().setBoolean("Divine_VolatileSoul_Immune", true);

            try {
                // Detonate power 20 explosion (ruins all terrain and destroys mobs)
                player.world.newExplosion(player, player.posX, player.posY + 0.5D, player.posZ, 20.0F, true, true);
            } finally {
                player.getEntityData().removeTag("Divine_VolatileSoul_Immune");
            }

            // Zero out explosion knockback on the player so they are not launched into orbit
            player.motionX = 0.0D;
            player.motionY = 0.0D;
            player.motionZ = 0.0D;
            player.velocityChanged = true;
            player.fallDistance = 0.0F;

            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
            }

            // Massive apocalyptic visual burst
            if (player.world instanceof WorldServer) {
                WorldServer ws = (WorldServer) player.world;
                ws.spawnParticle(EnumParticleTypes.TOTEM, player.posX, player.posY + 1.0D, player.posZ, 80, 0.6D, 0.8D, 0.6D, 0.3D);
                ws.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY + 1.0D, player.posZ, 5, 1.0D, 1.0D, 1.0D, 0.0D);
                ws.spawnParticle(EnumParticleTypes.LAVA, player.posX, player.posY + 0.5D, player.posZ, 40, 2.0D, 1.0D, 2.0D, 0.2D);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getEntityData().getBoolean("Divine_VolatileSoul_Immune") && event.getSource().isExplosion()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getEntityData().getBoolean("Divine_VolatileSoul_Immune") && event.getSource().isExplosion()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(net.minecraftforge.event.entity.living.LivingDamageEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getEntityData().getBoolean("Divine_VolatileSoul_Immune") && event.getSource().isExplosion()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingKnockBack(net.minecraftforge.event.entity.living.LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getEntityData().getBoolean("Divine_VolatileSoul_Immune")) {
                event.setCanceled(true);
            }
        }
    }
}
