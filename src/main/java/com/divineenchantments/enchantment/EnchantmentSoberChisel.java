package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentSoberChisel extends Enchantment {

    public EnchantmentSoberChisel() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.sober_chisel");
        this.setRegistryName("divineenchantments", "sober_chisel");
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
    public int getMinEnchantability(int enchantmentLevel) {
        return 12;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 45;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof ItemPickaxe) return true;
        ResourceLocation reg = stack.getItem().getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("pickaxe")) return true;
        return stack.getItem().getClass().getName().toLowerCase().contains("pickaxe");
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }
}
