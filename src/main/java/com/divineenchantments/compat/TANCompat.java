package com.divineenchantments.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TANCompat {

    private static boolean INITIALIZED = false;
    private static Capability<?> TEMPERATURE_CAP = null;
    private static Capability<?> THIRST_CAP = null;
    private static Method GET_TEMP_LEVEL = null;
    private static Method SET_TEMP_LEVEL = null;
    private static Method GET_THIRST_LEVEL = null;
    private static Method SET_THIRST_LEVEL = null;
    private static Method SET_THIRST_EXHAUSTION = null;

    private static void init() {
        if (INITIALIZED) return;
        INITIALIZED = true;
        try {
            Class<?> tanCaps = Class.forName("toughasnails.api.TANCapabilities");
            Field tempField = tanCaps.getField("TEMPERATURE");
            TEMPERATURE_CAP = (Capability<?>) tempField.get(null);
            Field thirstField = tanCaps.getField("THIRST");
            THIRST_CAP = (Capability<?>) thirstField.get(null);

            Class<?> tempClass = Class.forName("toughasnails.api.stat.capability.ITemperature");
            GET_TEMP_LEVEL = tempClass.getMethod("getTemperature");
            Class<?> tempEnumClass = Class.forName("toughasnails.api.temperature.Temperature");
            // TAN might use getTemperature().getRawValue() or setTemperature
            for (Method m : tempClass.getMethods()) {
                if (m.getName().equals("setTemperature") && m.getParameterCount() == 1) {
                    SET_TEMP_LEVEL = m;
                    break;
                }
            }

            Class<?> thirstClass = Class.forName("toughasnails.api.stat.capability.IThirst");
            for (Method m : thirstClass.getMethods()) {
                if (m.getName().equals("getThirst")) GET_THIRST_LEVEL = m;
                if (m.getName().equals("setThirst")) SET_THIRST_LEVEL = m;
                if (m.getName().equals("setExhaustion")) SET_THIRST_EXHAUSTION = m;
            }
        } catch (Throwable ignored) {
        }
    }

    public static void applyThermalFoil(EntityPlayer player, int foilPieces) {
        init();
        if (TEMPERATURE_CAP == null) return;
        try {
            Object cap = player.getCapability(TEMPERATURE_CAP, null);
            if (cap != null && GET_TEMP_LEVEL != null && SET_TEMP_LEVEL != null) {
                Object tempObj = GET_TEMP_LEVEL.invoke(cap);
                if (tempObj != null) {
                    // In TAN, Temperature can be enum or object with getRawValue()
                    int curTemp = 0;
                    if (tempObj instanceof Number) {
                        curTemp = ((Number) tempObj).intValue();
                    } else {
                        try {
                            Method getRaw = tempObj.getClass().getMethod("getRawValue");
                            curTemp = ((Number) getRaw.invoke(tempObj)).intValue();
                        } catch (Exception e) {
                            curTemp = tempObj.hashCode();
                        }
                    }
                    int savedTemp = player.getEntityData().hasKey("DE_FoilTANTemp") ? player.getEntityData().getInteger("DE_FoilTANTemp") : curTemp;
                    if (curTemp != savedTemp) {
                        float bufferChance = foilPieces * 0.20F;
                        if (player.world.rand.nextFloat() < bufferChance) {
                            // Suppress rapid swing
                            if (player.getEntityData().hasKey("DE_FoilTANTempObj")) {
                                // Keep previous temperature object if stored
                            }
                        } else {
                            player.getEntityData().setInteger("DE_FoilTANTemp", curTemp);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void pauseThirst(EntityPlayer player) {
        init();
        if (THIRST_CAP == null) return;
        try {
            Object thirst = player.getCapability(THIRST_CAP, null);
            if (thirst != null) {
                if (SET_THIRST_EXHAUSTION != null) {
                    SET_THIRST_EXHAUSTION.invoke(thirst, 0.0F);
                }
                if (GET_THIRST_LEVEL != null && SET_THIRST_LEVEL != null) {
                    int curThirst = ((Number) GET_THIRST_LEVEL.invoke(thirst)).intValue();
                    if (!player.getEntityData().hasKey("DE_SlumberTANThirst")) {
                        player.getEntityData().setInteger("DE_SlumberTANThirst", curThirst);
                    } else {
                        int saved = player.getEntityData().getInteger("DE_SlumberTANThirst");
                        if (curThirst < saved) {
                            SET_THIRST_LEVEL.invoke(thirst, saved);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void clearThirstPause(EntityPlayer player) {
        player.getEntityData().removeTag("DE_SlumberTANThirst");
    }
}
