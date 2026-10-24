package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentSalvageLine extends DivineEnchantment {

    public EnchantmentSalvageLine() {
        super(Rarity.RARE, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.salvage_line");
        this.setRegistryName("divineenchantments", "salvage_line");
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
        return ItemCheckHelper.isFishingRod(stack);
    }
}
