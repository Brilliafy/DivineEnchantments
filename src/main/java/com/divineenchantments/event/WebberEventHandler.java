package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ChanceHelper;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class WebberEventHandler {

    /**
     * Instantly breaks cobwebs when using a sword enchanted with Webber.
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getState().getBlock() == Blocks.WEB) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null) {
                ItemStack held = player.getHeldItemMainhand();
                if (ItemCheckHelper.isSword(held)) {
                    if (ModEnchantments.getLevel(ModEnchantments.WEBBER, held) > 0) {
                        event.setNewSpeed(Float.MAX_VALUE);
                    }
                }
            }
        }
    }

    /**
     * Ensures instant breaking on left-click tap as well.
     */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        if (world.getBlockState(pos).getBlock() == Blocks.WEB) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null && !player.isCreative()) {
                ItemStack held = player.getHeldItemMainhand();
                if (ItemCheckHelper.isSword(held)) {
                    if (ModEnchantments.getLevel(ModEnchantments.WEBBER, held) > 0) {
                        if (!world.isRemote && player instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) player).interactionManager.tryHarvestBlock(pos);
                        }
                    }
                }
            }
        }
    }

    /**
     * 0.4% chance on hitting an enemy to spawn a cobweb on the enemy.
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack held = player.getHeldItemMainhand();

            if (ItemCheckHelper.isSword(held)) {
                if (ModEnchantments.getLevel(ModEnchantments.WEBBER, held) > 0) {
                    // 0.4% chance
                    if (ChanceHelper.roll(player, "webber", 0.004F)) {
                        EntityLivingBase target = event.getEntityLiving();
                        spawnCobwebOn(target);
                    }
                }
            }
        }
    }

    private static void spawnCobwebOn(EntityLivingBase target) {
        if (target == null || target.world.isRemote) {
            return;
        }

        World world = target.world;
        BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);

        if (!world.isAirBlock(pos) && !world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            pos = pos.up();
        }

        if (world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            world.setBlockState(pos, Blocks.WEB.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

            if (world instanceof WorldServer) {
                WorldServer ws = (WorldServer) world;
                int webStateId = Block.getStateId(Blocks.WEB.getDefaultState());
                ws.spawnParticle(
                        EnumParticleTypes.BLOCK_CRACK,
                        target.posX,
                        target.posY + target.height * 0.5D,
                        target.posZ,
                        25,
                        0.3D,
                        0.3D,
                        0.3D,
                        0.05D,
                        webStateId
                );
            }
        }
    }
}
