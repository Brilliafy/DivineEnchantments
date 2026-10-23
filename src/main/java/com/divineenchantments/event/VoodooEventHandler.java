package com.divineenchantments.event;

import com.divineenchantments.entity.EntityVoodooAlly;
import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class VoodooEventHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase dead = event.getEntityLiving();
        if (dead == null || dead.world.isRemote || dead instanceof EntityVoodooAlly) {
            return;
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack held = player.getHeldItemMainhand();
        int level = held.isEmpty() ? 0 : EnchantmentHelper.getEnchantmentLevel(ModEnchantments.VOODOO, held);
        if (level <= 0 || player.world.rand.nextFloat() >= 0.02F * level) {
            return;
        }

        World world = player.world;
        EntityVoodooAlly ally = new EntityVoodooAlly(world);
        ally.setLocationAndAngles(dead.posX, dead.posY, dead.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
        ally.setOwner(player, level);
        world.spawnEntity(ally);
        ally.setPositionAndUpdate(dead.posX, dead.posY, dead.posZ);

        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    dead.posX, dead.posY + 0.15D, dead.posZ, 30, 0.45D, 0.25D, 0.45D, 0.03D);
            ws.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                    dead.posX, dead.posY + 0.6D, dead.posZ, 24, 0.4D, 0.55D, 0.4D, 0.05D);
            ws.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    dead.posX, dead.posY + 0.2D, dead.posZ, 6, 0.3D, 0.1D, 0.3D, 0.02D);
        }
        world.playSound(null, dead.posX, dead.posY, dead.posZ,
                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 1.0F, 0.7F);
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
        if (attacker == player) {
            return;
        }

        double radius = 32.0D;
        AxisAlignedBB box = player.getEntityBoundingBox().grow(radius);
        List<EntityVoodooAlly> allies = EntityVoodooAlly.findAllies(player.world, player, box);
        for (EntityVoodooAlly ally : allies) {
            ally.orderAttack(attacker);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        if (player == null || player.world.isRemote || player.getServer() == null) {
            return;
        }

        World oldWorld = player.getServer().getWorld(event.fromDim);
        World newWorld = player.getServer().getWorld(event.toDim);
        if (oldWorld == null || newWorld == null) {
            return;
        }

        List<EntityVoodooAlly> allies = oldWorld.getEntities(EntityVoodooAlly.class,
                ally -> ally != null && ally.isEntityAlive() && ally.isOwner(player));

        for (EntityVoodooAlly oldAlly : allies) {
            EntityVoodooAlly newAlly = new EntityVoodooAlly(newWorld);
            NBTTagCompound tag = new NBTTagCompound();
            oldAlly.writeEntityToNBT(tag);
            newAlly.readEntityFromNBT(tag);

            double x = player.posX + (newWorld.rand.nextDouble() - 0.5D) * 3.0D;
            double z = player.posZ + (newWorld.rand.nextDouble() - 0.5D) * 3.0D;
            newAlly.setLocationAndAngles(x, player.posY, z, newWorld.rand.nextFloat() * 360.0F, 0.0F);
            newWorld.spawnEntity(newAlly);
            newAlly.setPositionAndUpdate(x, player.posY, z);

            oldWorld.removeEntity(oldAlly);
            oldAlly.setDead();

            if (newWorld instanceof WorldServer) {
                ((WorldServer) newWorld).spawnParticle(EnumParticleTypes.PORTAL,
                        x, player.posY + 1.0D, z, 24, 0.35D, 0.6D, 0.35D, 0.08D);
            }
        }
    }
}
