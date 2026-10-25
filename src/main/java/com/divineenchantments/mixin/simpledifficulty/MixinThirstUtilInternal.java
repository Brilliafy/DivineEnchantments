package com.divineenchantments.mixin.simpledifficulty;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.charles445.simpledifficulty.util.internal.ThirstUtilInternal", remap = false)
public class MixinThirstUtilInternal {

    @Inject(method = "takeDrink(Lnet/minecraft/entity/player/EntityPlayer;IFF)V", at = @At("HEAD"), remap = false)
    private void divine$markDesalinationDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance, CallbackInfo ci) {
        if (player == null || player.world.isRemote || dirtyChance <= 0.0F) {
            return;
        }
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!helmet.isEmpty() && ModEnchantments.getLevel(ModEnchantments.DESALINATION_MESH, helmet) > 0) {
            player.getEntityData().setLong("DE_DesalinationDrinkingUntil", player.world.getTotalWorldTime() + 20L);
        }
    }
}
