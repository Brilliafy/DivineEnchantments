package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


@Mod.EventBusSubscriber(modid = "divineenchantments")
public class GyroscopicPlatingEventHandler {

    /**
     * Converts vertical fall momentum directly into horizontal forward velocity when sneaking on landing.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (!chest.isEmpty() && ModEnchantments.getLevel(ModEnchantments.GYROSCOPIC_PLATING, chest) > 0) {
            float fallDist = event.getDistance();

            if (fallDist > 4.0F && player.isSneaking()) {
                // Cancel fall damage completely
                event.setCanceled(true);
                event.setDistance(0.0F);
                event.setDamageMultiplier(0.0F);
                player.fallDistance = 0.0F;

                // v_horizontal = sqrt(2 * g * h), where g = 0.08 blocks/tick^2
                double vHorizontal = Math.sqrt(2.0D * 0.08D * fallDist);

                Vec3d look = player.getLookVec();
                double hLen = Math.sqrt(look.x * look.x + look.z * look.z);
                double dirX, dirZ;
                if (hLen > 0.001D) {
                    dirX = look.x / hLen;
                    dirZ = look.z / hLen;
                } else {
                    float yawRad = (float) Math.toRadians(player.rotationYaw);
                    dirX = -Math.sin(yawRad);
                    dirZ = Math.cos(yawRad);
                }

                player.motionX = dirX * vHorizontal;
                player.motionZ = dirZ * vHorizontal;
                player.motionY = 0.08D; // Small lift to prevent immediate ground friction dampening
                player.velocityChanged = true;

                if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                }

                // Audio & visual slingshot feedback
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 1.2F, 1.2F);
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.PLAYERS, 0.9F, 1.6F);

                if (!player.world.isRemote && player.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) player.world;
                    ws.spawnParticle(EnumParticleTypes.CLOUD, player.posX, player.posY + 0.1D, player.posZ, 20, 0.5D, 0.1D, 0.5D, 0.08D);
                    ws.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, player.posX, player.posY + 0.1D, player.posZ, 15, 0.3D, 0.1D, 0.3D, 0.05D);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        boolean active = !chest.isEmpty()
                && ModEnchantments.getLevel(ModEnchantments.GYROSCOPIC_PLATING, chest) > 0
                && player.isSneaking();
        if (active) {
            suppressCameraEffects(player);
        } else {
            restoreCameraEffects(player);
        }
    }

    private static void suppressCameraEffects(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (data.hasKey("DE_GyroSuppressed") && data.getTagList("DE_GyroSuppressed", 10).tagCount() > 0) {
            return;
        }
        NBTTagList stored = new NBTTagList();
        long now = player.world.getTotalWorldTime();
        for (PotionEffect effect : new java.util.ArrayList<>(player.getActivePotionEffects())) {
            if (!isCameraDistorting(effect.getPotion())) {
                continue;
            }
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("id", effect.getPotion().getRegistryName().toString());
            entry.setInteger("duration", effect.getDuration());
            entry.setInteger("amplifier", effect.getAmplifier());
            entry.setLong("start", now);
            stored.appendTag(entry);
            player.removePotionEffect(effect.getPotion());
        }
        if (stored.tagCount() > 0) {
            data.setTag("DE_GyroSuppressed", stored);
        }
    }

    private static void restoreCameraEffects(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey("DE_GyroSuppressed", 9)) {
            return;
        }
        NBTTagList stored = data.getTagList("DE_GyroSuppressed", 10);
        long now = player.world.getTotalWorldTime();
        for (int i = 0; i < stored.tagCount(); i++) {
            NBTTagCompound entry = stored.getCompoundTagAt(i);
            int remaining = entry.getInteger("duration") - (int) (now - entry.getLong("start"));
            if (remaining <= 0) {
                continue;
            }
            Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(entry.getString("id")));
            if (potion != null) {
                player.addPotionEffect(new PotionEffect(potion, remaining, entry.getInteger("amplifier"), false, true));
            }
        }
        data.removeTag("DE_GyroSuppressed");
    }

    private static boolean isCameraDistorting(Potion potion) {
        if (potion == null || potion.getRegistryName() == null) {
            return false;
        }
        String path = potion.getRegistryName().getPath().toLowerCase();
        return path.equals("nausea") || path.equals("instability") || path.equals("spin")
                || path.equals("perplexity") || path.equals("invert") || path.equals("inversion");
    }

}
