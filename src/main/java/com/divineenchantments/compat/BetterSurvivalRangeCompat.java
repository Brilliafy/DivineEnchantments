package com.divineenchantments.compat;

import com.divineenchantments.DivineEnchantments;
import com.divineenchantments.config.DivineConfig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Better Survival's Range enchantment multiplies arrow velocity, and vanilla arrow damage is
 * proportional to velocity. This stores the exact velocity multiplier applied to each arrow so
 * the damage can be divided back out later (arrows still fly faster/further, but hit no harder
 * than they would without Range).
 */
@Mod.EventBusSubscriber(modid = DivineEnchantments.MODID)
public final class BetterSurvivalRangeCompat {

    private static final Map<EntityArrow, double[]> RANGE_DATA =
            Collections.synchronizedMap(new WeakHashMap<EntityArrow, double[]>());

    private BetterSurvivalRangeCompat() {
    }

    public static void beforeRangeBoost(EntityArrow arrow) {
        double[] data = RANGE_DATA.get(arrow);
        if (data == null) {
            data = new double[]{0.0D, 1.0D};
            RANGE_DATA.put(arrow, data);
        }
        data[0] = speed(arrow);
    }

    public static void afterRangeBoost(EntityArrow arrow) {
        double[] data = RANGE_DATA.get(arrow);
        if (data == null) {
            return;
        }
        double baseSpeed = data[0];
        if (baseSpeed > 1.0E-6D) {
            data[1] *= speed(arrow) / baseSpeed;
        }
    }

    public static double getDamageFactor(EntityArrow arrow) {
        double[] data = RANGE_DATA.get(arrow);
        if (data == null || data[1] <= 0.0D || Math.abs(data[1] - 1.0D) < 1.0E-6D) {
            return 1.0D;
        }
        return data[1];
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!DivineConfig.fixRangeEnchantmentDamage) {
            return;
        }

        DamageSource source = event.getSource();
        if (source == null || !(source.getImmediateSource() instanceof EntityArrow)) {
            return;
        }

        double factor = getDamageFactor((EntityArrow) source.getImmediateSource());
        if (factor == 1.0D) {
            return;
        }

        event.setAmount((float) (event.getAmount() / factor));
    }

    private static double speed(EntityArrow arrow) {
        return Math.sqrt(arrow.motionX * arrow.motionX
                + arrow.motionY * arrow.motionY
                + arrow.motionZ * arrow.motionZ);
    }
}
