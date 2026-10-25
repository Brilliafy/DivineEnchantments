package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentSlipBore extends DivineEnchantment {

    public EnchantmentSlipBore() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.slip_bore");
        this.setRegistryName("divineenchantments", "slip_bore");
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
        return ItemCheckHelper.isBowOrCrossbow(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isBowOrCrossbow(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        if (!super.canApplyTogether(other)) {
            return false;
        }
        if (other != null && other.getRegistryName() != null) {
            String path = other.getRegistryName().getPath().toLowerCase();
            if (path.contains("multishot") || path.contains("splitshot") || path.contains("split_shot")) {
                return false;
            }
        }
        return true;
    }
}
