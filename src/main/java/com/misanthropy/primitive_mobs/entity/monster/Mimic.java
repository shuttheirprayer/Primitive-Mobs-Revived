package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Mimic extends Monster {
    private static final EntityDataAccessor<Integer> CHEST_STATE =
            SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.INT);
    private static final int EXPLOSION_DELAY = 40;

    private boolean primed;
    private int explosionTimer;
    private float mouthOpen;
    private float mouthOpenOld;
    private float mouthSpeed = 1.0F;
    private float mouthSpeedOld;

    public Mimic(EntityType<? extends Mimic> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, false));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CHEST_STATE, Block.getId(Blocks.CHEST.defaultBlockState()));
    }

    public void setChestState(BlockState state) {
        entityData.set(CHEST_STATE, Block.getId(state));
    }

    public BlockState getChestState() {
        return Block.stateById(entityData.get(CHEST_STATE));
    }

    public void prime() {
        primed = true;
    }

    public float getMouthOpen(float partialTick) {
        return mouthOpenOld + (mouthOpen - mouthOpenOld) * partialTick;
    }

    public float getMouthSpeed(float partialTick) {
        return mouthSpeedOld + (mouthSpeed - mouthSpeedOld) * partialTick;
    }

    @Override
    public void aiStep() {
        Vec3 motion = getDeltaMovement();
        if (onGround() && (Math.abs(motion.x) > 0.05D || Math.abs(motion.z) > 0.05D)) {
            setDeltaMovement(motion.x, 0.4D, motion.z);
        } else if (horizontalCollision) {
            setDeltaMovement(motion.x, 0.5D, motion.z);
        }

        mouthOpenOld = mouthOpen;
        mouthSpeedOld = mouthSpeed;
        mouthSpeed = Math.max(0.2F, mouthSpeed * 0.9F);
        mouthOpen += mouthSpeed * 2.0F;

        if (primed) {
            tickExplosion();
        }
        super.aiStep();
    }

    private void tickExplosion() {
        setSpeed(0.0F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 1.0D, getZ(), 10, 0.5D, 0.5D, 0.5D, 0.0D);
        }
        if (explosionTimer == 0) {
            playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
        } else if (explosionTimer >= EXPLOSION_DELAY && !level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 2.0F, Level.ExplosionInteraction.MOB);
            discard();
        }
        explosionTimer++;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CHEST_OPEN;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHEST_CLOSE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ChestState", entityData.get(CHEST_STATE));
        tag.putBoolean("Primed", primed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ChestState")) {
            entityData.set(CHEST_STATE, tag.getInt("ChestState"));
        }
        primed = tag.getBoolean("Primed");
    }
}
