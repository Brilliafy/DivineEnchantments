package com.divineenchantments.compat;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.entity.player.EntityPlayer;

public class SimpleDifficultyCompat {

    public static void applyThermalFoil(EntityPlayer player, int foilPieces) {
        try {
            ITemperatureCapability cap = SDCapabilities.getTemperatureData(player);
            if (cap != null) {
                int curTemp = cap.getTemperatureLevel();
                int savedTemp = player.getEntityData().hasKey("DE_FoilTemp") ? player.getEntityData().getInteger("DE_FoilTemp") : curTemp;
                if (curTemp != savedTemp) {
                    float bufferChance = foilPieces * 0.20F; // 20% to 80%
                    if (player.world.rand.nextFloat() < bufferChance) {
                        cap.setTemperatureLevel(savedTemp); // Dampens rapid swing!
                    } else {
                        player.getEntityData().setInteger("DE_FoilTemp", curTemp);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void pauseThirst(EntityPlayer player) {
        try {
            IThirstCapability thirst = SDCapabilities.getThirstData(player);
            if (thirst != null) {
                thirst.setThirstExhaustion(0.0F);
                if (!player.getEntityData().hasKey("DE_SlumberThirst")) {
                    player.getEntityData().setInteger("DE_SlumberThirst", thirst.getThirstLevel());
                } else {
                    int saved = player.getEntityData().getInteger("DE_SlumberThirst");
                    if (thirst.getThirstLevel() < saved) {
                        thirst.setThirstLevel(saved);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void clearThirstPause(EntityPlayer player) {
        player.getEntityData().removeTag("DE_SlumberThirst");
    }
}
