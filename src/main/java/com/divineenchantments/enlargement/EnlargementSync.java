package com.divineenchantments.enlargement;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.network.PacketEnlargement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

public class EnlargementSync {

    private static final String TAG_ACTIVE = "DE_EnlargeSyncActive";
    private static final String TAG_AMP = "DE_EnlargeSyncAmp";
    private static final String TAG_EXPIRY = "DE_EnlargeSyncExpiry";
    private static final String TAG_LAST_SYNC = "DE_EnlargeLastSync";

    public static void sync(EntityLivingBase entity, PotionEffect effect) {
        if (entity == null || entity.world.isRemote || effect == null) {
            return;
        }

        NBTTagCompound data = entity.getEntityData();
        long now = entity.world.getTotalWorldTime();
        long expiry = now + effect.getDuration();
        int lastAmp = data.getInteger(TAG_AMP);
        long lastExpiry = data.getLong(TAG_EXPIRY);
        long lastSync = data.getLong(TAG_LAST_SYNC);
        boolean active = data.getBoolean(TAG_ACTIVE);

        // Resend periodically so players who just started tracking the entity or
        // changed dimensions still receive the visual size sync.
        if (!active || lastAmp != effect.getAmplifier() || expiry > lastExpiry + 20L
                || now - lastSync >= 40L) {
            data.setBoolean(TAG_ACTIVE, true);
            data.setInteger(TAG_AMP, effect.getAmplifier());
            data.setLong(TAG_EXPIRY, expiry);
            data.setLong(TAG_LAST_SYNC, now);
            float scaleFactor = 1.0F + (float) DivineConfig.enlargementSizePerLevel * (effect.getAmplifier() + 1);
            com.divineenchantments.network.ModNetwork.sendToAllTracking(
                    new PacketEnlargement(entity.getEntityId(), scaleFactor, expiry, false), entity);
        }
    }

    public static void clear(EntityLivingBase entity) {
        if (entity == null || entity.world.isRemote) {
            return;
        }
        NBTTagCompound data = entity.getEntityData();
        if (data.getBoolean(TAG_ACTIVE)) {
            data.setBoolean(TAG_ACTIVE, false);
            data.removeTag(TAG_AMP);
            data.removeTag(TAG_EXPIRY);
            data.removeTag(TAG_LAST_SYNC);
            com.divineenchantments.network.ModNetwork.sendToAllTracking(
                    new PacketEnlargement(entity.getEntityId(), 0, 0L, true), entity);
        }
    }
}
