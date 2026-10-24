package com.divineenchantments.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "divineenchantments", value = Side.CLIENT)
public class OreGlowRenderer {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        List<BlockPos> blocks = OreGlowClient.getActive();
        if (blocks.isEmpty() || Minecraft.getMinecraft().world == null) {
            return;
        }
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        if (view == null) {
            return;
        }

        double camX = view.lastTickPosX + (view.posX - view.lastTickPosX) * event.getPartialTicks();
        double camY = view.lastTickPosY + (view.posY - view.lastTickPosY) * event.getPartialTicks();
        double camZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * event.getPartialTicks();
        float pulse = 0.55F + 0.35F * (float) Math.sin((Minecraft.getMinecraft().world.getTotalWorldTime() + event.getPartialTicks()) * 0.18D);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-camX, -camY, -camZ);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.glLineWidth(2.0F);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (BlockPos pos : blocks) {
            addBox(buffer, pos.getX() - 0.003D, pos.getY() - 0.003D, pos.getZ() - 0.003D,
                    pos.getX() + 1.003D, pos.getY() + 1.003D, pos.getZ() + 1.003D,
                    0.35F, 0.95F, 1.0F, pulse);
        }
        Tessellator.getInstance().draw();

        GlStateManager.glLineWidth(1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static void addBox(BufferBuilder buffer, double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               float r, float g, float b, float a) {
        line(buffer, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buffer, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buffer, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buffer, x1, y1, z2, x1, y1, z1, r, g, b, a);

        line(buffer, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buffer, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buffer, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buffer, x1, y2, z2, x1, y2, z1, r, g, b, a);

        line(buffer, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buffer, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buffer, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buffer, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void line(BufferBuilder buffer, double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a) {
        buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, a).endVertex();
    }
}
