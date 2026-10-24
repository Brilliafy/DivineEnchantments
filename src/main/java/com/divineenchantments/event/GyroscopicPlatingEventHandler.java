package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


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

}
