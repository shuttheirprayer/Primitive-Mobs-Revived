package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;

public class Harpy extends Monster {
    private static final int MAX_CARRY_HEIGHT = 20;

    public Harpy(EntityType<? extends Harpy> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, true);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new LiftAttackGoal(this, 1.5D));
        goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        if (isVehicle()) {
            setDeltaMovement(getDeltaMovement().x, 0.25D, getDeltaMovement().z);
            if (!level().isClientSide && (!level().isEmptyBlock(blockPosition().above(2)) || heightAboveGround() >= MAX_CARRY_HEIGHT)) {
                ejectPassengers();
            }
        }
        super.tick();
    }

    private double heightAboveGround() {
        BlockPos.MutableBlockPos cursor = blockPosition().mutable();
        int startY = cursor.getY();
        for (int i = 0; i < 64; i++) {
            cursor.setY(startY - i);
            if (!level().isEmptyBlock(cursor)) {
                return getY() - cursor.getY();
            }
        }
        return MAX_CARRY_HEIGHT;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && isVehicle()) {
            ejectPassengers();
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        move.accept(passenger, getX(), getY() - passenger.getBbHeight(), getZ());
        if (passenger.isShiftKeyDown()) {
            passenger.setShiftKeyDown(false);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HARPY_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HARPY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HARPY_HURT.get();
    }

    @Override
    public float getVoicePitch() {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 0.8F;
    }

    public static boolean checkSpawnRules(EntityType<Harpy> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return pos.getY() >= 70 && level.canSeeSky(pos)
                && Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    private static class LiftAttackGoal extends MeleeAttackGoal {
        private final Harpy harpy;

        LiftAttackGoal(Harpy harpy, double speed) {
            super(harpy, speed, false);
            this.harpy = harpy;
        }

        @Override
        public boolean canUse() {
            return !harpy.isVehicle() && super.canUse();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distanceSqr) {
            if (distanceSqr > getAttackReachSqr(target) || !isTimeToAttack()) {
                return;
            }
            resetAttackCooldown();
            if (!target.isVehicle() && harpy.level().canSeeSky(harpy.blockPosition())) {
                target.startRiding(harpy, true);
            } else {
                harpy.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                harpy.doHurtTarget(target);
            }
        }
    }
}
