package com.divineenchantments.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCarnage implements IMessage {

    private int stacks;
    private int level;
    private boolean clear;

    public PacketCarnage() {
    }

    public PacketCarnage(int stacks, int level, boolean clear) {
        this.stacks = stacks;
        this.level = level;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stacks = buf.readInt();
        this.level = buf.readInt();
        this.clear = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.stacks);
        buf.writeInt(this.level);
        buf.writeBoolean(this.clear);
    }

    public static class ClientHandler implements IMessageHandler<PacketCarnage, IMessage> {
        @Override
        public IMessage onMessage(PacketCarnage message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                net.minecraft.entity.player.EntityPlayer player =
                        net.minecraft.client.Minecraft.getMinecraft().player;
                if (player == null) {
                    return;
                }
                NBTTagCompound data = player.getEntityData();
                if (message.clear) {
                    data.setInteger("DE_CarnageSyncStacks", 0);
                    data.setInteger("DE_CarnageSyncLevel", 0);
                } else {
                    data.setInteger("DE_CarnageSyncStacks", message.stacks);
                    data.setInteger("DE_CarnageSyncLevel", message.level);
                }
            });
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketCarnage, IMessage> {
        @Override
        public IMessage onMessage(PacketCarnage message, MessageContext ctx) {
            return null;
        }
    }
}
