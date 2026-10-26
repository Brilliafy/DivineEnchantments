package com.divineenchantments.util;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;

/**
 * Shared Mudstrider checks. Slow blocks are countered by restoring the
 * pre-collision motion in MixinEntityMudstrider, which works for any block
 * that multiplies entity motion (soul sand and modded mud/silt included).
 */
public final class MudstriderHelper {

    private MudstriderHelper() {
    }

    public static boolean isActive(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return !boots.isEmpty() && ModEnchantments.getLevel(ModEnchantments.MUDSTRIDER, boots) > 0;
    }

    public static boolean onSlowGround(EntityPlayer player) {
        if (player == null || player.world == null) {
            return false;
        }
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        return isSlowBlock(player.world.getBlockState(pos).getBlock())
                || isSlowBlock(player.world.getBlockState(pos.down()).getBlock());
    }

    private static boolean isSlowBlock(Block block) {
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
