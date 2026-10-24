package com.divineenchantments.client;

import com.divineenchantments.init.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "divineenchantments", value = Side.CLIENT)
public class QuietChippingClientHandler {

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }
        ItemStack held = mc.player.getHeldItemMainhand();
        if (held.isEmpty() || ModEnchantments.getLevel(ModEnchantments.QUIET_CHIPPING, held) <= 0) {
            return;
        }
        String name = event.getName();
        if (name == null) {
            return;
        }
        String lower = name.toLowerCase();
        if (!(lower.contains(".break") || lower.contains("dig.") || lower.startsWith("dig."))) {
            return;
        }
        ISound sound = event.getSound();
        if (sound == null) {
            return;
        }
        event.setResultSound(new PositionedSoundRecord(
                sound.getSoundLocation(),
                sound.getCategory(),
                sound.getVolume() * 0.25F,
                sound.getPitch(),
                sound.canRepeat(),
                sound.getRepeatDelay(),
                sound.getAttenuationType(),
                sound.getXPosF(),
                sound.getYPosF(),
                sound.getZPosF()));
    }
}
