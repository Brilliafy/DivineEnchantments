package com.divineenchantments.event;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class GamblersGambitEventHandler {

    private static final List<PotionSupplier> NEGATIVE_POTIONS = new ArrayList<>();
    private static final Set<String> BLACKLIST = new HashSet<>();
    private static boolean INITIALIZED = false;

    private static class PotionSupplier {
        final Potion potion;
        final int duration;
        final int amplifier;

        PotionSupplier(Potion potion, int duration, int amplifier) {
            this.potion = potion;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }

    private static void initPotions() {
        if (INITIALIZED) return;
        INITIALIZED = true;

        Set<Potion> added = new HashSet<>();
        BLACKLIST.clear();
        for (String entry : DivineConfig.gamblersGambitBlacklist) {
            if (entry != null && !entry.trim().isEmpty()) {
                BLACKLIST.add(entry.trim().toLowerCase());
            }
        }

        // Vanilla negative effects with hand-tuned durations.
        addUnique(added, MobEffects.SLOWNESS, 140, 1);
        addUnique(added, MobEffects.WEAKNESS, 140, 1);
        addUnique(added, MobEffects.POISON, 100, 0);
        addUnique(added, MobEffects.WITHER, 80, 0);
        addUnique(added, MobEffects.BLINDNESS, 100, 0);
        addUnique(added, MobEffects.LEVITATION, 60, 0);
        addUnique(added, MobEffects.HUNGER, 140, 1);

        // Potion Core negative effects with hand-tuned durations.
        if (Loader.isModLoaded("potioncore")) {
            addPotionCoreIfPresent(added, "curse", 100, 0);
            addPotionCoreIfPresent(added, "broken_armor", 140, 0);
            addPotionCoreIfPresent(added, "broken_magic_shield", 140, 0);
            addPotionCoreIfPresent(added, "vulnerable", 120, 0);
            addPotionCoreIfPresent(added, "weight", 120, 1);
            addPotionCoreIfPresent(added, "perplexity", 100, 0);
            addPotionCoreIfPresent(added, "spin", 100, 0);
            addPotionCoreIfPresent(added, "rust", 80, 0);
            addPotionCoreIfPresent(added, "lightning", 40, 0);
            addPotionCoreIfPresent(added, "invert", 100, 0);
        }

        // Include every other negative potion registered by Potion Core or any other mod.
        for (Potion potion : Potion.REGISTRY) {
            if (potion != null && potion.isBadEffect() && !potion.isInstant() && !isBlacklisted(potion) && added.add(potion)) {
                NEGATIVE_POTIONS.add(new PotionSupplier(potion, 120, 0));
            }
        }
    }

    private static void addUnique(Set<Potion> added, Potion potion, int duration, int amplifier) {
        if (potion == null || isBlacklisted(potion) || !added.add(potion)) {
            return;
        }
        NEGATIVE_POTIONS.add(new PotionSupplier(potion, duration, amplifier));
    }

    private static boolean isBlacklisted(Potion potion) {
        ResourceLocation name = potion.getRegistryName();
        if (name == null || BLACKLIST.isEmpty()) {
            return false;
        }
        return BLACKLIST.contains(name.toString().toLowerCase()) || BLACKLIST.contains(name.getPath().toLowerCase());
    }

    private static void addPotionCoreIfPresent(Set<Potion> added, String name, int duration, int amplifier) {
        Potion p = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", name));
        addUnique(added, p, duration, amplifier);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack held = player.getHeldItemMainhand();

            if (ItemCheckHelper.isMeleeWeapon(held) && ModEnchantments.getLevel(ModEnchantments.GAMBLERS_GAMBIT, held) > 0) {
                initPotions();
                if (NEGATIVE_POTIONS.isEmpty()) return;

                Random rand = player.world.rand;
                PotionSupplier chosen = NEGATIVE_POTIONS.get(rand.nextInt(NEGATIVE_POTIONS.size()));

                EntityLivingBase victim = event.getEntityLiving();
                victim.addPotionEffect(new PotionEffect(chosen.potion, chosen.duration, chosen.amplifier, false, true));

                // Visual and audio feedback
                victim.world.playSound(null, victim.posX, victim.posY, victim.posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.PLAYERS, 0.9F, 1.8F);
                victim.world.playSound(null, victim.posX, victim.posY, victim.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8F, 1.6F);

                if (victim.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) victim.world;
                    ws.spawnParticle(EnumParticleTypes.SPELL_MOB, victim.posX, victim.posY + victim.height * 0.5D, victim.posZ, 20, 0.4D, 0.4D, 0.4D, 0.1D);
                }
            }
        }
    }
}
