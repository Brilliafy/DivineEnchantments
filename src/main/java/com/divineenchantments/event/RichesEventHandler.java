package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ChanceHelper;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class RichesEventHandler {

    private static Enchantment ADEPT = null;
    private static boolean LOOKED_UP_ADEPT = false;

    private static Enchantment getAdept() {
        if (!LOOKED_UP_ADEPT) {
            LOOKED_UP_ADEPT = true;
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                ResourceLocation name = enchantment.getRegistryName();
                if (name != null && "adept".equals(name.getPath())) {
                    ADEPT = enchantment;
                    break;
                }
            }
        }
        return ADEPT;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack held = player.getHeldItemMainhand();

            if (!held.isEmpty()) {
                int level = ModEnchantments.getLevel(ModEnchantments.RICHES, held);
                if (level > 0) {
                    EntityLivingBase target = event.getEntityLiving();
                    if (ItemCheckHelper.isBossEntity(target) || !target.isNonBoss()) {
                        return;
                    }
                    Enchantment adeptEnchantment = getAdept();
                    int adeptLevel = adeptEnchantment == null ? 0 : EnchantmentHelper.getEnchantmentLevel(adeptEnchantment, held);

                    // Base chance is 10% / 20%; Adept adds a chance bonus and extra XP.
                    float baseChance = (level == 1) ? 0.10F : 0.20F;
                    float chance = baseChance * (1.0F + 0.25F * adeptLevel);
                    if (ChanceHelper.roll(player, "riches", chance)) {
                        target.getEntityData().setBoolean("Divine_Riches_Proc", true);
                        target.getEntityData().setInteger("Divine_Riches_XpMultiplier", Math.min(50, 10 + adeptLevel * 2));
                        target.getEntityData().setInteger("Divine_Riches_LootMultiplier", 10);

                        // Grand sound & celebration particles
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.2F, 1.0F);
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.7F, 1.4F);

                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.TOTEM, target.posX, target.posY + 1.0D, target.posZ, 35, 0.6D, 0.6D, 0.6D, 0.2D);
                            ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, target.posX, target.posY + 1.0D, target.posZ, 25, 0.8D, 0.8D, 0.8D, 0.1D);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntityLiving() != null && event.getEntityLiving().getEntityData().getBoolean("Divine_Riches_Proc")) {
            int multiplier = event.getEntityLiving().getEntityData().getInteger("Divine_Riches_XpMultiplier");
            event.setDroppedExperience(event.getDroppedExperience() * Math.max(10, multiplier));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving() == null || event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getEntityLiving().getEntityData().getBoolean("Divine_Riches_Proc")) {
            int multiplier = Math.max(10, event.getEntityLiving().getEntityData().getInteger("Divine_Riches_LootMultiplier"));
            List<EntityItem> drops = event.getDrops();
            List<EntityItem> extraDrops = new ArrayList<>();

            for (EntityItem item : drops) {
                ItemStack stack = item.getItem();
                if (stack.isEmpty()) {
                    continue;
                }

                int totalCount = stack.getCount() * multiplier;
                int maxStack = stack.getMaxStackSize();

                stack.setCount(Math.min(totalCount, maxStack));
                int remaining = totalCount - stack.getCount();

                while (remaining > 0) {
                    ItemStack extraStack = stack.copy();
                    int countForNew = Math.min(remaining, maxStack);
                    extraStack.setCount(countForNew);
                    remaining -= countForNew;

                    EntityItem extraEntityItem = new EntityItem(item.world, item.posX, item.posY, item.posZ, extraStack);
                    extraEntityItem.motionX = item.motionX;
                    extraEntityItem.motionY = item.motionY;
                    extraEntityItem.motionZ = item.motionZ;
                    extraEntityItem.setDefaultPickupDelay();
                    extraDrops.add(extraEntityItem);
                }
            }

            drops.addAll(extraDrops);
        }
    }
}
