package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import java.util.EnumSet;

public class BlazingJuggernaut extends Monster {
    private float heightOffset = 0.5F;
    private int heightOffsetUpdateTime;

    public BlazingJuggernaut(EntityType<? extends BlazingJuggernaut> type, Level level) {
        super(type, level);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(4, new ChargeAttackGoal(this));
        goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public float getVoicePitch() {
        return (random.nextFloat() - random.nextFloat()) * 0.2F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public void aiStep() {
        if (!onGround() && getDeltaMovement().y < 0.0D) {
            setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (level().isClientSide) {
            if (random.nextInt(24) == 0 && !isSilent()) {
                level().playLocalSound(getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D, SoundEvents.BLAZE_BURN,
                        getSoundSource(), 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
            for (int i = 0; i < 2; i++) {
                level().addParticle(ParticleTypes.LARGE_SMOKE,
                        getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
                        getY() + random.nextDouble() * getBbHeight(),
                        getZ() + (random.nextDouble() - 0.5D) * getBbWidth(),
                        0.0D, 0.0D, 0.0D);
            }
        }
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        if (isInWaterRainOrBubble()) {
            hurt(damageSources().drown(), 1.0F);
        }

        if (--heightOffsetUpdateTime <= 0) {
            heightOffsetUpdateTime = 100;
            heightOffset = 0.5F + (float) random.nextGaussian() * 3.0F;
        }

        LivingEntity target = getTarget();
        if (target != null && target.getEyeY() > getEyeY() + heightOffset) {
            setDeltaMovement(getDeltaMovement().add(0.0D, (0.30000001192092896D - getDeltaMovement().y) * 0.30000001192092896D, 0.0D));
            hasImpulse = true;
        }
        super.customServerAiStep();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    private static class ChargeAttackGoal extends Goal {
        private final BlazingJuggernaut juggernaut;
        private int attackCooldown;

        ChargeAttackGoal(BlazingJuggernaut juggernaut) {
            this.juggernaut = juggernaut;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = juggernaut.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            attackCooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = juggernaut.getTarget();
            if (target == null) {
                return;
            }
            double distanceSqr = juggernaut.distanceToSqr(target);
            attackCooldown++;
            if (distanceSqr < 5.0D) {
                if (attackCooldown > 10) {
                    attackCooldown = 0;
                    juggernaut.doHurtTarget(target);
                }
                juggernaut.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
                if (target.getY() < juggernaut.getY()) {
                    juggernaut.setDeltaMovement(juggernaut.getDeltaMovement().add(0.0D, -0.1D, 0.0D));
                }
            } else if (distanceSqr < 30.0D) {
                double dx = target.getX() - juggernaut.getX();
                double dy = target.getBoundingBox().minY + target.getBbHeight() / 2.0F - (juggernaut.getY() + juggernaut.getBbHeight() / 2.0F);
                double dz = target.getZ() - juggernaut.getZ();
                if (attackCooldown > 5) {
                    net.minecraft.world.phys.Vec3 motion = juggernaut.getDeltaMovement();
                    juggernaut.setDeltaMovement(
                            motion.x + (Math.signum(dx) * 0.5D - motion.x) * 0.8D,
                            motion.y + (Math.signum(dy) * 0.7D - motion.y) * 0.8D,
                            motion.z + (Math.signum(dz) * 0.5D - motion.z) * 0.8D);
                    juggernaut.zza = 1.5F;
                    attackCooldown = 0;
                }
                juggernaut.getLookControl().setLookAt(target, 10.0F, 10.0F);
            } else {
                juggernaut.getNavigation().stop();
                juggernaut.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
            }
        }
    }
}
