package com.divineenchantments.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class DivineLateMixinLoader implements ILateMixinLoader {

    private static final String RANGE_FIX_CONFIG = "mixins.divineenchantments.bettersurvival.json";
    private static final String SIMPLEDIFFICULTY_CONFIG = "mixins.divineenchantments.simpledifficulty.json";
    private static final String LONGDRAW_CONFIG = "mixins.divineenchantments.longdraw.json";

    @Override
    public List<String> getMixinConfigs() {
        return java.util.Arrays.asList(RANGE_FIX_CONFIG, SIMPLEDIFFICULTY_CONFIG, LONGDRAW_CONFIG);
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.contains("bettersurvival")) {
            return Loader.isModLoaded("mujmajnkraftsbettersurvival");
        }
        if (mixinConfig.contains("simpledifficulty")) {
            return Loader.isModLoaded("simpledifficulty");
        }
        return true;
    }
}
