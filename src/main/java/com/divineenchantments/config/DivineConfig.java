package com.divineenchantments.config;

import com.divineenchantments.DivineEnchantments;
import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class DivineConfig {

    private static final Map<String, Boolean> ENABLED_ENCHANTMENTS = new HashMap<>();

    public static boolean enableAllEnchantments = true;

    // Compatibility fixes
    public static boolean fixRangeEnchantmentDamage = true;

    // Rune: Carnage
    public static double carnagePenaltyPerLevel = 0.10D;
    public static double carnageBonusLevel1 = 0.02D;
    public static double carnageBonusLevel2 = 0.03D;
    public static double carnageBonusLevel3 = 0.05D;
    public static int carnageMaxStacksPerLevel = 7;
    public static int carnageDecayTicks = 100;
    public static int carnageDecayStacks = 5;
    public static double carnageVulnerabilityPerLevel = 0.01D;

    // Enlargement
    public static double enlargementChancePerLevel = 0.02D;
    public static double enlargementSizePerLevel = 0.10D;
    public static int enlargementDurationTicks = 400;

    // Sound stealth
    public static double dampeningStrikeAggroRadius = 4.8D;
    public static double quietChippingAggroRadius = 4.0D;

    // Perpetual Crucible
    public static int perpetualCrucibleBaseDurationTicks = 60;
    public static int perpetualCrucibleDurationPerLevelTicks = 40;

    // Feathered Draw
    public static double featheredDrawSpeedLevel1 = 1.5D;
    public static double featheredDrawSpeedLevel2 = 2.5D;
    public static double featheredDrawSpeedLevel3 = 3.5D;
    public static double featheredDrawSpeedLevel4 = 4.0D;

    // Bullseye
    public static double bullseyeDamagePerLevel = 0.20D;
    public static int bullseyeDurationTicks = 60;
    public static double bullseyeHitRadius = 0.30D;

    // Hunter
    public static int hunterDurationPerLevelTicks = 400;

    // Voodoo
    public static double voodooChancePerLevel = 0.02D;
    public static double voodooHealthPerLevel = 4.0D;
    public static double voodooSpeedMultiplier = 1.35D;
    public static double voodooBaseDamage = 3.0D;
    public static double voodooDamagePerLevel = 1.0D;
    public static int voodooMaxAlliesPerPlayer = 8;

    // Ancient: Kratos Blades
    public static int kratosDurabilityCost = 1;
    public static double kratosHookSpeed = 2.1D;
    public static double kratosPullPower = 1.35D;
    public static int kratosHookLifetimeTicks = 70;
    public static int kratosCooldownTicks = 8;

    private DivineConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();

            config.setCategoryComment("general",
                    "Global Divine Enchantments settings. Changes require a game restart.");
            enableAllEnchantments = config.getBoolean("enableAllEnchantments", "general", true,
                    "Master switch. When false, every Divine Enchantment is inert and cannot be applied.");

            config.setCategoryComment("compatibility",
                    "Compatibility fixes for other mods. Changes require a game restart.");
            fixRangeEnchantmentDamage = config.getBoolean("fixRangeEnchantmentDamage", "compatibility", true,
                    "Remove the extra damage Better Survival's Range enchantment gains from its arrow velocity boost. "
                            + "Arrows still fly faster and further. Requires Better Survival.");

            config.setCategoryComment("enchantments",
                    "Enable or disable individual enchantments. Disabled enchantments cannot be applied and their effects stop working. Restart required.");

            ENABLED_ENCHANTMENTS.clear();
            for (Enchantment enchantment : ModEnchantments.ENCHANTMENTS) {
                if (enchantment == null || enchantment.getRegistryName() == null) {
                    continue;
                }
                String key = enchantment.getRegistryName().getPath();
                boolean enabled = config.getBoolean(key, "enchantments", true,
                        "Enable " + key + ".");
                ENABLED_ENCHANTMENTS.put(key, enabled);
            }

            // Ally is conditional on Lycanites Mobs. Keep a stable toggle in every config.
            if (!ENABLED_ENCHANTMENTS.containsKey("ally")) {
                ENABLED_ENCHANTMENTS.put("ally", config.getBoolean("ally", "enchantments", true,
                        "Enable ally. Requires Lycanites Mobs."));
            }

            config.setCategoryComment("balance",
                    "Tuning values for the newer enchantments. Restart required.");

            carnagePenaltyPerLevel = config.getFloat("carnagePenaltyPerLevel", "balance", 0.10F, 0.0F, 1.0F,
                    "Rune: Carnage attack speed penalty per enchantment level (0.10 = 10%).");
            carnageBonusLevel1 = config.getFloat("carnageBonusLevel1", "balance", 0.02F, 0.0F, 10.0F,
                    "Rune: Carnage attack speed gained per stack at level I (0.02 = 2%).");
            carnageBonusLevel2 = config.getFloat("carnageBonusLevel2", "balance", 0.03F, 0.0F, 10.0F,
                    "Rune: Carnage attack speed gained per stack at level II.");
            carnageBonusLevel3 = config.getFloat("carnageBonusLevel3", "balance", 0.05F, 0.0F, 10.0F,
                    "Rune: Carnage attack speed gained per stack at level III.");
            carnageMaxStacksPerLevel = config.getInt("carnageMaxStacksPerLevel", "balance", 7, 0, 100,
                    "Maximum Rune: Carnage stacks per enchantment level (7 means 7/14/21).");
            carnageDecayTicks = config.getInt("carnageDecayTicks", "balance", 100, 1, 72000,
                    "Ticks without a hit before Rune: Carnage stacks decay.");
            carnageDecayStacks = config.getInt("carnageDecayStacks", "balance", 5, 0, 100,
                    "Stacks lost each decay interval.");
            carnageVulnerabilityPerLevel = config.getFloat("carnageVulnerabilityPerLevel", "balance", 0.01F, 0.0F, 1.0F,
                    "Fraction of enemy invulnerability frames removed per level (0.01 = 1%).");

            enlargementChancePerLevel = config.getFloat("enlargementChancePerLevel", "balance", 0.02F, 0.0F, 1.0F,
                    "Enlargement proc chance per level (0.02 = 2%).");
            enlargementSizePerLevel = config.getFloat("enlargementSizePerLevel", "balance", 0.10F, 0.0F, 10.0F,
                    "Enlargement size increase per level (0.10 = 10%).");
            enlargementDurationTicks = config.getInt("enlargementDurationTicks", "balance", 400, 1, 72000,
                    "Enlargement effect duration in ticks (400 = 20 seconds).");

            dampeningStrikeAggroRadius = config.getFloat("dampeningStrikeAggroRadius", "balance", 4.8F, 0.0F, 64.0F,
                    "Maximum distance at which mobs can target a player holding a Dampening Strike tool.");
            quietChippingAggroRadius = config.getFloat("quietChippingAggroRadius", "balance", 4.0F, 0.0F, 64.0F,
                    "Maximum distance at which mobs can target a player holding a Quiet Chipping tool.");

            perpetualCrucibleBaseDurationTicks = config.getInt("perpetualCrucibleBaseDurationTicks", "balance", 60, 1, 72000,
                    "Perpetual Crucible Extension duration at level I in ticks (60 = 3 seconds).");
            perpetualCrucibleDurationPerLevelTicks = config.getInt("perpetualCrucibleDurationPerLevelTicks", "balance", 40, 0, 72000,
                    "Additional Perpetual Crucible duration per level in ticks (40 = +2 seconds).");

            featheredDrawSpeedLevel1 = config.getFloat("featheredDrawSpeedLevel1", "balance", 1.5F, 0.0F, 10.0F,
                    "Feathered Draw additive movement speed compensation at level I (1.5 = -50% draw slowdown).");
            featheredDrawSpeedLevel2 = config.getFloat("featheredDrawSpeedLevel2", "balance", 2.5F, 0.0F, 10.0F,
                    "Feathered Draw additive movement speed compensation at level II (2.5 = -30%).");
            featheredDrawSpeedLevel3 = config.getFloat("featheredDrawSpeedLevel3", "balance", 3.5F, 0.0F, 10.0F,
                    "Feathered Draw additive movement speed compensation at level III (3.5 = -10%).");
            featheredDrawSpeedLevel4 = config.getFloat("featheredDrawSpeedLevel4", "balance", 4.0F, 0.0F, 10.0F,
                    "Feathered Draw additive movement speed compensation at level IV (4.0 = no draw slowdown).");

            bullseyeDamagePerLevel = config.getFloat("bullseyeDamagePerLevel", "balance", 0.20F, 0.0F, 10.0F,
                    "Bullseye bonus damage per level (0.20 = 20%).");
            bullseyeDurationTicks = config.getInt("bullseyeDurationTicks", "balance", 60, 1, 72000,
                    "Bullseye marker lifetime in ticks (60 = 3 seconds).");
            bullseyeHitRadius = config.getFloat("bullseyeHitRadius", "balance", 0.30F, 0.05F, 5.0F,
                    "Bullseye hit radius in blocks.");

            hunterDurationPerLevelTicks = config.getInt("hunterDurationPerLevelTicks", "balance", 400, 1, 72000,
                    "Hunter glowing duration per level at full draw in ticks (400 = 20 seconds).");

            voodooChancePerLevel = config.getFloat("voodooChancePerLevel", "balance", 0.02F, 0.0F, 1.0F,
                    "Voodoo ally spawn chance per level (0.02 = 2%).");
            voodooHealthPerLevel = config.getFloat("voodooHealthPerLevel", "balance", 4.0F, 1.0F, 1000.0F,
                    "Voodoo ally health per level.");
            voodooSpeedMultiplier = config.getFloat("voodooSpeedMultiplier", "balance", 1.35F, 0.1F, 10.0F,
                    "Voodoo ally movement speed multiplier over a normal zombie.");
            voodooBaseDamage = config.getFloat("voodooBaseDamage", "balance", 3.0F, 0.0F, 1000.0F,
                    "Voodoo ally base attack damage.");
            voodooDamagePerLevel = config.getFloat("voodooDamagePerLevel", "balance", 1.0F, 0.0F, 1000.0F,
                    "Voodoo ally bonus attack damage per level.");
            voodooMaxAlliesPerPlayer = config.getInt("voodooMaxAlliesPerPlayer", "balance", 8, 0, 100,
                    "Maximum living Voodoo allies per player (0 disables spawning).");

            kratosDurabilityCost = config.getInt("kratosDurabilityCost", "balance", 1, 0, 100,
                    "Durability consumed by the Kratos chain on hit.");
            kratosHookSpeed = config.getFloat("kratosHookSpeed", "balance", 2.1F, 0.1F, 10.0F,
                    "Kratos chain launch speed.");
            kratosPullPower = config.getFloat("kratosPullPower", "balance", 1.35F, 0.0F, 10.0F,
                    "Kratos chain pull strength.");
            kratosHookLifetimeTicks = config.getInt("kratosHookLifetimeTicks", "balance", 70, 1, 1200,
                    "Kratos chain lifetime in ticks.");
            kratosCooldownTicks = config.getInt("kratosCooldownTicks", "balance", 8, 0, 200,
                    "Kratos chain cooldown in ticks.");
        } catch (Exception exception) {
            if (DivineEnchantments.logger != null) {
                DivineEnchantments.logger.error("Failed to load Divine Enchantments configuration", exception);
            }
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static boolean isEnabled(Enchantment enchantment) {
        if (!enableAllEnchantments || enchantment == null) {
            return false;
        }
        ResourceLocation name = enchantment.getRegistryName();
        if (name == null) {
            return true;
        }
        Boolean enabled = ENABLED_ENCHANTMENTS.get(name.getPath());
        return enabled == null || enabled;
    }
}
