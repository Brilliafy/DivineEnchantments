package com.divineenchantments.client;

import com.divineenchantments.DivineEnchantments;
import com.divineenchantments.entity.EntityKratosHook;
import com.divineenchantments.entity.EntityVoodooAlly;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.LongdrawHelper;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = DivineEnchantments.MODID, value = Side.CLIENT)
public class ClientSetup {

    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityVoodooAlly.class,
                manager -> new RenderZombie(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityKratosHook.class,
                manager -> new RenderKratosHook(manager));
    }

    /**
     * Stretches the bow model pull animation to match Longdraw's longer draw time.
     * Vanilla divides the draw ticks by 20; Longdraw I needs 30 ticks for full power.
     */
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ResourceLocation pullKey = new ResourceLocation("pull");
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof ItemBow)) {
                continue;
            }
            final IItemPropertyGetter vanillaPull = item.getPropertyGetter(pullKey);
            item.addPropertyOverride(pullKey, (stack, world, entity) -> {
                if (entity == null || stack.isEmpty()) {
                    return 0.0F;
                }
                int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.LONGDRAW, stack);
                if (level <= 0) {
                    return vanillaPull != null ? vanillaPull.apply(stack, world, entity) : 0.0F;
                }
                if (!entity.isHandActive() || !(entity.getActiveItemStack().getItem() instanceof ItemBow)) {
                    return 0.0F;
                }
                return LongdrawHelper.drawProgress(stack.getMaxItemUseDuration(), entity.getItemInUseCount(), level);
            });
        }
    }
}
