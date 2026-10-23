package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentQuietChipping extends Enchantment {

    public EnchantmentQuietChipping() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.quiet_chipping");
        this.setRegistryName("divineenchantments", "quiet_chipping");
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
    public int getMinEnchantability(int level) {
        return 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 40;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return ItemCheckHelper.isPickaxeOrHammer(stack);
    }
}
