package com.divineenchantments.mixin;

import com.divineenchantments.util.MudstriderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Block collision handlers (soul sand, modded mud/silt) multiply entity motion
 * inside doBlockCollisions. Capture the motion before the block pass and restore
 * it when the player has Mudstrider boots and is standing on a slow block.
 * This cancels any block's slowdown factor without hardcoding 0.4.
 */
@Mixin(Entity.class)
public abstract class MixinEntityMudstrider {

    @Unique
    private double divine$mudstriderMotionX;

    @Unique
    private double divine$mudstriderMotionZ;

    @Inject(method = "doBlockCollisions", at = @At("HEAD"))
    private void divine$captureSlowBlockMotion(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        divine$mudstriderMotionX = self.motionX;
        divine$mudstriderMotionZ = self.motionZ;
    }

    @Inject(method = "doBlockCollisions", at = @At("RETURN"))
    private void divine$restoreSlowBlockMotion(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) self;
        if (!MudstriderHelper.isActive(player) || !MudstriderHelper.onSlowGround(player)) {
            return;
        }
        self.motionX = divine$mudstriderMotionX;
        self.motionZ = divine$mudstriderMotionZ;
    }
}
