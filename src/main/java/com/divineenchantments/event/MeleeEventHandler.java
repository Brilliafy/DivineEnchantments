package com.divineenchantments.event;

import com.divineenchantments.bleed.BleedManager;
import com.divineenchantments.compat.LycanitesIntegration;
import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ChanceHelper;
import com.divineenchantments.potion.ModPotions;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class MeleeEventHandler {

    private static Potion VULNERABLE_POTION = null;
    private static boolean CHECKED_VULNERABLE = false;

    private static Potion getVulnerablePotion() {
        if (!CHECKED_VULNERABLE) {
            CHECKED_VULNERABLE = true;
            VULNERABLE_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "vulnerable"));
        }
        return VULNERABLE_POTION;
    }

    private static volatile Set<Class<?>> NETHER_SPAWN_CLASSES = null;

    private static void ensureNetherSpawnTable() {
        if (NETHER_SPAWN_CLASSES != null) {
            return;
        }
        synchronized (MeleeEventHandler.class) {
            if (NETHER_SPAWN_CLASSES != null) {
                return;
            }

            Set<Class<?>> classes = new HashSet<>();
            for (Biome biome : Biome.REGISTRY) {
                if (biome == null) {
                    continue;
                }
                ResourceLocation biomeName = biome.getRegistryName();
                boolean isNether = BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER);
                if (!isNether && biomeName != null) {
                    String path = biomeName.getPath().toLowerCase();
                    isNether = path.contains("nether") || path.contains("hell") || path.contains("infernal");
                }
                if (!isNether) {
                    continue;
                }
                for (EnumCreatureType type : EnumCreatureType.values()) {
                    for (Biome.SpawnListEntry entry : biome.getSpawnableList(type)) {
                        if (entry != null && entry.entityClass != null) {
                            classes.add(entry.entityClass);
                        }
                    }
                }
            }

            // Structure and boss mobs that do not appear in the Nether biome spawn lists.
            classes.add(EntityBlaze.class);
            classes.add(EntityWitherSkeleton.class);
            classes.add(EntityWither.class);

            NETHER_SPAWN_CLASSES = classes;
        }
    }

    private static boolean isNaturalNetherSpawn(EntityLivingBase target) {
        ensureNetherSpawnTable();
        for (Class<?> clazz = target.getClass(); clazz != null && clazz != Entity.class; clazz = clazz.getSuperclass()) {
            if (NETHER_SPAWN_CLASSES.contains(clazz)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDemonicOrNetherTarget(EntityLivingBase target) {
        if (target == null) {
            return false;
        }

        // Any mob that naturally spawns in a Nether biome, including modded Nether biomes.
        if (isNaturalNetherSpawn(target)) {
            return true;
        }

        // Vanilla Nether bosses and structure mobs.
        if (target instanceof EntityGhast || target instanceof EntityMagmaCube
                || target instanceof EntityBlaze || target instanceof EntityWitherSkeleton
                || target instanceof EntityPigZombie || target instanceof EntityWither) {
            return true;
        }

        // Lycanites Mobs demon / shadow check.
        if (Loader.isModLoaded("lycanitesmobs") && LycanitesIntegration.isDemonOrShadowCreature(target)) {
            return true;
        }

        // Registry key fallback for modded mobs whose spawn entries are not exposed.
        ResourceLocation reg = EntityList.getKey(target);
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("wither") || path.contains("blaze") || path.contains("ghast")
                    || path.contains("magma") || path.contains("nether") || path.contains("hell")
                    || path.contains("infernal") || path.contains("pigman") || path.contains("pig_zombie")
                    || path.contains("demon") || path.contains("shadow") || path.contains("imp")) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        // Prevent recursive bleed damage triggers
        if ("bleed".equals(event.getSource().getDamageType())) {
            return;
        }

        // Dulled Impact mitigation check on attacker
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase sourceEntity = (EntityLivingBase) event.getSource().getTrueSource();
            if (sourceEntity.getEntityData().hasKey("DE_DulledExpiry")) {
                long expiry = sourceEntity.getEntityData().getLong("DE_DulledExpiry");
                if (sourceEntity.world.getTotalWorldTime() <= expiry) {
                    float reduction = sourceEntity.getEntityData().getFloat("DE_DulledReduction");
                    event.setAmount(Math.max(0.0F, event.getAmount() * (1.0F - reduction)));
                }
            }

            // Stagger attack interruption: pauses basic mob attack windup for 10 ticks (0.5s)
            if (sourceEntity.getEntityData().hasKey("DE_StaggerUntil")) {
                long staggerUntil = sourceEntity.getEntityData().getLong("DE_StaggerUntil");
                if (sourceEntity.world.getTotalWorldTime() <= staggerUntil) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // 1. Direct melee hit by a player
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (player.getEntityData().getBoolean("DE_Sweeping")) {
                return; // Prevent recursive sweep triggers on secondary swept targets
            }
            player.getEntityData().setLong("DE_LastAttackTime", player.world.getTotalWorldTime());
            ItemStack held = player.getHeldItemMainhand();
            EntityLivingBase target = event.getEntityLiving();

            // Challenger's Mark: Shield bash forces target to lock onto player for 3s (I) / 6s (II) with a 6s cooldown
            int challengerLvl = 0;
            ItemStack shield = ItemStack.EMPTY;
            ItemStack offhand = player.getHeldItemOffhand();
            if (ItemCheckHelper.isShield(offhand) && ModEnchantments.getLevel(ModEnchantments.CHALLENGERS_MARK, offhand) > 0) {
                challengerLvl = ModEnchantments.getLevel(ModEnchantments.CHALLENGERS_MARK, offhand);
                shield = offhand;
            } else if (ItemCheckHelper.isShield(held) && ModEnchantments.getLevel(ModEnchantments.CHALLENGERS_MARK, held) > 0) {
                challengerLvl = ModEnchantments.getLevel(ModEnchantments.CHALLENGERS_MARK, held);
                shield = held;
            }

            if (challengerLvl > 0) {
                boolean isShieldBash = player.isActiveItemStackBlocking() || player.isHandActive() || held == shield;
                if (isShieldBash) {
                    long cd = target.getEntityData().getLong("DE_ChallengerCD_" + player.getUniqueID());
                    if (target.world.getTotalWorldTime() >= cd) {
                        target.getEntityData().setLong("DE_ChallengerCD_" + player.getUniqueID(), target.world.getTotalWorldTime() + 120L); // 6s cooldown
                        int lockTicks = (challengerLvl >= 2) ? 120 : 60; // 3s for I, 6s for II
                        target.getEntityData().setLong("DE_ChallengerLockUntil", target.world.getTotalWorldTime() + lockTicks);
                        target.getEntityData().setString("DE_ChallengerLockPlayer", player.getUniqueID().toString());

                        if (target instanceof net.minecraft.entity.EntityLiving) {
                            net.minecraft.entity.EntityLiving livingMob = (net.minecraft.entity.EntityLiving) target;
                            livingMob.setAttackTarget(player);
                            livingMob.setRevengeTarget(player);
                            if (livingMob.getNavigator() != null) {
                                livingMob.getNavigator().tryMoveToEntityLiving(player, 1.25D);
                            }
                        }

                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, target.posX, target.posY + target.height * 0.8D, target.posZ, 5, 0.3D, 0.2D, 0.3D, 0.05D);
                        }
                        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.8F);
                        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_IRONGOLEM_ATTACK, SoundCategory.PLAYERS, 0.7F, 1.3F);
                    }
                }
            }

            if (!held.isEmpty()) {

                // Chainsaw Enchantment
                int chainsawLevel = ModEnchantments.getLevel(ModEnchantments.CHAINSAW, held);
                if (chainsawLevel > 0) {
                    float weaponDamage = event.getAmount();
                    if (weaponDamage <= 0.0F) {
                        weaponDamage = 1.0F;
                    }
                    // "(4+n)% of the weapon damage for 3+n times, each bleed happens 0.3s after the other"
                    float damagePerTick = weaponDamage * (4.0F + chainsawLevel) / 100.0F;
                    int totalBleeds = 3 + chainsawLevel;

                    BleedManager.addBleed(target, player, damagePerTick, totalBleeds);
                }

                // Demonic Edge: +2.5 * level bonus damage to nether mobs and lycanites demon/shadow mobs
                int demonicLevel = ModEnchantments.getLevel(ModEnchantments.DEMONIC_EDGE, held);
                if (demonicLevel > 0 && isDemonicOrNetherTarget(target)) {
                    float bonus = 2.5F * demonicLevel;
                    event.setAmount(event.getAmount() + bonus);
                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY + target.height * 0.5D, target.posZ, 15, 0.25D, 0.25D, 0.25D, 0.05D);
                        ws.spawnParticle(EnumParticleTypes.LAVA, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.05D);
                    }
                    target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 0.8F, 1.2F);
                }

                // Breach: 20*n% chance to apply Potion Core vulnerability for 15s, 15*n% chance for 10x durability damage to armor
                int breachLevel = ModEnchantments.getLevel(ModEnchantments.BREACH, held);
                if (breachLevel > 0) {
                    if (ChanceHelper.roll(player, "breach_vulnerable_" + target.getEntityId(), 0.20F * breachLevel)) {
                        Potion vuln = getVulnerablePotion();
                        if (vuln != null) {
                            target.addPotionEffect(new PotionEffect(vuln, 300, 0, false, true));
                        } else {
                            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 1, false, true));
                        }
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.9F, 1.4F);
                    }

                    if (ChanceHelper.roll(player, "breach_armor_" + target.getEntityId(), 0.15F * breachLevel)) {
                        boolean damagedAny = false;
                        for (ItemStack armor : target.getArmorInventoryList()) {
                            if (!armor.isEmpty() && armor.isItemStackDamageable()) {
                                armor.damageItem(9, target); // Deals 9 extra durability damage (10x total)
                                damagedAny = true;
                            }
                        }
                        if (damagedAny) {
                            target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.5F);
                            if (target.world instanceof WorldServer) {
                                WorldServer ws = (WorldServer) target.world;
                                ws.spawnParticle(EnumParticleTypes.CRIT, target.posX, target.posY + target.height * 0.5D, target.posZ, 20, 0.3D, 0.3D, 0.3D, 0.1D);
                            }
                        }
                    }
                }

                // Necrotic Stasis: Level 1 applies lycanitemobs:decay for 10s; Level 2 additionally strips regeneration and absorption
                int necroticLevel = ModEnchantments.getLevel(ModEnchantments.NECROTIC_STASIS, held);
                if (necroticLevel > 0) {
                    Potion decay = LycanitesIntegration.getDecayPotion();
                    if (decay != null) {
                        target.addPotionEffect(new PotionEffect(decay, 200, 0, false, true));
                    } else {
                        target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0, false, true));
                    }

                    if (necroticLevel >= 2) {
                        target.removePotionEffect(MobEffects.REGENERATION);
                        target.removePotionEffect(MobEffects.ABSORPTION);
                        target.setAbsorptionAmount(0.0F);
                    }

                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.SPELL_WITCH, target.posX, target.posY + target.height * 0.5D, target.posZ, 12, 0.25D, 0.25D, 0.25D, 0.05D);
                    }
                    target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 0.5F, 1.5F);
                }

                // Volatile Discharge: Charges enemy by 3*n% per hit; at 100% and over explodes like a creeper
                int volatileLevel = ModEnchantments.getLevel(ModEnchantments.VOLATILE_DISCHARGE, held);
                if (volatileLevel > 0) {
                    float charge = target.getEntityData().getFloat("DE_VolatileCharge");
                    charge += (3.0F * volatileLevel);

                    if (charge >= 100.0F) {
                        target.getEntityData().setFloat("DE_VolatileCharge", 0.0F);
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.2F, 0.5F);
                        boolean grief = target.world.getGameRules().getBoolean("mobGriefing");
                        target.world.createExplosion(target, target.posX, target.posY + target.height * 0.5D, target.posZ, (float) DivineConfig.volatileDischargeExplosionPower, grief);
                    } else {
                        target.getEntityData().setFloat("DE_VolatileCharge", charge);
                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Berserker's Bloodlust: Up to +150% damage at near-death
                int berserkLvl = ModEnchantments.getLevel(ModEnchantments.BERSERKERS_BLOODLUST, held);
                if (berserkLvl > 0) {
                    float missingRatio = net.minecraft.util.math.MathHelper.clamp(1.0F - (player.getHealth() / player.getMaxHealth()), 0.0F, 1.0F);
                    event.setAmount(event.getAmount() * (1.0F + 1.50F * missingRatio));
                    if (player.world instanceof WorldServer && missingRatio > 0.15F) {
                        WorldServer ws = (WorldServer) player.world;
                        ws.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + target.height * 0.5D, target.posZ, Math.max(1, (int) (missingRatio * 8)), 0.2D, 0.2D, 0.2D, 0.05D);
                    }
                }

                // Headslicer: +15%, +30%, +50% extra damage while airborne
                int headslicerLvl = ModEnchantments.getLevel(ModEnchantments.HEADSLICER, held);
                if (headslicerLvl > 0) {
                    if (!player.onGround && !player.isInWater() && !player.isOnLadder() && !player.isRiding()) {
                        float bonus = (headslicerLvl == 1 ? 0.15F : (headslicerLvl == 2 ? 0.30F : 0.50F));
                        event.setAmount(event.getAmount() * (1.0F + bonus));
                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, target.posX, target.posY + target.height * 0.7D, target.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            ws.spawnParticle(EnumParticleTypes.CRIT, target.posX, target.posY + target.height * 0.5D, target.posZ, 10, 0.2D, 0.2D, 0.2D, 0.1D);
                        }
                        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0F, 1.3F);
                    }
                }

                // Culling: +25% damage exclusively to targets below 25% max health
                int cullingLvl = ModEnchantments.getLevel(ModEnchantments.CULLING, held);
                if (cullingLvl > 0) {
                    if (target.getHealth() <= target.getMaxHealth() * 0.25F) {
                        event.setAmount(event.getAmount() * 1.25F);
                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + target.height * 0.5D, target.posZ, 5, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_IRONGOLEM_ATTACK, SoundCategory.PLAYERS, 0.8F, 1.5F);
                    }
                }

                // Dulled Impact: Reduces target's outgoing damage by 10/20/30% for 2 seconds
                int dulledLvl = ModEnchantments.getLevel(ModEnchantments.DULLED_IMPACT, held);
                if (dulledLvl > 0) {
                    float reduction = (float) (DivineConfig.dulledImpactReductionPerLevel * dulledLvl);
                    target.getEntityData().setFloat("DE_DulledReduction", reduction);
                    target.getEntityData().setLong("DE_DulledExpiry", target.world.getTotalWorldTime() + DivineConfig.dulledImpactDurationTicks);
                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.02D);
                    }
                }

                // Umbral Prowess: +20% bonus physical damage in deep darkness (light level 4 or below)
                net.minecraft.item.ItemStack helm = player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.HEAD);
                if (!helm.isEmpty() && ModEnchantments.getLevel(ModEnchantments.UMBRAL_PROWESS, helm) > 0) {
                    int light = player.world.getLight(player.getPosition());
                    if (light <= 4) {
                        event.setAmount(event.getAmount() * 1.20F);
                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.PORTAL, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Stagger: pauses attack windup and applies Lycanites paralysis (3s, or 1s if the target wears a helmet).
                int staggerLvl = ModEnchantments.getLevel(ModEnchantments.STAGGER, held);
                if (staggerLvl > 0 && !ItemCheckHelper.isBossEntity(target)) {
                    boolean isChamp = target.getEntityData().hasKey("champions:championship");
                    if (!isChamp) {
                        target.getEntityData().setLong("DE_StaggerUntil", target.world.getTotalWorldTime() + 10L);
                        int paralysisTicks = DivineConfig.staggerParalysisTicks;
                        if (!target.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.HEAD).isEmpty()) {
                            paralysisTicks = Math.max(20, paralysisTicks / 3);
                        }
                        if (Loader.isModLoaded("lycanitesmobs")) {
                            com.divineenchantments.compat.LycanitesIntegration.applyParalysis(target, paralysisTicks);
                        }
                        target.motionX *= 0.6D;
                        target.motionZ *= 0.6D;
                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.CRIT, target.posX, target.posY + target.height * 0.6D, target.posZ, 5, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Puncture: Bypasses 10% of natural mob armor
                int punctureLvl = ModEnchantments.getLevel(ModEnchantments.PUNCTURE, held);
                if (punctureLvl > 0) {
                    int naturalArmor = target.getTotalArmorValue();
                    if (naturalArmor > 0) {
                        float dmg = event.getAmount();
                        float toughness = 0.0F;
                        try {
                            toughness = (float) target.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
                        } catch (Throwable ignored) {
                        }
                        float f1 = 2.0F + toughness / 4.0F;
                        float fOrig = net.minecraft.util.math.MathHelper.clamp((float) naturalArmor - dmg / f1, (float) naturalArmor * 0.2F, 20.0F);
                        float redOrig = dmg * (1.0F - fOrig / 25.0F);

                        int reducedArmor = Math.max(0, (int) Math.floor(naturalArmor * (1.0D - DivineConfig.punctureArmorBypass)));
                        float fNew = net.minecraft.util.math.MathHelper.clamp((float) reducedArmor - dmg / f1, (float) reducedArmor * 0.2F, 20.0F);
                        float redNew = dmg * (1.0F - fNew / 25.0F);

                        float extra = Math.max(0.0F, redNew - redOrig);
                        if (extra > 0.0F) {
                            event.setAmount(dmg + extra);
                            if (target.world instanceof WorldServer) {
                                WorldServer ws = (WorldServer) target.world;
                                ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, target.posX, target.posY + target.height * 0.5D, target.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            }
                        }
                    }
                }

                // Low Sweep: custom radial sweep. Full damage to the struck target, 50% splash to nearby enemies.
                int lowSweepLvl = ModEnchantments.getLevel(ModEnchantments.LOW_SWEEP, held);
                if (lowSweepLvl > 0) {
                    long now = target.world.getTotalWorldTime();
                    target.getEntityData().setLong("DE_StaggerUntil", now + 20L);
                    if (!ItemCheckHelper.isBossEntity(target)) {
                        boolean targetShort = isShortMob(target);
                        if (targetShort) {
                            if (Loader.isModLoaded("lycanitesmobs")) {
                                com.divineenchantments.compat.LycanitesIntegration.applyParalysis(target, DivineConfig.lowSweepParalysisTicks);
                            }
                        }
                    }

                    double sweepRadius = 2.8D + lowSweepLvl * 0.6D;
                    java.util.List<EntityLivingBase> sweepList = target.world.getEntitiesWithinAABB(EntityLivingBase.class,
                            player.getEntityBoundingBox().grow(sweepRadius, 1.0D, sweepRadius));
                    player.getEntityData().setBoolean("DE_Sweeping", true);
                    try {
                        for (EntityLivingBase other : sweepList) {
                            if (other == player || other == target || !other.isEntityAlive() || player.isOnSameTeam(other)) {
                                continue;
                            }
                            other.getEntityData().setLong("DE_StaggerUntil", now + 20L);
                            if (!ItemCheckHelper.isBossEntity(other)) {
                                if (Loader.isModLoaded("lycanitesmobs")) {
                                    com.divineenchantments.compat.LycanitesIntegration.applyParalysis(other, DivineConfig.lowSweepParalysisTicks);
                                }
                            }
                            double dx = other.posX - player.posX;
                            double dz = other.posZ - player.posZ;
                            double dLen = Math.sqrt(dx * dx + dz * dz);
                            if (dLen > 0.001D) {
                                other.knockBack(player, 0.30F + 0.08F * lowSweepLvl, -dx / dLen, -dz / dLen);
                            }
                            other.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player),
                                    event.getAmount() * (0.35F + 0.10F * lowSweepLvl));
                            if (other.world instanceof WorldServer) {
                                ((WorldServer) other.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK,
                                        other.posX, other.posY + 0.35D, other.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            }
                        }
                    } finally {
                        player.getEntityData().removeTag("DE_Sweeping");
                    }

                    if (player.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) player.world;
                        ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, target.posX, target.posY + 0.15D, target.posZ, 3, 0.3D, 0.0D, 0.3D, 0.0D);
                        int dirtId = net.minecraft.block.Block.getStateId(net.minecraft.init.Blocks.DIRT.getDefaultState());
                        ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, target.posX, target.posY + 0.05D, target.posZ, 10, 0.5D, 0.05D, 0.5D, 0.02D, dirtId);
                    }
                }

                // Enlargement: 2 * level% chance to inflate the target by 10 * level% for 20 seconds.
                int enlargementLevel = ModEnchantments.getLevel(ModEnchantments.ENLARGEMENT, held);
                if (enlargementLevel > 0 && target.isEntityAlive()) {
                    if (ChanceHelper.roll(player, "enlargement_" + target.getEntityId(), (float) DivineConfig.enlargementChancePerLevel * enlargementLevel)) {
                        int duration = DivineConfig.enlargementDurationTicks;
                        if (ItemCheckHelper.isBossEntity(target) || target.getMaxHealth() > 150.0F) {
                            duration = Math.max(20, duration / 2);
                        }
                        target.addPotionEffect(new PotionEffect(ModPotions.ENLARGEMENT, duration, enlargementLevel - 1, false, true));
                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                                    target.posX, target.posY + target.height * 0.5D, target.posZ,
                                    18, 0.4D, 0.5D, 0.4D, 0.05D);
                            ws.spawnParticle(EnumParticleTypes.END_ROD,
                                    target.posX, target.posY + target.height * 0.5D, target.posZ,
                                    10, 0.35D, 0.45D, 0.35D, 0.03D);
                        }
                        target.world.playSound(null, target.posX, target.posY, target.posZ,
                                SoundEvents.ENTITY_SHULKER_OPEN, SoundCategory.PLAYERS, 0.8F, 0.7F);
                    }
                }

                // Ally Enchantment (2% chance on hit to spawn friendly ephemeral Aegis)
                if (ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs")) {
                    int allyLevel = ModEnchantments.getLevel(ModEnchantments.ALLY, held);
                    if (allyLevel > 0) {
                        if (ChanceHelper.roll(player, "ally_hit", 0.02F)) {
                            LycanitesIntegration.spawnAegis(player, target);
                        }
                    }
                }
            }
        }

        // 2. Ally defense proc: If a player is attacked and has Ally on an equipped item
        if (event.getEntityLiving() instanceof EntityPlayer && ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs")) {
            EntityPlayer victim = (EntityPlayer) event.getEntityLiving();
            int totalAllyLevel = ModEnchantments.getMaxLevel(ModEnchantments.ALLY, victim);
            if (totalAllyLevel > 0 && ChanceHelper.roll(victim, "ally_defense", 0.02F)) {
                EntityLivingBase attacker = null;
                if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                    attacker = (EntityLivingBase) event.getSource().getTrueSource();
                }
                LycanitesIntegration.spawnAegis(victim, attacker);
            }
        }
    }

    private static boolean isShortMob(EntityLivingBase entity) {
        if (entity == null) {
            return false;
        }
        String name = entity.getClass().getName().toLowerCase();
        return entity.height <= 0.95F
                || entity instanceof net.minecraft.entity.monster.EntitySilverfish
                || entity instanceof net.minecraft.entity.monster.EntitySpider
                || name.contains("hound") || name.contains("crawler") || name.contains("chupacabra");
    }
}
