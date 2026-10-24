package com.divineenchantments.network;

import com.divineenchantments.client.OreGlowClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOreGlow implements IMessage {

    private long[] positions = new long[0];
    private int durationTicks;

    public PacketOreGlow() {
    }

    public PacketOreGlow(java.util.List<BlockPos> blocks, int durationTicks) {
        this.positions = new long[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            this.positions[i] = blocks.get(i).toLong();
        }
        this.durationTicks = durationTicks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        this.positions = new long[count];
        for (int i = 0; i < count; i++) {
            this.positions[i] = buf.readLong();
        }
        this.durationTicks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.positions.length);
        for (long position : this.positions) {
            buf.writeLong(position);
        }
        buf.writeInt(this.durationTicks);
    }

    public static class ClientHandler implements IMessageHandler<PacketOreGlow, IMessage> {
        @Override
        public IMessage onMessage(PacketOreGlow message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(
                    () -> OreGlowClient.add(message.positions, message.durationTicks));
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketOreGlow, IMessage> {
        @Override
        public IMessage onMessage(PacketOreGlow message, MessageContext ctx) {
            return null;
        }
    }
}
