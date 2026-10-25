package com.divineenchantments.event;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.network.ModNetwork;
import com.divineenchantments.network.PacketCarnage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class CarnageEventHandler {

    private static final UUID PENALTY_UUID = UUID.fromString("d1a4c001-7c2e-4a61-9b3f-52a8d4c9e101");
    private static final UUID STACKS_UUID = UUID.fromString("d1a4c002-7c2e-4a61-9b3f-52a8d4c9e102");

    private static final String TAG_STACKS = "DE_CarnageStacks";
    private static final String TAG_LEVEL = "DE_CarnageLevel";
    private static final String TAG_LAST_HIT = "DE_CarnageLastHit";
    private static final String TAG_LAST_STACK_TICK = "DE_CarnageLastStackTick";
    private static final String TAG_LAST_SENT_STACKS = "DE_CarnageLastSentStacks";
    private static final String TAG_LAST_SENT_LEVEL = "DE_CarnageLastSentLevel";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) {
            return;
        }

        int level = ModEnchantments.getLevel(ModEnchantments.RUNE_CARNAGE, held);
        if (level <= 0) {
            return;
        }

        long now = player.world.getTotalWorldTime();
        NBTTagCompound data = player.getEntityData();

        int storedLevel = data.getInteger(TAG_LEVEL);
        if (storedLevel != level) {
            data.setInteger(TAG_LEVEL, level);
            data.setInteger(TAG_STACKS, 0);
        }

        data.setLong(TAG_LAST_HIT, now);

        // Only one stack gain per swing tick (sweeping can fire several hurt events in a tick).
        if (data.getLong(TAG_LAST_STACK_TICK) == now) {
            return;
        }

        // "Full hit": the attack indicator must be completely recharged.
        if (player.getCooledAttackStrength(0.5F) >= 0.99F) {
            int stacks = data.getInteger(TAG_STACKS);
            int maxStacks = DivineConfig.carnageMaxStacksPerLevel * level;
            if (stacks < maxStacks) {
                stacks++;
                data.setInteger(TAG_STACKS, stacks);
                data.setLong(TAG_LAST_STACK_TICK, now);

                if (player.world instanceof WorldServer) {
                    WorldServer world = (WorldServer) player.world;
                    world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
                            player.posX, player.posY + player.getEyeHeight() * 0.7D, player.posZ,
                            3, 0.25D, 0.35D, 0.25D, 0.02D);
                }
            }
            applyAttackCooldownIframes(player, event);
        }
    }

    private static void applyAttackCooldownIframes(EntityPlayer player, LivingHurtEvent event) {
        // Enemy i-frames last exactly as long as the wielder's next full charge.
        int cooldownTicks = Math.max(1, Math.round(player.getCooldownPeriod()));
        event.getEntityLiving().maxHurtResistantTime = cooldownTicks;
        event.getEntityLiving().hurtResistantTime = cooldownTicks;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        EntityPlayer player = event.player;
        ItemStack held = player.getHeldItemMainhand();
        boolean clientSide = player.world.isRemote;
        int level = held.isEmpty() ? 0
                : (clientSide
                ? EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RUNE_CARNAGE, held)
                : ModEnchantments.getLevel(ModEnchantments.RUNE_CARNAGE, held));

        if (clientSide) {
            if (level <= 0) {
                removeModifier(player, PENALTY_UUID);
                removeModifier(player, STACKS_UUID);
                return;
            }
            double syncedPenalty = player.getEntityData().getDouble("DE_CarnageSyncPenalty");
            double syncedBonus = player.getEntityData().getDouble("DE_CarnageSyncBonus");
            if (syncedPenalty != 0.0D) {
                applyModifier(player, PENALTY_UUID, "Rune Carnage Penalty", syncedPenalty, 2);
            } else {
                removeModifier(player, PENALTY_UUID);
            }
            if (syncedBonus > 0.0D) {
                applyModifier(player, STACKS_UUID, "Rune Carnage Stacks", syncedBonus, 1);
            } else {
                removeModifier(player, STACKS_UUID);
            }
            return;
        }

        NBTTagCompound data = player.getEntityData();

        if (level <= 0) {
            boolean hadStacks = data.getInteger(TAG_STACKS) != 0;
            data.setInteger(TAG_STACKS, 0);
            removeModifier(player, PENALTY_UUID);
            removeModifier(player, STACKS_UUID);
            sendSync(player, 0, 0, 0.0D, 0.0D, true, hadStacks);
            return;
        }

        if (data.getInteger(TAG_LEVEL) != level) {
            data.setInteger(TAG_LEVEL, level);
            data.setInteger(TAG_STACKS, 0);
        }

        int stacks = data.getInteger(TAG_STACKS);
        long now = player.world.getTotalWorldTime();
        long lastHit = data.getLong(TAG_LAST_HIT);

        if (stacks > 0 && lastHit > 0L && DivineConfig.carnageDecayStacks > 0) {
            int safety = 0;
            while (stacks > 0 && now - lastHit >= DivineConfig.carnageDecayTicks && safety++ < 200) {
                stacks = Math.max(0, stacks - DivineConfig.carnageDecayStacks);
                lastHit += DivineConfig.carnageDecayTicks;
            }
            if (stacks != data.getInteger(TAG_STACKS)) {
                data.setInteger(TAG_STACKS, stacks);
                data.setLong(TAG_LAST_HIT, lastHit);
            }
        }

        int maxStacks = DivineConfig.carnageMaxStacksPerLevel * level;
        if (stacks > maxStacks) {
            stacks = maxStacks;
            data.setInteger(TAG_STACKS, stacks);
        }

        // Permanent weapon penalty: configurable attack speed reduction per level.
        double penalty = -DivineConfig.carnagePenaltyPerLevel * level;
        if (penalty != 0.0D) {
            applyModifier(player, PENALTY_UUID, "Rune Carnage Penalty", penalty, 2);
        } else {
            removeModifier(player, PENALTY_UUID);
        }

        // Full-hit momentum: configurable attack speed bonus per stack.
        float perStack = level == 1 ? (float) DivineConfig.carnageBonusLevel1
                    : (level == 2 ? (float) DivineConfig.carnageBonusLevel2 : (float) DivineConfig.carnageBonusLevel3);
        double bonus = DivineConfig.carnageBaseBonusPerLevel * level + perStack * stacks;
        if (bonus > 0.0D) {
            applyModifier(player, STACKS_UUID, "Rune Carnage Stacks", bonus, 1);
        } else {
            removeModifier(player, STACKS_UUID);
        }

        sendSync(player, stacks, level, penalty, bonus, false, false);
    }

    private static void sendSync(EntityPlayer player, int stacks, int level, double penalty, double bonus, boolean clear, boolean force) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        int lastStacks = data.getInteger(TAG_LAST_SENT_STACKS);
        int lastLevel = data.getInteger(TAG_LAST_SENT_LEVEL);

        if (!clear && stacks == lastStacks && level == lastLevel) {
            return;
        }
        if (clear && lastLevel == 0 && !force) {
            return;
        }

        data.setInteger(TAG_LAST_SENT_STACKS, clear ? 0 : stacks);
        data.setInteger(TAG_LAST_SENT_LEVEL, clear ? 0 : level);
        ModNetwork.INSTANCE.sendTo(new PacketCarnage(stacks, level, penalty, bonus, clear), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        resetSyncState(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        resetSyncState(event.player);
    }

    private static void resetSyncState(EntityPlayer player) {
        if (player == null || player.world.isRemote) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.removeTag(TAG_LAST_SENT_STACKS);
        data.removeTag(TAG_LAST_SENT_LEVEL);
    }

    private static void applyModifier(EntityPlayer player, UUID uuid, String name, double amount, int operation) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
            if (inst == null) {
                return;
            }
            AttributeModifier existing = inst.getModifier(uuid);
            if (existing != null && existing.getAmount() == amount && existing.getOperation() == operation) {
                return;
            }
            if (existing != null) {
                inst.removeModifier(uuid);
            }
            AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);
            modifier.setSaved(false);
            inst.applyModifier(modifier);
        } catch (Throwable ignored) {
        }
    }

    private static void removeModifier(EntityPlayer player, UUID uuid) {
        try {
            IAttributeInstance inst = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
            if (inst != null && inst.getModifier(uuid) != null) {
                inst.removeModifier(uuid);
            }
        } catch (Throwable ignored) {
        }
    }
}
