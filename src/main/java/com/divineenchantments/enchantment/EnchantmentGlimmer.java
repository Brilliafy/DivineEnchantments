package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentGlimmer extends Enchantment {

    public EnchantmentGlimmer() {
        super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.glimmer");
        this.setRegistryName("divineenchantments", "glimmer");
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
        return 5;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 30;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return ItemCheckHelper.isBowOrCrossbow(stack);
    }
}
