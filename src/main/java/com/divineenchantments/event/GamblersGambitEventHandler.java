package com.divineenchantments.event;

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
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class GamblersGambitEventHandler {

    private static final List<PotionSupplier> NEGATIVE_POTIONS = new ArrayList<>();
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

        // Vanilla negative effects
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.SLOWNESS, 140, 1));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.WEAKNESS, 140, 1));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.POISON, 100, 0));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.WITHER, 80, 0));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.BLINDNESS, 100, 0));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.GLOWING, 160, 0));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.LEVITATION, 60, 0));
        NEGATIVE_POTIONS.add(new PotionSupplier(MobEffects.HUNGER, 140, 1));

        // Potion Core negative effects
        if (Loader.isModLoaded("potioncore")) {
            addPotionCoreIfPresent("curse", 100, 0);
            addPotionCoreIfPresent("broken_armor", 140, 0);
            addPotionCoreIfPresent("broken_magic_shield", 140, 0);
            addPotionCoreIfPresent("vulnerable", 120, 0);
            addPotionCoreIfPresent("weight", 120, 1);
            addPotionCoreIfPresent("perplexity", 100, 0);
            addPotionCoreIfPresent("spin", 100, 0);
            addPotionCoreIfPresent("rust", 80, 0);
            addPotionCoreIfPresent("lightning", 40, 0);
        }
    }

    private static void addPotionCoreIfPresent(String name, int duration, int amplifier) {
        Potion p = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", name));
        if (p != null) {
            NEGATIVE_POTIONS.add(new PotionSupplier(p, duration, amplifier));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack held = player.getHeldItemMainhand();

            if (ItemCheckHelper.isMeleeWeapon(held) && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.GAMBLERS_GAMBIT, held) > 0) {
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
