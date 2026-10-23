package com.divineenchantments.event;

import com.divineenchantments.bleed.BleedManager;
import com.divineenchantments.compat.LycanitesIntegration;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.potion.ModPotions;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    private static boolean isDemonicOrNetherTarget(EntityLivingBase target) {
        if (target == null) {
            return false;
        }

        // Vanilla Nether bosses and mobs
        if (target instanceof EntityGhast || target instanceof EntityMagmaCube
                || target instanceof EntityBlaze || target instanceof EntityWitherSkeleton
                || target instanceof EntityPigZombie || target instanceof EntityWither) {
            return true;
        }

        // Nether dimension fire-immune mobs
        if (target.dimension == -1 && target.isImmuneToFire()) {
            return true;
        }

        // Lycanites Mobs demon / shadow check
        if (Loader.isModLoaded("lycanitesmobs") && LycanitesIntegration.isDemonOrShadowCreature(target)) {
            return true;
        }

        // Registry key or class name fallback
        ResourceLocation reg = EntityList.getKey(target);
        if (reg != null) {
            String path = reg.getPath().toLowerCase();
            if (path.contains("wither") || path.contains("blaze") || path.contains("ghast")
                    || path.contains("magma") || path.contains("nether") || path.contains("pigman")
                    || path.contains("pig_zombie") || path.contains("demon") || path.contains("shadow")) {
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
                    event.setAmount(Math.max(1.0F, event.getAmount() - reduction));
                }
            }

            // Stagger attack interruption: pauses basic mob attack windup for 5 ticks (0.25s)
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
            if (ItemCheckHelper.isShield(offhand) && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CHALLENGERS_MARK, offhand) > 0) {
                challengerLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CHALLENGERS_MARK, offhand);
                shield = offhand;
            } else if (ItemCheckHelper.isShield(held) && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CHALLENGERS_MARK, held) > 0) {
                challengerLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CHALLENGERS_MARK, held);
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
                int chainsawLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CHAINSAW, held);
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
                int demonicLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.DEMONIC_EDGE, held);
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
                int breachLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BREACH, held);
                if (breachLevel > 0) {
                    if (player.world.rand.nextFloat() < (0.20F * breachLevel)) {
                        Potion vuln = getVulnerablePotion();
                        if (vuln != null) {
                            target.addPotionEffect(new PotionEffect(vuln, 300, 0, false, true));
                        } else {
                            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 1, false, true));
                        }
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.9F, 1.4F);
                    }

                    if (player.world.rand.nextFloat() < (0.15F * breachLevel)) {
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
                int necroticLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.NECROTIC_STASIS, held);
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
                int volatileLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.VOLATILE_DISCHARGE, held);
                if (volatileLevel > 0) {
                    float charge = target.getEntityData().getFloat("DE_VolatileCharge");
                    charge += (3.0F * volatileLevel);

                    if (charge >= 100.0F) {
                        target.getEntityData().setFloat("DE_VolatileCharge", 0.0F);
                        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.2F, 0.5F);
                        boolean grief = target.world.getGameRules().getBoolean("mobGriefing");
                        target.world.createExplosion(target, target.posX, target.posY + target.height * 0.5D, target.posZ, 3.0F, grief);
                    } else {
                        target.getEntityData().setFloat("DE_VolatileCharge", charge);
                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Berserker's Bloodlust: Up to +150% damage at near-death
                int berserkLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BERSERKERS_BLOODLUST, held);
                if (berserkLvl > 0) {
                    float missingRatio = net.minecraft.util.math.MathHelper.clamp(1.0F - (player.getHealth() / player.getMaxHealth()), 0.0F, 1.0F);
                    event.setAmount(event.getAmount() * (1.0F + 1.50F * missingRatio));
                    if (player.world instanceof WorldServer && missingRatio > 0.15F) {
                        WorldServer ws = (WorldServer) player.world;
                        ws.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + target.height * 0.5D, target.posZ, Math.max(1, (int) (missingRatio * 8)), 0.2D, 0.2D, 0.2D, 0.05D);
                    }
                }

                // Headslicer: +15%, +30%, +50% extra damage if airborne and near jump peak/maxima
                int headslicerLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HEADSLICER, held);
                if (headslicerLvl > 0) {
                    if (!player.onGround && !player.isInWater() && !player.isOnLadder() && !player.isRiding()) {
                        if (Math.abs(player.motionY) <= 0.12D) {
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
                }

                // Culling: +15% damage exclusively to targets below 20% max health
                int cullingLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CULLING, held);
                if (cullingLvl > 0) {
                    if (target.getHealth() <= target.getMaxHealth() * 0.20F) {
                        event.setAmount(event.getAmount() * 1.15F);
                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + target.height * 0.5D, target.posZ, 5, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                        player.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_IRONGOLEM_ATTACK, SoundCategory.PLAYERS, 0.8F, 1.5F);
                    }
                }

                // Dulled Impact: Reduces target's attack damage by flat 1/2/3 points for 2 seconds
                int dulledLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.DULLED_IMPACT, held);
                if (dulledLvl > 0) {
                    float flatReduction = (float) dulledLvl;
                    target.getEntityData().setFloat("DE_DulledReduction", flatReduction);
                    target.getEntityData().setLong("DE_DulledExpiry", target.world.getTotalWorldTime() + 40L);
                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.02D);
                    }
                }

                // Umbral Prowess: +20% bonus physical damage in deep darkness (light level 4 or below)
                net.minecraft.item.ItemStack helm = player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.HEAD);
                if (!helm.isEmpty() && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.UMBRAL_PROWESS, helm) > 0) {
                    int light = player.world.getLight(player.getPosition());
                    if (light <= 4) {
                        event.setAmount(event.getAmount() * 1.20F);
                        if (player.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) player.world;
                            ws.spawnParticle(EnumParticleTypes.PORTAL, target.posX, target.posY + target.height * 0.5D, target.posZ, 6, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Stagger: Melee hits pause basic mob attack windup by 0.25s (5 ticks). Bosses and champions immune.
                int staggerLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.STAGGER, held);
                if (staggerLvl > 0 && !ItemCheckHelper.isBossEntity(target)) {
                    boolean isChamp = target.getEntityData().hasKey("champions:championship");
                    if (!isChamp) {
                        target.getEntityData().setLong("DE_StaggerUntil", target.world.getTotalWorldTime() + 5L);
                        target.motionX *= 0.6D;
                        target.motionZ *= 0.6D;
                        if (target.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) target.world;
                            ws.spawnParticle(EnumParticleTypes.CRIT, target.posX, target.posY + target.height * 0.6D, target.posZ, 5, 0.2D, 0.2D, 0.2D, 0.05D);
                        }
                    }
                }

                // Puncture: Bypasses a flat 2 points of natural mob armor
                int punctureLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.PUNCTURE, held);
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

                        int reducedArmor = Math.max(0, naturalArmor - 2);
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

                // Low Sweep: Sweeping strikes prioritize ankle-height hitboxes, applying 0.5s stagger to short/crawling mobs, sweep dmg -30%
                int lowSweepLvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.LOW_SWEEP, held);
                if (lowSweepLvl > 0 && ItemCheckHelper.isPolearmOrHalberd(held)) {
                    event.setAmount(event.getAmount() * 0.70F); // Sweep damage reduced by 30%

                    boolean isTargetShort = target.height <= 0.95F || target instanceof net.minecraft.entity.monster.EntitySilverfish
                            || target instanceof net.minecraft.entity.monster.EntitySpider
                            || target.getClass().getName().toLowerCase().contains("hound")
                            || target.getClass().getName().toLowerCase().contains("crawler")
                            || target.getClass().getName().toLowerCase().contains("chupacabra");

                    if (isTargetShort && !ItemCheckHelper.isBossEntity(target)) {
                        target.getEntityData().setLong("DE_StaggerUntil", target.world.getTotalWorldTime() + 10L); // 0.5s stagger
                        target.motionX *= 0.3D;
                        target.motionZ *= 0.3D;
                    }

                    // Hit surrounding short/ground mobs around target to clear tangled feet
                    double sweepRadius = 2.0D + (lowSweepLvl * 0.5D);
                    java.util.List<EntityLivingBase> sweepList = target.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(sweepRadius, 0.5D, sweepRadius));
                    player.getEntityData().setBoolean("DE_Sweeping", true);
                    try {
                        for (EntityLivingBase other : sweepList) {
                            if (other != target && other != player && !player.isOnSameTeam(other) && other.isEntityAlive()) {
                                boolean otherShort = other.height <= 0.95F || other instanceof net.minecraft.entity.monster.EntitySilverfish
                                        || other instanceof net.minecraft.entity.monster.EntitySpider
                                        || other.getClass().getName().toLowerCase().contains("hound")
                                        || other.getClass().getName().toLowerCase().contains("crawler")
                                        || other.getClass().getName().toLowerCase().contains("chupacabra");
                                if (otherShort && !ItemCheckHelper.isBossEntity(other)) {
                                    other.getEntityData().setLong("DE_StaggerUntil", other.world.getTotalWorldTime() + 10L);
                                    double dx = other.posX - player.posX;
                                    double dz = other.posZ - player.posZ;
                                    double dLen = Math.sqrt(dx * dx + dz * dz);
                                    if (dLen > 0.001D) {
                                        other.knockBack(player, 0.35F * lowSweepLvl, -dx / dLen, -dz / dLen);
                                    }
                                    other.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), event.getAmount() * 0.5F);
                                }
                            }
                        }
                    } finally {
                        player.getEntityData().removeTag("DE_Sweeping");
                    }

                    if (player.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) player.world;
                        ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, target.posX, target.posY + 0.15D, target.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        int dirtId = net.minecraft.block.Block.getStateId(net.minecraft.init.Blocks.DIRT.getDefaultState());
                        ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, target.posX, target.posY + 0.05D, target.posZ, 8, 0.4D, 0.05D, 0.4D, 0.02D, dirtId);
                    }
                }

                // Enlargement: 2 * level% chance to inflate the target by 10 * level% for 20 seconds.
                int enlargementLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ENLARGEMENT, held);
                if (enlargementLevel > 0 && target.isEntityAlive()) {
                    if (player.world.rand.nextFloat() < 0.02F * enlargementLevel) {
                        target.addPotionEffect(new PotionEffect(ModPotions.ENLARGEMENT, 400, enlargementLevel - 1, false, true));
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
                    int allyLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ALLY, held);
                    if (allyLevel > 0) {
                        if (player.world.rand.nextFloat() < 0.02F) {
                            LycanitesIntegration.spawnAegis(player, target);
                        }
                    }
                }
            }
        }

        // 2. Ally defense proc: If a player is attacked and has Ally on an equipped item
        if (event.getEntityLiving() instanceof EntityPlayer && ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs")) {
            EntityPlayer victim = (EntityPlayer) event.getEntityLiving();
            int totalAllyLevel = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.ALLY, victim);
            if (totalAllyLevel > 0 && victim.world.rand.nextFloat() < 0.02F) {
                EntityLivingBase attacker = null;
                if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                    attacker = (EntityLivingBase) event.getSource().getTrueSource();
                }
                LycanitesIntegration.spawnAegis(victim, attacker);
            }
        }
    }
}
