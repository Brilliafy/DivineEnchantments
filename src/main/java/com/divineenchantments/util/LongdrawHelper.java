package com.divineenchantments.util;

import net.minecraft.util.math.MathHelper;

/**
 * Shared Longdraw draw-time math. Full draw is 20 ticks for a normal bow and
 * 10 extra ticks per Longdraw level, so Longdraw I reaches full power at 30 ticks.
 */
public final class LongdrawHelper {

    public static final float BASE_DRAW_TICKS = 20.0F;
    public static final float EXTRA_DRAW_TICKS_PER_LEVEL = 10.0F;

    private LongdrawHelper() {
    }

    public static float drawTicks(int level) {
        if (level <= 0) {
            return BASE_DRAW_TICKS;
        }
        return BASE_DRAW_TICKS + EXTRA_DRAW_TICKS_PER_LEVEL * level;
    }

    public static float drawProgress(int maxUseDuration, int useCount, int level) {
        return MathHelper.clamp((maxUseDuration - useCount) / drawTicks(level), 0.0F, 1.0F);
    }
}
