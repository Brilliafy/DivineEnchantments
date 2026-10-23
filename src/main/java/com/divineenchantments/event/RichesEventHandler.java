package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
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

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack held = player.getHeldItemMainhand();

            if (!held.isEmpty()) {
                int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RICHES, held);
                if (level > 0) {
                    // Level 1: 0.05% (0.0005F), Level 2: 0.1% (0.001F)
                    float chance = (level == 1) ? 0.0005F : 0.001F;
                    if (player.world.rand.nextFloat() < chance) {
                        EntityLivingBase target = event.getEntityLiving();
                        target.getEntityData().setBoolean("Divine_Riches_Proc", true);

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
            event.setDroppedExperience(event.getDroppedExperience() * 10);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving() == null || event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getEntityLiving().getEntityData().getBoolean("Divine_Riches_Proc")) {
            List<EntityItem> drops = event.getDrops();
            List<EntityItem> extraDrops = new ArrayList<>();

            for (EntityItem item : drops) {
                ItemStack stack = item.getItem();
                if (stack.isEmpty()) {
                    continue;
                }

                int totalCount = stack.getCount() * 10;
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
