package com.divineenchantments.network;

import com.divineenchantments.config.DivineConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSync implements IMessage {

    private byte[] payload = new byte[0];

    public PacketConfigSync() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.payload = new byte[buf.readableBytes()];
        buf.readBytes(this.payload);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBuf temp = Unpooled.buffer();
        DivineConfig.writeTo(temp);
        byte[] data = new byte[temp.readableBytes()];
        temp.readBytes(data);
        temp.release();
        buf.writeBytes(data);
    }

    public static class ClientHandler implements IMessageHandler<PacketConfigSync, IMessage> {
        @Override
        public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                ByteBuf buf = Unpooled.wrappedBuffer(message.payload);
                try {
                    DivineConfig.readFrom(buf);
                } finally {
                    buf.release();
                }
            });
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketConfigSync, IMessage> {
        @Override
        public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
            return null;
        }
    }
}
