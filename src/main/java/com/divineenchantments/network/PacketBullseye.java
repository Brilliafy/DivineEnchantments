package com.divineenchantments.network;

import com.divineenchantments.bullseye.BullseyeManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBullseye implements IMessage {

    private int entityId;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private long expiryTime;
    private boolean clear;

    public PacketBullseye() {
    }

    public PacketBullseye(int entityId, float offsetX, float offsetY, float offsetZ, long expiryTime, boolean clear) {
        this.entityId = entityId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.expiryTime = expiryTime;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.offsetX = buf.readFloat();
        this.offsetY = buf.readFloat();
        this.offsetZ = buf.readFloat();
        this.expiryTime = buf.readLong();
        this.clear = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.offsetX);
        buf.writeFloat(this.offsetY);
        buf.writeFloat(this.offsetZ);
        buf.writeLong(this.expiryTime);
        buf.writeBoolean(this.clear);
    }

    public static class ClientHandler implements IMessageHandler<PacketBullseye, IMessage> {
        @Override
        public IMessage onMessage(PacketBullseye message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() ->
                    BullseyeManager.applyNetworkUpdate(message.entityId, message.offsetX, message.offsetY,
                            message.offsetZ, message.expiryTime, message.clear));
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketBullseye, IMessage> {
        @Override
        public IMessage onMessage(PacketBullseye message, MessageContext ctx) {
            return null;
        }
    }
}
