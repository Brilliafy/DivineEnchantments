package com.divineenchantments.compat.jei;

import com.divineenchantments.init.ModEnchantments;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class DivineEnchantmentsJEIPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        for (Enchantment ench : ModEnchantments.ENCHANTMENTS) {
            if (ench == null || ench.getRegistryName() == null) {
                continue;
            }

            List<ItemStack> books = new ArrayList<>();
            for (int lvl = ench.getMinLevel(); lvl <= ench.getMaxLevel(); lvl++) {
                books.add(ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(ench, lvl)));
            }

            String descKey = "enchantment." + ench.getRegistryName().getNamespace() + "." + ench.getRegistryName().getPath() + ".desc";
            registry.addIngredientInfo(books, VanillaTypes.ITEM, descKey);
        }
    }
}
