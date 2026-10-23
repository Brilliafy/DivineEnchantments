package com.divineenchantments.client;

import com.divineenchantments.bullseye.BullseyeManager;
import com.divineenchantments.bullseye.BullseyeTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = "divineenchantments")
public class BullseyeRenderer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("divineenchantments", "textures/particle/bullseye.png");

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) {
            return;
        }

        RenderManager rm = mc.getRenderManager();
        float partial = event.getPartialTicks();

        for (BullseyeTarget spot : new ArrayList<>(BullseyeManager.getAll())) {
            if (spot.isExpired(mc.world)) {
                BullseyeManager.clear(spot.entityId);
                continue;
            }

            Entity entity = mc.world.getEntityByID(spot.entityId);
            if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive()) {
                BullseyeManager.clear(spot.entityId);
                continue;
            }

            EntityLivingBase living = (EntityLivingBase) entity;
            double x = living.lastTickPosX + (living.posX - living.lastTickPosX) * partial + spot.offsetX;
            double y = living.lastTickPosY + (living.posY - living.lastTickPosY) * partial + spot.offsetY;
            double z = living.lastTickPosZ + (living.posZ - living.lastTickPosZ) * partial + spot.offsetZ;

            double dx = x - rm.viewerPosX;
            double dy = y - rm.viewerPosY;
            double dz = z - rm.viewerPosZ;

            float pulse = 1.0F + 0.08F * MathHelper.sin((mc.world.getTotalWorldTime() + partial) * 0.55F);
            float size = 0.21F * pulse;

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) dx, (float) dy, (float) dz);
            GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * rm.playerViewX, 1.0F, 0.0F, 0.0F);

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-size, -size, 0.0D).tex(0.0D, 1.0D).endVertex();
            buffer.pos(size, -size, 0.0D).tex(1.0D, 1.0D).endVertex();
            buffer.pos(size, size, 0.0D).tex(1.0D, 0.0D).endVertex();
            buffer.pos(-size, size, 0.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
