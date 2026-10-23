package com.divineenchantments.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class DivineLateMixinLoader implements ILateMixinLoader {

    private static final String RANGE_FIX_CONFIG = "mixins.divineenchantments.bettersurvival.json";

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList(RANGE_FIX_CONFIG);
    }

    @Override
    public boolean shouldMixinConfigQueue(zone.rong.mixinbooter.Context context) {
        return Loader.isModLoaded("mujmajnkraftsbettersurvival");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return Loader.isModLoaded("mujmajnkraftsbettersurvival");
    }
}
