package com.divineenchantments.client;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.init.ModEnchantments;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "divineenchantments", value = Side.CLIENT)
public class FeatheredDrawClientHandler {

    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.isRiding() || !player.isHandActive()) {
            return;
        }
        ItemStack active = player.getActiveItemStack();
        if (active.isEmpty() || !(active.getItem() instanceof ItemBow)) {
            return;
        }
        int level = ModEnchantments.getLevel(ModEnchantments.FEATHERED_DRAW, active);
        if (level <= 0) {
            return;
        }

        double compensation = level == 1 ? DivineConfig.featheredDrawSpeedLevel1
                : (level == 2 ? DivineConfig.featheredDrawSpeedLevel2
                : (level == 3 ? DivineConfig.featheredDrawSpeedLevel3 : DivineConfig.featheredDrawSpeedLevel4));
        float multiplier = (float) (1.0D + compensation);
        MovementInput input = event.getMovementInput();
        input.moveStrafe *= multiplier;
        input.moveForward *= multiplier;
    }
}
