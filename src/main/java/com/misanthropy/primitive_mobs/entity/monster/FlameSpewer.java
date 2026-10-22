package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModSounds;
import com.misanthropy.primitive_mobs.entity.projectile.FlameSpit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FlameSpewer extends Monster {
    private static final EntityDataAccessor<Boolean> SPEWING =
            SynchedEntityData.defineId(FlameSpewer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ALARMED =
            SynchedEntityData.defineId(FlameSpewer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_STEP =
            SynchedEntityData.defineId(FlameSpewer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ATTACK_SIGNAL =
            SynchedEntityData.defineId(FlameSpewer.class, EntityDataSerializers.FLOAT);

    public FlameSpewer(EntityType<? extends FlameSpewer> type, Level level) {
        super(type, level);
        setPathfindingMalus(BlockPathTypes.LAVA, 10.0F);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(3, new SeekLavaGoal(this));
        goalSelector.addGoal(4, new SpewFlamesGoal(this));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SPEWING, false);
        entityData.define(ALARMED, false);
        entityData.define(ATTACK_STEP, 10);
        entityData.define(ATTACK_SIGNAL, 0.0F);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return getAttackStep() < 10 || getAttackSignal() > 0.0F || super.isInvulnerableTo(source);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        boolean submerged = isInLava() || isInWater();
        if (submerged) {
            double lift = level().isEmptyBlock(BlockPos.containing(getX(), getY() + 0.5D, getZ())) ? 0.0D : 0.1D;
            setDeltaMovement(getDeltaMovement().x, lift, getDeltaMovement().z);
            if (isInWater() && tickCount % 15 == 0) {
                hurt(damageSources().drown(), 4.0F);
            }
        } else if (tickCount % 25 == 0) {
            hurt(damageSources().drown(), 1.0F);
            jumpFromGround();
            setZza(1.0F);
        }
    }

    public boolean isSpewing() {
        return entityData.get(SPEWING);
    }

    public void setSpewing(boolean spewing) {
        entityData.set(SPEWING, spewing);
    }

    public boolean isAlarmed() {
        return entityData.get(ALARMED);
    }

    public void setAlarmed(boolean alarmed) {
        entityData.set(ALARMED, alarmed);
    }

    public int getAttackStep() {
        return entityData.get(ATTACK_STEP);
    }

    public void setAttackStep(int step) {
        entityData.set(ATTACK_STEP, step);
    }

    public float getAttackSignal() {
        return entityData.get(ATTACK_SIGNAL);
    }

    public void setAttackSignal(float signal) {
        entityData.set(ATTACK_SIGNAL, signal);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FLAME_SPEWER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AttackStep", getAttackStep());
        tag.putFloat("AttackSignal", getAttackSignal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAttackStep(tag.getInt("AttackStep"));
        setAttackSignal(tag.getFloat("AttackSignal"));
    }

    public static boolean checkSpawnRules(EntityType<FlameSpewer> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return level.getFluidState(pos).is(net.minecraft.tags.FluidTags.LAVA);
    }

    private static class SpewFlamesGoal extends Goal {
        private final FlameSpewer spewer;
        private int attackStep;
        private int attackTime;
        private float attackSignal;
        private boolean spitting;
        private boolean sawTarget;

        SpewFlamesGoal(FlameSpewer spewer) {
            this.spewer = spewer;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = spewer.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            attackStep = 10;
            attackSignal = 0.0F;
            attackTime = 100;
            spitting = false;
            sawTarget = false;
        }

        @Override
        public void stop() {
            attackStep = 10;
            attackSignal = 0.0F;
            attackTime = 100;
            spitting = false;
            spewer.setSpewing(false);
            spewer.setAlarmed(false);
            spewer.setAttackStep(attackStep);
            spewer.setAttackSignal(attackSignal);
        }

        @Override
        public void tick() {
            LivingEntity target = spewer.getTarget();
            if (target == null) {
                return;
            }
            attackTime--;
            boolean canSee = spewer.getSensing().hasLineOfSight(target);
            boolean inLava = spewer.isInLava();

            if (!spitting && inLava && canSee) {
                if (attackTime <= 50) {
                    spewer.setSpewing(false);
                    sawTarget = false;
                }
                if (!spewer.isSpewing()) {
                    sawTarget = true;
                    attackStep = attackTime * 2 / 10;
                    if (attackTime <= 3) {
                        attackSignal += 0.05F;
                    }
                }
            }

            double distanceSqr = spewer.distanceToSqr(target);
            double followRange = spewer.getAttributeValue(Attributes.FOLLOW_RANGE);
            double spitRangeSqr = followRange / 2.0D * (followRange / 2.0D);

            if (distanceSqr < 5.0D && !inLava) {
                spewer.setAlarmed(true);
                if (attackTime <= 0) {
                    attackTime = 20;
                    spewer.doHurtTarget(target);
                }
            } else if (distanceSqr < spitRangeSqr && inLava) {
                spewer.setAlarmed(false);
                tickVolley(target, distanceSqr);
            } else if (inLava && canSee) {
                spewer.setDeltaMovement((target.getX() - spewer.getX()) * 0.01D, spewer.getDeltaMovement().y,
                        (target.getZ() - spewer.getZ()) * 0.01D);
                spewer.setAlarmed(false);
                spewer.setSpewing(false);
            } else {
                spewer.getNavigation().stop();
                spewer.setAlarmed(false);
                spewer.setSpewing(false);
            }

            spewer.getLookControl().setLookAt(target, 10.0F, 10.0F);
            attackSignal = Mth.clamp(attackSignal, 0.0F, 0.4F);
            spewer.setAttackSignal(attackSignal);
            spewer.setAttackStep(attackStep);
        }

        private void tickVolley(LivingEntity target, double distanceSqr) {
            if (attackTime > 0) {
                return;
            }
            attackStep++;
            attackSignal -= 0.05F;
            if (attackStep == 1) {
                attackTime = 30;
                spewer.setSpewing(true);
                spitting = true;
            } else if (attackStep <= 10) {
                attackTime = 3;
                spewer.setSpewing(true);
                spitting = true;
            } else {
                attackTime = 100;
                attackStep = 10;
                spitting = false;
            }

            if (attackStep <= 1 || !sawTarget) {
                return;
            }
            double dx = target.getX() - spewer.getX();
            double dy = target.getBoundingBox().minY + target.getBbHeight() / 2.0F + 0.25F
                    - (spewer.getY() + spewer.getBbHeight() / 2.0F);
            double dz = target.getZ() - spewer.getZ();
            float spread = Mth.sqrt((float) Math.sqrt(distanceSqr) * 0.1F);
            spewer.level().levelEvent(null, 1018, spewer.blockPosition(), 0);

            FlameSpit spit = new FlameSpit(spewer.level(), spewer,
                    dx + spewer.getRandom().nextGaussian() * 0.02D * spread,
                    dy - spewer.getRandom().nextGaussian() * 0.02D * spread,
                    dz + spewer.getRandom().nextGaussian() * 0.02D * spread);
            spit.setPos(spit.getX(), spewer.getY() + spewer.getBbHeight() / 2.0F - 0.5D, spit.getZ());
            spewer.level().addFreshEntity(spit);
        }
    }

    private static class SeekLavaGoal extends MoveToBlockGoal {
        private final FlameSpewer spewer;
        private final BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        SeekLavaGoal(FlameSpewer spewer) {
            super(spewer, 0.7D, 25);
            this.spewer = spewer;
        }

        @Override
        public boolean canUse() {
            return !spewer.isInLava() && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            spewer.getLookControl().setLookAt(blockPos.getX() + 0.5D, blockPos.getY() + 1.0D, blockPos.getZ() + 0.5D,
                    10.0F, spewer.getMaxHeadXRot());
            if (isReachedTarget()) {
                nextStartTick = 10;
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(Blocks.LAVA)
                    && level.isEmptyBlock(neighbor.setWithOffset(pos, Direction.UP))
                    && isLava(level, pos, Direction.EAST)
                    && isLava(level, pos, Direction.WEST)
                    && isLava(level, pos, Direction.NORTH)
                    && isLava(level, pos, Direction.SOUTH);
        }

        private boolean isLava(LevelReader level, BlockPos pos, Direction direction) {
            return level.getBlockState(neighbor.setWithOffset(pos, direction)).is(Blocks.LAVA);
        }
    }
}
