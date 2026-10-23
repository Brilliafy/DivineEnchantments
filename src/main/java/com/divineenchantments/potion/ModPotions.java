package com.divineenchantments.potion;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class ModPotions {

    public static final PotionEnlargement ENLARGEMENT = new PotionEnlargement();

    @SubscribeEvent
    public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(ENLARGEMENT);
    }
}
