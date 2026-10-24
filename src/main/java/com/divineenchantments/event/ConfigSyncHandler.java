package com.divineenchantments.event;

import com.divineenchantments.network.ModNetwork;
import com.divineenchantments.network.PacketConfigSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class ConfigSyncHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player != null && !event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
            ModNetwork.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player != null && !event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
            ModNetwork.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) event.player);
        }
    }
}
