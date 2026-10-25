package com.divineenchantments.event;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.compat.SimpleDifficultyCompat;
import com.divineenchantments.compat.TANCompat;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ChanceHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class CurseAndBuffEventHandler {

    public static final UUID INERTIA_SPEED_UUID = UUID.fromString("d5e4a001-49fa-4c40-8f6b-71a2e391cb21");
    public static final UUID UMBRAL_SPEED_UUID = UUID.fromString("b8372648-91c2-4742-b673-c82017384110");
    public static final UUID MUDSTRIDER_SPEED_UUID = UUID.fromString("6a8581e2-04e7-4934-8c83-09418e21bc71");

    private static final Field FIELD_IS_IN_WEB;
    private static final Field FIELD_FOOD_EXHAUSTION;

    static {
        Field f = null;
        try {
            f = ReflectionHelper.findField(Entity.class, "isInWeb", "field_70134_J");
            f.setAccessible(true);
        } catch (Throwable t) {
        }
        FIELD_IS_IN_WEB = f;

        Field fe = null;
        try {
            fe = ReflectionHelper.findField(net.minecraft.util.FoodStats.class, "foodExhaustionLevel", "field_75126_c");
            fe.setAccessible(true);
        } catch (Throwable t) {
        }
        FIELD_FOOD_EXHAUSTION = fe;
    }

    private static void resetFoodExhaustion(EntityPlayer player) {
        if (FIELD_FOOD_EXHAUSTION != null && player != null && player.getFoodStats() != null) {
            try {
                FIELD_FOOD_EXHAUSTION.setFloat(player.getFoodStats(), 0.0F);
            } catch (Throwable ignored) {
            }
        }
    }

    private static boolean getIsInWeb(Entity entity) {
        if (FIELD_IS_IN_WEB != null) {
            try {
                return FIELD_IS_IN_WEB.getBoolean(entity);
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private static void setIsInWeb(Entity entity, boolean val) {
        if (FIELD_IS_IN_WEB != null) {
            try {
                FIELD_IS_IN_WEB.setBoolean(entity, val);
            } catch (Throwable ignored) {
            }
        }
    }

    private static Potion CLIMB_POTION = null;
    private static boolean CHECKED_CLIMB = false;

    private static Potion getClimbPotion() {
        if (!CHECKED_CLIMB) {
            CHECKED_CLIMB = true;
            CLIMB_POTION = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "climb"));
        }
        return CLIMB_POTION;
    }

    /**
     * Slowsink: Gentle slow falling in water.
     * Arachnid Ascent: Grants climbing when wearing leggings.
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null) {
            return;
        }

        // Arachnid Ascent: Grants potioncore:climb while wearing leggings
        ItemStack legs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        int arachnidLevel = (!legs.isEmpty()) ? ModEnchantments.getLevel(ModEnchantments.ARACHNID_ASCENT, legs) : 0;
        if (arachnidLevel > 0) {
            Potion climb = getClimbPotion();
            if (climb != null) {
                entity.addPotionEffect(new PotionEffect(climb, 40, 0, true, false));
            } else {
                if (entity.collidedHorizontally) {
                    if (entity.isSneaking()) {
                        entity.motionY = 0.0D;
                    } else if (entity.moveForward > 0.0F && entity.motionY < 0.2D) {
                        entity.motionY = 0.2D;
                    }
                    entity.fallDistance = 0.0F;
                }
            }
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            // Umbral Prowess: Deep darkness (light level <= 4) grants Night Vision and +20% speed; light >= 12 inflicts Slowness
            ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            int umbralLevel = (!helmet.isEmpty()) ? ModEnchantments.getLevel(ModEnchantments.UMBRAL_PROWESS, helmet) : 0;
            if (umbralLevel > 0) {
                int light = player.world.getLight(player.getPosition());
                if (light <= 4) {
                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, false));
                    applySpeedModifier(player, UMBRAL_SPEED_UUID, "Umbral Prowess Speed", 0.20D);
                } else if (light >= 12) {
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 0, false, true));
                    removeSpeedModifier(player, UMBRAL_SPEED_UUID);
                } else {
                    removeSpeedModifier(player, UMBRAL_SPEED_UUID);
                }
            } else {
                removeSpeedModifier(player, UMBRAL_SPEED_UUID);
            }

            // Mudstrider: Negates movement speed penalty of soul sand, cobwebs, modded mud/silt, shallow water
            ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            int mudLevel = (!boots.isEmpty()) ? ModEnchantments.getLevel(ModEnchantments.MUDSTRIDER, boots) : 0;
            if (mudLevel > 0) {
                BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
                Block blockHere = player.world.getBlockState(pos).getBlock();
                Block blockDown = player.world.getBlockState(pos.down()).getBlock();
                boolean onSoulSand = blockHere == Blocks.SOUL_SAND || blockDown == Blocks.SOUL_SAND;
                boolean inWeb = getIsInWeb(player) || blockHere == Blocks.WEB;
                boolean inWater = player.isInWater();
                boolean onMud = (blockHere.getRegistryName() != null && blockHere.getRegistryName().getPath().contains("mud"))
                        || (blockDown.getRegistryName() != null && blockDown.getRegistryName().getPath().contains("mud"));

                if (inWeb) {
                    setIsInWeb(player, false);
                }

                if (onSoulSand || onMud || (inWater && !player.isSneaking())) {
                    double boost = mudLevel == 1 ? 0.06D : (mudLevel == 2 ? 0.12D : 0.18D);
                    applySpeedModifier(player, MUDSTRIDER_SPEED_UUID, "Mudstrider Speed", boost);
                } else {
                    removeSpeedModifier(player, MUDSTRIDER_SPEED_UUID);
                }
            } else {
                removeSpeedModifier(player, MUDSTRIDER_SPEED_UUID);
            }

            // Suregrip: Quality of life utility. Cannot dual wield items (offhand drops)
            ItemStack main = player.getHeldItemMainhand();
            if (!main.isEmpty() && ModEnchantments.getLevel(ModEnchantments.SUREGRIP, main) > 0) {
                ItemStack off = player.getHeldItemOffhand();
                if (!off.isEmpty()) {
                    player.dropItem(off.copy(), false, false);
                    player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
                    player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.8F, 0.5F);
                }
            }

            // Braced Pivot: cuts the base 100-tick shield disable cooldown by the configured percentage (60% by default)
            ItemStack shield = player.getHeldItemOffhand();
            if (!com.divineenchantments.util.ItemCheckHelper.isShield(shield)) {
                shield = player.getHeldItemMainhand();
            }
            if (!shield.isEmpty() && com.divineenchantments.util.ItemCheckHelper.isShield(shield)
                    && ModEnchantments.getLevel(ModEnchantments.BRACED_PIVOT, shield) > 0) {
                if (player.getCooldownTracker().hasCooldown(shield.getItem())) {
                    if (!player.getEntityData().getBoolean("DE_BracedPivotAdjusted")) {
                        player.getEntityData().setBoolean("DE_BracedPivotAdjusted", true);
                        int reducedCooldown = Math.max(1, (int) Math.round(100.0D * (1.0D - DivineConfig.bracedPivotCooldownReduction)));
                        player.getCooldownTracker().setCooldown(shield.getItem(), reducedCooldown);
                    }
                } else {
                    player.getEntityData().removeTag("DE_BracedPivotAdjusted");
                }
            }
        }

        if (!entity.isInWater() || entity.isSneaking()) {
            return;
        }

        int slowsinkLevel = ModEnchantments.getMaxLevel(ModEnchantments.SLOWSINK, entity);
        if (slowsinkLevel > 0 && entity.motionY < 0.0D) {
            double maxSink;
            if (slowsinkLevel == 1) {
                maxSink = -0.05D;
            } else if (slowsinkLevel == 2) {
                maxSink = -0.025D;
            } else {
                maxSink = -0.008D; // Level 3: feather-like slow floating
            }

            if (entity.motionY < maxSink) {
                entity.motionY = maxSink;
                entity.fallDistance = 0.0F;
            }
        }
    }

    /**
     * Apex and Curse of the Tether incoming damage calculation,
     * plus recording pre-damage armor durability for Tether and Royal Guard.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();

        // 1. Apex Damage Modifiers
        // Attacker wearing Apex helmet deals +2% damage per block above target
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            ItemStack attackerHelmet = attacker.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (!attackerHelmet.isEmpty() && ModEnchantments.getLevel(ModEnchantments.APEX, attackerHelmet) > 0) {
                double dy = attacker.posY - target.posY;
                if (dy > 0.0D) {
                    float bonus = (float) Math.min(DivineConfig.apexMaxBonus, dy * 0.02D);
                    event.setAmount(event.getAmount() * (1.0F + bonus));
                }
            }
        }

        // Victim wearing Apex helmet takes +2% damage per block below attacker
        ItemStack victimHelmet = target.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!victimHelmet.isEmpty() && ModEnchantments.getLevel(ModEnchantments.APEX, victimHelmet) > 0) {
            if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                double dy = attacker.posY - target.posY;
                if (dy > 0.0D) {
                    float penalty = (float) Math.min(DivineConfig.apexMaxBonus, dy * 0.02D);
                    event.setAmount(event.getAmount() * (1.0F + penalty));
                }
            }
        }

        // 2. Curse of the Tether (Chestplate): 10% less damage from all attacks
        ItemStack chest = target.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chest.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, chest) > 0) {
            event.setAmount(event.getAmount() * 0.90F);
        }

        // 3. Cushioned Heel: Dampens block-impact kinetic damage and minor falls (<= 4.5 blocks) by 30%
        if (target instanceof EntityPlayer) {
            ItemStack boots = target.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (!boots.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CUSHIONED_HEEL, boots) > 0) {
                DamageSource src = event.getSource();
                String type = src.getDamageType().toLowerCase();
                if (src == DamageSource.FLY_INTO_WALL || type.contains("kinetic") || type.contains("wall")
                        || type.contains("collision") || (src == DamageSource.FALL && target.fallDistance <= 4.5F)) {
                    event.setAmount(event.getAmount() * 0.70F);
                }
            }

            // 4. Cushioned Shirt: Dampens horizontal kinetic impact damage by 60% when colliding with terrain at high speed
            ItemStack chestplate = target.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chestplate.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CUSHIONED_SHIRT, chestplate) > 0) {
                DamageSource src = event.getSource();
                String type = src.getDamageType().toLowerCase();
                boolean isKineticCollision = src == DamageSource.FLY_INTO_WALL || type.contains("kinetic") || type.contains("wall") || type.contains("collision");
                double hSpeed = Math.sqrt(target.motionX * target.motionX + target.motionZ * target.motionZ);
                if (isKineticCollision || ((src == DamageSource.FALL || type.contains("fall")) && (hSpeed > 0.28D || ((EntityPlayer) target).isElytraFlying()))) {
                    event.setAmount(event.getAmount() * 0.40F);
                    if (target.world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) target.world;
                        ws.spawnParticle(EnumParticleTypes.CLOUD, target.posX, target.posY + 1.0D, target.posZ, 8, 0.3D, 0.3D, 0.3D, 0.05D);
                    }
                }
            }
        }

        // Record pre-damage durability of worn armor pieces and held items for Tether, Royal Guard, and Debt of the Void
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (!chest.isEmpty() && chest.isItemStackDamageable()) {
                player.getEntityData().setInteger("DE_PreChestDmg", chest.getItemDamage());
            }

            int[] preArmorDmg = new int[4];
            int idx = 0;
            for (ItemStack armor : player.getArmorInventoryList()) {
                if (idx < 4) {
                    preArmorDmg[idx++] = (!armor.isEmpty() && armor.isItemStackDamageable()) ? armor.getItemDamage() : -1;
                }
            }
            player.getEntityData().setIntArray("DE_PreArmorDmg", preArmorDmg);

            ItemStack main = player.getHeldItemMainhand();
            if (!main.isEmpty() && main.isItemStackDamageable()) {
                player.getEntityData().setInteger("DE_PreMainHandDmg", main.getItemDamage());
            }
            ItemStack off = player.getHeldItemOffhand();
            if (!off.isEmpty() && off.isItemStackDamageable()) {
                player.getEntityData().setInteger("DE_PreOffHandDmg", off.getItemDamage());
            }
        }
    }

    /**
     * Post-armor damage durability adjustments for Curse of the Tether and Royal Guard.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        // 1. Curse of the Tether: 20% less durability damage to chestplate
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chest.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, chest) > 0) {
            if (player.getEntityData().hasKey("DE_PreChestDmg")) {
                int preDmg = player.getEntityData().getInteger("DE_PreChestDmg");
                int currentDmg = chest.getItemDamage();
                int took = currentDmg - preDmg;
                if (took > 0) {
                    float savedFloat = took * 0.20F;
                    int saved = (int) savedFloat;
                    if (player.world.rand.nextFloat() < (savedFloat - saved)) {
                        saved++;
                    }
                    if (saved > 0) {
                        chest.setItemDamage(Math.max(0, currentDmg - saved));
                    }
                }
                player.getEntityData().removeTag("DE_PreChestDmg");
            }
        }

        // 2. Royal Guard: Redirects 10 * n% of durability damage from armor onto weapon
        ItemStack weapon = player.getHeldItemMainhand();
        int guardLevel = ModEnchantments.getLevel(ModEnchantments.ROYAL_GUARD, weapon);
        if (guardLevel == 0) {
            weapon = player.getHeldItemOffhand();
            guardLevel = ModEnchantments.getLevel(ModEnchantments.ROYAL_GUARD, weapon);
        }

        if (guardLevel > 0 && !weapon.isEmpty() && weapon.isItemStackDamageable() && player.getEntityData().hasKey("DE_PreArmorDmg")) {
            int[] preArmorDmg = player.getEntityData().getIntArray("DE_PreArmorDmg");
            float redirectRatio = 0.10F * guardLevel; // 10%, 20%, 30%
            int totalRedirected = 0;
            int idx = 0;

            for (ItemStack armor : player.getArmorInventoryList()) {
                if (idx < preArmorDmg.length) {
                    int pre = preArmorDmg[idx];
                    if (pre >= 0 && !armor.isEmpty() && armor.isItemStackDamageable()) {
                        int took = armor.getItemDamage() - pre;
                        if (took > 0) {
                            float redirFloat = took * redirectRatio;
                            int redir = (int) redirFloat;
                            if (player.world.rand.nextFloat() < (redirFloat - redir)) {
                                redir++;
                            }
                            if (redir > 0) {
                                armor.setItemDamage(Math.max(0, armor.getItemDamage() - redir));
                                totalRedirected += redir;
                            }
                        }
                    }
                }
                idx++;
            }

            if (totalRedirected > 0) {
                weapon.damageItem(totalRedirected, player);
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.7F, 1.6F);
            }
        }

        // 3. Debt of the Void: Armor pieces and held items never lose durability, converts to hunger and true void damage
        int totalVoidDurability = 0;
        if (player.getEntityData().hasKey("DE_PreArmorDmg")) {
            int[] preArmorDmg = player.getEntityData().getIntArray("DE_PreArmorDmg");
            int idxVoid = 0;
            for (ItemStack armor : player.getArmorInventoryList()) {
                if (idxVoid < preArmorDmg.length) {
                    int pre = preArmorDmg[idxVoid];
                    if (pre >= 0 && !armor.isEmpty() && armor.isItemStackDamageable()
                            && ModEnchantments.getLevel(ModEnchantments.DEBT_OF_THE_VOID, armor) > 0) {
                        int took = armor.getItemDamage() - pre;
                        if (took > 0) {
                            armor.setItemDamage(pre); // Durability completely preserved!
                            totalVoidDurability += took;
                        }
                    }
                }
                idxVoid++;
            }
        }

        // Check main hand and off hand for Debt of the Void (shield blocking, Royal Guard redirect, etc.)
        if (player.getEntityData().hasKey("DE_PreMainHandDmg")) {
            int preMain = player.getEntityData().getInteger("DE_PreMainHandDmg");
            ItemStack main = player.getHeldItemMainhand();
            if (preMain >= 0 && !main.isEmpty() && main.isItemStackDamageable()
                    && ModEnchantments.getLevel(ModEnchantments.DEBT_OF_THE_VOID, main) > 0) {
                int took = main.getItemDamage() - preMain;
                if (took > 0) {
                    main.setItemDamage(preMain);
                    totalVoidDurability += took;
                }
            }
            player.getEntityData().removeTag("DE_PreMainHandDmg");
        }
        if (player.getEntityData().hasKey("DE_PreOffHandDmg")) {
            int preOff = player.getEntityData().getInteger("DE_PreOffHandDmg");
            ItemStack off = player.getHeldItemOffhand();
            if (preOff >= 0 && !off.isEmpty() && off.isItemStackDamageable()
                    && ModEnchantments.getLevel(ModEnchantments.DEBT_OF_THE_VOID, off) > 0) {
                int took = off.getItemDamage() - preOff;
                if (took > 0) {
                    off.setItemDamage(preOff);
                    totalVoidDurability += took;
                }
            }
            player.getEntityData().removeTag("DE_PreOffHandDmg");
        }

        if (totalVoidDurability > 0) {
            player.getFoodStats().addExhaustion(totalVoidDurability * 1.5F);
            player.attackEntityFrom(DamageSource.OUT_OF_WORLD, Math.max(0.5F, totalVoidDurability * 0.25F));
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.4F, 1.8F);
        }

        // Clean up temporary NBT tags
        player.getEntityData().removeTag("DE_PreChestDmg");
        player.getEntityData().removeTag("DE_PreArmorDmg");
    }

    /**
     * Curse of the Tether: Cannot be removed from chest slot in survival.
     */
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EntityEquipmentSlot.CHEST && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || player.isCreative() || !player.isEntityAlive()
                    || player.getEntityData().getBoolean("Divine_VolatileSoul_Consuming")) {
                return;
            }

            ItemStack prev = event.getFrom();
            if (!prev.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, prev) > 0) {
                ItemStack current = event.getTo();
                if (current.isEmpty() || ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, current) == 0) {
                    // Force the tethered chestplate back into the chest slot and purge every duplicate.
                    ItemStack restored = prev.copy();
                    player.setItemStackToSlot(EntityEquipmentSlot.CHEST, restored);
                    clearTetherDuplicates(player, restored);

                    player.inventoryContainer.detectAndSendChanges();
                    if (player.openContainer != null && player.openContainer != player.inventoryContainer) {
                        player.openContainer.detectAndSendChanges();
                    }
                    syncSlot(player, 6, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                    syncCursor(player);
                }
            }
        }

        if (event.getSlot() == EntityEquipmentSlot.LEGS) {
            ItemStack from = event.getFrom();
            if (!from.isEmpty() && ModEnchantments.getLevel(ModEnchantments.ARACHNID_ASCENT, from) > 0) {
                ItemStack to = event.getTo();
                if (to.isEmpty() || ModEnchantments.getLevel(ModEnchantments.ARACHNID_ASCENT, to) == 0) {
                    Potion climb = getClimbPotion();
                    if (climb != null && event.getEntityLiving().isPotionActive(climb)) {
                        event.getEntityLiving().removePotionEffect(climb);
                    }
                }
            }
        }
    }

    /**
     * Curse of the Tether: Prevent dropping tethered chestplate.
     */
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.world.isRemote || player.isCreative() || !player.isEntityAlive()) {
            return;
        }

        ItemStack stack = event.getEntityItem().getItem();
        if (!stack.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, stack) > 0) {
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (chest.isEmpty()) {
                player.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack.copy());
                event.setCanceled(true);
                player.inventoryContainer.detectAndSendChanges();
                syncSlot(player, 6, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                return;
            }
        }

        // Suregrip: Prevent weapon from being knocked out of main hand if offhand is empty (not dual wielding)
        if (!stack.isEmpty() && player.getHeldItemOffhand().isEmpty()
                && ModEnchantments.getLevel(ModEnchantments.SUREGRIP, stack) > 0) {
            event.setCanceled(true);
            if (player.getHeldItemMainhand().isEmpty()) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            } else {
                player.inventory.addItemStackToInventory(stack);
            }
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.5F, 1.8F);
            player.inventoryContainer.detectAndSendChanges();
            syncSlot(player, 36 + player.inventory.currentItem, player.getHeldItemMainhand());
            syncSlot(player, 45, player.getHeldItemOffhand());
        }
    }

    private static boolean isTetherChest(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_THE_TETHER, stack) > 0;
    }

    private static void clearTetherDuplicates(EntityPlayer player, ItemStack keep) {
        ItemStack cursor = player.inventory.getItemStack();
        if (isTetherChest(cursor) && !ItemStack.areItemStacksEqual(cursor, keep)) {
            player.inventory.setItemStack(ItemStack.EMPTY);
        }

        if (player.openContainer != null) {
            for (Slot slot : player.openContainer.inventorySlots) {
                if (slot == null || !slot.getHasStack()) {
                    continue;
                }
                if (slot.inventory == player.inventory && slot.getSlotIndex() >= 36 && slot.getSlotIndex() <= 39) {
                    continue;
                }
                if (isTetherChest(slot.getStack())) {
                    slot.putStack(ItemStack.EMPTY);
                }
            }
        }

        ItemStack equipped = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (isTetherChest(stack) && stack != equipped) {
                player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }

    private static void syncCursor(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, -1, player.inventory.getItemStack()));
        }
    }

    private static void syncSlot(EntityPlayer player, int slot, ItemStack stack) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(0, slot, stack));
        }
    }

    /**
     * Curse of Inertia: Sprint speed ramp-up (+80% over 5s), reduced turning radius, 1.5s sliding deceleration.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        EntityPlayer player = event.player;

        // Curse of Berserker's Bloodlust: catch direct health increases that bypass LivingHealEvent.
        boolean berserkActive = !player.world.isRemote
                && (ModEnchantments.getLevel(ModEnchantments.BERSERKERS_BLOODLUST, player.getHeldItemMainhand()) > 0
                || ModEnchantments.getLevel(ModEnchantments.BERSERKERS_BLOODLUST, player.getHeldItemOffhand()) > 0);
        if (berserkActive) {
            NBTTagCompound berserkData = player.getEntityData();
            if (!berserkData.hasKey("DE_BerserkLastHealth")) {
                berserkData.setFloat("DE_BerserkLastHealth", player.getHealth());
            }
            float lastHealth = berserkData.getFloat("DE_BerserkLastHealth");
            float currentHealth = player.getHealth();
            boolean handledByHealEvent = berserkData.getBoolean("DE_BerserkHealHandled");
            berserkData.setBoolean("DE_BerserkHealHandled", false);
            if (!handledByHealEvent && lastHealth > 0.0F && currentHealth > lastHealth + 0.001F) {
                player.setHealth(lastHealth + (currentHealth - lastHealth) * 0.25F);
            }
            berserkData.setFloat("DE_BerserkLastHealth", player.getHealth());
        } else {
            player.getEntityData().removeTag("DE_BerserkLastHealth");
            player.getEntityData().removeTag("DE_BerserkHealHandled");
        }

        // Mudstrider END-tick correction: undo soul sand and cobweb motion damping
        ItemStack mudBoots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!player.world.isRemote && !mudBoots.isEmpty() && ModEnchantments.getLevel(ModEnchantments.MUDSTRIDER, mudBoots) > 0) {
            BlockPos mudPos = new BlockPos(player.posX, player.posY, player.posZ);
            Block mudHere = player.world.getBlockState(mudPos).getBlock();
            Block mudDown = player.world.getBlockState(mudPos.down()).getBlock();
            boolean onSoul = mudHere == Blocks.SOUL_SAND || mudDown == Blocks.SOUL_SAND;
            boolean inWeb = getIsInWeb(player) || mudHere == Blocks.WEB;
            if (onSoul || inWeb) {
                player.motionX *= 2.5D;
                player.motionZ *= 2.5D;
                double horizontal = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
                if (horizontal > 0.50D) {
                    double scale = 0.50D / horizontal;
                    player.motionX *= scale;
                    player.motionZ *= scale;
                }
                setIsInWeb(player, false);
                player.velocityChanged = true;
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                }
            }
        }

        // Debt of the Void continuous protection for all equipment slots
        if (!player.world.isRemote) {
            int totalVoidTick = 0;
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                ItemStack eq = player.getItemStackFromSlot(slot);
                if (!eq.isEmpty() && eq.isItemStackDamageable() && ModEnchantments.getLevel(ModEnchantments.DEBT_OF_THE_VOID, eq) > 0) {
                    NBTTagCompound tag = eq.getOrCreateSubCompound("DivineEnchantments");
                    int saved = tag.hasKey("VoidDmg") ? tag.getInteger("VoidDmg") : eq.getItemDamage();
                    int cur = eq.getItemDamage();
                    if (cur > saved) {
                        int diff = cur - saved;
                        eq.setItemDamage(saved);
                        totalVoidTick += diff;
                    } else if (cur < saved) {
                        tag.setInteger("VoidDmg", cur);
                    } else {
                        tag.setInteger("VoidDmg", saved);
                    }

                    // Provide buffer so a single heavy hit cannot destroy the item
                    if (eq.getItemDamage() >= eq.getMaxDamage() - 2) {
                        int safe = Math.max(0, eq.getMaxDamage() - 10);
                        eq.setItemDamage(safe);
                        tag.setInteger("VoidDmg", safe);
                    }
                }
            }
            if (totalVoidTick > 0) {
                player.getFoodStats().addExhaustion(totalVoidTick * 1.5F);
                player.attackEntityFrom(DamageSource.OUT_OF_WORLD, Math.max(0.5F, totalVoidTick * 0.25F));
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.4F, 1.8F);
            }
        }

        // Ground Sweep: Restores vanilla collision pickup without requiring right-clicks
        ItemStack bootsSweep = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!bootsSweep.isEmpty() && ModEnchantments.getLevel(ModEnchantments.GROUND_SWEEP, bootsSweep) > 0) {
            net.minecraft.util.math.AxisAlignedBB sweepBox = new net.minecraft.util.math.AxisAlignedBB(
                    player.posX - 1.25D, player.posY - 0.2D, player.posZ - 1.25D,
                    player.posX + 1.25D, player.posY + 1.6D, player.posZ + 1.25D);
            List<net.minecraft.entity.item.EntityItem> nearbyItems = player.world.getEntitiesWithinAABB(net.minecraft.entity.item.EntityItem.class, sweepBox);
            for (net.minecraft.entity.item.EntityItem groundItem : nearbyItems) {
                if (groundItem == null || groundItem.isDead || groundItem.cannotPickup()) {
                    continue;
                }
                groundItem.onCollideWithPlayer(player);
                if (!groundItem.isDead && groundItem.getAge() > 10 && !groundItem.getItem().isEmpty()) {
                    ItemStack stack = groundItem.getItem().copy();
                    if (player.inventory.addItemStackToInventory(stack)) {
                        groundItem.setItem(ItemStack.EMPTY);
                        groundItem.setDead();
                        player.world.playSound(null, player.posX, player.posY, player.posZ,
                                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                                ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    } else if (stack.getCount() != groundItem.getItem().getCount()) {
                        groundItem.setItem(stack);
                    }
                }
            }
        }

        // Magnetic Clasp: Attracts ground items within 3 blocks (6 blocks if both chest and boots) if incomplete stack exists in inventory
        int claspPieces = 0;
        ItemStack chestClasp = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chestClasp.isEmpty() && ModEnchantments.getLevel(ModEnchantments.MAGNETIC_CLASP, chestClasp) > 0) {
            claspPieces++;
        }
        if (!bootsSweep.isEmpty() && ModEnchantments.getLevel(ModEnchantments.MAGNETIC_CLASP, bootsSweep) > 0) {
            claspPieces++;
        }
        if (claspPieces > 0) {
            double magnetRadius = (claspPieces >= 2) ? 6.0D : 3.0D;
            net.minecraft.util.math.AxisAlignedBB magnetBox = new net.minecraft.util.math.AxisAlignedBB(
                    player.posX - magnetRadius, player.posY - magnetRadius, player.posZ - magnetRadius,
                    player.posX + magnetRadius, player.posY + magnetRadius, player.posZ + magnetRadius);
            List<net.minecraft.entity.item.EntityItem> magnetItems = player.world.getEntitiesWithinAABB(net.minecraft.entity.item.EntityItem.class, magnetBox);
            for (net.minecraft.entity.item.EntityItem groundItem : magnetItems) {
                if (groundItem != null && !groundItem.isDead && !groundItem.cannotPickup()) {
                    ItemStack groundStack = groundItem.getItem();
                    if (!groundStack.isEmpty()) {
                        boolean hasMatchingIncomplete = false;
                        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
                            ItemStack invStack = player.inventory.mainInventory.get(i);
                            if (!invStack.isEmpty() && invStack.isItemEqual(groundStack)
                                    && ItemStack.areItemStackTagsEqual(invStack, groundStack)
                                    && invStack.getCount() < invStack.getMaxStackSize()) {
                                hasMatchingIncomplete = true;
                                break;
                            }
                        }
                        if (hasMatchingIncomplete) {
                            double dx = player.posX - groundItem.posX;
                            double dy = (player.posY + 0.3D) - groundItem.posY;
                            double dz = player.posZ - groundItem.posZ;
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            if (dist > 0.1D) {
                                double pull = 0.14D;
                                groundItem.motionX += (dx / dist) * pull;
                                groundItem.motionY += (dy / dist) * pull;
                                groundItem.motionZ += (dz / dist) * pull;
                                groundItem.velocityChanged = true;
                            }
                        }
                    }
                }
            }
        }

        // Metabolic Slumber: Pauses hunger and thirst depletion while idle/not moving (takes effect after 10s = 200 ticks)
        ItemStack chestSlumber = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chestSlumber.isEmpty() && ModEnchantments.getLevel(ModEnchantments.METABOLIC_SLUMBER, chestSlumber) > 0) {
            double dx = player.posX - player.prevPosX;
            double dy = player.posY - player.prevPosY;
            double dz = player.posZ - player.prevPosZ;
            if (Math.abs(dx) < 0.005D && Math.abs(dy) < 0.005D && Math.abs(dz) < 0.005D && !player.isSwingInProgress) {
                int idleTicks = player.getEntityData().getInteger("DE_SlumberIdle") + 1;
                player.getEntityData().setInteger("DE_SlumberIdle", idleTicks);
                if (idleTicks >= 200) {
                    resetFoodExhaustion(player);
                    if (!player.getEntityData().hasKey("DE_SlumberFood")) {
                        player.getEntityData().setInteger("DE_SlumberFood", player.getFoodStats().getFoodLevel());
                    } else {
                        int saved = player.getEntityData().getInteger("DE_SlumberFood");
                        if (player.getFoodStats().getFoodLevel() < saved) {
                            player.getFoodStats().setFoodLevel(saved);
                        }
                    }
                    if (Loader.isModLoaded("simpledifficulty")) {
                        SimpleDifficultyCompat.pauseThirst(player);
                    }
                    if (Loader.isModLoaded("toughasnails")) {
                        TANCompat.pauseThirst(player);
                    }
                }
            } else {
                player.getEntityData().setInteger("DE_SlumberIdle", 0);
                player.getEntityData().removeTag("DE_SlumberFood");
                if (Loader.isModLoaded("simpledifficulty")) {
                    SimpleDifficultyCompat.clearThirstPause(player);
                }
                if (Loader.isModLoaded("toughasnails")) {
                    TANCompat.clearThirstPause(player);
                }
            }
        } else {
            player.getEntityData().removeTag("DE_SlumberIdle");
            player.getEntityData().removeTag("DE_SlumberFood");
            if (Loader.isModLoaded("simpledifficulty")) {
                SimpleDifficultyCompat.clearThirstPause(player);
            }
            if (Loader.isModLoaded("toughasnails")) {
                TANCompat.clearThirstPause(player);
            }
        }



        // Thermal Foil: Slows body temperature change in Simple Difficulty / Tough As Nails by 20% per piece
        if (!player.world.isRemote && player.world.getTotalWorldTime() % 10 == 0) {
            int foilPieces = 0;
            for (ItemStack arm : player.getArmorInventoryList()) {
                if (!arm.isEmpty() && ModEnchantments.getLevel(ModEnchantments.THERMAL_FOIL, arm) > 0) {
                    foilPieces++;
                }
            }
            if (foilPieces > 0) {
                if (Loader.isModLoaded("simpledifficulty")) {
                    SimpleDifficultyCompat.applyThermalFoil(player, foilPieces);
                }
                if (Loader.isModLoaded("toughasnails")) {
                    TANCompat.applyThermalFoil(player, foilPieces);
                }
            }
        }

        // Gargoyle Stance: Eliminates displacement from suction and vacuum effects (Djinn, whirlpools, siren pulls) while firmly on solid blocks
        ItemStack bootsGargoyle = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!bootsGargoyle.isEmpty() && ModEnchantments.getLevel(ModEnchantments.GARGOYLE_STANCE, bootsGargoyle) > 0) {
            net.minecraft.util.math.BlockPos feetPos = new net.minecraft.util.math.BlockPos(player.posX, player.posY - 0.2D, player.posZ);
            boolean solidBelow = player.world.getBlockState(feetPos).getMaterial().isSolid() || player.world.getBlockState(player.getPosition().down()).getMaterial().isSolid();
            if (player.onGround && solidBelow) {
                // Potion Core Weight makes Lycanites Roa/whirlpool effects skip the player.
                Potion weight = Potion.REGISTRY.getObject(new ResourceLocation("potioncore", "weight"));
                if (weight != null) {
                    player.addPotionEffect(new PotionEffect(weight, 20, 2, false, false));
                }
                boolean suctionNearby = false;
                net.minecraft.util.math.AxisAlignedBB checkArea = player.getEntityBoundingBox().grow(16.0D, 8.0D, 16.0D);
                List<Entity> nearbyEntities = player.world.getEntitiesWithinAABB(Entity.class, checkArea);
                for (Entity e : nearbyEntities) {
                    if (e != player) {
                        String name = e.getClass().getName().toLowerCase();
                        net.minecraft.util.ResourceLocation reg = net.minecraft.entity.EntityList.getKey(e);
                        String regName = (reg != null) ? reg.toString().toLowerCase() : "";
                        if (name.contains("djinn") || name.contains("whirlpool") || name.contains("siren")
                                || name.contains("vortex") || name.contains("roa") || name.contains("lochness")
                                || name.contains("sharknado") || regName.contains("djinn") || regName.contains("siren")
                                || regName.contains("whirlpool") || regName.contains("vortex")
                                || regName.contains("roa") || regName.contains("lochness") || regName.contains("sharknado")) {
                            suctionNearby = true;
                            break;
                        }
                    }
                }
                if (!suctionNearby) {
                    for (PotionEffect pe : player.getActivePotionEffects()) {
                        net.minecraft.util.ResourceLocation reg = pe.getPotion().getRegistryName();
                        if (reg != null && (reg.getPath().toLowerCase().contains("siren") || reg.getPath().toLowerCase().contains("charm")
                                || reg.getPath().toLowerCase().contains("vortex") || reg.getPath().toLowerCase().contains("pull"))) {
                            suctionNearby = true;
                            break;
                        }
                    }
                }
                if (suctionNearby) {
                    long lastKb = player.getEntityData().getLong("DE_LastKnockbackTime");
                    boolean isNormalKnockback = (player.world.getTotalWorldTime() - lastKb < 10L);
                    if (!isNormalKnockback) {
                        if (Math.abs(player.motionX) > 0.05D || Math.abs(player.motionZ) > 0.05D || player.motionY > 0.0D) {
                            player.motionX = 0.0D;
                            player.motionZ = 0.0D;
                            if (player.motionY > 0.0D) player.motionY = 0.0D;
                            player.velocityChanged = true;
                            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                            }
                            if (player.world.getTotalWorldTime() % 10 == 0 && player.world instanceof WorldServer) {
                                WorldServer ws = (WorldServer) player.world;
                                int stoneId = Block.getStateId(Blocks.STONE.getDefaultState());
                                ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, player.posX, player.posY + 0.05D, player.posZ, 4, 0.2D, 0.02D, 0.2D, 0.01D, stoneId);
                            }
                        }
                    }
                }
            }
        }

        // Shadow Stalker visual indicator while sneaking in deep stealth
        ItemStack legsStalker = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (!legsStalker.isEmpty() && ModEnchantments.getLevel(ModEnchantments.SHADOW_STALKER, legsStalker) > 0) {
            if (player.isSneaking() && !player.isSprinting() && (player.world.getTotalWorldTime() % 15 == 0)) {
                long lastAttack = player.getEntityData().getLong("DE_LastAttackTime");
                if (player.world.getTotalWorldTime() - lastAttack >= 60L && player.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) player.world;
                    ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.1D, player.posZ, 1, 0.1D, 0.02D, 0.1D, 0.005D);
                }
            }
        }

        ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        boolean hasInertia = !legs.isEmpty() && ModEnchantments.getLevel(ModEnchantments.CURSE_OF_INERTIA, legs) > 0;

        if (!hasInertia) {
            if (player.getEntityData().hasKey("Inertia_SprintTicks")) {
                player.getEntityData().removeTag("Inertia_SprintTicks");
                player.getEntityData().removeTag("Inertia_StoppingTicks");
                player.getEntityData().removeTag("Inertia_SlideX");
                player.getEntityData().removeTag("Inertia_SlideZ");
                removeInertiaSpeed(player);
            }
            return;
        }

        if (player.isSprinting()) {
            int sprintTicks = player.getEntityData().getInteger("Inertia_SprintTicks") + 1;
            if (sprintTicks > 100) {
                sprintTicks = 100; // 5 seconds = 100 ticks
            }
            player.getEntityData().setInteger("Inertia_SprintTicks", sprintTicks);
            player.getEntityData().setInteger("Inertia_StoppingTicks", 0);

            // Up to +80% linearly over 5s
            double speedBoost = 0.80D * ((double) sprintTicks / 100.0D);
            applyInertiaSpeed(player, speedBoost);

            // Severely reduced turning radius when sprinting with momentum
            double hSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
            if (hSpeed > 0.12D && sprintTicks > 15) {
                double curMoveAngle = Math.toDegrees(Math.atan2(player.motionZ, player.motionX));
                double targetAngle = player.rotationYaw + 90.0D;
                double diff = MathHelper.wrapDegrees(targetAngle - curMoveAngle);
                double maxTurn = 2.2D; // Clamp turn rate per tick
                double clampedDiff = Math.max(-maxTurn, Math.min(maxTurn, diff));
                double newAngleRad = Math.toRadians(curMoveAngle + clampedDiff);

                player.motionX = Math.cos(newAngleRad) * hSpeed;
                player.motionZ = Math.sin(newAngleRad) * hSpeed;
                player.velocityChanged = true;
            }
        } else {
            // Not sprinting
            int prevSprint = player.getEntityData().getInteger("Inertia_SprintTicks");
            if (prevSprint > 20) {
                // Just stopped sprinting -> start 30 ticks (1.5s) of ice sliding deceleration!
                player.getEntityData().setInteger("Inertia_StoppingTicks", 30);
                player.getEntityData().setDouble("Inertia_SlideX", player.motionX);
                player.getEntityData().setDouble("Inertia_SlideZ", player.motionZ);
            }
            player.getEntityData().setInteger("Inertia_SprintTicks", 0);
            removeInertiaSpeed(player);

            int stoppingTicks = player.getEntityData().getInteger("Inertia_StoppingTicks");
            if (stoppingTicks > 0) {
                if (player.collidedHorizontally) {
                    player.getEntityData().setInteger("Inertia_StoppingTicks", 0);
                } else {
                    player.getEntityData().setInteger("Inertia_StoppingTicks", stoppingTicks - 1);

                    // True ice physics sliding deceleration across all surfaces
                    double slideX = player.getEntityData().getDouble("Inertia_SlideX") * 0.895D;
                    double slideZ = player.getEntityData().getDouble("Inertia_SlideZ") * 0.895D;
                    player.getEntityData().setDouble("Inertia_SlideX", slideX);
                    player.getEntityData().setDouble("Inertia_SlideZ", slideZ);

                    player.motionX = slideX;
                    player.motionZ = slideZ;
                    player.velocityChanged = true;

                    if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
                    }

                    if (!player.world.isRemote && player.world instanceof WorldServer && stoppingTicks % 3 == 0) {
                        WorldServer ws = (WorldServer) player.world;
                        int iceId = Block.getStateId(Blocks.ICE.getDefaultState());
                        ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, player.posX, player.posY + 0.1D, player.posZ, 8, 0.25D, 0.05D, 0.25D, 0.02D, iceId);
                    }
                }
            }
        }
    }

    private static void applyInertiaSpeed(EntityPlayer player, double amount) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (inst == null) return;

            AttributeModifier existing = inst.getModifier(INERTIA_SPEED_UUID);
            if (existing == null || Math.abs(existing.getAmount() - amount) > 0.01D) {
                if (existing != null) {
                    inst.removeModifier(INERTIA_SPEED_UUID);
                }
                AttributeModifier mod = new AttributeModifier(INERTIA_SPEED_UUID, "Curse of Inertia Speed", amount, 2);
                mod.setSaved(false);
                inst.applyModifier(mod);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void removeInertiaSpeed(EntityPlayer player) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (inst != null && inst.getModifier(INERTIA_SPEED_UUID) != null) {
                inst.removeModifier(INERTIA_SPEED_UUID);
            }
        } catch (Throwable ignored) {
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty() || ModEnchantments.getLevel(ModEnchantments.DESALINATION_MESH, helmet) <= 0) {
            return;
        }
        long until = player.getEntityData().getLong("DE_DesalinationDrinkingUntil");
        if (until <= 0L || player.world.getTotalWorldTime() > until) {
            return;
        }
        ResourceLocation name = event.getPotionEffect().getPotion().getRegistryName();
        if (name == null) {
            return;
        }
        String path = name.getPath().toLowerCase();
        if ((path.contains("thirst") || path.contains("parasite")) && player.world.rand.nextFloat() < 0.85F) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onDrinkStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty() || ModEnchantments.getLevel(ModEnchantments.DESALINATION_MESH, helmet) <= 0) {
            return;
        }
        ResourceLocation itemName = event.getItem().getItem().getRegistryName();
        String path = itemName == null ? "" : itemName.getPath().toLowerCase();
        if (path.contains("water") || path.contains("dirty") || path.contains("salt") || path.contains("canteen")) {
            player.getEntityData().setLong("DE_DesalinationDrinkingUntil", player.world.getTotalWorldTime() + 120L);
        }
    }

    @SubscribeEvent
    public static void onRightClickWater(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote || !(event.getEntityPlayer() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty() || ModEnchantments.getLevel(ModEnchantments.DESALINATION_MESH, helmet) <= 0) {
            return;
        }
        if (event.getWorld().getBlockState(event.getPos()).getMaterial() == net.minecraft.block.material.Material.WATER) {
            player.getEntityData().setLong("DE_DesalinationDrinkingUntil", player.world.getTotalWorldTime() + 40L);
        }
    }

    /**
     * Berserker's Bloodlust: Healing from natural regen, potions, and lifesteal reduced by 75%.
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            boolean hasBerserk = ModEnchantments.getLevel(ModEnchantments.BERSERKERS_BLOODLUST, player.getHeldItemMainhand()) > 0
                    || ModEnchantments.getLevel(ModEnchantments.BERSERKERS_BLOODLUST, player.getHeldItemOffhand()) > 0;
            if (hasBerserk) {
                event.setAmount(event.getAmount() * 0.25F);
                player.getEntityData().setBoolean("DE_BerserkHealHandled", true);
            }
        }
    }

    /**
     * Gourmand's Vitality: Eating food grants a surge of health regeneration, resistance, and absorption.
     */
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            int gourmandLvl = (!chest.isEmpty()) ? ModEnchantments.getLevel(ModEnchantments.GOURMANDS_VITALITY, chest) : 0;
            if (gourmandLvl > 0 && event.getItem().getItem() instanceof ItemFood) {
                ItemFood food = (ItemFood) event.getItem().getItem();
                int hunger = food.getHealAmount(event.getItem());
                float sat = food.getSaturationModifier(event.getItem());

                int regenTicks = 3 * gourmandLvl * 20;
                int resTicks = 5 * gourmandLvl * 20;
                int absTicks = 10 * gourmandLvl * 20;

                int amp = (hunger >= 7 || sat >= 0.8F) ? 1 : 0;

                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, regenTicks, amp));
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, resTicks, amp));
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, absTicks, amp));

                if (player.world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) player.world;
                    ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, player.posX, player.posY + 1.0D, player.posZ, 10, 0.3D, 0.3D, 0.3D, 0.05D);
                }
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.8F, 1.2F);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockBack(net.minecraftforge.event.entity.living.LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            player.getEntityData().setLong("DE_LastKnockbackTime", player.world.getTotalWorldTime());
        }
    }

    @SubscribeEvent
    public static void onLivingSetAttackTarget(net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        EntityLivingBase mob = event.getEntityLiving();
        long worldTime = mob.world.getTotalWorldTime();

        // 1. Challenger's Mark aggro lock
        if (mob.getEntityData().hasKey("DE_ChallengerLockUntil")) {
            long lockUntil = mob.getEntityData().getLong("DE_ChallengerLockUntil");
            if (worldTime < lockUntil) {
                String lockedUUID = mob.getEntityData().getString("DE_ChallengerLockPlayer");
                if (event.getTarget() == null || !event.getTarget().getUniqueID().toString().equals(lockedUUID)) {
                    EntityPlayer lockedPlayer = mob.world.getPlayerEntityByUUID(UUID.fromString(lockedUUID));
                    if (lockedPlayer != null && lockedPlayer.isEntityAlive() && mob instanceof net.minecraft.entity.EntityLiving) {
                        ((net.minecraft.entity.EntityLiving) mob).setAttackTarget(lockedPlayer);
                    } else {
                        mob.getEntityData().removeTag("DE_ChallengerLockUntil");
                        mob.getEntityData().removeTag("DE_ChallengerLockPlayer");
                    }
                }
                return;
            } else {
                mob.getEntityData().removeTag("DE_ChallengerLockUntil");
                mob.getEntityData().removeTag("DE_ChallengerLockPlayer");
            }
        }

        // 2. Shadow Stalker detection radius reduction
        if (event.getTarget() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            int stalkerLvl = (!legs.isEmpty()) ? ModEnchantments.getLevel(ModEnchantments.SHADOW_STALKER, legs) : 0;
            if (stalkerLvl > 0) {
                boolean isSneaking = player.isSneaking();
                boolean isSprinting = player.isSprinting();
                long lastAttack = player.getEntityData().getLong("DE_LastAttackTime");
                boolean attackedRecently = (worldTime - lastAttack < 60L);

                double distSq = mob.getDistanceSq(player);
                boolean directLosClose = (distSq <= 36.0D && mob.canEntityBeSeen(player));

                // Breaks if player sprints, attacks, or enters direct line of sight within 6 blocks
                if (isSneaking && !isSprinting && !attackedRecently && !directLosClose) {
                    boolean isRevenge = (mob.getRevengeTarget() == player);
                    if (!isRevenge) {
                        double baseRange = 16.0D;
                        try {
                            baseRange = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
                        } catch (Throwable ignored) {
                        }
                        double reduction = stalkerLvl * 0.25D; // 25% for I, 50% for II
                        double reducedRange = baseRange * (1.0D - reduction);
                        if (distSq > (reducedRange * reducedRange) || !mob.canEntityBeSeen(player)) {
                            if (mob instanceof net.minecraft.entity.EntityLiving) {
                                ((net.minecraft.entity.EntityLiving) mob).setAttackTarget(null);
                                ((net.minecraft.entity.EntityLiving) mob).getNavigator().clearPath();
                            }
                        }
                    }
                }
            }
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
