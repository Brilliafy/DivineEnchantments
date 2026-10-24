package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentChallengersMark extends DivineEnchantment {

    public EnchantmentChallengersMark() {
        super(Rarity.RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.challengers_mark");
        this.setRegistryName("divineenchantments", "challengers_mark");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isShield(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return canApply(stack);
    }
}
