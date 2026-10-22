package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModSounds;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.CarryingStrollGoal;
import com.misanthropy.primitive_mobs.entity.ai.SpiderAttackGoal;
import com.misanthropy.primitive_mobs.entity.ai.SpiderTargetGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class MotherSpider extends Spider {
    private static final EntityDataAccessor<Boolean> ANGRY =
            SynchedEntityData.defineId(MotherSpider.class, EntityDataSerializers.BOOLEAN);
    private static final double CALM_DAMAGE = 4.0D;
    private static final double ANGRY_DAMAGE = 6.0D;
    private static final double FREE_SPEED = 0.3D;
    private static final double CARRYING_SPEED = 0.18D;

    public MotherSpider(EntityType<? extends MotherSpider> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, CALM_DAMAGE);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new SpiderAttackGoal(this, 4.0F));
        goalSelector.addGoal(5, new CarryingStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new SpiderTargetGoal<>(this, Player.class));
        targetSelector.addGoal(4, new SpiderTargetGoal<>(this, IronGolem.class));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANGRY, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        spawnBrood(level, difficulty);
        return result;
    }

    private void spawnBrood(ServerLevelAccessor level, DifficultyInstance difficulty) {
        int maxBrood = PrimitiveMobsConfig.CONFIG.maxSpiderFamilySize.get();
        if (maxBrood <= 0) {
            return;
        }
        int broodSize = 1 + random.nextInt(maxBrood);
        for (int i = 0; i < broodSize; i++) {
            BabySpider baby = ModEntities.BABY_SPIDER.get().create(level.getLevel());
            if (baby == null) {
                return;
            }
            baby.moveTo(getX(), getY(), getZ(), getYRot(), 0.0F);
            baby.finalizeSpawn(level, difficulty, MobSpawnType.MOB_SUMMONED, null, null);
            baby.setOwnerUuid(getUUID());
            if (isVehicle()) {
                level.addFreshEntity(baby);
            } else {
                baby.startRiding(this);
            }
        }
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (isAngry()) {
                level().addParticle(ParticleTypes.SMOKE,
                        getX() + (random.nextFloat() - random.nextFloat()),
                        getY() + random.nextFloat(),
                        getZ() + (random.nextFloat() - random.nextFloat()),
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        double wantedSpeed = isVehicle() ? CARRYING_SPEED : FREE_SPEED;
        if (speed.getBaseValue() != wantedSpeed) {
            speed.setBaseValue(wantedSpeed);
        }
        AttributeInstance damage = getAttribute(Attributes.ATTACK_DAMAGE);
        double wantedDamage = isAngry() ? ANGRY_DAMAGE : CALM_DAMAGE;
        if (damage.getBaseValue() != wantedDamage) {
            damage.setBaseValue(wantedDamage);
        }
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        float sin = Mth.sin(yBodyRot * ((float) Math.PI / 180F));
        float cos = Mth.cos(yBodyRot * ((float) Math.PI / 180F));
        float lift = passenger instanceof BabySpider ? getBbHeight() * 0.5F : 0.0F;
        move.accept(passenger,
                getX() + 0.1F * sin,
                getY() + getBbHeight() * 0.5F + lift + passenger.getMyRidingOffset(),
                getZ() - 0.1F * cos);
        if (passenger instanceof net.minecraft.world.entity.LivingEntity living) {
            living.yBodyRot = yBodyRot;
        }
    }

    public boolean isAngry() {
        return entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        entityData.set(ANGRY, angry);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MOTHER_SPIDER_SCREECH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Angry", isAngry());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAngry(tag.getBoolean("Angry"));
    }
}
