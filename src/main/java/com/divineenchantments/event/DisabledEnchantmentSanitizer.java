package com.divineenchantments.event;

import com.divineenchantments.config.DivineConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class DisabledEnchantmentSanitizer {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        if (event.player.world.getTotalWorldTime() % 20L != 0L) {
            return;
        }
        sanitizePlayer(event.player);
        sanitizeContainer(event.player.openContainer);
    }

    @SubscribeEvent
    public static void onContainerOpened(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        sanitizePlayer(player);
        sanitizeContainer(event.getContainer());
    }

    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        sanitizeContainer(event.getContainer());
        sanitizePlayer(player);
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            ItemStack replacement = sanitize(item.getItem());
            if (replacement != item.getItem()) {
                item.setItem(replacement);
            }
        } else if (entity instanceof EntityItemFrame) {
            EntityItemFrame frame = (EntityItemFrame) entity;
            ItemStack replacement = sanitize(frame.getDisplayedItem());
            if (replacement != frame.getDisplayedItem()) {
                frame.setDisplayedItem(replacement);
            }
        } else if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
            EntityLivingBase living = (EntityLivingBase) entity;
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                ItemStack stack = living.getItemStackFromSlot(slot);
                ItemStack replacement = sanitize(stack);
                if (replacement != stack) {
                    living.setItemStackToSlot(slot, replacement);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }
        for (EntityItem item : event.getDrops()) {
            ItemStack replacement = sanitize(item.getItem());
            if (replacement != item.getItem()) {
                item.setItem(replacement);
            }
        }
    }

    @SubscribeEvent
    public static void onMerchantTradeOffers(MerchantTradeOffersEvent event) {
        if (event.getPlayer() == null || event.getPlayer().world.isRemote) {
            return;
        }

        MerchantRecipeList offers = event.getList();
        if (offers == null || offers.isEmpty()) {
            return;
        }
        Iterator<MerchantRecipe> iterator = offers.iterator();
        while (iterator.hasNext()) {
            MerchantRecipe recipe = iterator.next();
            if (recipe == null) {
                continue;
            }
            ItemStack sell = recipe.getItemToSell();
            if (sell.isEmpty()) {
                continue;
            }

            if (sell.getItem() == Items.ENCHANTED_BOOK) {
                ItemStack probe = sanitize(sell.copy());
                if (probe.getItem() == Items.BOOK) {
                    iterator.remove();
                    continue;
                }
            }
            sanitize(sell);
        }
    }

    public static void sanitizePlayer(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            ItemStack replacement = sanitize(stack);
            if (replacement != stack) {
                player.inventory.setInventorySlotContents(i, replacement);
            }
        }

        ItemStack cursor = player.inventory.getItemStack();
        ItemStack cursorReplacement = sanitize(cursor);
        if (cursorReplacement != cursor) {
            player.inventory.setItemStack(cursorReplacement);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                ((net.minecraft.entity.player.EntityPlayerMP) player).connection.sendPacket(
                        new SPacketSetSlot(-1, -1, cursorReplacement));
            }
        }
    }

    public static void sanitizeContainer(Container container) {
        if (container == null) {
            return;
        }
        for (Slot slot : container.inventorySlots) {
            if (slot == null) {
                continue;
            }
            ItemStack stack = slot.getStack();
            ItemStack replacement = sanitize(stack);
            if (replacement != stack) {
                slot.putStack(replacement);
            }
        }
    }

    /**
     * Strips disabled Divine Enchantments from a stack. Returns the same stack unless an
     * enchanted book has lost every stored enchantment, in which case a vanilla book is returned.
     */
    public static ItemStack sanitize(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return stack;
        }

        NBTTagCompound tag = stack.getTagCompound();
        boolean book = stack.getItem() == Items.ENCHANTED_BOOK;
        if (tag.hasKey("ench", 9)) {
            NBTTagList ench = tag.getTagList("ench", 10);
            boolean stripped = stripDisabled(ench);
            if (stripped && ench.tagCount() == 0) {
                tag.removeTag("ench");
            }
        }

        if (tag.hasKey("StoredEnchantments", 9)) {
            NBTTagList stored = tag.getTagList("StoredEnchantments", 10);
            boolean stripped = stripDisabled(stored);
            if (stripped && stored.tagCount() == 0) {
                tag.removeTag("StoredEnchantments");
            }
        }

        if (book) {
            boolean hasStored = tag.hasKey("StoredEnchantments", 9)
                    && tag.getTagList("StoredEnchantments", 10).tagCount() > 0;
            boolean hasEnch = tag.hasKey("ench", 9)
                    && tag.getTagList("ench", 10).tagCount() > 0;
            if (!hasStored && !hasEnch) {
                return new ItemStack(Items.BOOK);
            }
        }

        return stack;
    }

    private static boolean stripDisabled(NBTTagList list) {
        boolean changed = false;
        for (int i = list.tagCount() - 1; i >= 0; i--) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            Enchantment enchantment = Enchantment.getEnchantmentByID(entry.getShort("id"));
            if (DivineConfig.isDisabled(enchantment)) {
                list.removeTag(i);
                changed = true;
            }
        }
        return changed;
    }
}
