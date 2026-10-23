package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentPuncture extends Enchantment {

    public EnchantmentPuncture() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.puncture");
        this.setRegistryName("divineenchantments", "puncture");
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
        return 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 45;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (ItemCheckHelper.isDagger(stack)) return true;
        ResourceLocation reg = stack.getItem().getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("spear") || path.contains("pike") || path.contains("lance")
                    || path.contains("rapier") || path.contains("dagger") || path.contains("javelin")) {
                return true;
            }
        }
        String className = stack.getItem().getClass().getName().toLowerCase();
        return className.contains("spear") || className.contains("pike") || className.contains("lance")
                || className.contains("rapier") || className.contains("dagger") || className.contains("javelin");
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }
}
