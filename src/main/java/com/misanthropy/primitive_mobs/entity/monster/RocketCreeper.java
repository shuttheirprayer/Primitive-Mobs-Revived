package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RocketCreeper extends PrimitiveCreeper {
    private static final EntityDataAccessor<Boolean> LAUNCHED =
            SynchedEntityData.defineId(RocketCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final float EXPLOSION_RADIUS = 3.0F;

    private int swellTicks;

    public RocketCreeper(EntityType<? extends RocketCreeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new SwellGoal(this));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        addTamedGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LAUNCHED, false);
    }

    public boolean isLaunched() {
        return entityData.get(LAUNCHED);
    }

    public void setLaunched(boolean launched) {
        entityData.set(LAUNCHED, launched);
    }

    @Override
    public void tick() {
        LivingEntity target = getTarget();
        if (target != null && distanceToSqr(target) > 25.0D) {
            setSwellDir(-1);
        }

        swellTicks = getSwellDir() > 0 ? swellTicks + 1 : 0;

        if (swellTicks > 15 && isAlive() && target != null && onGround() && hasHeadroom(target)) {
            launchAt(target);
        }
        super.tick();
    }

    private void launchAt(LivingEntity target) {
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.SMOKE,
                    getX() + (random.nextFloat() - random.nextFloat()),
                    getY() - (random.nextFloat() - random.nextFloat()) - 1.0D,
                    getZ() + (random.nextFloat() - random.nextFloat()),
                    0.0D, 0.0D, 0.0D);
        }
        playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F, 0.5F);
        setDeltaMovement((target.getX() - getX()) / 6.0D, 1.2D, (target.getZ() - getZ()) / 6.0D);
        hasImpulse = true;
        setLaunched(true);
    }

    private boolean hasHeadroom(LivingEntity target) {
        if (PrimitiveMobsConfig.CONFIG.rocketCreeperAlwaysJumps.get()) {
            return true;
        }
        Vec3 to = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        for (int i = 1; i <= 5; i++) {
            Vec3 from = new Vec3(getX(), getEyeY() + i, getZ());
            HitResult hit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        if (isLaunched()) {
            explodeOnImpact();
        }
        return false;
    }

    private void explodeOnImpact() {
        if (level().isClientSide) {
            return;
        }
        boolean griefing = level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);
        float power = isPowered() ? EXPLOSION_RADIUS * 2.0F : EXPLOSION_RADIUS;
        setLaunched(false);
        if (isTamed()) {
            hurt(damageSources().generic(), 1.0F);
        } else {
            dead = true;
            discard();
        }
        level().explode(this, getX(), getY(), getZ(), power,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isLaunched()) {
            super.playStepSound(pos, state);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Launched", isLaunched());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setLaunched(tag.getBoolean("Launched"));
    }

    public static boolean checkSpawnRules(EntityType<RocketCreeper> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
