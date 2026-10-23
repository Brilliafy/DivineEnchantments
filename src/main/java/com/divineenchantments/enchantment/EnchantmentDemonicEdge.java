package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDemonicEdge extends DivineEnchantment {

    public EnchantmentDemonicEdge() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.demonic_edge");
        this.setRegistryName("divineenchantments", "demonic_edge");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 5 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return this.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isMeleeWeapon(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench instanceof EnchantmentDamage || ench == Enchantments.SHARPNESS || ench == Enchantments.SMITE || ench == Enchantments.BANE_OF_ARTHROPODS) {
            return false;
        }

        ResourceLocation reg = ench.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("sharpness") || path.contains("smite") || path.contains("bane") || path.contains("damage")) {
                return false;
            }
        }

        return super.canApplyTogether(ench);
    }
}
