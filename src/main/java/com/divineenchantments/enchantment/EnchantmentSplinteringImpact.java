package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentSplinteringImpact extends DivineEnchantment {

    public EnchantmentSplinteringImpact() {
        super(Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.splintering_impact");
        this.setRegistryName("divineenchantments", "splintering_impact");
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
    public boolean canApply(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isCrossbow(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return ItemCheckHelper.isCrossbow(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        if (!super.canApplyTogether(other)) {
            return false;
        }
        if (other != null && other.getRegistryName() != null) {
            String path = other.getRegistryName().getPath().toLowerCase();
            if (path.contains("multishot") || path.contains("splitshot") || path.contains("split_shot")
                    || path.contains("spreadshot") || path.contains("spread_shot")) {
                return false;
            }
        }
        return true;
    }
}
