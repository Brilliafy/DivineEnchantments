package com.divineenchantments.network;

import com.divineenchantments.client.EnlargementClientData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnlargement implements IMessage {

    private int entityId;
    private int amplifier;
    private long expiryTime;
    private boolean clear;

    public PacketEnlargement() {
    }

    public PacketEnlargement(int entityId, int amplifier, long expiryTime, boolean clear) {
        this.entityId = entityId;
        this.amplifier = amplifier;
        this.expiryTime = expiryTime;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.amplifier = buf.readInt();
        this.expiryTime = buf.readLong();
        this.clear = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.amplifier);
        buf.writeLong(this.expiryTime);
        buf.writeBoolean(this.clear);
    }

    public static class ClientHandler implements IMessageHandler<PacketEnlargement, IMessage> {
        @Override
        public IMessage onMessage(PacketEnlargement message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() ->
                    EnlargementClientData.apply(message.entityId, message.amplifier, message.expiryTime, message.clear));
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
