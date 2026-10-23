package com.divineenchantments.event;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.init.ModEnchantments;
import com.divineenchantments.util.ItemCheckHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@Mod.EventBusSubscriber(modid = "divineenchantments")
public class ToolAndMiningEventHandler {

    // ------------------------------------------------------------------------
    // 1. Tempered Core: Eliminates extra durability penalty for wrong tool use
    // ------------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onAttackEntity(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) return;

        ItemStack held = player.getHeldItemMainhand();
        if (!held.isEmpty() && held.isItemStackDamageable()
                && ModEnchantments.getLevel(ModEnchantments.TEMPERED_CORE, held) > 0) {
            // If tool is being used to attack a mob (which normally incurs 2 durability penalty)
            if (held.getItem() instanceof ItemTool) {
                player.getEntityData().setInteger("DE_TemperedCorePreDmg", held.getItemDamage());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtPost(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (player.getEntityData().hasKey("DE_TemperedCorePreDmg")) {
                int pre = player.getEntityData().getInteger("DE_TemperedCorePreDmg");
                player.getEntityData().removeTag("DE_TemperedCorePreDmg");

                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.isItemStackDamageable()) {
                    int took = held.getItemDamage() - pre;
                    if (took >= 2) {
                        // Consumed 2 durability; refund 1 so it only consumed 1!
                        held.setItemDamage(held.getItemDamage() - 1);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // 2. Sober Chisel: -5% mining speed & 75% reduction in Geonach/Tremor spawns
    // ------------------------------------------------------------------------

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        ItemStack held = player.getHeldItemMainhand();
        if (!held.isEmpty() && ModEnchantments.getLevel(ModEnchantments.SOBER_CHISEL, held) > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.95F); // -5% speed
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.world.isRemote) return;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = event.getState();
        Block block = state.getBlock();
        ItemStack held = player.getHeldItemMainhand();

        if (held.isEmpty()) return;

        // Tempered Core for swords breaking non-foliage blocks
        if (held.getItem() instanceof ItemSword && ModEnchantments.getLevel(ModEnchantments.TEMPERED_CORE, held) > 0) {
            player.getEntityData().setInteger("DE_SwordBreakPreDmg", held.getItemDamage());
        }

        // Sober Chisel: track recent ore break position to intercept Geonachs and Tremors
        if (ModEnchantments.getLevel(ModEnchantments.SOBER_CHISEL, held) > 0) {
            player.getEntityData().setLong("DE_SoberTime", world.getTotalWorldTime());
            player.getEntityData().setLong("DE_SoberPos", pos.toLong());
        }

        // Shimmered: highlight adjacent identical ore blocks within 2 blocks
        if (ModEnchantments.getLevel(ModEnchantments.SHIMMERED, held) > 0) {
            if (isOreBlock(block, state)) {
                triggerShimmer(world, pos, block);
            }
        }

        // Dampening Strike: reduces sound / vibration radius by 70%
        if (ModEnchantments.getLevel(ModEnchantments.DAMPENING_STRIKE, held) > 0) {
            double radius = 16.0D;
            AxisAlignedBB box = new AxisAlignedBB(pos).grow(radius);
            List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, box, m -> m instanceof IMob);
            for (EntityLiving mob : mobs) {
                if (mob.getAttackTarget() == player) {
                    double dist = mob.getDistance(player);
                    if (dist > DivineConfig.dampeningStrikeAggroRadius) { // Reduced sound/vibration aggro radius
                        mob.setAttackTarget(null);
                        mob.setRevengeTarget(null);
                    }
                }
            }
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 4, 0.2D, 0.2D, 0.2D, 0.01D);
            }
        }

        // Quiet Chipping: reduces acoustic vibration range of mining blocks by 75%
        if (ModEnchantments.getLevel(ModEnchantments.QUIET_CHIPPING, held) > 0) {
            double radius = 16.0D;
            AxisAlignedBB box = new AxisAlignedBB(pos).grow(radius);
            List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, box, m -> m instanceof IMob);
            for (EntityLiving mob : mobs) {
                if (mob.getAttackTarget() == player) {
                    double dist = mob.getDistance(player);
                    if (dist > DivineConfig.quietChippingAggroRadius) { // Reduced sound/vibration aggro radius
                        mob.setAttackTarget(null);
                        mob.setRevengeTarget(null);
                        mob.getNavigator().clearPath();
                    }
                }
            }
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2, 0.1D, 0.1D, 0.1D, 0.01D);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getTarget() instanceof EntityPlayer)) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityLiving)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getTarget();
        EntityLiving mob = (EntityLiving) event.getEntityLiving();
        ItemStack held = player.getHeldItemMainhand();

        int dampening = ModEnchantments.getLevel(ModEnchantments.DAMPENING_STRIKE, held);
        int quiet = ModEnchantments.getLevel(ModEnchantments.QUIET_CHIPPING, held);
        if (dampening <= 0 && quiet <= 0) {
            return;
        }

        double radius = Double.MAX_VALUE;
        if (dampening > 0) {
            radius = Math.min(radius, DivineConfig.dampeningStrikeAggroRadius);
        }
        if (quiet > 0) {
            radius = Math.min(radius, DivineConfig.quietChippingAggroRadius);
        }

        if (mob.getDistance(player) > radius) {
            mob.setAttackTarget(null);
            mob.setRevengeTarget(null);
            mob.getNavigator().clearPath();
        }
    }

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getWorld().isRemote) return;

        ResourceLocation key = EntityList.getKey(event.getEntity());
        if (key != null) {
            String path = key.getPath().toLowerCase();
            if (path.contains("geonach") || path.contains("tremor")) {
                World world = (World) event.getWorld();
                // Check players within 12 blocks
                EntityPlayer nearest = world.getClosestPlayer(event.getX(), event.getY(), event.getZ(), 12.0D, false);
                if (nearest != null && nearest.getEntityData().hasKey("DE_SoberTime")) {
                    long time = nearest.getEntityData().getLong("DE_SoberTime");
                    if (world.getTotalWorldTime() - time <= 20L) { // Within 1s of break
                        if (nearest.world.rand.nextFloat() < 0.75F) { // 75% reduction
                            event.setResult(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // 3. Silt Sifter: 5% chance to yield foraging bits (flint, string, nuggets)
    // ------------------------------------------------------------------------

    @SubscribeEvent
    public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        if (player == null || player.world.isRemote) return;

        ItemStack held = player.getHeldItemMainhand();

        // Tempered Core sword durability check
        if (!held.isEmpty() && player.getEntityData().hasKey("DE_SwordBreakPreDmg")) {
            int pre = player.getEntityData().getInteger("DE_SwordBreakPreDmg");
            player.getEntityData().removeTag("DE_SwordBreakPreDmg");
            int took = held.getItemDamage() - pre;
            if (took >= 2) {
                held.setItemDamage(held.getItemDamage() - 1);
            }
        }

        // Silt Sifter
        if (!held.isEmpty() && (held.getItem() instanceof ItemSpade || ItemCheckHelper.isMeleeWeapon(held))
                && ModEnchantments.getLevel(ModEnchantments.SILT_SIFTER, held) > 0) {
            Block block = event.getState().getBlock();
            if (block == Blocks.GRAVEL || block == Blocks.DIRT || block == Blocks.GRASS
                    || block == Blocks.CLAY || block == Blocks.SAND || block == Blocks.SOUL_SAND) {
                if (player.world.rand.nextFloat() < 0.05F) { // 5% chance
                    float pick = player.world.rand.nextFloat();
                    ItemStack bonus;
                    if (pick < 0.35F) {
                        bonus = new ItemStack(Items.FLINT);
                    } else if (pick < 0.65F) {
                        bonus = new ItemStack(Items.STRING);
                    } else if (pick < 0.85F) {
                        bonus = new ItemStack(Items.CLAY_BALL);
                    } else {
                        bonus = new ItemStack(Items.IRON_NUGGET);
                    }
                    event.getDrops().add(bonus);

                    BlockPos pos = event.getPos();
                    player.world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.BLOCKS, 0.7F, 1.4F);
                    if (player.world instanceof WorldServer) {
                        ((WorldServer) player.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK,
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    private static boolean isOreBlock(Block block, IBlockState state) {
        if (block == null) return false;
        ResourceLocation reg = block.getRegistryName();
        if (reg != null && reg.getPath().toLowerCase().contains("ore")) {
            return true;
        }
        ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
        if (!stack.isEmpty()) {
            int[] oreIds = OreDictionary.getOreIDs(stack);
            for (int id : oreIds) {
                String oreName = OreDictionary.getOreName(id);
                if (oreName != null && oreName.startsWith("ore")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void triggerShimmer(World world, BlockPos origin, Block targetBlock) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos p = origin.add(dx, dy, dz);
                    if (world.getBlockState(p).getBlock() == targetBlock) {
                        if (world instanceof WorldServer) {
                            WorldServer ws = (WorldServer) world;
                            ws.spawnParticle(EnumParticleTypes.TOTEM, p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D, 6, 0.35D, 0.35D, 0.35D, 0.05D);
                            ws.spawnParticle(EnumParticleTypes.END_ROD, p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D, 3, 0.25D, 0.25D, 0.25D, 0.02D);
                        }
                        world.playSound(null, p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D,
                                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.4F, 1.8F);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // 4. Salvage Line: Catching non-fish junk items converts them to bonus XP and materials
    // ------------------------------------------------------------------------

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world.isRemote) return;

        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || !ItemCheckHelper.isFishingRod(held) || ModEnchantments.getLevel(ModEnchantments.SALVAGE_LINE, held) == 0) {
            held = player.getHeldItemOffhand();
        }
        if (held.isEmpty() || !ItemCheckHelper.isFishingRod(held) || ModEnchantments.getLevel(ModEnchantments.SALVAGE_LINE, held) == 0) {
            return;
        }

        NonNullList<ItemStack> drops = event.getDrops();
        boolean salvagedAny = false;
        int totalBonusXp = 0;

        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (drop.isEmpty()) continue;

            net.minecraft.item.Item item = drop.getItem();
            // Don't convert fish
            if (item instanceof ItemFishFood) continue;

            ItemStack replacement = ItemStack.EMPTY;
            int xp = 0;

            if (item == Items.LEATHER_BOOTS || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_HELMET) {
                replacement = new ItemStack(Items.LEATHER, 2 + player.world.rand.nextInt(2));
                xp = 6;
            } else if (item == Items.BONE) {
                replacement = new ItemStack(Items.DYE, 3 + player.world.rand.nextInt(3), 15); // Bone meal
                xp = 4;
            } else if (item == Items.STRING) {
                replacement = new ItemStack(Blocks.WOOL, 1, player.world.rand.nextInt(16));
                xp = 4;
            } else if (item == Items.ROTTEN_FLESH) {
                replacement = new ItemStack(Items.LEATHER, 1);
                xp = 3;
            } else if (item == Items.BOWL || item == Items.STICK) {
                replacement = new ItemStack(Blocks.PLANKS, 1);
                xp = 2;
            } else if (item == Items.POTIONITEM || item == Items.GLASS_BOTTLE) {
                replacement = new ItemStack(Items.GLASS_BOTTLE, 1);
                xp = 3;
            } else if (item == net.minecraft.item.Item.getItemFromBlock(Blocks.TRIPWIRE_HOOK)) {
                replacement = new ItemStack(Items.IRON_NUGGET, 4);
                xp = 5;
            } else if (item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemArmor) {
                // Rusted / junk equipment
                String name = item.getClass().getName().toLowerCase();
                ResourceLocation reg = item.getRegistryName();
                String path = (reg != null) ? reg.getPath().toLowerCase() : "";
                if (path.contains("gold") || name.contains("gold")) {
                    replacement = new ItemStack(Items.GOLD_NUGGET, 4 + player.world.rand.nextInt(4));
                    xp = 10;
                } else if (path.contains("iron") || name.contains("iron")) {
                    replacement = new ItemStack(Items.IRON_NUGGET, 4 + player.world.rand.nextInt(4));
                    xp = 10;
                } else {
                    replacement = new ItemStack(Items.FLINT, 2);
                    xp = 5;
                }
            } else if (item == Items.BOW || item == Items.FISHING_ROD) {
                replacement = new ItemStack(Items.STRING, 2);
                xp = 5;
            }

            if (!replacement.isEmpty()) {
                drops.set(i, replacement);
                salvagedAny = true;
                totalBonusXp += xp;
            }
        }

        if (salvagedAny) {
            if (totalBonusXp > 0) {
                player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY + 0.5D, player.posZ, totalBonusXp));
            }
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.7F, 1.6F);
            if (player.world instanceof WorldServer) {
                WorldServer ws = (WorldServer) player.world;
                ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, player.posX, player.posY + 1.0D, player.posZ, 12, 0.3D, 0.3D, 0.3D, 0.05D);
            }
        }
    }
}
