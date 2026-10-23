package com.divineenchantments.util;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;

public class ItemCheckHelper {

    /**
     * Checks if the given ItemStack is considered a melee weapon.
     * This includes vanilla swords, vanilla axes, Spartan Weaponry melee weapons,
     * and any other modded swords/axes or weapons that grant attack damage.
     */
    public static boolean isMeleeWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        // 1. Direct vanilla sword and axe inheritance
        // Note: All Spartan Weaponry melee weapons (ItemWeaponBase) extend ItemSwordBase, which extends ItemSword!
        if (item instanceof ItemSword || item instanceof ItemAxe) {
            return true;
        }

        // 2. Check registry namespace for Spartan Weaponry
        ResourceLocation reg = item.getRegistryName();
        if (reg != null && "spartanweaponry".equals(reg.getNamespace())) {
            String path = reg.getPath().toLowerCase();
            // Exclude ranged weapons, projectiles, quivers, shields, explosives
            if (path.contains("arrow") || path.contains("bolt") || path.contains("quiver")
                    || path.contains("bow") || path.contains("crossbow") || path.contains("shield")
                    || path.contains("dynamite")) {
                return false;
            }
            return true;
        }

        // 3. Check class name for known melee weapon types
        String className = item.getClass().getName().toLowerCase();
        if (className.contains("sword") || className.contains("battleaxe") || className.contains("warhammer")
                || className.contains("spear") || className.contains("halberd") || className.contains("rapier")
                || className.contains("dagger") || className.contains("katana") || className.contains("mace")
                || className.contains("glaive") || className.contains("pike") || className.contains("lance")) {
            return true;
        }

        // 4. Attribute fallback: Check if the item provides positive attack damage in mainhand
        try {
            Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
            if (modifiers != null && modifiers.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                for (AttributeModifier mod : modifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                    if (mod.getAmount() > 0.0D) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return false;
    }

    /**
     * Checks if the given ItemStack is a sword (vanilla, Spartan Weaponry, or other modded swords).
     */
    public static boolean isSword(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            return true;
        }

        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("sword") || path.contains("katana") || path.contains("rapier")
                    || path.contains("saber") || path.contains("dagger") || path.contains("greatsword")
                    || path.contains("longsword") || path.contains("sabre") || path.contains("scimitar")
                    || path.contains("blade")) {
                return true;
            }
        }

        String className = item.getClass().getName().toLowerCase();
        return className.contains("sword") || className.contains("katana") || className.contains("rapier")
                || className.contains("saber") || className.contains("dagger") || className.contains("blade");
    }

    /**
     * Checks if the given ItemStack is a shield (vanilla, Spartan Shields, or other modded shields).
     */
    public static boolean isShield(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof ItemShield || item.isShield(stack, null)) {
            return true;
        }

        ResourceLocation reg = item.getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("shield")) {
            return true;
        }

        String className = item.getClass().getName().toLowerCase();
        return className.contains("shield");
    }

    /**
     * Checks if the given ItemStack is a crossbow (Spartan Weaponry or modded).
     */
    public static boolean isCrossbow(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("crossbow")) {
            return true;
        }

        String className = item.getClass().getName().toLowerCase();
        return className.contains("crossbow");
    }

    /**
     * Checks if the given ItemStack is a bow or crossbow (vanilla, Spartan Weaponry, or modded).
     */
    public static boolean isBowOrCrossbow(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof ItemBow) {
            return true;
        }

        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("bow") || path.contains("crossbow")) {
                return true;
            }
        }

        String className = item.getClass().getName().toLowerCase();
        return className.contains("crossbow") || className.contains("bow");
    }

    /**
     * Checks if the given ItemStack is a fishing rod.
     */
    public static boolean isFishingRod(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof ItemFishingRod) {
            return true;
        }

        ResourceLocation reg = item.getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("fishing")) {
            return true;
        }

        String className = item.getClass().getName().toLowerCase();
        return className.contains("fishing");
    }

    /**
     * Checks if the given ItemStack is a dagger.
     */
    public static boolean isDagger(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("dagger")) {
            return true;
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("dagger");
    }

    /**
     * Checks if the given ItemStack is an axe or dagger.
     */
    public static boolean isAxeOrDagger(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemAxe || isDagger(stack)) {
            return true;
        }
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("axe") || path.contains("dagger")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("axe") || className.contains("dagger");
    }

    /**
     * Checks if the given ItemStack is a blunt weapon (Warhammer, Hammer, Mace, Club, Quarterstaff, Axe).
     */
    public static boolean isBluntWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemAxe) {
            return true;
        }
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("hammer") || path.contains("warhammer") || path.contains("mace")
                    || path.contains("club") || path.contains("quarterstaff") || path.contains("blunt")
                    || path.contains("battleaxe") || path.contains("caestus") || path.contains("flail")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("hammer") || className.contains("warhammer") || className.contains("mace")
                || className.contains("club") || className.contains("quarterstaff") || className.contains("battleaxe")
                || className.contains("caestus") || className.contains("flail");
    }

    /**
     * Checks if the given ItemStack is a polearm or halberd (Spartan Weaponry Halberd, Pike, Spear, Glaive, Lance, or similar).
     */
    public static boolean isPolearmOrHalberd(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("halberd") || path.contains("polearm") || path.contains("spear")
                    || path.contains("pike") || path.contains("glaive") || path.contains("lance")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("halberd") || className.contains("polearm") || className.contains("spear")
                || className.contains("pike") || className.contains("glaive") || className.contains("lance");
    }

    /**
     * Checks if the given ItemStack is a nunchaku (Better Survival, Spartan Weaponry addons, or modded).
     */
    public static boolean isNunchaku(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("nunchaku")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("nunchaku");
    }

    /**
     * Checks if the given ItemStack is a saber / sabre or rapier (Spartan Weaponry or modded).
     */
    public static boolean isSaberOrRapier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("saber") || path.contains("sabre") || path.contains("rapier")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("saber") || className.contains("sabre") || className.contains("rapier");
    }

    /**
     * Checks if the given ItemStack is a saber / sabre (used by Ancient: Kratos Blades).
     */
    public static boolean isSaber(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("saber") || path.contains("sabre")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("saber") || className.contains("sabre");
    }

    /**
     * Rune: Carnage applies to nunchaku, sabers, and rapiers only.
     */
    public static boolean isCarnageWeapon(ItemStack stack) {
        return isNunchaku(stack) || isSaberOrRapier(stack);
    }

    /**
     * Checks if the given ItemStack is a pickaxe or hammer/warhammer.
     */
    public static boolean isPickaxeOrHammer(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof net.minecraft.item.ItemPickaxe) {
            return true;
        }
        ResourceLocation reg = item.getRegistryName();
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("pickaxe") || path.contains("hammer") || path.contains("warhammer") || path.contains("mattock")) {
                return true;
            }
        }
        String className = item.getClass().getName().toLowerCase();
        return className.contains("pickaxe") || className.contains("hammer") || className.contains("warhammer");
    }

    /**
     * Checks if an entity is considered a boss mob.
     */
    public static boolean isBossEntity(net.minecraft.entity.Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!entity.isNonBoss()) {
            return true;
        }
        if (net.minecraftforge.fml.common.Loader.isModLoaded("lycanitesmobs")) {
            try {
                if (com.divineenchantments.compat.LycanitesIntegration.isBoss(entity)) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
        String name = entity.getClass().getName().toLowerCase();
        return name.contains("boss") || name.contains("dragon") || name.contains("wither");
    }
}
