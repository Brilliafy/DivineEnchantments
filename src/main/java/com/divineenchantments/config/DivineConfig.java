package com.divineenchantments.config;

import com.divineenchantments.DivineEnchantments;
import com.divineenchantments.init.ModEnchantments;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class DivineConfig {

    private static final Map<String, Boolean> ENABLED_ENCHANTMENTS = new HashMap<>();

    public static boolean enableAllEnchantments = true;

    // Compatibility fixes
    public static boolean fixRangeEnchantmentDamage = true;

    // Rune: Carnage
    public static double carnagePenaltyPerLevel = 0.0D;
    public static double carnageBaseBonusPerLevel = 0.20D;
    public static double carnageBonusLevel1 = 0.1334D;
    public static double carnageBonusLevel2 = 0.1334D;
    public static double carnageBonusLevel3 = 0.1334D;
    public static int carnageMaxStacksPerLevel = 7;
    public static int carnageDecayTicks = 100;
    public static int carnageDecayStacks = 5;

    // Enlargement
    public static double enlargementChancePerLevel = 0.02D;
    public static double enlargementSizePerLevel = 0.10D;
    public static double enlargementMaxSizeMultiplier = 1.30D;
    public static int enlargementDurationTicks = 400;

    // Sound stealth
    public static double dampeningStrikeAggroRadius = 4.8D;
    public static double quietChippingAggroRadius = 4.0D;

    // Puncture
    public static double punctureArmorBypass = 0.10D;

    // Dulled Impact
    public static double dulledImpactReductionPerLevel = 0.10D;
    public static int dulledImpactDurationTicks = 100;

    // Braced Pivot
    public static double bracedPivotCooldownReduction = 0.60D;

    // Volatile Discharge
    public static double volatileDischargeExplosionPower = 5.0D;

    // Stagger
    public static int staggerParalysisTicks = 60;

    // Ally
    public static int allyMaxAegisPerPlayer = 4;

    // Shimmered
    public static int shimmeredHighlightDurationTicks = 20;
    public static int shimmeredHighlightRadius = 2;

    // Apex
    public static double apexMaxBonus = 0.25D;

    // Perpetual Crucible
    public static int perpetualCrucibleBaseDurationTicks = 60;
    public static int perpetualCrucibleDurationPerLevelTicks = 30;

    // Feathered Draw
    public static double featheredDrawSpeedLevel1 = 0.8D;
    public static double featheredDrawSpeedLevel2 = 1.6D;
    public static double featheredDrawSpeedLevel3 = 2.4D;
    public static double featheredDrawSpeedLevel4 = 3.2D;

    // Bullseye
    public static double bullseyeDamagePerLevel = 0.20D;
    public static int bullseyeDurationTicks = 60;
    public static double bullseyeHitRadius = 0.30D;

    // Gambler's Gambit
    public static double gamblersGambitChance = 0.25D;
    public static String[] gamblersGambitBlacklist = new String[0];

    // Splintering Impact
    public static double splinteringDamageMultiplier = 0.50D;

    // Autofire
    public static double autofireDamageMultiplier = 0.60D;

    // Hunter
    public static int hunterDurationPerLevelTicks = 400;

    // Voodoo
    public static double voodooChancePerLevel = 0.02D;
    public static double voodooHealthPerLevel = 4.0D;
    public static double voodooSpeedMultiplier = 1.35D;
    public static double voodooBaseDamage = 3.0D;
    public static double voodooDamagePerLevel = 1.0D;
    public static int voodooMaxAlliesPerPlayer = 6;

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

            carnagePenaltyPerLevel = config.getFloat("carnagePenaltyPerLevel", "balance", 0.0F, 0.0F, 1.0F,
                    "Rune: Carnage attack speed penalty per enchantment level (0.0 = no penalty).");
            carnageBaseBonusPerLevel = config.getFloat("carnageBaseBonusPerLevel", "balance", 0.20F, 0.0F, 10.0F,
                    "Rune: Carnage immediate multiplicative attack speed per enchantment level (0.20 = 20 percent per level).");
            carnageBonusLevel1 = config.getFloat("carnageBonusLevel1", "balance", 0.1334F, 0.0F, 10.0F,
                    "Rune: Carnage multiplicative attack speed per stack at level I (0.1334 = 13.34%).");
            carnageBonusLevel2 = config.getFloat("carnageBonusLevel2", "balance", 0.1334F, 0.0F, 10.0F,
                    "Rune: Carnage multiplicative attack speed per stack at level II.");
            carnageBonusLevel3 = config.getFloat("carnageBonusLevel3", "balance", 0.1334F, 0.0F, 10.0F,
                    "Rune: Carnage multiplicative attack speed per stack at level III (4.4x at 21 stacks with the base bonus).");
            // Migrate older default values to the current doubled Rune: Carnage balance.
            if (carnagePenaltyPerLevel == 0.10D) {
                carnagePenaltyPerLevel = 0.0D;
            }
            if (carnageBaseBonusPerLevel == 0.10D) {
                carnageBaseBonusPerLevel = 0.20D;
            }
            if (carnageBonusLevel1 == 0.05D || carnageBonusLevel1 == 0.0667D || carnageBonusLevel1 == 0.0524D || carnageBonusLevel1 == 0.042857D || carnageBonusLevel1 == 0.045238D || carnageBonusLevel1 == 0.090476D) {
                carnageBonusLevel1 = 0.1334D;
            }
            if (carnageBonusLevel2 == 0.0572D || carnageBonusLevel2 == 0.0667D || carnageBonusLevel2 == 0.0524D || carnageBonusLevel2 == 0.042857D || carnageBonusLevel2 == 0.045238D || carnageBonusLevel2 == 0.090476D) {
                carnageBonusLevel2 = 0.1334D;
            }
            if (carnageBonusLevel3 == 0.0715D || carnageBonusLevel3 == 0.0667D || carnageBonusLevel3 == 0.0524D || carnageBonusLevel3 == 0.042857D || carnageBonusLevel3 == 0.045238D || carnageBonusLevel3 == 0.090476D) {
                carnageBonusLevel3 = 0.1334D;
            }
            carnageMaxStacksPerLevel = config.getInt("carnageMaxStacksPerLevel", "balance", 7, 0, 100,
                    "Maximum Rune: Carnage stacks per enchantment level (7 means 7/14/21).");
            carnageDecayTicks = config.getInt("carnageDecayTicks", "balance", 100, 1, 72000,
                    "Ticks without a hit before Rune: Carnage stacks decay.");
            carnageDecayStacks = config.getInt("carnageDecayStacks", "balance", 5, 0, 100,
                    "Stacks lost each decay interval.");

            enlargementChancePerLevel = config.getFloat("enlargementChancePerLevel", "balance", 0.02F, 0.0F, 1.0F,
                    "Enlargement proc chance per level (0.02 = 2%).");
            enlargementSizePerLevel = config.getFloat("enlargementSizePerLevel", "balance", 0.10F, 0.0F, 10.0F,
                    "Enlargement size increase per level (0.10 = 10%).");
            enlargementMaxSizeMultiplier = config.getFloat("enlargementMaxSizeMultiplier", "balance", 1.30F, 1.0F, 10.0F,
                    "Maximum Enlargement size multiplier (1.30 = 30% larger).");
            enlargementDurationTicks = config.getInt("enlargementDurationTicks", "balance", 400, 1, 72000,
                    "Enlargement effect duration in ticks (400 = 20 seconds).");

            dampeningStrikeAggroRadius = config.getFloat("dampeningStrikeAggroRadius", "balance", 4.8F, 0.0F, 64.0F,
                    "Maximum distance at which mobs can target a player holding a Dampening Strike tool.");
            quietChippingAggroRadius = config.getFloat("quietChippingAggroRadius", "balance", 4.0F, 0.0F, 64.0F,
                    "Maximum distance at which mobs can target a player holding a Quiet Chipping tool.");

            punctureArmorBypass = config.getFloat("punctureArmorBypass", "balance", 0.10F, 0.0F, 1.0F,
                    "Puncture natural armor bypass fraction (0.10 = 10%).");

            dulledImpactReductionPerLevel = config.getFloat("dulledImpactReductionPerLevel", "balance", 0.10F, 0.0F, 1.0F,
                    "Dulled Impact outgoing damage reduction per level (0.10 = 10%).");
            dulledImpactDurationTicks = config.getInt("dulledImpactDurationTicks", "balance", 100, 1, 72000,
                    "Dulled Impact duration in ticks (100 = 5 seconds).");

            bracedPivotCooldownReduction = config.getFloat("bracedPivotCooldownReduction", "balance", 0.60F, 0.0F, 1.0F,
                    "Fraction of the base 100-tick shield disable cooldown removed by Braced Pivot (0.60 = -60%).");

            volatileDischargeExplosionPower = config.getFloat("volatileDischargeExplosionPower", "balance", 5.0F, 0.0F, 100.0F,
                    "Volatile Discharge explosion power (vanilla creeper = 3.0).");

            staggerParalysisTicks = config.getInt("staggerParalysisTicks", "balance", 60, 0, 72000,
                    "Lycanites paralysis duration from Stagger in ticks (60 = 3 seconds).");

            allyMaxAegisPerPlayer = config.getInt("allyMaxAegisPerPlayer", "balance", 4, 0, 100,
                    "Maximum living Aegis summons per player from the Ally enchantment.");

            shimmeredHighlightDurationTicks = config.getInt("shimmeredHighlightDurationTicks", "balance", 20, 1, 72000,
                    "Shimmered ore outline duration in ticks (20 = 1 second).");
            if (shimmeredHighlightDurationTicks == 100) {
                shimmeredHighlightDurationTicks = 20;
            }
            shimmeredHighlightRadius = config.getInt("shimmeredHighlightRadius", "balance", 2, 1, 8,
                    "Shimmered ore scan radius in blocks.");

            apexMaxBonus = config.getFloat("apexMaxBonus", "balance", 0.25F, 0.0F, 10.0F,
                    "Maximum Apex high-ground bonus and falling penalty (0.25 = 25%).");

            perpetualCrucibleBaseDurationTicks = config.getInt("perpetualCrucibleBaseDurationTicks", "balance", 60, 1, 72000,
                    "Perpetual Crucible Extension duration at level I in ticks (60 = 3 seconds).");
            perpetualCrucibleDurationPerLevelTicks = config.getInt("perpetualCrucibleDurationPerLevelTicks", "balance", 30, 0, 72000,
                    "Additional Perpetual Crucible duration per level in ticks (30 = +1.5 seconds).");

            featheredDrawSpeedLevel1 = config.getFloat("featheredDrawSpeedLevel1", "balance", 0.8F, 0.0F, 10.0F,
                    "Feathered Draw movement compensation at level I: removes 20% of the draw slowdown (-64% remaining).");
            featheredDrawSpeedLevel2 = config.getFloat("featheredDrawSpeedLevel2", "balance", 1.6F, 0.0F, 10.0F,
                    "Feathered Draw movement compensation at level II: removes 40% of the draw slowdown (-48% remaining).");
            featheredDrawSpeedLevel3 = config.getFloat("featheredDrawSpeedLevel3", "balance", 2.4F, 0.0F, 10.0F,
                    "Feathered Draw movement compensation at level III: removes 60% of the draw slowdown (-32% remaining).");
            featheredDrawSpeedLevel4 = config.getFloat("featheredDrawSpeedLevel4", "balance", 3.2F, 0.0F, 10.0F,
                    "Feathered Draw movement compensation at level IV: removes 80% of the draw slowdown (-16% remaining).");

            bullseyeDamagePerLevel = config.getFloat("bullseyeDamagePerLevel", "balance", 0.20F, 0.0F, 10.0F,
                    "Bullseye bonus damage per level (0.20 = 20%).");
            bullseyeDurationTicks = config.getInt("bullseyeDurationTicks", "balance", 60, 1, 72000,
                    "Bullseye marker lifetime in ticks (60 = 3 seconds).");
            bullseyeHitRadius = config.getFloat("bullseyeHitRadius", "balance", 0.30F, 0.05F, 5.0F,
                    "Bullseye hit radius in blocks.");

            gamblersGambitChance = config.getFloat("gamblersGambitChance", "balance", 0.25F, 0.0F, 1.0F,
                    "Chance per hit for Gambler's Gambit to apply a negative effect (0.25 = 25%).");
            gamblersGambitBlacklist = config.getStringList("gamblersGambitBlacklist", "balance", new String[0],
                    "Potion registry names or paths that Gambler's Gambit must never apply. Example: potioncore:flight, potioncore:teleport.");

            splinteringDamageMultiplier = config.getFloat("splinteringDamageMultiplier", "balance", 0.50F, 0.0F, 10.0F,
                    "Splintering Impact shard damage as a multiple of the original bolt's damage (Power/Advanced Power scales it).");

            autofireDamageMultiplier = config.getFloat("autofireDamageMultiplier", "balance", 0.60F, 0.0F, 10.0F,
                    "Damage multiplier for Autofire arrows, including the original arrow (0.60 = 60%).");

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
            voodooMaxAlliesPerPlayer = Math.min(6, config.getInt("voodooMaxAlliesPerPlayer", "balance", 6, 0, 100,
                    "Maximum living Voodoo allies per player (capped at 6; 0 disables spawning)."));

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

    public static void writeTo(ByteBuf buf) {
        buf.writeBoolean(enableAllEnchantments);
        buf.writeBoolean(fixRangeEnchantmentDamage);
        buf.writeDouble(carnagePenaltyPerLevel);
        buf.writeDouble(carnageBaseBonusPerLevel);
        buf.writeDouble(carnageBonusLevel1);
        buf.writeDouble(carnageBonusLevel2);
        buf.writeDouble(carnageBonusLevel3);
        buf.writeInt(carnageMaxStacksPerLevel);
        buf.writeInt(carnageDecayTicks);
        buf.writeInt(carnageDecayStacks);
        buf.writeDouble(enlargementChancePerLevel);
        buf.writeDouble(enlargementSizePerLevel);
        buf.writeDouble(enlargementMaxSizeMultiplier);
        buf.writeInt(enlargementDurationTicks);
        buf.writeDouble(dampeningStrikeAggroRadius);
        buf.writeDouble(quietChippingAggroRadius);
        buf.writeDouble(punctureArmorBypass);
        buf.writeDouble(dulledImpactReductionPerLevel);
        buf.writeInt(dulledImpactDurationTicks);
        buf.writeDouble(bracedPivotCooldownReduction);
        buf.writeDouble(volatileDischargeExplosionPower);
        buf.writeInt(staggerParalysisTicks);
        buf.writeInt(allyMaxAegisPerPlayer);
        buf.writeInt(shimmeredHighlightDurationTicks);
        buf.writeInt(shimmeredHighlightRadius);
        buf.writeDouble(apexMaxBonus);
        buf.writeInt(perpetualCrucibleBaseDurationTicks);
        buf.writeInt(perpetualCrucibleDurationPerLevelTicks);
        buf.writeDouble(featheredDrawSpeedLevel1);
        buf.writeDouble(featheredDrawSpeedLevel2);
        buf.writeDouble(featheredDrawSpeedLevel3);
        buf.writeDouble(featheredDrawSpeedLevel4);
        buf.writeDouble(bullseyeDamagePerLevel);
        buf.writeInt(bullseyeDurationTicks);
        buf.writeDouble(bullseyeHitRadius);
        buf.writeDouble(splinteringDamageMultiplier);
        buf.writeDouble(autofireDamageMultiplier);
        buf.writeDouble(gamblersGambitChance);
        buf.writeInt(hunterDurationPerLevelTicks);
        buf.writeDouble(voodooChancePerLevel);
        buf.writeDouble(voodooHealthPerLevel);
        buf.writeDouble(voodooSpeedMultiplier);
        buf.writeDouble(voodooBaseDamage);
        buf.writeDouble(voodooDamagePerLevel);
        buf.writeInt(voodooMaxAlliesPerPlayer);
        buf.writeInt(kratosDurabilityCost);
        buf.writeDouble(kratosHookSpeed);
        buf.writeDouble(kratosPullPower);
        buf.writeInt(kratosHookLifetimeTicks);
        buf.writeInt(kratosCooldownTicks);

        buf.writeInt(gamblersGambitBlacklist.length);
        for (String entry : gamblersGambitBlacklist) {
            ByteBufUtils.writeUTF8String(buf, entry == null ? "" : entry);
        }

        buf.writeInt(ENABLED_ENCHANTMENTS.size());
        for (Map.Entry<String, Boolean> entry : ENABLED_ENCHANTMENTS.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeBoolean(entry.getValue());
        }
    }

    public static void readFrom(ByteBuf buf) {
        enableAllEnchantments = buf.readBoolean();
        fixRangeEnchantmentDamage = buf.readBoolean();
        carnagePenaltyPerLevel = buf.readDouble();
        carnageBaseBonusPerLevel = buf.readDouble();
        carnageBonusLevel1 = buf.readDouble();
        carnageBonusLevel2 = buf.readDouble();
        carnageBonusLevel3 = buf.readDouble();
        carnageMaxStacksPerLevel = buf.readInt();
        carnageDecayTicks = buf.readInt();
        carnageDecayStacks = buf.readInt();
        enlargementChancePerLevel = buf.readDouble();
        enlargementSizePerLevel = buf.readDouble();
        enlargementMaxSizeMultiplier = buf.readDouble();
        enlargementDurationTicks = buf.readInt();
        dampeningStrikeAggroRadius = buf.readDouble();
        quietChippingAggroRadius = buf.readDouble();
        punctureArmorBypass = buf.readDouble();
        dulledImpactReductionPerLevel = buf.readDouble();
        dulledImpactDurationTicks = buf.readInt();
        bracedPivotCooldownReduction = buf.readDouble();
        volatileDischargeExplosionPower = buf.readDouble();
        staggerParalysisTicks = buf.readInt();
        allyMaxAegisPerPlayer = buf.readInt();
        shimmeredHighlightDurationTicks = buf.readInt();
        shimmeredHighlightRadius = buf.readInt();
        apexMaxBonus = buf.readDouble();
        perpetualCrucibleBaseDurationTicks = buf.readInt();
        perpetualCrucibleDurationPerLevelTicks = buf.readInt();
        featheredDrawSpeedLevel1 = buf.readDouble();
        featheredDrawSpeedLevel2 = buf.readDouble();
        featheredDrawSpeedLevel3 = buf.readDouble();
        featheredDrawSpeedLevel4 = buf.readDouble();
        bullseyeDamagePerLevel = buf.readDouble();
        bullseyeDurationTicks = buf.readInt();
        bullseyeHitRadius = buf.readDouble();
        splinteringDamageMultiplier = buf.readDouble();
        autofireDamageMultiplier = buf.readDouble();
        gamblersGambitChance = buf.readDouble();
        hunterDurationPerLevelTicks = buf.readInt();
        voodooChancePerLevel = buf.readDouble();
        voodooHealthPerLevel = buf.readDouble();
        voodooSpeedMultiplier = buf.readDouble();
        voodooBaseDamage = buf.readDouble();
        voodooDamagePerLevel = buf.readDouble();
        voodooMaxAlliesPerPlayer = buf.readInt();
        kratosDurabilityCost = buf.readInt();
        kratosHookSpeed = buf.readDouble();
        kratosPullPower = buf.readDouble();
        kratosHookLifetimeTicks = buf.readInt();
        kratosCooldownTicks = buf.readInt();

        int blacklistSize = buf.readInt();
        gamblersGambitBlacklist = new String[blacklistSize];
        for (int i = 0; i < blacklistSize; i++) {
            gamblersGambitBlacklist[i] = ByteBufUtils.readUTF8String(buf);
        }

        ENABLED_ENCHANTMENTS.clear();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            String key = ByteBufUtils.readUTF8String(buf);
            ENABLED_ENCHANTMENTS.put(key, buf.readBoolean());
        }
    }

    public static boolean isDisabled(Enchantment enchantment) {
        if (enchantment == null) {
            return false;
        }
        ResourceLocation name = enchantment.getRegistryName();
        return name != null && "divineenchantments".equals(name.getNamespace()) && !isEnabled(enchantment);
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
