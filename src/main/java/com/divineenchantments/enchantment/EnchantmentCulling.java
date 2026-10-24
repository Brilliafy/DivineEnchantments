package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentCulling extends DivineEnchantment {

    public EnchantmentCulling() {
        super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.culling");
        this.setRegistryName("divineenchantments", "culling");
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
        return ItemCheckHelper.isAxeOrDagger(stack);
    }
}
