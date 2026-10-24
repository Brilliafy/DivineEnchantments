package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentSiltSifter extends DivineEnchantment {

    public EnchantmentSiltSifter() {
        super(Rarity.COMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.silt_sifter");
        this.setRegistryName("divineenchantments", "silt_sifter");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof ItemSpade) return true;
        ResourceLocation reg = stack.getItem().getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("shovel") || path.contains("spade")) return true;
        }
        return stack.getItem().getClass().getName().toLowerCase().contains("shovel");
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return canApply(stack);
    }
}
