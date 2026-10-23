package com.divineenchantments.mixin.bettersurvival;

import com.divineenchantments.compat.BetterSurvivalRangeCompat;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.mujmajnkraft.bettersurvival.enchantments.EnchantmentRange", remap = false)
public abstract class MixinEnchantmentRange {

    @Inject(method = "modifyArrow", at = @At("HEAD"), remap = false)
    private static void divineenchantments$beforeRangeBoost(EntityArrow arrow, CallbackInfo ci) {
        BetterSurvivalRangeCompat.beforeRangeBoost(arrow);
    }

    @Inject(method = "modifyArrow", at = @At("TAIL"), remap = false)
    private static void divineenchantments$afterRangeBoost(EntityArrow arrow, CallbackInfo ci) {
        BetterSurvivalRangeCompat.afterRangeBoost(arrow);
    }
}
