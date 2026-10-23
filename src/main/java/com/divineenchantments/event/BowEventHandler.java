package com.divineenchantments.event;

import com.divineenchantments.compat.LycanitesIntegration;
import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.bullseye.BullseyeManager;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.slipbore.SlipBoreManager;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class BowEventHandler {

    public static final UUID FEATHERED_DRAW_SPEED_UUID = UUID.fromString("c7821649-74e2-4ea5-a249-183027b140fa");
    private static final Map<UUID, BowData> PENDING_BOW_DATA = new ConcurrentHashMap<>();
    private static final java.util.Set<EntityArrow> GLIMMER_ARROWS = java.util.Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static class BowData {
        final int gravitational;
        final int pierceful;
        final int ally;
        final int autofire;
        final int headhunter;
        final int slipBore;
        final int muzzle;
        final int archery;
        final int ballisticOverdrive;
        final int splinteringImpact;
        final int longdraw;
        final float longdrawCharge;
        final int glimmer;
        final int scavengersHabit;
        final int bullseye;
        final int hunter;
        final float drawFraction;

        BowData(int gravitational, int pierceful, int ally, int autofire, int headhunter, int slipBore, int muzzle, int archery,
                int ballisticOverdrive, int splinteringImpact, int longdraw, float longdrawCharge, int glimmer, int scavengersHabit,
                int bullseye, int hunter, float drawFraction) {
            this.gravitational = gravitational;
            this.pierceful = pierceful;
            this.ally = ally;
            this.autofire = autofire;
            this.headhunter = headhunter;
            this.slipBore = slipBore;
            this.muzzle = muzzle;
            this.archery = archery;
            this.ballisticOverdrive = ballisticOverdrive;
            this.splinteringImpact = splinteringImpact;
            this.longdraw = longdraw;
            this.longdrawCharge = longdrawCharge;
            this.glimmer = glimmer;
            this.scavengersHabit = scavengersHabit;
            this.bullseye = bullseye;
            this.hunter = hunter;
            this.drawFraction = drawFraction;
        }
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }

        PENDING_BOW_DATA.remove(player.getUniqueID());

        ItemStack bow = event.getBow();
        if (bow.isEmpty()) {
            return;
        }

        int grav = ModEnchantments.getLevel(ModEnchantments.GRAVITATIONAL, bow);
        int pierce = ModEnchantments.getLevel(ModEnchantments.PIERCEFUL, bow);
        int ally = (ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs"))
                ? ModEnchantments.getLevel(ModEnchantments.ALLY, bow)
                : 0;
        int autofire = ModEnchantments.getLevel(ModEnchantments.AUTOFIRE, bow);
        int headhunter = ModEnchantments.getLevel(ModEnchantments.HEADHUNTER, bow);
        int slipBore = ModEnchantments.getLevel(ModEnchantments.SLIP_BORE, bow);
        int muzzle = ModEnchantments.getLevel(ModEnchantments.MUZZLE, bow);
        int archery = ModEnchantments.getLevel(ModEnchantments.ARCHERY, bow);
        int ballistic = ModEnchantments.getLevel(ModEnchantments.BALLISTIC_OVERDRIVE, bow);
        int splintering = ModEnchantments.getLevel(ModEnchantments.SPLINTERING_IMPACT, bow);
        int longdraw = ModEnchantments.getLevel(ModEnchantments.LONGDRAW, bow);
        int glimmer = ModEnchantments.getLevel(ModEnchantments.GLIMMER, bow);
        int scav = ModEnchantments.getLevel(ModEnchantments.SCAVENGERS_HABIT, bow);
        int bullseye = ModEnchantments.getLevel(ModEnchantments.BULLSEYE, bow);
        int hunter = ModEnchantments.getLevel(ModEnchantments.HUNTER, bow);
        float drawFraction = MathHelper.clamp((float) event.getCharge() / 20.0F, 0.0F, 1.0F);

        float longdrawCharge = 1.0F;
        if (longdraw > 0) {
            int charge = event.getCharge();
            // Longdraw overdrawing up to 250% (50 ticks)
            if (charge >= 30) {
                longdrawCharge = Math.min(2.5F, (float) charge / 20.0F);

                // Recoil: flings the player backward 6 blocks
                Vec3d look = player.getLookVec();
                player.motionX -= look.x * 1.55D;
                player.motionY = Math.max(player.motionY + 0.38D, 0.42D);
                player.motionZ -= look.z * 1.55D;
                player.velocityChanged = true;
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                }

                // Concussive muzzle blast: damage and knock back frontal mobs
                double blastX = player.posX + look.x * 2.0D;
                double blastY = player.posY + player.getEyeHeight();
                double blastZ = player.posZ + look.z * 2.0D;

                if (player.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) player.world;
                    ws.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, blastX, blastY, blastZ, 2, 0.2D, 0.2D, 0.2D, 0.05D);
                    ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, blastX, blastY, blastZ, 12, 0.4D, 0.4D, 0.4D, 0.05D);
                }
                player.world.playSound(null, blastX, blastY, blastZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.2F, 1.4F);

                AxisAlignedBB blastBox = new AxisAlignedBB(blastX - 2.5D, blastY - 2.0D, blastZ - 2.5D, blastX + 2.5D, blastY + 2.0D, blastZ + 2.5D);
                List<EntityLivingBase> blastTargets = player.world.getEntitiesWithinAABB(EntityLivingBase.class, blastBox,
                        e -> e != null && e.isEntityAlive() && e != player);
                for (EntityLivingBase target : blastTargets) {
                    target.attackEntityFrom(DamageSource.causePlayerDamage(player), 6.0F);
                    double tdx = target.posX - player.posX;
                    double tdz = target.posZ - player.posZ;
                    double tdist = Math.sqrt(tdx * tdx + tdz * tdz);
                    if (tdist > 0.01D) {
                        target.motionX += (tdx / tdist) * 0.9D;
                        target.motionY += 0.25D;
                        target.motionZ += (tdz / tdist) * 0.9D;
                        target.velocityChanged = true;
                    }
                }
            }
        }

        if (grav > 0 || pierce > 0 || ally > 0 || autofire > 0 || headhunter > 0 || slipBore > 0 || muzzle > 0 || archery > 0
                || ballistic > 0 || splintering > 0 || longdraw > 0 || glimmer > 0 || scav > 0 || bullseye > 0 || hunter > 0) {
            PENDING_BOW_DATA.put(player.getUniqueID(), new BowData(grav, pierce, ally, autofire, headhunter, slipBore, muzzle, archery,
                    ballistic, splintering, longdraw, longdrawCharge, glimmer, scav, bullseye, hunter, drawFraction));
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityArrow)) {
            return;
        }

        EntityArrow arrow = (EntityArrow) event.getEntity();
        if (arrow.shootingEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
            BowData data = PENDING_BOW_DATA.get(player.getUniqueID());

            // If not found in pending map, fallback to held items
            if (data == null) {
                ItemStack held = player.getHeldItemMainhand();
                if (!ItemCheckHelper.isBowOrCrossbow(held)) {
                    held = player.getHeldItemOffhand();
                }
                if (!held.isEmpty() && ItemCheckHelper.isBowOrCrossbow(held)) {
                    int grav = ModEnchantments.getLevel(ModEnchantments.GRAVITATIONAL, held);
                    int pierce = ModEnchantments.getLevel(ModEnchantments.PIERCEFUL, held);
                    int ally = (ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs"))
                            ? ModEnchantments.getLevel(ModEnchantments.ALLY, held)
                            : 0;
                    int autofire = ModEnchantments.getLevel(ModEnchantments.AUTOFIRE, held);
                    int headhunter = ModEnchantments.getLevel(ModEnchantments.HEADHUNTER, held);
                    int slipBore = ModEnchantments.getLevel(ModEnchantments.SLIP_BORE, held);
                    int muzzle = ModEnchantments.getLevel(ModEnchantments.MUZZLE, held);
                    int archery = ModEnchantments.getLevel(ModEnchantments.ARCHERY, held);
                    int ballistic = ModEnchantments.getLevel(ModEnchantments.BALLISTIC_OVERDRIVE, held);
                    int splintering = ModEnchantments.getLevel(ModEnchantments.SPLINTERING_IMPACT, held);
                    int longdraw = ModEnchantments.getLevel(ModEnchantments.LONGDRAW, held);
                    int glimmer = ModEnchantments.getLevel(ModEnchantments.GLIMMER, held);
                    int scav = ModEnchantments.getLevel(ModEnchantments.SCAVENGERS_HABIT, held);
                    int bullseye = ModEnchantments.getLevel(ModEnchantments.BULLSEYE, held);
                    int hunter = ModEnchantments.getLevel(ModEnchantments.HUNTER, held);
                    data = new BowData(grav, pierce, ally, autofire, headhunter, slipBore, muzzle, archery,
                            ballistic, splintering, longdraw, 1.0F, glimmer, scav, bullseye, hunter, 1.0F);
                }
            }

            if (data != null) {
                if (data.gravitational > 0) {
                    arrow.getEntityData().setInteger("Divine_Gravitational", data.gravitational);
                }
                if (data.pierceful > 0) {
                    arrow.getEntityData().setInteger("Divine_Pierceful", data.pierceful);
                }
                if (data.ally > 0) {
                    arrow.getEntityData().setInteger("Divine_Ally", data.ally);
                }
                if (data.headhunter > 0) {
                    arrow.getEntityData().setInteger("Divine_Headhunter", data.headhunter);
                }
                if (data.slipBore > 0) {
                    arrow.getEntityData().setBoolean("Divine_SlipBore", true);
                    SlipBoreManager.trackArrow(arrow);
                }
                if (data.muzzle > 0) {
                    arrow.getEntityData().setInteger("Divine_Muzzle", data.muzzle);
                }
                if (data.archery > 0) {
                    arrow.getEntityData().setInteger("Divine_Archery", data.archery);
                }
                if (data.ballisticOverdrive > 0) {
                    arrow.getEntityData().setBoolean("Divine_BallisticOverdrive", true);
                    arrow.setNoGravity(true);
                }
                if (data.splinteringImpact > 0) {
                    arrow.getEntityData().setInteger("Divine_SplinteringImpact", data.splinteringImpact);
                }
                if (data.longdraw > 0 && data.longdrawCharge > 1.0F) {
                    arrow.getEntityData().setInteger("Divine_LongdrawPierce", 5);
                    arrow.setDamage(arrow.getDamage() * data.longdrawCharge);
                    arrow.motionX *= 1.35D;
                    arrow.motionY *= 1.35D;
                    arrow.motionZ *= 1.35D;
                }
                if (data.glimmer > 0) {
                    arrow.getEntityData().setBoolean("Divine_Glimmer", true);
                    GLIMMER_ARROWS.add(arrow);
                }
                if (data.scavengersHabit > 0) {
                    arrow.getEntityData().setInteger("Divine_ScavengersHabit", data.scavengersHabit);
                }
                if (data.bullseye > 0) {
                    arrow.getEntityData().setInteger("Divine_Bullseye", data.bullseye);
                }
                if (data.hunter > 0) {
                    arrow.getEntityData().setInteger("Divine_Hunter", data.hunter);
                    arrow.getEntityData().setFloat("Divine_HunterDraw", data.drawFraction);
                }

                // Autofire logic: Spawns 1 (level I) or 2 (level II) trailing arrows right behind this arrow.
                // Works for original arrow and any multishot/splitshot arrows that join world.
                if (data.autofire > 0 && !arrow.getEntityData().getBoolean("Divine_IsAutofireCopy")) {
                    arrow.getEntityData().setBoolean("Divine_Autofire", true);

                    double speed = MathHelper.sqrt(arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ);
                    double dirX = speed > 0.001D ? arrow.motionX / speed : 0.0D;
                    double dirY = speed > 0.001D ? arrow.motionY / speed : 0.0D;
                    double dirZ = speed > 0.001D ? arrow.motionZ / speed : 0.0D;

                    for (int i = 1; i <= data.autofire; i++) {
                        EntityArrow trailing;
                        try {
                            trailing = arrow.getClass().getConstructor(World.class).newInstance(event.getWorld());
                        } catch (Throwable t) {
                            trailing = new EntityTippedArrow(event.getWorld());
                        }

                        trailing.shootingEntity = arrow.shootingEntity;
                        double offset = 0.45D * i; // Right behind the arrow
                        trailing.setPosition(arrow.posX - dirX * offset, arrow.posY - dirY * offset, arrow.posZ - dirZ * offset);
                        trailing.motionX = arrow.motionX;
                        trailing.motionY = arrow.motionY;
                        trailing.motionZ = arrow.motionZ;
                        trailing.rotationYaw = arrow.rotationYaw;
                        trailing.rotationPitch = arrow.rotationPitch;
                        trailing.prevRotationYaw = arrow.prevRotationYaw;
                        trailing.prevRotationPitch = arrow.prevRotationPitch;
                        trailing.setIsCritical(arrow.getIsCritical());
                        trailing.setDamage(arrow.getDamage());
                        if (arrow.isBurning()) {
                            trailing.setFire(100);
                        }
                        trailing.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                        // Mark as copy to prevent recursion
                        trailing.getEntityData().setBoolean("Divine_IsAutofireCopy", true);
                        trailing.getEntityData().setBoolean("Divine_Autofire", true);

                        // Copy other divine enchantment tags
                        if (arrow.getEntityData().hasKey("Divine_Gravitational")) {
                            trailing.getEntityData().setInteger("Divine_Gravitational", arrow.getEntityData().getInteger("Divine_Gravitational"));
                        }
                        if (arrow.getEntityData().hasKey("Divine_Pierceful")) {
                            trailing.getEntityData().setInteger("Divine_Pierceful", arrow.getEntityData().getInteger("Divine_Pierceful"));
                        }
                        if (arrow.getEntityData().hasKey("Divine_Ally")) {
                            trailing.getEntityData().setInteger("Divine_Ally", arrow.getEntityData().getInteger("Divine_Ally"));
                        }
                        if (arrow.getEntityData().hasKey("Divine_Headhunter")) {
                            trailing.getEntityData().setInteger("Divine_Headhunter", arrow.getEntityData().getInteger("Divine_Headhunter"));
                        }
                        if (arrow.getEntityData().getBoolean("Divine_SlipBore")) {
                            trailing.getEntityData().setBoolean("Divine_SlipBore", true);
                            SlipBoreManager.trackArrow(trailing);
                        }
                        if (arrow.getEntityData().hasKey("Divine_Muzzle")) {
                            trailing.getEntityData().setInteger("Divine_Muzzle", arrow.getEntityData().getInteger("Divine_Muzzle"));
                        }
                        if (arrow.getEntityData().hasKey("Divine_Archery")) {
                            trailing.getEntityData().setInteger("Divine_Archery", arrow.getEntityData().getInteger("Divine_Archery"));
                        }
                        if (arrow.getEntityData().getBoolean("Divine_BallisticOverdrive")) {
                            trailing.getEntityData().setBoolean("Divine_BallisticOverdrive", true);
                            trailing.setNoGravity(true);
                        }
                        if (arrow.getEntityData().hasKey("Divine_SplinteringImpact")) {
                            trailing.getEntityData().setInteger("Divine_SplinteringImpact", arrow.getEntityData().getInteger("Divine_SplinteringImpact"));
                        }
                        if (arrow.getEntityData().getBoolean("Divine_Glimmer")) {
                            trailing.getEntityData().setBoolean("Divine_Glimmer", true);
                            GLIMMER_ARROWS.add(trailing);
                        }
                        if (arrow.getEntityData().hasKey("Divine_Bullseye")) {
                            trailing.getEntityData().setInteger("Divine_Bullseye", arrow.getEntityData().getInteger("Divine_Bullseye"));
                        }
                        if (arrow.getEntityData().hasKey("Divine_Hunter")) {
                            trailing.getEntityData().setInteger("Divine_Hunter", arrow.getEntityData().getInteger("Divine_Hunter"));
                            trailing.getEntityData().setFloat("Divine_HunterDraw", arrow.getEntityData().getFloat("Divine_HunterDraw"));
                        }

                        event.getWorld().spawnEntity(trailing);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent.Arrow event) {
        EntityArrow arrow = event.getArrow();
        if (arrow == null || arrow.world.isRemote) {
            return;
        }

        RayTraceResult ray = event.getRayTraceResult();
        if (ray == null) {
            return;
        }

        // 1. Entity collision
        if (ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null) {
            Entity hit = ray.entityHit;

            // Never hit shooter
            if (hit == arrow.shootingEntity) {
                return;
            }

            if (hit instanceof EntityLivingBase) {
                EntityLivingBase livingHit = (EntityLivingBase) hit;

                // Bullseye: reward hitting the small moving bullseye, then place a fresh one.
                int bullseyeLevel = arrow.getEntityData().getInteger("Divine_Bullseye");
                if (bullseyeLevel > 0) {
                    Vec3d hitPos = ray.hitVec != null ? ray.hitVec : new Vec3d(arrow.posX, arrow.posY, arrow.posZ);
                    Vec3d rayStart = new Vec3d(arrow.prevPosX, arrow.prevPosY, arrow.prevPosZ);
                    arrow.getEntityData().setBoolean("Divine_BullseyeHit", false);
                    if (BullseyeManager.matches(livingHit, rayStart, hitPos)) {
                        arrow.getEntityData().setBoolean("Divine_BullseyeHit", true);
                    }
                    BullseyeManager.place(livingHit);
                }

                // Hunter: glowing duration scales with bow draw strength at release.
                int hunterLevel = arrow.getEntityData().getInteger("Divine_Hunter");
                if (hunterLevel > 0) {
                    float draw = MathHelper.clamp(arrow.getEntityData().getFloat("Divine_HunterDraw"), 0.0F, 1.0F);
                    int duration = (int) (DivineConfig.hunterDurationPerLevelTicks * hunterLevel * draw);
                    if (duration > 0) {
                        livingHit.addPotionEffect(new PotionEffect(MobEffects.GLOWING, duration, 0, false, false));
                    }
                }
            }

            // Headhunter detection: check if hit was on the head / topmost area (height ratio >= 0.72)
            if (arrow.getEntityData().hasKey("Divine_Headhunter") && hit instanceof EntityLivingBase) {
                EntityLivingBase livingTarget = (EntityLivingBase) hit;
                double hitY = ray.hitVec != null ? ray.hitVec.y : arrow.posY;
                double relativeY = hitY - livingTarget.posY;
                double heightRatio = livingTarget.height > 0 ? (relativeY / livingTarget.height) : 1.0D;
                if (heightRatio >= 0.72D) {
                    arrow.getEntityData().setBoolean("Divine_Headshot", true);
                }
            }

            // Autofire: reset hurt resistance so subsequent trailing arrows will deal full damage
            if (arrow.getEntityData().getBoolean("Divine_Autofire") && hit instanceof EntityLivingBase) {
                ((EntityLivingBase) hit).hurtResistantTime = 0;
            }

            // Muzzle: inflicts aphagia for 10s (200 ticks) preventing item use and eating
            if (arrow.getEntityData().hasKey("Divine_Muzzle") && hit instanceof EntityLivingBase) {
                EntityLivingBase livingTarget = (EntityLivingBase) hit;
                Potion aphagia = LycanitesIntegration.getAphagiaPotion();
                if (aphagia != null) {
                    livingTarget.addPotionEffect(new PotionEffect(aphagia, 200, 0));
                } else {
                    livingTarget.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 2));
                    livingTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1));
                }
                if (arrow.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) arrow.world;
                    ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, livingTarget.posX, livingTarget.posY + livingTarget.height * 0.7D, livingTarget.posZ, 8, 0.2D, 0.2D, 0.2D, 0.02D);
                }
            }

            // Glimmer: marks target with glowing for 5s (100 ticks)
            if (arrow.getEntityData().getBoolean("Divine_Glimmer") && hit instanceof EntityLivingBase) {
                ((EntityLivingBase) hit).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 100, 0, false, false));
            }

            // Ballistic Overdrive: travels in laser-straight line with infinite punch-through on non-boss entities
            if (arrow.getEntityData().getBoolean("Divine_BallisticOverdrive")) {
                if (!ItemCheckHelper.isBossEntity(hit)) {
                    int[] piercedList = arrow.getEntityData().getIntArray("Divine_BallisticPiercedList");
                    if (!arrayContains(piercedList, hit.getEntityId())) {
                        arrow.getEntityData().setIntArray("Divine_BallisticPiercedList", appendInt(piercedList, hit.getEntityId()));
                        applyArrowDamage(arrow, hit);
                        checkAllyProc(arrow, hit);
                        if (arrow.world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) arrow.world;
                            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, arrow.posX, arrow.posY, arrow.posZ, 6, 0.1D, 0.1D, 0.1D, 0.05D);
                        }
                    }
                    event.setCanceled(true);
                    return;
                }
            }

            // Longdraw Pierce
            if (arrow.getEntityData().hasKey("Divine_LongdrawPierce")) {
                int maxPierce = arrow.getEntityData().getInteger("Divine_LongdrawPierce");
                int count = arrow.getEntityData().getInteger("Divine_LongdrawPiercedCount");
                int[] piercedList = arrow.getEntityData().getIntArray("Divine_LongdrawPiercedList");
                if (arrayContains(piercedList, hit.getEntityId())) {
                    event.setCanceled(true);
                    return;
                }
                if (count < maxPierce) {
                    arrow.getEntityData().setIntArray("Divine_LongdrawPiercedList", appendInt(piercedList, hit.getEntityId()));
                    arrow.getEntityData().setInteger("Divine_LongdrawPiercedCount", count + 1);
                    applyArrowDamage(arrow, hit);
                    checkAllyProc(arrow, hit);
                    if (arrow.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) arrow.world;
                        ws.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, arrow.posX, arrow.posY, arrow.posZ, 3, 0.1D, 0.1D, 0.1D, 0.02D);
                    }
                    event.setCanceled(true);
                    return;
                }
            }

            // Pierceful handling
            if (arrow.getEntityData().hasKey("Divine_Pierceful")) {
                int maxPierce = arrow.getEntityData().getInteger("Divine_Pierceful");
                int piercedCount = arrow.getEntityData().getInteger("Divine_PiercedCount");
                int[] piercedList = arrow.getEntityData().getIntArray("Divine_PiercedList");

                if (arrayContains(piercedList, hit.getEntityId())) {
                    // Arrow already pierced this entity in a previous tick, pass through smoothly
                    event.setCanceled(true);
                    return;
                }

                if (piercedCount < maxPierce) {
                    // PIERCE THIS ENTITY: damage, mark pierced, cancel event to pass through!
                    arrow.getEntityData().setIntArray("Divine_PiercedList", appendInt(piercedList, hit.getEntityId()));
                    arrow.getEntityData().setInteger("Divine_PiercedCount", piercedCount + 1);

                    applyArrowDamage(arrow, hit);
                    checkAllyProc(arrow, hit);

                    if (arrow.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) arrow.world;
                        ws.spawnParticle(
                                EnumParticleTypes.SWEEP_ATTACK,
                                arrow.posX,
                                arrow.posY,
                                arrow.posZ,
                                1, 0.0D, 0.0D, 0.0D, 0.0D
                        );
                    }

                    // Cancel event so the arrow doesn't stop and flies through the entity
                    event.setCanceled(true);
                    return;
                }

                // If piercedCount >= maxPierce, this is the final landing hit!
                arrow.getEntityData().setBoolean("Divine_InGround", true);
                checkAllyProc(arrow, hit);

                if (arrow.getEntityData().hasKey("Divine_Gravitational")) {
                    Vec3d hitPos = ray.hitVec != null ? ray.hitVec : new Vec3d(arrow.posX, arrow.posY, arrow.posZ);
                    triggerGravitational(arrow, hitPos);
                }
                return;
            } else {
                // No Pierceful, but hit an entity
                arrow.getEntityData().setBoolean("Divine_InGround", true);
                checkAllyProc(arrow, hit);
                if (arrow.getEntityData().hasKey("Divine_Gravitational")) {
                    Vec3d hitPos = ray.hitVec != null ? ray.hitVec : new Vec3d(arrow.posX, arrow.posY, arrow.posZ);
                    triggerGravitational(arrow, hitPos);
                }
                return;
            }
        }

        // 2. Arrow lands on a block
        if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            arrow.getEntityData().setBoolean("Divine_InGround", true);
            Vec3d hitPos = ray.hitVec != null ? ray.hitVec : new Vec3d(arrow.posX, arrow.posY, arrow.posZ);
            if (arrow.getEntityData().hasKey("Divine_Gravitational")) {
                triggerGravitational(arrow, hitPos);
            }
            if (arrow.getEntityData().hasKey("Divine_SplinteringImpact")) {
                int lvl = arrow.getEntityData().getInteger("Divine_SplinteringImpact");
                triggerSplinteringImpact(arrow, hitPos, lvl);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getImmediateSource() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getSource().getImmediateSource();

            // Bullseye: +20% damage per level when the arrow strikes the marked spot.
            if (arrow.getEntityData().getBoolean("Divine_BullseyeHit")) {
                int bullseyeLevel = arrow.getEntityData().getInteger("Divine_Bullseye");
                if (bullseyeLevel > 0) {
                    event.setAmount(event.getAmount() * (1.0F + (float) DivineConfig.bullseyeDamagePerLevel * bullseyeLevel));
                    EntityLivingBase target = event.getEntityLiving();
                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                                target.posX, target.posY + target.height * 0.55D, target.posZ,
                                18, 0.25D, 0.25D, 0.25D, 0.08D);
                    }
                    target.world.playSound(null, target.posX, target.posY + target.height * 0.5D, target.posZ,
                            SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1.2F, 1.45F);
                    target.world.playSound(null, target.posX, target.posY + target.height * 0.5D, target.posZ,
                            SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 1.1F, 1.9F);
                }
            }

            // Headhunter 1.5x damage on headshot
            if (arrow.getEntityData().getBoolean("Divine_Headshot")) {
                event.setAmount(event.getAmount() * 1.5F);

                EntityLivingBase target = event.getEntityLiving();
                if (target.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) target.world;
                    ws.spawnParticle(
                            EnumParticleTypes.CRIT_MAGIC,
                            target.posX,
                            target.posY + target.height,
                            target.posZ,
                            25,
                            0.25D,
                            0.25D,
                            0.25D,
                            0.1D
                    );
                }
                target.world.playSound(null, target.posX, target.posY + target.height, target.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.2F, 1.8F);
            }

            // Autofire: ensure hurt resistance is 0 so trailing arrows aren't blocked
            if (arrow.getEntityData().getBoolean("Divine_Autofire")) {
                event.getEntityLiving().hurtResistantTime = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getSource().getImmediateSource() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getSource().getImmediateSource();
            if (arrow.getEntityData().getBoolean("Divine_Autofire")) {
                // Keep invulnerability frames at 0 for upcoming trailing arrows
                event.getEntityLiving().hurtResistantTime = 0;
            }
        }
    }

    private static void applyArrowDamage(EntityArrow arrow, Entity hit) {
        float speed = MathHelper.sqrt(arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ);
        int damage = MathHelper.ceil((double) speed * arrow.getDamage());
        if (arrow.getIsCritical()) {
            damage += arrow.world.rand.nextInt(damage / 2 + 2);
        }

        // Apply Headhunter bonus for pierced hits if applicable
        if (arrow.getEntityData().getBoolean("Divine_Headshot")) {
            damage = (int) (damage * 1.5F);
        }

        DamageSource damagesource;
        if (arrow.shootingEntity == null) {
            damagesource = DamageSource.causeArrowDamage(arrow, arrow);
        } else {
            damagesource = DamageSource.causeArrowDamage(arrow, arrow.shootingEntity);
        }

        if (arrow.isBurning() && !(hit instanceof EntityEnderman)) {
            hit.setFire(5);
        }

        if (hit.attackEntityFrom(damagesource, (float) damage)) {
            if (hit instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) hit;
                if (!arrow.world.isRemote) {
                    living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
                }
                if (arrow.shootingEntity instanceof EntityLivingBase) {
                    EnchantmentHelper.applyThornEnchantments(living, arrow.shootingEntity);
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) arrow.shootingEntity, living);
                }
                if (arrow.getEntityData().getBoolean("Divine_Autofire")) {
                    living.hurtResistantTime = 0;
                }
            }
            arrow.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (arrow.world.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private static void triggerGravitational(EntityArrow arrow, Vec3d hitVec) {
        if (arrow.getEntityData().getBoolean("Divine_Gravitational_Triggered")) {
            return;
        }
        arrow.getEntityData().setBoolean("Divine_Gravitational_Triggered", true);

        double lx = hitVec.x;
        double ly = hitVec.y;
        double lz = hitVec.z;

        double radius = 5.0D;
        AxisAlignedBB box = new AxisAlignedBB(lx - radius, ly - radius, lz - radius, lx + radius, ly + radius, lz + radius);
        List<EntityLivingBase> entities = arrow.world.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase mob : entities) {
            if (mob == arrow.shootingEntity || !mob.isEntityAlive()) {
                continue;
            }
            double dist = mob.getDistance(lx, ly, lz);
            if (dist <= radius && dist > 0.05D) {
                double dx = lx - mob.posX;
                double dy = ly - mob.posY;
                double dz = lz - mob.posZ;

                double normX = dx / dist;
                double normY = dy / dist;
                double normZ = dz / dist;

                double pullDist = Math.min(dist, 2.0D);
                double factor = (pullDist / 2.0D) * 0.85D;

                mob.motionX += normX * factor;
                mob.motionY = Math.max(mob.motionY, 0.18D + Math.max(0.0D, normY) * factor * 0.3D);
                mob.motionZ += normZ * factor;
                mob.velocityChanged = true;

                if (mob instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) mob).connection.sendPacket(new SPacketEntityVelocity(mob));
                }
            }
        }

        if (arrow.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) arrow.world;
            ws.spawnParticle(EnumParticleTypes.PORTAL, lx, ly + 0.5D, lz, 45, 1.5D, 0.6D, 1.5D, 0.1D);
            ws.spawnParticle(EnumParticleTypes.DRAGON_BREATH, lx, ly + 0.2D, lz, 25, 0.6D, 0.2D, 0.6D, 0.02D);
        }
        arrow.world.playSound(null, lx, ly, lz, SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.PLAYERS, 1.0F, 0.7F);
    }

    private static void checkAllyProc(EntityArrow arrow, Entity hit) {
        if (arrow.getEntityData().hasKey("Divine_Ally") && ModEnchantments.ALLY != null && Loader.isModLoaded("lycanitesmobs")) {
            if (arrow.world.rand.nextFloat() < 0.02F && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer shooter = (EntityPlayer) arrow.shootingEntity;
                EntityLivingBase target = hit instanceof EntityLivingBase ? (EntityLivingBase) hit : null;
                LycanitesIntegration.spawnAegis(shooter, target);
            }
        }
    }

    private static boolean arrayContains(int[] array, int val) {
        if (array == null) return false;
        for (int i : array) {
            if (i == val) return true;
        }
        return false;
    }

    private static int[] appendInt(int[] array, int val) {
        if (array == null || array.length == 0) {
            return new int[]{val};
        }
        int[] result = new int[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = val;
        return result;
    }

    private static Potion TRUESHOT_POTION = null;
    private static boolean CHECKED_TRUESHOT = false;

    private static Potion getTrueshotPotion() {
        if (!CHECKED_TRUESHOT) {
            CHECKED_TRUESHOT = true;
            TRUESHOT_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "archery"));
        }
        return TRUESHOT_POTION;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        DamageSource source = event.getSource();
        Entity shooter = source.getTrueSource();
        if (!(shooter instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) shooter;

        int archeryLvl = 0;
        if (source.getImmediateSource() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) source.getImmediateSource();
            archeryLvl = arrow.getEntityData().getInteger("Divine_Archery");

            // Scavenger's Habit: 25% * level chance to refund arrow on kill (only for initial arrow)
            if (arrow.getEntityData().hasKey("Divine_ScavengersHabit") && !arrow.getEntityData().getBoolean("Divine_ScavengerUsed")) {
                arrow.getEntityData().setBoolean("Divine_ScavengerUsed", true);
                int scavLvl = arrow.getEntityData().getInteger("Divine_ScavengersHabit");
                float chance = 0.25F * scavLvl;
                if (player.world.rand.nextFloat() < chance) {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.ARROW));
                    player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.7F, 1.4F);
                    if (player.world instanceof WorldServer) {
                        ((WorldServer) player.world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, player.posX, player.posY + 1.0D, player.posZ, 8, 0.3D, 0.3D, 0.3D, 0.05D);
                    }
                }
            }
        }

        // If not found on arrow, check held bow/crossbow
        if (archeryLvl <= 0) {
            ItemStack held = player.getHeldItemMainhand();
            if (!ItemCheckHelper.isBowOrCrossbow(held)) {
                held = player.getHeldItemOffhand();
            }
            if (!held.isEmpty() && ItemCheckHelper.isBowOrCrossbow(held)) {
                archeryLvl = ModEnchantments.getLevel(ModEnchantments.ARCHERY, held);
            }
        }

        if (archeryLvl <= 0) {
            return;
        }

        triggerArcheryKillStreak(player, archeryLvl);
    }

    private static void triggerArcheryKillStreak(EntityPlayer player, int archeryLvl) {
        Potion trueshot = getTrueshotPotion();
        Potion activeCheckPotion = (trueshot != null) ? trueshot : MobEffects.STRENGTH;

        PotionEffect activeEffect = player.getActivePotionEffect(activeCheckPotion);
        int currentStreak = 0;
        if (activeEffect != null && activeEffect.getDuration() > 0) {
            currentStreak = player.getEntityData().getInteger("DE_ArcheryStreak");
        }
        currentStreak++;
        player.getEntityData().setInteger("DE_ArcheryStreak", currentStreak);

        // Archery lvl I: 1 kill -> Trueshot I for 10s (200 ticks)
        // Archery lvl II: 4 kills within 10s -> Trueshot II for 10s
        // Archery lvl III: 10 kills within 10s -> Trueshot III for 10s
        int amp = 0; // Trueshot I
        if (archeryLvl >= 3 && currentStreak >= 10) {
            amp = 2; // Trueshot III
        } else if (archeryLvl >= 2 && currentStreak >= 4) {
            amp = 1; // Trueshot II
        }

        if (trueshot != null) {
            player.addPotionEffect(new PotionEffect(trueshot, 200, amp));
        } else {
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, amp));
        }

        if (player.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) player.world;
            ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, player.posX, player.posY + 1.2D, player.posZ, 8, 0.3D, 0.3D, 0.3D, 0.05D);
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.8F, 1.4F + amp * 0.2F);
    }

    private static void triggerSplinteringImpact(EntityArrow arrow, Vec3d hitVec, int level) {
        if (arrow.getEntityData().getBoolean("Divine_SplinteringTriggered")) {
            return;
        }
        arrow.getEntityData().setBoolean("Divine_SplinteringTriggered", true);

        World world = arrow.world;
        int shardCount = level == 1 ? 4 : (level == 2 ? 9 : 13);

        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            ws.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, hitVec.x, hitVec.y + 0.2D, hitVec.z, 2, 0.2D, 0.2D, 0.2D, 0.05D);
            ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, hitVec.x, hitVec.y + 0.2D, hitVec.z, 15, 0.4D, 0.4D, 0.4D, 0.05D);
        }
        world.playSound(null, hitVec.x, hitVec.y, hitVec.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.2F, 1.6F);

        // Grenade-style fragmentation: a radial burst of independent shrapnel, no target seeking.
        double baseAngle = world.rand.nextDouble() * Math.PI * 2.0D;
        double angleStep = (Math.PI * 2.0D) / shardCount;

        for (int i = 0; i < shardCount; i++) {
            EntityTippedArrow shard = new EntityTippedArrow(world);
            shard.shootingEntity = arrow.shootingEntity;
            shard.setPosition(
                    hitVec.x + (world.rand.nextDouble() - 0.5D) * 0.15D,
                    hitVec.y + 0.25D + world.rand.nextDouble() * 0.2D,
                    hitVec.z + (world.rand.nextDouble() - 0.5D) * 0.15D);
            shard.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            shard.setDamage(3.0D + level * 0.5D);
            shard.setIsCritical(true);

            double angle = baseAngle + angleStep * i + (world.rand.nextDouble() - 0.5D) * 0.45D;
            double pitch = -0.15D + world.rand.nextDouble() * 0.95D;
            double horizontal = Math.cos(pitch);

            double dirX = Math.cos(angle) * horizontal;
            double dirY = Math.sin(pitch);
            double dirZ = Math.sin(angle) * horizontal;

            double speed = 1.05D + world.rand.nextDouble() * 0.75D;
            shard.motionX = dirX * speed;
            shard.motionY = dirY * speed;
            shard.motionZ = dirZ * speed;

            float yaw = (float) (Math.atan2(shard.motionX, shard.motionZ) * (180.0D / Math.PI));
            float shardPitch = (float) (Math.atan2(shard.motionY,
                    Math.sqrt(shard.motionX * shard.motionX + shard.motionZ * shard.motionZ)) * (180.0D / Math.PI));
            shard.rotationYaw = yaw;
            shard.rotationPitch = shardPitch;
            shard.prevRotationYaw = yaw;
            shard.prevRotationPitch = shardPitch;

            shard.getEntityData().setBoolean("Divine_IsShrapnelShard", true);
            world.spawnEntity(shard);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            GLIMMER_ARROWS.removeIf(arrow -> arrow != null && arrow.world == event.getWorld());
            PENDING_BOW_DATA.clear();
        }
        BullseyeManager.clearAll();
    }

    @SubscribeEvent
    public static void onServerTick(net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent event) {
        if (event.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END || GLIMMER_ARROWS.isEmpty()) {
            return;
        }
        java.util.Iterator<EntityArrow> it = GLIMMER_ARROWS.iterator();
        while (it.hasNext()) {
            EntityArrow arr = it.next();
            if (arr == null || arr.isDead || arr.world.isRemote || arr.onGround || arr.getEntityData().getBoolean("Divine_InGround")) {
                it.remove();
                continue;
            }
            if (arr.world instanceof WorldServer && arr.world.getTotalWorldTime() % 2 == 0) {
                WorldServer ws = (WorldServer) arr.world;
                ws.spawnParticle(EnumParticleTypes.END_ROD, arr.posX, arr.posY, arr.posZ, 1, 0.05D, 0.05D, 0.05D, 0.01D);
            }
        }
    }

    @SubscribeEvent
    public static void onBowUseTick(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack bow = event.getItem();
            if (!bow.isEmpty() && bow.getItem() instanceof ItemBow) {
                // Feathered Draw: reduce movement speed penalty while drawing bow from -80% down to -50%
                int feathered = ModEnchantments.getLevel(ModEnchantments.FEATHERED_DRAW, bow);
                if (feathered > 0) {
                    applySpeedModifier(player, FEATHERED_DRAW_SPEED_UUID, "Feathered Draw Speed", 1.5D);
                } else {
                    removeSpeedModifier(player, FEATHERED_DRAW_SPEED_UUID);
                }

                // Machine Bow: auto-fire at full charge with recoil and spread
                int machineLevel = ModEnchantments.getLevel(ModEnchantments.MACHINE_BOW, bow);
                if (machineLevel > 0) {
                    int maxDuration = bow.getMaxItemUseDuration();
                    int durationUsed = maxDuration - event.getDuration();
                    // Draw time: I: 20 ticks, II: 16 ticks, III: 13 ticks, IV: 10 ticks
                    int threshold = (machineLevel == 1) ? 20 : (machineLevel == 2 ? 16 : (machineLevel == 3 ? 13 : 10));
                    if (durationUsed >= threshold) {
                        if (!player.world.isRemote) {
                            player.stopActiveHand();
                            bow.onPlayerStoppedUsing(player.world, player, maxDuration - 20);

                            // Recoil push
                            Vec3d look = player.getLookVec();
                            double recoilPush = 0.20D - (machineLevel * 0.03D);
                            player.motionX -= look.x * recoilPush;
                            player.motionZ -= look.z * recoilPush;
                            player.velocityChanged = true;
                            if (player instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                            }
                        }

                        // Client/server pitch and yaw recoil
                        float pitchRecoil = 1.5F - (machineLevel * 0.3F);
                        player.rotationPitch -= pitchRecoil;
                        if (player.world.rand.nextFloat() < 0.3F) {
                            float yawRecoil = (player.world.rand.nextFloat() - 0.5F) * (1.2F - machineLevel * 0.2F);
                            player.rotationYaw += yawRecoil;
                        }

                        // Re-trigger active hand to keep firing continuously
                        player.setActiveHand(player.getActiveHand());
                        event.setDuration(maxDuration);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBowUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            removeSpeedModifier((EntityPlayer) event.getEntityLiving(), FEATHERED_DRAW_SPEED_UUID);
        }
    }

    @SubscribeEvent
    public static void onBowUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            removeSpeedModifier((EntityPlayer) event.getEntityLiving(), FEATHERED_DRAW_SPEED_UUID);
        }
    }

    private static void applySpeedModifier(EntityPlayer player, UUID uuid, String name, double amount) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (inst == null) return;
            AttributeModifier mod = inst.getModifier(uuid);
            if (mod == null || Math.abs(mod.getAmount() - amount) > 0.005D) {
                if (mod != null) {
                    inst.removeModifier(uuid);
                }
                AttributeModifier newMod = new AttributeModifier(uuid, name, amount, 2);
                newMod.setSaved(false);
                inst.applyModifier(newMod);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void removeSpeedModifier(EntityPlayer player, UUID uuid) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (inst != null && inst.getModifier(uuid) != null) {
                inst.removeModifier(uuid);
            }
        } catch (Throwable ignored) {
        }
    }
}
