package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMachineBow extends DivineEnchantment {

    public EnchantmentMachineBow() {
        super(Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.machine_bow");
        this.setRegistryName("divineenchantments", "machine_bow");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemBow;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return canApply(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (!super.canApplyTogether(ench)) {
            return false;
        }
        if (ench == Enchantments.INFINITY) {
            return false;
        }
        ResourceLocation reg = ench.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("multishot") || path.contains("splitshot") || path.contains("rapid")
                    || path.contains("strafe") || path.contains("quick_charge") || path.contains("infinity")) {
                return false;
            }
        }
        return true;
    }
}
