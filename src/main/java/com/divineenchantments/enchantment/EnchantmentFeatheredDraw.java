package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class EnchantmentFeatheredDraw extends Enchantment {

    public EnchantmentFeatheredDraw() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.feathered_draw");
        this.setRegistryName("divineenchantments", "feathered_draw");
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
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemBow;
    }
}
