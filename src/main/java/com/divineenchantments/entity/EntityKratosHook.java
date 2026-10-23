package com.divineenchantments.entity;

import com.divineenchantments.config.DivineConfig;
import com.divineenchantments.event.KratosBladesEventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.UUID;

public class EntityKratosHook extends Entity {

    private static final DataParameter<Integer> OWNER_ID =
            EntityDataManager.createKey(EntityKratosHook.class, DataSerializers.VARINT);

    private UUID ownerUUID;
    private int bladeLevel = 1;

    public EntityKratosHook(World world) {
        super(world);
        this.setSize(0.22F, 0.22F);
        this.ignoreFrustumCheck = true;
    }

    public EntityKratosHook(World world, EntityPlayer owner, int level) {
        this(world);
        this.ownerUUID = owner.getUniqueID();
        this.bladeLevel = Math.max(1, level);
        this.getDataManager().set(OWNER_ID, owner.getEntityId());
        this.setPosition(owner.posX, owner.posY + owner.getEyeHeight() - 0.1D, owner.posZ);

        Vec3d look = owner.getLookVec();
        this.motionX = look.x * DivineConfig.kratosHookSpeed;
        this.motionY = look.y * DivineConfig.kratosHookSpeed;
        this.motionZ = look.z * DivineConfig.kratosHookSpeed;
        this.velocityChanged = true;

        this.rotationYaw = owner.rotationYaw;
        this.rotationPitch = owner.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    protected void entityInit() {
        this.getDataManager().register(OWNER_ID, 0);
    }

    public EntityPlayer getOwnerEntity() {
        Entity entity = this.world.getEntityByID(this.getDataManager().get(OWNER_ID));
        return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
    }

    public boolean isOwner(EntityPlayer player) {
        return player != null && this.ownerUUID != null && this.ownerUUID.equals(player.getUniqueID());
    }

    public int getBladeLevel() {
        return this.bladeLevel;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        if (this.ticksExisted > DivineConfig.kratosHookLifetimeTicks) {
            this.setDead();
            return;
        }

        EntityPlayer owner = this.getOwnerEntity();
        if (!this.world.isRemote && owner == null) {
            this.setDead();
            return;
        }

        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d end = start.add(this.motionX, this.motionY, this.motionZ);

        RayTraceResult blockHit = this.world.rayTraceBlocks(start, end, false, true, false);
        Vec3d traceEnd = blockHit != null ? blockHit.hitVec : end;
        EntityLivingBase target = this.findTarget(start, traceEnd, owner);

        if (target != null) {
            if (!this.world.isRemote) {
                this.pullTarget(target, owner);
                this.damageBlade(owner);
            }
            this.spawnImpactEffects();
            this.setDead();
            return;
        }

        if (blockHit != null) {
            this.spawnImpactEffects();
            this.setDead();
            return;
        }

        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * (180.0D / Math.PI));
        this.rotationPitch = (float) (Math.atan2(this.motionY,
                Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)) * (180.0D / Math.PI));

        this.motionY -= 0.035D;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99D;
        this.motionY *= 0.99D;
        this.motionZ *= 0.99D;

        if (this.world.isRemote && this.ticksExisted % 2 == 0) {
            this.world.spawnParticle(EnumParticleTypes.END_ROD,
                    this.posX, this.posY, this.posZ, 0.02D, 0.02D, 0.02D, 0);
        }
    }

    private EntityLivingBase findTarget(Vec3d start, Vec3d end, EntityPlayer owner) {
        AxisAlignedBB search = new AxisAlignedBB(
                Math.min(start.x, end.x) - 0.35D, Math.min(start.y, end.y) - 0.35D, Math.min(start.z, end.z) - 0.35D,
                Math.max(start.x, end.x) + 0.35D, Math.max(start.y, end.y) + 0.35D, Math.max(start.z, end.z) + 0.35D);

        List<EntityLivingBase> candidates = this.world.getEntitiesWithinAABB(EntityLivingBase.class, search,
                entity -> entity != null && entity.isEntityAlive() && entity != owner && !this.isFriendlyAllied(entity, owner));

        EntityLivingBase nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (EntityLivingBase candidate : candidates) {
            RayTraceResult intercept = candidate.getEntityBoundingBox().grow(0.30D).calculateIntercept(start, end);
            if (intercept != null) {
                double distance = start.squareDistanceTo(intercept.hitVec);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = candidate;
                }
            }
        }
        return nearest;
    }

    private boolean isFriendlyAllied(EntityLivingBase entity, EntityPlayer owner) {
        if (entity instanceof EntityVoodooAlly) {
            EntityVoodooAlly ally = (EntityVoodooAlly) entity;
            return ally.isOwner(owner);
        }
        return false;
    }

    private void pullTarget(EntityLivingBase target, EntityPlayer owner) {
        if (owner == null) {
            return;
        }

        double dx = owner.posX - target.posX;
        double dy = (owner.posY + owner.getEyeHeight() * 0.65D) - (target.posY + target.height * 0.5D);
        double dz = owner.posZ - target.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.01D) {
            return;
        }

        double power = DivineConfig.kratosPullPower;
        target.motionX += (dx / distance) * power;
        target.motionY += 0.42D + Math.max(0.0D, dy / distance) * 0.35D;
        target.motionZ += (dz / distance) * power;
        target.velocityChanged = true;

        if (target instanceof EntityPlayerMP) {
            ((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
        }
    }

    private void damageBlade(EntityPlayer owner) {
        if (owner == null || owner.capabilities.isCreativeMode) {
            return;
        }

        ItemStack stack = owner.getHeldItemMainhand();
        if (!KratosBladesEventHandler.isKratosOriginal(stack)) {
            stack = owner.getHeldItemOffhand();
        }
        if (KratosBladesEventHandler.isKratosOriginal(stack) && stack.isItemStackDamageable()) {
            stack.damageItem(DivineConfig.kratosDurabilityCost, owner);
        }
    }

    private void spawnImpactEffects() {
        if (this.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) this.world;
            ws.spawnParticle(EnumParticleTypes.CRIT,
                    this.posX, this.posY, this.posZ, 14, 0.2D, 0.2D, 0.2D, 0.1D);
            ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    this.posX, this.posY, this.posZ, 6, 0.15D, 0.15D, 0.15D, 0.03D);
        }
        if (!this.world.isRemote) {
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    net.minecraft.init.SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.PLAYERS, 1.0F, 1.2F);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("BladeLevel", this.bladeLevel);
        if (this.ownerUUID != null) {
            compound.setString("OwnerUUID", this.ownerUUID.toString());
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.bladeLevel = Math.max(1, compound.getInteger("BladeLevel"));
        if (compound.hasKey("OwnerUUID")) {
            try {
                this.ownerUUID = UUID.fromString(compound.getString("OwnerUUID"));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
