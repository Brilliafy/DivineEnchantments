package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentLowSweep extends Enchantment {

    public EnchantmentLowSweep() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.low_sweep");
        this.setRegistryName("divineenchantments", "low_sweep");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinEnchantability(int level) {
        return 8 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 25;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return ItemCheckHelper.isPolearmOrHalberd(stack) || ItemCheckHelper.isMeleeWeapon(stack);
    }
}
