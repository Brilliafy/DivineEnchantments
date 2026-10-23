package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentLongdraw extends DivineEnchantment {

    public EnchantmentLongdraw() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.longdraw");
        this.setRegistryName("divineenchantments", "longdraw");
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
        return 30;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 60;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemBow;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return false;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (!super.canApplyTogether(ench)) {
            return false;
        }
        if (ench == Enchantments.MENDING) {
            return false;
        }
        ResourceLocation reg = ench.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("mending") || path.contains("strafe") || path.contains("multishot")
                    || path.contains("splitshot") || path.contains("rapid") || path.contains("range")
                    || path.contains("pierceful")) {
                return false;
            }
        }
        return true;
    }
}
