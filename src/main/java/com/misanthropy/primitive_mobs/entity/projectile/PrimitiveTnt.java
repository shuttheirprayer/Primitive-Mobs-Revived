package com.misanthropy.primitive_mobs.entity.projectile;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PrimitiveTnt extends Entity {
    private static final EntityDataAccessor<Integer> FUSE =
            SynchedEntityData.defineId(PrimitiveTnt.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> STRENGTH =
            SynchedEntityData.defineId(PrimitiveTnt.class, EntityDataSerializers.FLOAT);

    @Nullable
    private LivingEntity owner;

    public PrimitiveTnt(EntityType<? extends PrimitiveTnt> type, Level level) {
        super(type, level);
        blocksBuilding = true;
    }

    public PrimitiveTnt(Level level, double x, double y, double z, @Nullable LivingEntity owner, float strength, int fuse) {
        this(ModEntities.PRIMITIVE_TNT.get(), level);
        setPos(x, y, z);
        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        setDeltaMovement(-Math.sin(angle) * 0.02D, 0.2D, -Math.cos(angle) * 0.02D);
        xo = x;
        yo = y;
        zo = z;
        this.owner = owner;
        setFuse(fuse);
        setStrength(strength);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUSE, 80);
        entityData.define(STRENGTH, 1.5F);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return isAlive();
    }

    @Override
    public void tick() {
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.98D));
        if (onGround()) {
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(motion.x * 0.7D, motion.y * -0.5D, motion.z * 0.7D);
        }

        int fuse = getFuse() - 1;
        setFuse(fuse);
        if (fuse <= 0) {
            discard();
            if (!level().isClientSide) {
                explode();
            }
        } else {
            updateInWaterStateAndDoFluidPushing();
            if (level().isClientSide) {
                level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void explode() {
        boolean griefing = PrimitiveMobsConfig.CONFIG.festiveCreeperDestruction.get()
                && level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);
        level().explode(this, getX(), getY(0.0625D), getZ(), getStrength(),
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Fuse", (short) getFuse());
        tag.putFloat("Strength", getStrength());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFuse(tag.getShort("Fuse"));
        setStrength(tag.getFloat("Strength"));
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }

    public void setFuse(int fuse) {
        entityData.set(FUSE, fuse);
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    public void setStrength(float strength) {
        entityData.set(STRENGTH, strength);
    }

    public float getStrength() {
        return entityData.get(STRENGTH);
    }

    @Override
    protected float getEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return 0.15F;
    }
}
