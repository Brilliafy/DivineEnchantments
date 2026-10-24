package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentDulledImpact extends DivineEnchantment {

    public EnchantmentDulledImpact() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.dulled_impact");
        this.setRegistryName("divineenchantments", "dulled_impact");
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
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isBluntWeapon(stack);
    }
}
