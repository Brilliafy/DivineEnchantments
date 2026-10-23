package com.divineenchantments.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCarnage implements IMessage {

    private int stacks;
    private int level;
    private double penalty;
    private double stackBonus;
    private boolean clear;

    public PacketCarnage() {
    }

    public PacketCarnage(int stacks, int level, double penalty, double stackBonus, boolean clear) {
        this.stacks = stacks;
        this.level = level;
        this.penalty = penalty;
        this.stackBonus = stackBonus;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stacks = buf.readInt();
        this.level = buf.readInt();
        this.penalty = buf.readDouble();
        this.stackBonus = buf.readDouble();
        this.clear = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.stacks);
        buf.writeInt(this.level);
        buf.writeDouble(this.penalty);
        buf.writeDouble(this.stackBonus);
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
                    data.setDouble("DE_CarnageSyncPenalty", 0.0D);
                    data.setDouble("DE_CarnageSyncBonus", 0.0D);
                } else {
                    data.setInteger("DE_CarnageSyncStacks", message.stacks);
                    data.setInteger("DE_CarnageSyncLevel", message.level);
                    data.setDouble("DE_CarnageSyncPenalty", message.penalty);
                    data.setDouble("DE_CarnageSyncBonus", message.stackBonus);
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
