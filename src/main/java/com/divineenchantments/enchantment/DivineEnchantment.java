package com.divineenchantments.enchantment;

import com.divineenchantments.config.DivineConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class DivineEnchantment extends Enchantment {

    protected DivineEnchantment(Rarity rarity, EnumEnchantmentType type, EntityEquipmentSlot[] slots) {
        super(rarity, type, slots);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return DivineConfig.isEnabled(this) && super.canApply(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return DivineConfig.isEnabled(this) && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return DivineConfig.isEnabled(this) && super.isAllowedOnBooks();
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        int min = EnchantingTableTiers.getMinEnchantability(this, enchantmentLevel);
        return min >= 0 ? min : super.getMinEnchantability(enchantmentLevel);
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        int max = EnchantingTableTiers.getMaxEnchantability(this, enchantmentLevel);
        return max >= 0 ? max : super.getMaxEnchantability(enchantmentLevel);
    }
}
