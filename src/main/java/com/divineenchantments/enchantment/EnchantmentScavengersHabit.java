package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentScavengersHabit extends DivineEnchantment {

    public EnchantmentScavengersHabit() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("divineenchantments.scavengers_habit");
        this.setRegistryName("divineenchantments", "scavengers_habit");
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
        return stack != null && !stack.isEmpty() && (stack.getItem() instanceof ItemBow || ItemCheckHelper.isBowOrCrossbow(stack));
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
        ResourceLocation reg = ench.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("arrowrecovery") || path.contains("arrow_recovery") || path.contains("recovery")) {
                return false;
            }
        }
        return true;
    }
}
