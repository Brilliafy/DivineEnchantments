package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentRuneCarnage extends DivineEnchantment {

    public EnchantmentRuneCarnage() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.rune_carnage");
        this.setRegistryName("divineenchantments", "rune_carnage");
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
    public int getMinEnchantability(int enchantmentLevel) {
        // 8 levels per tier: 8, 16, 24. Level III is reachable with a level 30 enchant.
        return 8 * enchantmentLevel;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 26;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isCarnageWeapon(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isCarnageWeapon(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        if (other != null) {
            ResourceLocation name = other.getRegistryName();
            if (name != null && "somanyenchantments".equals(name.getNamespace())
                    && "swifterslashes".equals(name.getPath())) {
                return false;
            }
        }
        return super.canApplyTogether(other);
    }

}
