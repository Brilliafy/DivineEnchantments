package com.divineenchantments.util;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Mudstrider support. Block onEntityCollision handlers (soul sand, modded
 * mud/silt) multiply entity motion. LivingUpdateEvent fires before
 * doBlockCollisions, so pre-multiplying by the inverse factor cancels the
 * slowdown in the same tick without a vanilla-class mixin.
 */
public final class MudstriderHelper {

    private static final Map<Block, Double> SLOW_FACTORS = new IdentityHashMap<Block, Double>();

    private MudstriderHelper() {
    }

    public static boolean isActive(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return !boots.isEmpty() && ModEnchantments.getLevel(ModEnchantments.MUDSTRIDER, boots) > 0;
    }

    /**
     * Returns the horizontal motion multiplier that the block under the player's
     * feet applies, or 1.0 if the block is not a Mudstrider slow block.
     */
    public static double slowFactorAtFeet(EntityPlayer player) {
        if (player == null || player.world == null) {
            return 1.0D;
        }
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        if (!isSlowBlock(block)) {
            return 1.0D;
        }

        Double cached = SLOW_FACTORS.get(block);
        if (cached != null) {
            return cached;
        }

        double factor = 1.0D;
        try {
            EntityZombie probe = new EntityZombie(player.world);
            probe.motionX = 1.0D;
            probe.motionY = 0.0D;
            probe.motionZ = 1.0D;
            block.onEntityCollision(player.world, pos, state, probe);
            factor = MathHelper.clamp(probe.motionX, 0.05D, 1.0D);
        } catch (Throwable ignored) {
        }
        SLOW_FACTORS.put(block, factor);
        return factor;
    }

    public static boolean isSlowBlock(Block block) {
        if (block == Blocks.SOUL_SAND) {
            return true;
        }
        ResourceLocation name = block.getRegistryName();
        if (name == null) {
            return false;
        }
        String path = name.getPath().toLowerCase(Locale.ROOT);
        return path.contains("mud") || path.contains("silt");
    }
}
