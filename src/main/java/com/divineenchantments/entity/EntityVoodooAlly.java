package com.divineenchantments.entity;

import com.divineenchantments.config.DivineConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.UUID;

public class EntityVoodooAlly extends EntityZombie {

    private static final String TAG_OWNER = "DE_VoodooOwner";
    private static final String TAG_LEVEL = "DE_VoodooLevel";
    private static final String TEAM_NAME = "de_voodoo";

    private static final DataParameter<String> OWNER_NAME =
            EntityDataManager.createKey(EntityVoodooAlly.class, DataSerializers.STRING);

    private UUID ownerUUID;
    private int allyLevel = 1;

    public EntityVoodooAlly(World world) {
        super(world);
        this.setCanPickUpLoot(false);
        this.enablePersistence();
        this.setArmsRaised(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(OWNER_NAME, "");
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true));
        this.tasks.addTask(3, new AIFollowOwner(this));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 0.9D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
                10, true, true, this::isHostileTarget));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D * DivineConfig.voodooSpeedMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty,
                                                                net.minecraft.entity.IEntityLivingData livingdata) {
        this.setCanPickUpLoot(false);
        this.enablePersistence();
        return livingdata;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    protected boolean shouldBurnInDay() {
        return false;
    }

    public void setOwner(EntityPlayer owner, int level) {
        this.ownerUUID = owner.getUniqueID();
        this.allyLevel = Math.max(1, level);
        this.getDataManager().set(OWNER_NAME, owner.getName());
        this.setCustomNameTag(owner.getName() + "'s Ally");
        this.setAlwaysRenderNameTag(true);
        this.setCanPickUpLoot(false);
        this.enablePersistence();
        this.applyAllyStats();
        this.joinRedTeam();
    }

    public EntityPlayer getOwner() {
        if (this.ownerUUID == null) {
            return null;
        }
        if (this.world.isRemote) {
            return this.world.getPlayerEntityByUUID(this.ownerUUID);
        }
        if (this.world.getMinecraftServer() != null) {
            return this.world.getMinecraftServer().getPlayerList().getPlayerByUUID(this.ownerUUID);
        }
        return this.world.getPlayerEntityByUUID(this.ownerUUID);
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public int getAllyLevel() {
        return this.allyLevel;
    }

    public boolean isOwner(Entity entity) {
        return entity instanceof EntityPlayer && this.ownerUUID != null
                && this.ownerUUID.equals(((EntityPlayer) entity).getUniqueID());
    }

    private void applyAllyStats() {
        double health = Math.max(1.0D, this.allyLevel * DivineConfig.voodooHealthPerLevel);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        this.setHealth((float) health);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(DivineConfig.voodooBaseDamage + this.allyLevel * DivineConfig.voodooDamagePerLevel);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D * DivineConfig.voodooSpeedMultiplier);

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            this.setItemStackToSlot(slot, ItemStack.EMPTY);
        }

        if (!this.isPotionActive(MobEffects.GLOWING)) {
            this.addPotionEffect(new PotionEffect(MobEffects.GLOWING, Integer.MAX_VALUE, 0, false, false));
        }
    }

    private void joinRedTeam() {
        if (this.world.isRemote) {
            return;
        }
        Scoreboard scoreboard = this.world.getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.createTeam(TEAM_NAME);
            team.setColor(TextFormatting.RED);
            team.setPrefix(TextFormatting.RED.toString());
            team.setNameTagVisibility(Team.EnumVisible.ALWAYS);
            team.setSeeFriendlyInvisiblesEnabled(true);
            team.setAllowFriendlyFire(false);
        }
        scoreboard.addPlayerToTeam(this.getCachedUniqueIdString(), TEAM_NAME);
    }

    private void leaveTeam() {
        if (this.world.isRemote) {
            return;
        }
        Scoreboard scoreboard = this.world.getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(TEAM_NAME);
        if (team != null) {
            scoreboard.removePlayerFromTeam(this.getCachedUniqueIdString(), team);
        }
    }

    private boolean isHostileTarget(EntityLivingBase target) {
        if (target == null || target == this || !target.isEntityAlive()) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            return false;
        }
        if (target instanceof EntityVoodooAlly) {
            return false;
        }
        if (target instanceof EntityTameable) {
            EntityLivingBase tameOwner = ((EntityTameable) target).getOwner();
            if (tameOwner != null && this.isOwner(tameOwner)) {
                return false;
            }
        }
        EntityPlayer owner = this.getOwner();
        if (owner != null) {
            if (target.getDistanceSq(owner) > 24.0D * 24.0D) {
                return false;
            }
            if (!owner.canEntityBeSeen(target)) {
                return false;
            }
        }
        return target instanceof EntityMob || target instanceof net.minecraft.entity.monster.IMob;
    }

    private boolean isFriendly(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (this.isOwner(entity)) {
            return true;
        }
        if (entity instanceof EntityVoodooAlly) {
            EntityVoodooAlly other = (EntityVoodooAlly) entity;
            return this.ownerUUID != null && this.ownerUUID.equals(other.ownerUUID);
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isFriendly(source.getTrueSource()) || this.isFriendly(source.getImmediateSource())) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            if (!this.isPotionActive(MobEffects.GLOWING)) {
                this.addPotionEffect(new PotionEffect(MobEffects.GLOWING, Integer.MAX_VALUE, 0, false, false));
            }

            if (this.ticksExisted > 40) {
                EntityPlayer owner = this.getOwner();
                if (owner == null || !owner.isEntityAlive()) {
                    this.vanish();
                }
            }
        }
    }

    public void vanish() {
        if (!this.world.isRemote) {
            this.leaveTeam();
            if (this.world instanceof WorldServer) {
                WorldServer world = (WorldServer) this.world;
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        this.posX, this.posY + this.height * 0.5D, this.posZ,
                        18, 0.35D, 0.4D, 0.35D, 0.02D);
            }
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.8F, 0.6F);
        }
        this.setDead();
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.leaveTeam();
        if (!this.world.isRemote && this.world instanceof WorldServer) {
            WorldServer world = (WorldServer) this.world;
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    this.posX, this.posY + this.height * 0.5D, this.posZ,
                    24, 0.4D, 0.5D, 0.4D, 0.03D);
            world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                    this.posX, this.posY + this.height * 0.5D, this.posZ,
                    16, 0.35D, 0.4D, 0.35D, 0.04D);
        }
    }

    public void orderAttack(EntityLivingBase target) {
        if (target == null || !target.isEntityAlive() || this.isFriendly(target)) {
            return;
        }
        EntityPlayer owner = this.getOwner();
        if (owner != null && (!owner.canEntityBeSeen(target) || target.getDistanceSq(owner) > 32.0D * 32.0D)) {
            return;
        }
        this.setAttackTarget(target);
        this.setRevengeTarget(target);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger(TAG_LEVEL, this.allyLevel);
        if (this.ownerUUID != null) {
            compound.setString(TAG_OWNER, this.ownerUUID.toString());
        }
        compound.setString("DE_VoodooOwnerName", this.getDataManager().get(OWNER_NAME));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.allyLevel = Math.max(1, compound.getInteger(TAG_LEVEL));
        if (compound.hasKey(TAG_OWNER)) {
            try {
                this.ownerUUID = UUID.fromString(compound.getString(TAG_OWNER));
            } catch (IllegalArgumentException ignored) {
            }
        }
        String ownerName = compound.getString("DE_VoodooOwnerName");
        if (!ownerName.isEmpty()) {
            this.getDataManager().set(OWNER_NAME, ownerName);
            this.setCustomNameTag(ownerName + "'s Ally");
            this.setAlwaysRenderNameTag(true);
        }
        this.setCanPickUpLoot(false);
        this.enablePersistence();
        this.applyAllyStats();
        this.joinRedTeam();
    }

    private class AIFollowOwner extends EntityAIBase {
        private final EntityVoodooAlly ally;
        private EntityPlayer owner;
        private int pathTicks;

        AIFollowOwner(EntityVoodooAlly ally) {
            this.ally = ally;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityPlayer player = this.ally.getOwner();
            if (player == null || !player.isEntityAlive() || player.world != this.ally.world) {
                return false;
            }
            if (this.ally.getAttackTarget() != null && this.ally.getAttackTarget().isEntityAlive()) {
                return false;
            }
            if (this.ally.getDistanceSq(player) < 16.0D) {
                return false;
            }
            this.owner = player;
            return true;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.owner != null && this.owner.isEntityAlive() && this.owner.world == this.ally.world
                    && this.ally.getAttackTarget() == null
                    && this.ally.getDistanceSq(this.owner) > 4.0D;
        }

        @Override
        public void startExecuting() {
            this.pathTicks = 0;
        }

        @Override
        public void updateTask() {
            if (this.owner == null) {
                return;
            }
            if (--this.pathTicks <= 0) {
                this.pathTicks = 10;
                this.ally.getNavigator().tryMoveToEntityLiving(this.owner, 1.15D);
            }

            if (this.ally.getDistanceSq(this.owner) > 24.0D * 24.0D) {
                double x = this.owner.posX + (this.ally.world.rand.nextDouble() - 0.5D) * 3.0D;
                double z = this.owner.posZ + (this.ally.world.rand.nextDouble() - 0.5D) * 3.0D;
                this.ally.setPositionAndUpdate(x, this.owner.posY, z);
                this.ally.getNavigator().tryMoveToEntityLiving(this.owner, 1.15D);
            }
        }

        @Override
        public void resetTask() {
            this.owner = null;
            this.ally.getNavigator().clearPath();
        }
    }

    public static List<EntityVoodooAlly> findAllies(World world, EntityPlayer owner, AxisAlignedBB box) {
        return world.getEntitiesWithinAABB(EntityVoodooAlly.class, box,
                ally -> ally != null && ally.isEntityAlive() && ally.isOwner(owner));
    }
}
