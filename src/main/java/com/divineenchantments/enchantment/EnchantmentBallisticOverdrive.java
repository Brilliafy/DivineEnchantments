package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentBallisticOverdrive extends Enchantment {

    public EnchantmentBallisticOverdrive() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.ballistic_overdrive");
        this.setRegistryName("divineenchantments", "ballistic_overdrive");
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
        return 28;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 60;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return ItemCheckHelper.isCrossbow(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return ItemCheckHelper.isCrossbow(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (!super.canApplyTogether(ench)) {
            return false;
        }
        ResourceLocation reg = ench.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("rapid") || path.contains("strafe") || path.contains("quick_charge") || path.contains("load")) {
                return false;
            }
        }
        return true;
    }
}
