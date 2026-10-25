package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentVoodoo extends DivineEnchantment {

    public EnchantmentVoodoo() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.voodoo");
        this.setRegistryName("divineenchantments", "voodoo");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 20 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 25;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return ItemCheckHelper.isSword(stack)
                || ItemCheckHelper.isAxe(stack)
                || ItemCheckHelper.isPolearmOrHalberd(stack)
                || ItemCheckHelper.isBluntWeapon(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return false;
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        if (!super.canApplyTogether(other)) {
            return false;
        }
        return other == null || other.getRegistryName() == null
                || !"ally".equals(other.getRegistryName().getPath());
    }

}
