package com.divineenchantments.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Central enchanting table level map.
 *
 * Each table-available enchantment has a minimum table power. The highest
 * enchantment tier always unlocks at {@link #MAX_TABLE_LEVEL}. Intermediate
 * tiers are spaced evenly between the minimum and the cap.
 */
public final class EnchantingTableTiers {

    public static final int TABLE_POWER_CAP = 30;
    public static final int MAX_ENCHANTABILITY = 100;

    private static final Map<String, Integer> MIN_TABLE_LEVELS = new HashMap<>();

    static {
        // Common
        min("glimmer", 1);
        min("silt_sifter", 1);
        min("tempered_core", 1);
        min("shimmered", 3);

        // Uncommon
        min("chainsaw", 5);
        min("demonic_edge", 5);
        min("dulled_impact", 5);
        min("gourmands_vitality", 5);
        min("heavyplated", 5);
        min("hunter", 5);
        min("slowsink", 5);
        min("archery", 8);
        min("cushioned_heel", 8);
        min("cushioned_shirt", 8);
        min("dampening_strike", 8);
        min("desalination_mesh", 8);
        min("feathered_draw", 8);
        min("necrotic_stasis", 8);
        min("puncture", 8);
        min("quiet_chipping", 8);
        min("sober_chisel", 8);
        min("thermal_foil", 8);

        // Rare
        min("braced_pivot", 10);
        min("breach", 10);
        min("bullseye", 10);
        min("challengers_mark", 10);
        min("culling", 10);
        min("enlargement", 10);
        min("gargoyle_stance", 10);
        min("headslicer", 10);
        min("machine_bow", 10);
        min("magnetic_clasp", 10);
        min("metabolic_slumber", 10);
        min("mudstrider", 10);
        min("sacrificial", 10);
        min("salvage_line", 10);
        min("shadow_stalker", 10);
        min("splintering_impact", 10);
        min("stagger", 10);
        min("volatile_discharge", 10);

        // Very rare
        min("ally", 20);
        min("riches", 20);
        min("rune_carnage", 24);
        min("ballistic_overdrive", 25);
        min("grounded_vanguard", 28);
        min("gamblers_gambit", 30);
        min("muzzle", 30);
        min("slip_bore", 30);
    }

    private EnchantingTableTiers() {
    }

    private static void min(String path, int level) {
        MIN_TABLE_LEVELS.put(path, level);
    }

    public static boolean isManaged(Enchantment enchantment) {
        return minLevel(enchantment) >= 0;
    }

    /**
     * @return the minimum table power for this tier, or -1 when the enchantment is not table-managed.
     */
    public static int getMinEnchantability(Enchantment enchantment, int enchantmentLevel) {
        int min = minLevel(enchantment);
        if (min < 0) {
            return -1;
        }
        return min + Math.max(0, enchantmentLevel - 1) * getStep(enchantment, min);
    }

    /**
     * @return the maximum table power for this tier, or -1 when the enchantment is not table-managed.
     */
    public static int getMaxEnchantability(Enchantment enchantment, int enchantmentLevel) {
        return isManaged(enchantment) ? MAX_ENCHANTABILITY : -1;
    }

    /**
     * @return the first table power that unlocks the requested tier, or -1 when unmanaged.
     */
    public static int getTierLevel(Enchantment enchantment, int tier) {
        int min = minLevel(enchantment);
        if (min < 0) {
            return -1;
        }
        return min + Math.max(0, tier - 1) * getStep(enchantment, min);
    }

    public static int getMinimumTableLevel(Enchantment enchantment) {
        return minLevel(enchantment);
    }

    private static int getStep(Enchantment enchantment, int min) {
        int maxLevel = enchantment.getMaxLevel();
        if (maxLevel <= 1) {
            return 0;
        }
        return Math.max(1, (TABLE_POWER_CAP - min) / (maxLevel - 1));
    }

    private static int minLevel(Enchantment enchantment) {
        if (enchantment == null) {
            return -1;
        }
        ResourceLocation name = enchantment.getRegistryName();
        if (name == null || !"divineenchantments".equals(name.getNamespace())) {
            return -1;
        }
        Integer min = MIN_TABLE_LEVELS.get(name.getPath());
        return min == null ? -1 : min;
    }
}
