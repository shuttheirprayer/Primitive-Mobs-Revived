package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class VoidEye extends Monster {
    private static final EntityDataAccessor<Integer> BEAM_TARGET =
            SynchedEntityData.defineId(VoidEye.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SEES_TARGET =
            SynchedEntityData.defineId(VoidEye.class, EntityDataSerializers.BOOLEAN);
    private static final int BEAM_DURATION = 75;

    @Nullable
    private LivingEntity clientBeamTarget;
    private int clientBeamTime;
    private boolean wantsTeleport;

    public VoidEye(EntityType<? extends VoidEye> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, true);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(false);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new BeamAttackGoal(this));
        goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BEAM_TARGET, 0);
        entityData.define(SEES_TARGET, false);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.5F;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        if (level().isClientSide) {
            if (hasBeamTarget() && clientBeamTime < BEAM_DURATION) {
                clientBeamTime++;
            }
            level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
        } else {
            LivingEntity beamed = getBeamTarget();
            setSeesTarget(beamed != null && hasLineOfSight(beamed));
            if (wantsTeleport) {
                wantsTeleport = false;
                teleportRandomly();
            }
        }

        if (hasBeamTarget()) {
            setYRot(yHeadRot);
        }
        super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)
                && random.nextInt(5) != 0) {
            wantsTeleport = true;
        }
        return hurt;
    }

    private void teleportRandomly() {
        double x = getX() + (random.nextDouble() - 0.5D) * 16.0D;
        double y = getY() + random.nextInt(32) - 16;
        double z = getZ() + (random.nextDouble() - 0.5D) * 16.0D;
        if (randomTeleport(x, y, z, true)) {
            level().playSound(null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1.0F, 1.0F);
            playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public int getBeamDuration() {
        return BEAM_DURATION;
    }

    public float getBeamProgress(float partialTick) {
        return (clientBeamTime + partialTick) / BEAM_DURATION;
    }

    public boolean hasBeamTarget() {
        return entityData.get(BEAM_TARGET) != 0;
    }

    public void setBeamTarget(int entityId) {
        entityData.set(BEAM_TARGET, entityId);
    }

    @Nullable
    public LivingEntity getBeamTarget() {
        if (!hasBeamTarget()) {
            return null;
        }
        if (!level().isClientSide) {
            return getTarget();
        }
        if (clientBeamTarget != null) {
            return clientBeamTarget;
        }
        Entity entity = level().getEntity(entityData.get(BEAM_TARGET));
        if (entity instanceof LivingEntity living) {
            clientBeamTarget = living;
        }
        return clientBeamTarget;
    }

    public boolean seesTarget() {
        return entityData.get(SEES_TARGET);
    }

    public void setSeesTarget(boolean sees) {
        entityData.set(SEES_TARGET, sees);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (BEAM_TARGET.equals(key)) {
            clientBeamTime = 0;
            clientBeamTarget = null;
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.VOID_EYE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDER_EYE_LAUNCH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_EYE_DEATH;
    }

    private static class BeamAttackGoal extends Goal {
        private final VoidEye eye;
        private int tickCounter;
        private int claimCheckCooldown;

        BeamAttackGoal(VoidEye eye) {
            this.eye = eye;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = eye.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            if (eye.hasBeamTarget()) {
                return true;
            }
            if (--claimCheckCooldown > 0) {
                return false;
            }
            claimCheckCooldown = reducedTickDelay(10);
            AABB range = eye.getBoundingBox().inflate(20.0D);
            List<VoidEye> others = eye.level().getEntitiesOfClass(VoidEye.class, range,
                    other -> other != eye && other.hasBeamTarget());
            for (VoidEye other : others) {
                if (other.entityData.get(BEAM_TARGET) == target.getId()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = eye.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            tickCounter = -10;
            eye.getNavigation().stop();
            LivingEntity target = eye.getTarget();
            if (target != null) {
                eye.getLookControl().setLookAt(target, 90.0F, 90.0F);
            }
        }

        @Override
        public void stop() {
            eye.setBeamTarget(0);
            eye.setTarget(null);
        }

        @Override
        public void tick() {
            LivingEntity target = eye.getTarget();
            if (target == null) {
                return;
            }
            eye.getNavigation().stop();
            eye.getMoveControl().setWantedPosition(eye.getX(), eye.getY(), eye.getZ(), 0.0D);
            eye.getLookControl().setLookAt(target, 90.0F, 90.0F);

            tickCounter++;
            if (tickCounter == 0) {
                eye.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                eye.setBeamTarget(target.getId());
            } else if (tickCounter >= eye.getBeamDuration()) {
                if (eye.seesTarget()) {
                    target.hurt(eye.damageSources().indirectMagic(eye, eye),
                            (float) eye.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 240));
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 240));
                target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 240));
                eye.setTarget(null);
            }
        }
    }

    public static boolean checkSpawnRules(EntityType<VoidEye> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return pos.getY() <= 20 && Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
