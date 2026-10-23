package com.divineenchantments.client;

import com.divineenchantments.entity.EntityKratosHook;
import com.divineenchantments.entity.EntityVoodooAlly;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSetup {

    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityVoodooAlly.class,
                manager -> new RenderZombie(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityKratosHook.class,
                manager -> new RenderKratosHook(manager));
    }
}
