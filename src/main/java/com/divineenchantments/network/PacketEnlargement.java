package com.divineenchantments.network;

import com.divineenchantments.client.EnlargementClientData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnlargement implements IMessage {

    private int entityId;
    private float scaleFactor;
    private long expiryTime;
    private boolean clear;

    public PacketEnlargement() {
    }

    public PacketEnlargement(int entityId, float scaleFactor, long expiryTime, boolean clear) {
        this.entityId = entityId;
        this.scaleFactor = scaleFactor;
        this.expiryTime = expiryTime;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.scaleFactor = buf.readFloat();
        this.expiryTime = buf.readLong();
        this.clear = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.scaleFactor);
        buf.writeLong(this.expiryTime);
        buf.writeBoolean(this.clear);
    }

    public static class ClientHandler implements IMessageHandler<PacketEnlargement, IMessage> {
        @Override
        public IMessage onMessage(PacketEnlargement message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() ->
                    EnlargementClientData.apply(message.entityId, message.scaleFactor, message.expiryTime, message.clear));
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketEnlargement, IMessage> {
        @Override
        public IMessage onMessage(PacketEnlargement message, MessageContext ctx) {
            return null;
        }
    }
}
