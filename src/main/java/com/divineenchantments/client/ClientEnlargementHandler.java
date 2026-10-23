package com.divineenchantments.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = "divineenchantments")
public class ClientEnlargementHandler {

    private static final Set<EntityLivingBase> SCALED =
            Collections.newSetFromMap(new IdentityHashMap<>());

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (event.isCanceled()) {
            return;
        }
        EntityLivingBase entity = event.getEntity();
        float factor = EnlargementClientData.getFactor(entity, entity.world.getTotalWorldTime());
        if (factor < 0.0F) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY(), event.getZ());
        GlStateManager.scale(factor, factor, factor);
        GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
        SCALED.add(entity);
    }

    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (SCALED.remove(event.getEntity())) {
            GlStateManager.popMatrix();
        }
    }
}
