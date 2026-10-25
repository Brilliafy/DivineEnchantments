package com.divineenchantments.init;

import com.divineenchantments.enchantment.*;
import com.divineenchantments.config.DivineConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class ModEnchantments {

    public static final EnchantmentGravitational GRAVITATIONAL = new EnchantmentGravitational();
    public static final EnchantmentChainsaw CHAINSAW = new EnchantmentChainsaw();
    public static final EnchantmentPierceful PIERCEFUL = new EnchantmentPierceful();
    public static final EnchantmentSacrificial SACRIFICIAL = new EnchantmentSacrificial();
    public static final EnchantmentHeavyplated HEAVYPLATED = new EnchantmentHeavyplated();
    public static final EnchantmentAutofire AUTOFIRE = new EnchantmentAutofire();
    public static final EnchantmentWebber WEBBER = new EnchantmentWebber();
    public static final EnchantmentHeadhunter HEADHUNTER = new EnchantmentHeadhunter();
    public static final EnchantmentRiches RICHES = new EnchantmentRiches();
    public static final EnchantmentSlowsink SLOWSINK = new EnchantmentSlowsink();
    public static final EnchantmentApex APEX = new EnchantmentApex();
    public static final EnchantmentRoyalGuard ROYAL_GUARD = new EnchantmentRoyalGuard();
    public static final CurseOfTheTether CURSE_OF_THE_TETHER = new CurseOfTheTether();
    public static final CurseOfInertia CURSE_OF_INERTIA = new CurseOfInertia();

    // New Enchantments
    public static final EnchantmentAperture APERTURE = new EnchantmentAperture();
    public static final EnchantmentGyroscopicPlating GYROSCOPIC_PLATING = new EnchantmentGyroscopicPlating();
    public static final EnchantmentSlipBore SLIP_BORE = new EnchantmentSlipBore();
    public static final EnchantmentHarmonicAnchor HARMONIC_ANCHOR = new EnchantmentHarmonicAnchor();
    public static final EnchantmentPerpetualCrucible PERPETUAL_CRUCIBLE = new EnchantmentPerpetualCrucible();
    public static final EnchantmentGamblersGambit GAMBLERS_GAMBIT = new EnchantmentGamblersGambit();
    public static final EnchantmentGroundedVanguard GROUNDED_VANGUARD = new EnchantmentGroundedVanguard();
    public static final CurseOfTheVolatileSoul CURSE_OF_THE_VOLATILE_SOUL = new CurseOfTheVolatileSoul();

    // Latest Batch
    public static final EnchantmentBreach BREACH = new EnchantmentBreach();
    public static final EnchantmentDemonicEdge DEMONIC_EDGE = new EnchantmentDemonicEdge();
    public static final EnchantmentNecroticStasis NECROTIC_STASIS = new EnchantmentNecroticStasis();
    public static final EnchantmentVolatileDischarge VOLATILE_DISCHARGE = new EnchantmentVolatileDischarge();
    public static final EnchantmentMuzzle MUZZLE = new EnchantmentMuzzle();
    public static final EnchantmentArchery ARCHERY = new EnchantmentArchery();
    public static final EnchantmentArachnidAscent ARACHNID_ASCENT = new EnchantmentArachnidAscent();

    // New 15 Enchantments
    public static final EnchantmentUmbralProwess UMBRAL_PROWESS = new EnchantmentUmbralProwess();
    public static final EnchantmentGourmandsVitality GOURMANDS_VITALITY = new EnchantmentGourmandsVitality();
    public static final EnchantmentBallisticOverdrive BALLISTIC_OVERDRIVE = new EnchantmentBallisticOverdrive();
    public static final CurseDebtOfTheVoid DEBT_OF_THE_VOID = new CurseDebtOfTheVoid();
    public static final CurseBerserkersBloodlust BERSERKERS_BLOODLUST = new CurseBerserkersBloodlust();
    public static final EnchantmentSplinteringImpact SPLINTERING_IMPACT = new EnchantmentSplinteringImpact();
    public static final EnchantmentHeadslicer HEADSLICER = new EnchantmentHeadslicer();
    public static final EnchantmentLongdraw LONGDRAW = new EnchantmentLongdraw();
    public static final EnchantmentSuregrip SUREGRIP = new EnchantmentSuregrip();
    public static final EnchantmentCushionedHeel CUSHIONED_HEEL = new EnchantmentCushionedHeel();
    public static final EnchantmentMudstrider MUDSTRIDER = new EnchantmentMudstrider();
    public static final EnchantmentCulling CULLING = new EnchantmentCulling();
    public static final EnchantmentDulledImpact DULLED_IMPACT = new EnchantmentDulledImpact();
    public static final EnchantmentGlimmer GLIMMER = new EnchantmentGlimmer();
    public static final EnchantmentBracedPivot BRACED_PIVOT = new EnchantmentBracedPivot();

    // 16 New Enchantments
    public static final EnchantmentFeatheredDraw FEATHERED_DRAW = new EnchantmentFeatheredDraw();
    public static final EnchantmentStagger STAGGER = new EnchantmentStagger();
    public static final EnchantmentPuncture PUNCTURE = new EnchantmentPuncture();
    public static final EnchantmentTemperedCore TEMPERED_CORE = new EnchantmentTemperedCore();
    public static final EnchantmentThermalFoil THERMAL_FOIL = new EnchantmentThermalFoil();
    public static final EnchantmentMachineBow MACHINE_BOW = new EnchantmentMachineBow();
    public static final EnchantmentDesalinationMesh DESALINATION_MESH = new EnchantmentDesalinationMesh();
    public static final EnchantmentMetabolicSlumber METABOLIC_SLUMBER = new EnchantmentMetabolicSlumber();
    public static final EnchantmentSoberChisel SOBER_CHISEL = new EnchantmentSoberChisel();
    public static final EnchantmentShimmered SHIMMERED = new EnchantmentShimmered();
    public static final EnchantmentSiltSifter SILT_SIFTER = new EnchantmentSiltSifter();
    public static final EnchantmentDampeningStrike DAMPENING_STRIKE = new EnchantmentDampeningStrike();
    public static final EnchantmentCushionedShirt CUSHIONED_SHIRT = new EnchantmentCushionedShirt();
    public static final EnchantmentMagneticClasp MAGNETIC_CLASP = new EnchantmentMagneticClasp();
    public static final EnchantmentGroundSweep GROUND_SWEEP = new EnchantmentGroundSweep();

    public static final EnchantmentShadowStalker SHADOW_STALKER = new EnchantmentShadowStalker();
    public static final EnchantmentChallengersMark CHALLENGERS_MARK = new EnchantmentChallengersMark();
    public static final EnchantmentGargoyleStance GARGOYLE_STANCE = new EnchantmentGargoyleStance();
    public static final EnchantmentQuietChipping QUIET_CHIPPING = new EnchantmentQuietChipping();
    public static final EnchantmentSalvageLine SALVAGE_LINE = new EnchantmentSalvageLine();

    // Ancient / Rune batch
    public static final EnchantmentRuneCarnage RUNE_CARNAGE = new EnchantmentRuneCarnage();
    public static final EnchantmentEnlargement ENLARGEMENT = new EnchantmentEnlargement();
    public static final EnchantmentBullseye BULLSEYE = new EnchantmentBullseye();
    public static final EnchantmentHunter HUNTER = new EnchantmentHunter();
    public static final EnchantmentVoodoo VOODOO = new EnchantmentVoodoo();
    public static final EnchantmentKratosBlades KRATOS_BLADES = new EnchantmentKratosBlades();

    public static EnchantmentAlly ALLY = null;

    public static final List<Enchantment> ENCHANTMENTS = new ArrayList<>();

    static {
        ENCHANTMENTS.add(GRAVITATIONAL);
        ENCHANTMENTS.add(CHAINSAW);
        ENCHANTMENTS.add(PIERCEFUL);
        ENCHANTMENTS.add(SACRIFICIAL);
        ENCHANTMENTS.add(HEAVYPLATED);
        ENCHANTMENTS.add(AUTOFIRE);
        ENCHANTMENTS.add(WEBBER);
        ENCHANTMENTS.add(HEADHUNTER);
        ENCHANTMENTS.add(RICHES);
        ENCHANTMENTS.add(SLOWSINK);
        ENCHANTMENTS.add(APEX);
        ENCHANTMENTS.add(ROYAL_GUARD);
        ENCHANTMENTS.add(CURSE_OF_THE_TETHER);
        ENCHANTMENTS.add(CURSE_OF_INERTIA);

        ENCHANTMENTS.add(APERTURE);
        ENCHANTMENTS.add(GYROSCOPIC_PLATING);
        ENCHANTMENTS.add(SLIP_BORE);
        ENCHANTMENTS.add(HARMONIC_ANCHOR);
        ENCHANTMENTS.add(PERPETUAL_CRUCIBLE);
        ENCHANTMENTS.add(GAMBLERS_GAMBIT);
        ENCHANTMENTS.add(GROUNDED_VANGUARD);
        ENCHANTMENTS.add(CURSE_OF_THE_VOLATILE_SOUL);

        ENCHANTMENTS.add(BREACH);
        ENCHANTMENTS.add(DEMONIC_EDGE);
        ENCHANTMENTS.add(NECROTIC_STASIS);
        ENCHANTMENTS.add(VOLATILE_DISCHARGE);
        ENCHANTMENTS.add(MUZZLE);
        ENCHANTMENTS.add(ARCHERY);
        ENCHANTMENTS.add(ARACHNID_ASCENT);

        ENCHANTMENTS.add(UMBRAL_PROWESS);
        ENCHANTMENTS.add(GOURMANDS_VITALITY);
        ENCHANTMENTS.add(BALLISTIC_OVERDRIVE);
        ENCHANTMENTS.add(DEBT_OF_THE_VOID);
        ENCHANTMENTS.add(BERSERKERS_BLOODLUST);
        ENCHANTMENTS.add(SPLINTERING_IMPACT);
        ENCHANTMENTS.add(HEADSLICER);
        ENCHANTMENTS.add(LONGDRAW);
        ENCHANTMENTS.add(SUREGRIP);
        ENCHANTMENTS.add(CUSHIONED_HEEL);
        ENCHANTMENTS.add(MUDSTRIDER);
        ENCHANTMENTS.add(CULLING);
        ENCHANTMENTS.add(DULLED_IMPACT);
        ENCHANTMENTS.add(GLIMMER);
        ENCHANTMENTS.add(BRACED_PIVOT);

        ENCHANTMENTS.add(FEATHERED_DRAW);
        ENCHANTMENTS.add(STAGGER);
        ENCHANTMENTS.add(PUNCTURE);
        ENCHANTMENTS.add(TEMPERED_CORE);
        ENCHANTMENTS.add(THERMAL_FOIL);
        ENCHANTMENTS.add(MACHINE_BOW);
        ENCHANTMENTS.add(DESALINATION_MESH);
        ENCHANTMENTS.add(METABOLIC_SLUMBER);
        ENCHANTMENTS.add(SOBER_CHISEL);
        ENCHANTMENTS.add(SHIMMERED);
        ENCHANTMENTS.add(SILT_SIFTER);
        ENCHANTMENTS.add(DAMPENING_STRIKE);
        ENCHANTMENTS.add(CUSHIONED_SHIRT);
        ENCHANTMENTS.add(MAGNETIC_CLASP);
        ENCHANTMENTS.add(GROUND_SWEEP);

        ENCHANTMENTS.add(SHADOW_STALKER);
        ENCHANTMENTS.add(CHALLENGERS_MARK);
        ENCHANTMENTS.add(GARGOYLE_STANCE);
        ENCHANTMENTS.add(QUIET_CHIPPING);
        ENCHANTMENTS.add(SALVAGE_LINE);

        ENCHANTMENTS.add(RUNE_CARNAGE);
        ENCHANTMENTS.add(ENLARGEMENT);
        ENCHANTMENTS.add(BULLSEYE);
        ENCHANTMENTS.add(HUNTER);
        ENCHANTMENTS.add(VOODOO);
        ENCHANTMENTS.add(KRATOS_BLADES);

        if (Loader.isModLoaded("lycanitesmobs")) {
            ALLY = new EnchantmentAlly();
            ENCHANTMENTS.add(ALLY);
        }
    }

    @SubscribeEvent
    public static void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {
        // Double check in case Loader wasn't ready at static init
        if (ALLY == null && Loader.isModLoaded("lycanitesmobs")) {
            ALLY = new EnchantmentAlly();
            ENCHANTMENTS.add(ALLY);
        }

        for (Enchantment ench : ENCHANTMENTS) {
            event.getRegistry().register(ench);
        }
    }

    public static int getLevel(Enchantment enchantment, ItemStack stack) {
        if (enchantment == null || stack == null || stack.isEmpty() || !DivineConfig.isEnabled(enchantment)) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantment, stack);
    }

    public static int getMaxLevel(Enchantment enchantment, EntityLivingBase entity) {
        if (enchantment == null || entity == null || !DivineConfig.isEnabled(enchantment)) {
            return 0;
        }
        return EnchantmentHelper.getMaxEnchantmentLevel(enchantment, entity);
    }
}
