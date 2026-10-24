package com.divineenchantments.enchantment;

import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDampeningStrike extends DivineEnchantment {

    public EnchantmentDampeningStrike() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("divineenchantments.dampening_strike");
        this.setRegistryName("divineenchantments", "dampening_strike");
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
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof ItemPickaxe || ItemCheckHelper.isBluntWeapon(stack)) return true;
        ResourceLocation reg = stack.getItem().getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("pickaxe") || path.contains("hammer") || path.contains("warhammer")) return true;
        }
        String className = stack.getItem().getClass().getName().toLowerCase();
        return className.contains("pickaxe") || className.contains("hammer") || className.contains("warhammer");
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!com.divineenchantments.config.DivineConfig.isEnabled(this)) { return false; }
        return canApply(stack);
    }
}
