package com.divineenchantments.network;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetwork {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel("divineenchantments");

    public static void init(Side side) {
        if (side.isClient()) {
            INSTANCE.registerMessage(PacketBullseye.ClientHandler.class, PacketBullseye.class, 0, Side.CLIENT);
            INSTANCE.registerMessage(PacketEnlargement.ClientHandler.class, PacketEnlargement.class, 1, Side.CLIENT);
            INSTANCE.registerMessage(PacketCarnage.ClientHandler.class, PacketCarnage.class, 2, Side.CLIENT);
            INSTANCE.registerMessage(PacketConfigSync.ClientHandler.class, PacketConfigSync.class, 3, Side.CLIENT);
            INSTANCE.registerMessage(PacketOreGlow.ClientHandler.class, PacketOreGlow.class, 4, Side.CLIENT);
        } else {
            INSTANCE.registerMessage(PacketBullseye.ServerHandler.class, PacketBullseye.class, 0, Side.SERVER);
            INSTANCE.registerMessage(PacketEnlargement.ServerHandler.class, PacketEnlargement.class, 1, Side.SERVER);
            INSTANCE.registerMessage(PacketCarnage.ServerHandler.class, PacketCarnage.class, 2, Side.SERVER);
            INSTANCE.registerMessage(PacketConfigSync.ServerHandler.class, PacketConfigSync.class, 3, Side.SERVER);
            INSTANCE.registerMessage(PacketOreGlow.ServerHandler.class, PacketOreGlow.class, 4, Side.SERVER);
        }
    }

    public static void sendToAllTracking(IMessage message, Entity entity) {
        INSTANCE.sendToAllTracking(message, entity);
        if (entity instanceof net.minecraft.entity.player.EntityPlayerMP) {
            INSTANCE.sendTo(message, (net.minecraft.entity.player.EntityPlayerMP) entity);
        }
    }
}
