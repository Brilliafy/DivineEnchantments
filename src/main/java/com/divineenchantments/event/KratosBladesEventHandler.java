package com.divineenchantments.event;

import com.divineenchantments.entity.EntityKratosHook;
import com.divineenchantments.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class KratosBladesEventHandler {

    public static final String CLONE_TAG = "DE_KratosClone";

    private static final String TAG_LAST_DAMAGE = "DE_KratosLastDamage";
    private static final int OFFHAND_SLOT_INDEX = 40;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;
        NBTTagCompound data = player.getEntityData();

        // A clone on the cursor can be used to smuggle/copy the blade. Delete it.
        clearCursorClone(player);

        // Remove clones from the currently open container (chest, crafting, etc.).
        purgeClones(player.openContainer, player);

        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        boolean mainOriginal = isKratosOriginal(main);
        boolean offOriginal = isKratosOriginal(off);

        if (!mainOriginal && !offOriginal) {
            if (isClone(main)) {
                player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            if (isClone(off)) {
                player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            data.removeTag(TAG_LAST_DAMAGE);
            return;
        }

        ItemStack original;
        ItemStack other;
        EntityEquipmentSlot otherSlot;

        if (mainOriginal) {
            original = main;
            other = off;
            otherSlot = EntityEquipmentSlot.OFFHAND;
        } else {
            original = off;
            other = main;
            otherSlot = EntityEquipmentSlot.MAINHAND;
        }

        boolean otherIsMatchingClone = isClone(other) && other.getItem() == original.getItem();
        int lastDamage = data.hasKey(TAG_LAST_DAMAGE)
                ? data.getInteger(TAG_LAST_DAMAGE)
                : original.getItemDamage();

        // Any damage dealt to the duplicate belongs to the original too.
        int cloneDelta = 0;
        if (otherIsMatchingClone && other.isItemStackDamageable() && original.isItemStackDamageable()) {
            int cloneDamage = other.getItemDamage();
            if (cloneDamage > lastDamage) {
                cloneDelta = cloneDamage - lastDamage;
            }
        }

        if (!otherIsMatchingClone) {
            // Drop whatever non-clone item occupied the other hand (the enchantment's downside).
            if (!other.isEmpty() && !isClone(other)) {
                ItemStack toDrop = other.copy();
                player.setItemStackToSlot(otherSlot, ItemStack.EMPTY);
                player.dropItem(toDrop, false);
            } else if (!other.isEmpty()) {
                // A clone of a different blade is stale.
                player.setItemStackToSlot(otherSlot, ItemStack.EMPTY);
            }
            lastDamage = original.getItemDamage();
            cloneDelta = 0;
        }

        int newDamage = original.getItemDamage() + cloneDelta;
        if (original.isItemStackDamageable()) {
            newDamage = Math.max(0, Math.min(original.getMaxDamage(), newDamage));
            if (newDamage != original.getItemDamage()) {
                original.setItemDamage(newDamage);
            }
        }

        ItemStack clone = createClone(original);
        if (!ItemStack.areItemStacksEqual(other, clone)) {
            player.setItemStackToSlot(otherSlot, clone);
        }
        data.setInteger(TAG_LAST_DAMAGE, original.getItemDamage());
    }

    private static void clearCursorClone(EntityPlayer player) {
        ItemStack cursor = player.inventory.getItemStack();
        if (!isClone(cursor)) {
            return;
        }
        player.inventory.setItemStack(ItemStack.EMPTY);
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, -1, ItemStack.EMPTY));
        }
    }

    private static void purgeClones(Container container, EntityPlayer player) {
        if (container == null) {
            return;
        }
        for (Slot slot : container.inventorySlots) {
            if (slot == null) {
                continue;
            }
            // Never delete the duplicate while it is actively held.
            if (slot.inventory == player.inventory) {
                int index = slot.getSlotIndex();
                if (index == player.inventory.currentItem || index == OFFHAND_SLOT_INDEX) {
                    continue;
                }
            }
            if (isClone(slot.getStack())) {
                slot.putStack(ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDestroyItem(PlayerDestroyItemEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        if (!isClone(event.getOriginal())) {
            return;
        }

        // The duplicate just broke. The original must take that final point of durability.
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        ItemStack original = isKratosOriginal(main) ? main : (isKratosOriginal(off) ? off : ItemStack.EMPTY);

        if (!original.isEmpty() && original.isItemStackDamageable()) {
            original.damageItem(1, player);
            if (original.isEmpty()) {
                player.getEntityData().removeTag(TAG_LAST_DAMAGE);
            } else {
                player.getEntityData().setInteger(TAG_LAST_DAMAGE, original.getItemDamage());
            }
        } else {
            player.getEntityData().removeTag(TAG_LAST_DAMAGE);
        }
    }

    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        purgeClones(event.getContainer(), player);
        clearCursorClone(player);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isClone(event.getItemStack())) {
            event.setCanceled(true);
            return;
        }
        if (!isKratosOriginal(event.getItemStack())) {
            return;
        }

        event.setCanceled(true);
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        if (player.getCooldownTracker().hasCooldown(event.getItemStack().getItem())) {
            return;
        }

        // Remove any previous chain still flying from this blade.
        List<EntityKratosHook> existing = player.world.getEntities(EntityKratosHook.class,
                hook -> hook != null && hook.isOwner(player));
        for (EntityKratosHook hook : existing) {
            hook.setDead();
        }

        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.KRATOS_BLADES, event.getItemStack());
        EntityKratosHook hook = new EntityKratosHook(player.world, player, level);
        player.world.spawnEntity(hook);
        player.getCooldownTracker().setCooldown(event.getItemStack().getItem(), 8);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (isClone(event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (isClone(event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (!event.getPlayer().world.isRemote && isClone(event.getEntityItem().getItem())) {
            event.setCanceled(true);
            event.getEntityItem().setDead();
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(net.minecraftforge.event.entity.player.PlayerDropsEvent event) {
        if (!event.getEntityPlayer().world.isRemote) {
            event.getDrops().removeIf(item -> item != null && isClone(item.getItem()));
        }
    }

    public static boolean isKratos(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.KRATOS_BLADES, stack) > 0;
    }

    public static boolean isClone(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getTagCompound() == null) {
            return false;
        }
        return stack.getTagCompound().getBoolean(CLONE_TAG);
    }

    public static boolean isKratosOriginal(ItemStack stack) {
        return isKratos(stack) && !isClone(stack);
    }

    private static ItemStack createClone(ItemStack original) {
        ItemStack clone = original.copy();
        clone.setCount(1);
        NBTTagCompound tag = clone.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            clone.setTagCompound(tag);
        }
        tag.setBoolean(CLONE_TAG, true);
        return clone;
    }
}
