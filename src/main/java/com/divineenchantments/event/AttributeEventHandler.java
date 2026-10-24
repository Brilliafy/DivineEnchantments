package com.divineenchantments.event;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class AttributeEventHandler {

    // Sacrificial UUIDs
    public static final UUID SACRIFICIAL_DMG_UUID = UUID.fromString("b3e2a001-49fa-4c40-8f6b-71a2e391cb01");
    public static final UUID SACRIFICIAL_SPD_UUID = UUID.fromString("b3e2a002-49fa-4c40-8f6b-71a2e391cb02");
    public static final UUID SACRIFICIAL_MOVE_UUID = UUID.fromString("b3e2a003-49fa-4c40-8f6b-71a2e391cb03");
    public static final UUID SACRIFICIAL_HP_UUID = UUID.fromString("b3e2a004-49fa-4c40-8f6b-71a2e391cb04");
    public static final UUID SACRIFICIAL_ARMOR_UUID = UUID.fromString("b3e2a005-49fa-4c40-8f6b-71a2e391cb05");

    // Heavyplated UUIDs per slot
    public static final UUID HEAVYPLATED_ARMOR_HEAD_UUID = UUID.fromString("c4f3b001-49fa-4c40-8f6b-71a2e391cb11");
    public static final UUID HEAVYPLATED_MOVE_HEAD_UUID = UUID.fromString("c4f3b002-49fa-4c40-8f6b-71a2e391cb12");

    public static final UUID HEAVYPLATED_ARMOR_CHEST_UUID = UUID.fromString("c4f3b003-49fa-4c40-8f6b-71a2e391cb13");
    public static final UUID HEAVYPLATED_MOVE_CHEST_UUID = UUID.fromString("c4f3b004-49fa-4c40-8f6b-71a2e391cb14");

    public static final UUID HEAVYPLATED_ARMOR_LEGS_UUID = UUID.fromString("c4f3b005-49fa-4c40-8f6b-71a2e391cb15");
    public static final UUID HEAVYPLATED_MOVE_LEGS_UUID = UUID.fromString("c4f3b006-49fa-4c40-8f6b-71a2e391cb16");

    public static final UUID HEAVYPLATED_ARMOR_FEET_UUID = UUID.fromString("c4f3b007-49fa-4c40-8f6b-71a2e391cb17");
    public static final UUID HEAVYPLATED_MOVE_FEET_UUID = UUID.fromString("c4f3b008-49fa-4c40-8f6b-71a2e391cb18");

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntityLiving() == null || event.getEntityLiving().world.isRemote) {
            return;
        }
        updateEntityModifiers(event.getEntityLiving());
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityLivingBase)) {
            return;
        }
        updateEntityModifiers((EntityLivingBase) event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player != null && !event.player.world.isRemote) {
            updateEntityModifiers(event.player);
        }
    }

    public static void updateEntityModifiers(EntityLivingBase entity) {
        if (entity == null || entity.world.isRemote) {
            return;
        }

        // 1. Sacrificial modifier on mainhand melee weapon
        ItemStack mainhand = entity.getHeldItemMainhand();
        boolean hasSacrificial = !mainhand.isEmpty()
                && ModEnchantments.getLevel(ModEnchantments.SACRIFICIAL, mainhand) > 0;

        // Operation 2: Multiply total
        applyOrRemove(entity, SharedMonsterAttributes.ATTACK_DAMAGE, SACRIFICIAL_DMG_UUID, "Sacrificial Damage", 0.10D, 2, hasSacrificial);
        applyOrRemove(entity, SharedMonsterAttributes.ATTACK_SPEED, SACRIFICIAL_SPD_UUID, "Sacrificial Attack Speed", 0.10D, 2, hasSacrificial);
        applyOrRemove(entity, SharedMonsterAttributes.MOVEMENT_SPEED, SACRIFICIAL_MOVE_UUID, "Sacrificial Movement Speed", 0.10D, 2, hasSacrificial);
        applyOrRemove(entity, SharedMonsterAttributes.MAX_HEALTH, SACRIFICIAL_HP_UUID, "Sacrificial Max Health", -0.10D, 2, hasSacrificial);
        applyOrRemove(entity, SharedMonsterAttributes.ARMOR, SACRIFICIAL_ARMOR_UUID, "Sacrificial Armor", -0.10D, 2, hasSacrificial);

        // Clamp health if it exceeds new max health
        if (hasSacrificial && entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }

        // 2. Heavyplated modifier per armor slot (+8% armor, -3% movement speed)
        updateHeavyplatedSlot(entity, EntityEquipmentSlot.HEAD, HEAVYPLATED_ARMOR_HEAD_UUID, HEAVYPLATED_MOVE_HEAD_UUID, "Head");
        updateHeavyplatedSlot(entity, EntityEquipmentSlot.CHEST, HEAVYPLATED_ARMOR_CHEST_UUID, HEAVYPLATED_MOVE_CHEST_UUID, "Chest");
        updateHeavyplatedSlot(entity, EntityEquipmentSlot.LEGS, HEAVYPLATED_ARMOR_LEGS_UUID, HEAVYPLATED_MOVE_LEGS_UUID, "Legs");
        updateHeavyplatedSlot(entity, EntityEquipmentSlot.FEET, HEAVYPLATED_ARMOR_FEET_UUID, HEAVYPLATED_MOVE_FEET_UUID, "Feet");
    }

    private static void updateHeavyplatedSlot(EntityLivingBase entity, EntityEquipmentSlot slot, UUID armorUuid, UUID moveUuid, String slotName) {
        ItemStack piece = entity.getItemStackFromSlot(slot);
        boolean hasHeavyplated = !piece.isEmpty()
                && ModEnchantments.getLevel(ModEnchantments.HEAVYPLATED, piece) > 0;

        applyOrRemove(entity, SharedMonsterAttributes.ARMOR, armorUuid, "Heavyplated Armor " + slotName, 0.08D, 2, hasHeavyplated);
        applyOrRemove(entity, SharedMonsterAttributes.MOVEMENT_SPEED, moveUuid, "Heavyplated Speed " + slotName, -0.03D, 2, hasHeavyplated);
    }

    private static void applyOrRemove(EntityLivingBase entity, IAttribute attribute, UUID uuid, String name, double amount, int operation, boolean shouldApply) {
        try {
            IAttributeInstance inst = entity.getEntityAttribute(attribute);
            if (inst == null) {
                return;
            }

            AttributeModifier existing = inst.getModifier(uuid);
            if (shouldApply) {
                if (existing == null || existing.getAmount() != amount || existing.getOperation() != operation) {
                    if (existing != null) {
                        inst.removeModifier(uuid);
                    }
                    AttributeModifier mod = new AttributeModifier(uuid, name, amount, operation);
                    mod.setSaved(false); // Never save to NBT so it is clean on reload
                    inst.applyModifier(mod);
                }
            } else {
                if (existing != null) {
                    inst.removeModifier(uuid);
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
