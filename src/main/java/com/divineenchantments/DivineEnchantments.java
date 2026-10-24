package com.divineenchantments;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.util.ResourceLocation;
import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.entity.EntityKratosHook;
import com.divineenchantments.entity.EntityVoodooAlly;
import com.divineenchantments.network.ModNetwork;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = DivineEnchantments.MODID,
        name = DivineEnchantments.NAME,
        version = DivineEnchantments.VERSION,
        dependencies = "after:spartanweaponry;after:spartanshields;after:lycanitesmobs;after:somanyenchantments;after:potioncore;after:simpledifficulty;after:toughasnails;after:itemphysic;after:jei;after:mujmajnkraftsbettersurvival;after:collisiondamage;after:dsurround;after:infernalmobs"
)
public class DivineEnchantments {

    public static final String MODID = "divineenchantments";
    public static final String NAME = "Divine Enchantments";
    public static final String VERSION = "1.0.5";

    @Mod.Instance(MODID)
    public static DivineEnchantments instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Initializing " + NAME + " v" + VERSION + "...");

        DivineConfig.load(event.getSuggestedConfigurationFile());

        if (Loader.isModLoaded("lycanitesmobs")) {
            logger.info("Lycanites Mobs detected! Ally enchantment enabled.");
        } else {
            logger.info("Lycanites Mobs not detected. Ally enchantment will remain disabled.");
        }

        if (Loader.isModLoaded("spartanweaponry")) {
            logger.info("Spartan Weaponry detected! All melee weaponry supported.");
        }

        ModNetwork.init(event.getSide());

        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "voodoo_ally"),
                EntityVoodooAlly.class, "voodoo_ally", 0, this, 80, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "kratos_hook"),
                EntityKratosHook.class, "kratos_hook", 1, this, 64, 1, true);

        if (event.getSide() == Side.CLIENT) {
            com.divineenchantments.client.ClientSetup.registerRenderers();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info(NAME + " initialized successfully.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
