package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class GroundedVanguardEventHandler {

    private static Potion SOLID_CORE_POTION = null;
    private static Potion WEIGHT_POTION = null;
    private static boolean CHECKED_POTIONS = false;

    private static void checkPotions() {
        if (!CHECKED_POTIONS) {
            CHECKED_POTIONS = true;
            SOLID_CORE_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "solid_core"));
            WEIGHT_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "weight"));
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        boolean hasGrounded = !boots.isEmpty() && ModEnchantments.getLevel(ModEnchantments.GROUNDED_VANGUARD, boots) > 0;

        if (hasGrounded && player.isSneaking()) {
            checkPotions();

            // Dismount if being lifted or carried by flying entities (Rocs, Raikos, Djinns)
            if (player.isRiding()) {
                player.dismountRidingEntity();
            }
            if (!player.getPassengers().isEmpty()) {
                player.removePassengers();
            }

            // Apply solid_core (anti-knockback) and weight III (heavy downward gravity)
            if (SOLID_CORE_POTION != null) {
                player.addPotionEffect(new PotionEffect(SOLID_CORE_POTION, 40, 0, false, false));
            }
            if (WEIGHT_POTION != null) {
                player.addPotionEffect(new PotionEffect(WEIGHT_POTION, 40, 2, false, false)); // Weight III
            }

            // Fallback downward anchoring
            if (player.motionY > 0.0D) {
                player.motionY = 0.0D;
            }
            player.motionY -= 0.06D;
            player.fallDistance = 0.0F;

            player.getEntityData().setBoolean("DE_GroundedVanguardActive", true);
        } else {
            // Releasing sneak smoothly purges the effect without fall damage
            if (player.getEntityData().getBoolean("DE_GroundedVanguardActive")) {
                player.getEntityData().setBoolean("DE_GroundedVanguardActive", false);
                player.getEntityData().setInteger("DE_GroundedGrace", 20); // 1s grace to land safely
                player.fallDistance = 0.0F;

                checkPotions();
                if (SOLID_CORE_POTION != null && player.isPotionActive(SOLID_CORE_POTION)) {
                    player.removePotionEffect(SOLID_CORE_POTION);
                }
                if (WEIGHT_POTION != null && player.isPotionActive(WEIGHT_POTION)) {
                    player.removePotionEffect(WEIGHT_POTION);
                }
            }
        }

        int grace = player.getEntityData().getInteger("DE_GroundedGrace");
        if (grace > 0) {
            player.getEntityData().setInteger("DE_GroundedGrace", grace - 1);
            player.fallDistance = 0.0F;
        }
    }

    /**
     * Prevents fall damage when transitioning from Grounded Vanguard sneak.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.getEntityData().getBoolean("DE_GroundedVanguardActive") || player.getEntityData().getInteger("DE_GroundedGrace") > 0) {
                event.setCanceled(true);
                event.setDistance(0.0F);
                player.fallDistance = 0.0F;
            }
        }
    }
}
