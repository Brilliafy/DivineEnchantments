package com.divineenchantments.client;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "divineenchantments", value = Side.CLIENT)
public class ClientCameraHandler {

    /**
     * Gyroscopic Plating: Intercepts camera setup to lock camera roll to 0.0F.
     * Immunizes the player against screen inversion, spinning, and forced roll tilt.
     */
    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            ItemStack chest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chest.isEmpty() && ModEnchantments.getLevel(ModEnchantments.GYROSCOPIC_PLATING, chest) > 0) {
                event.setRoll(0.0F);
            }
        }
    }

    /**
     * Renders informative tooltip stats on clients for Sacrificial and Heavyplated items.
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        if (ModEnchantments.getLevel(ModEnchantments.SACRIFICIAL, stack) > 0) {
            event.getToolTip().add(TextFormatting.GRAY + "Sacrificial Effects (When in Main Hand):");
            event.getToolTip().add(TextFormatting.DARK_GREEN + " +10% Attack Damage");
            event.getToolTip().add(TextFormatting.DARK_GREEN + " +10% Attack Speed");
            event.getToolTip().add(TextFormatting.DARK_GREEN + " +10% Movement Speed");
            event.getToolTip().add(TextFormatting.RED + " -10% Max Health");
            event.getToolTip().add(TextFormatting.RED + " -10% Armor");
        }

        if (ModEnchantments.getLevel(ModEnchantments.HEAVYPLATED, stack) > 0) {
            event.getToolTip().add(TextFormatting.GRAY + "Heavyplated Effects (When Equipped):");
            event.getToolTip().add(TextFormatting.DARK_GREEN + " +5% Armor");
            event.getToolTip().add(TextFormatting.RED + " -5% Movement Speed");
        }
    }
}
