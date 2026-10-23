package com.divineenchantments.event;

import com.divineenchantments.client.EnlargementClientData;
import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.enlargement.EnlargementSync;
import com.divineenchantments.potion.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class EnlargementEventHandler {

    private static final String TAG_ENLARGED = "DE_Enlarged";
    private static final String TAG_BASE_WIDTH = "DE_EnlargeBaseWidth";
    private static final String TAG_BASE_HEIGHT = "DE_EnlargeBaseHeight";

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null) {
            return;
        }

        NBTTagCompound data = entity.getEntityData();

        if (entity.world.isRemote) {
            float factor = EnlargementClientData.getFactor(entity, entity.world.getTotalWorldTime());
            if (factor >= 0.0F) {
                applyScale(entity, data, factor);
            } else if (data.getBoolean(TAG_ENLARGED)) {
                restoreScale(entity, data);
            }
            return;
        }

        PotionEffect effect = entity.getActivePotionEffect(ModPotions.ENLARGEMENT);

        if (effect != null) {
            float factor = 1.0F + (float) DivineConfig.enlargementSizePerLevel * (effect.getAmplifier() + 1);
            applyScale(entity, data, factor);
            EnlargementSync.sync(entity, effect);
        } else if (data.getBoolean(TAG_ENLARGED)) {
            restoreScale(entity, data);
            EnlargementSync.clear(entity);
        } else {
            EnlargementSync.clear(entity);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity != null) {
            NBTTagCompound data = entity.getEntityData();
            if (data.getBoolean(TAG_ENLARGED)) {
                restoreScale(entity, data);
            }
            EnlargementSync.clear(entity);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            EnlargementClientData.clearAll();
        }
    }

    private static void applyScale(EntityLivingBase entity, NBTTagCompound data, float factor) {
        if (!data.getBoolean(TAG_ENLARGED)) {
            data.setFloat(TAG_BASE_WIDTH, entity.width);
            data.setFloat(TAG_BASE_HEIGHT, entity.height);
            data.setBoolean(TAG_ENLARGED, true);
        }

        float baseWidth = data.getFloat(TAG_BASE_WIDTH);
        float baseHeight = data.getFloat(TAG_BASE_HEIGHT);
        if (baseWidth <= 0.0F || baseHeight <= 0.0F) {
            baseWidth = entity.width;
            baseHeight = entity.height;
        }

        float newWidth = baseWidth * factor;
        float newHeight = baseHeight * factor;

        if (Math.abs(entity.width - newWidth) > 1.0E-4F || Math.abs(entity.height - newHeight) > 1.0E-4F) {
            AxisAlignedBB box = entity.getEntityBoundingBox();
            double centerX = (box.minX + box.maxX) * 0.5D;
            double centerZ = (box.minZ + box.maxZ) * 0.5D;
            double minY = box.minY;

            entity.width = newWidth;
            entity.height = newHeight;
            entity.setEntityBoundingBox(new AxisAlignedBB(
                    centerX - newWidth * 0.5D, minY, centerZ - newWidth * 0.5D,
                    centerX + newWidth * 0.5D, minY + newHeight, centerZ + newWidth * 0.5D));
        }
    }

    private static void restoreScale(EntityLivingBase entity, NBTTagCompound data) {
        float baseWidth = data.getFloat(TAG_BASE_WIDTH);
        float baseHeight = data.getFloat(TAG_BASE_HEIGHT);
        if (baseWidth > 0.0F && baseHeight > 0.0F) {
            AxisAlignedBB box = entity.getEntityBoundingBox();
            double centerX = (box.minX + box.maxX) * 0.5D;
            double centerZ = (box.minZ + box.maxZ) * 0.5D;
            double minY = box.minY;

            entity.width = baseWidth;
            entity.height = baseHeight;
            entity.setEntityBoundingBox(new AxisAlignedBB(
                    centerX - baseWidth * 0.5D, minY, centerZ - baseWidth * 0.5D,
                    centerX + baseWidth * 0.5D, minY + baseHeight, centerZ + baseWidth * 0.5D));
        }
        data.removeTag(TAG_ENLARGED);
        data.removeTag(TAG_BASE_WIDTH);
        data.removeTag(TAG_BASE_HEIGHT);
    }
}
