package com.divineenchantments.mixin.client;

import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.LongdrawHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla hardcodes the first-person bow pull animation to 20 ticks. Longdraw
 * makes full draw take 30 ticks, so the hand/bow transform must use the same
 * stretched timeline. renderItemInFirstPerson is a Forge-added method, so its
 * name is identical in development and production and needs no refmap.
 */
@Mixin(ItemRenderer.class)
public class MixinItemRendererLongdraw {

    @Unique
    private static ItemStack divine$longdrawStack;

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At("HEAD"), require = 0, remap = false)
    private void divine$captureStack(AbstractClientPlayer player, float partialTicks, float equippedProgress,
                                     EnumHand hand, float swingProgress, ItemStack stack,
                                     float equippedProgressMainHand, CallbackInfo ci) {
        divine$longdrawStack = stack;
    }

    @ModifyConstant(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            constant = @Constant(floatValue = 20.0F), require = 0, remap = false)
    private float divine$longdrawDrawTicks(float original) {
        ItemStack stack = divine$longdrawStack;
        if (stack == null || stack.isEmpty()) {
            return original;
        }
        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.LONGDRAW, stack);
        return LongdrawHelper.drawTicks(level);
    }
}
