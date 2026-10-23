package com.divineenchantments.client;

import com.divineenchantments.entity.EntityKratosHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderKratosHook extends Render<EntityKratosHook> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/particles.png");

    public RenderKratosHook(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityKratosHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer owner = entity.getOwnerEntity();
        if (owner != null) {
            this.drawLine(owner, x, y, z, partialTicks);
        }
        this.drawBobber(x, y + 0.08D, z);
    }

    private void drawLine(EntityPlayer owner, double x, double y, double z, float partialTicks) {
        double ox = owner.lastTickPosX + (owner.posX - owner.lastTickPosX) * partialTicks - this.renderManager.viewerPosX;
        double oy = owner.lastTickPosY + (owner.posY - owner.lastTickPosY) * partialTicks
                + owner.getEyeHeight() - 0.25D - this.renderManager.viewerPosY;
        double oz = owner.lastTickPosZ + (owner.posZ - owner.lastTickPosZ) * partialTicks - this.renderManager.viewerPosZ;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.glLineWidth(2.0F);
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 16; i++) {
            double t = i / 16.0D;
            buffer.pos(ox + (x - ox) * t, oy + (y - oy) * t, oz + (z - oz) * t)
                    .color(0.55F, 0.55F, 0.55F, 1.0F).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private void drawBobber(double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX,
                1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.30F, 0.30F, 0.30F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityKratosHook entity) {
        return TEXTURE;
    }
}
