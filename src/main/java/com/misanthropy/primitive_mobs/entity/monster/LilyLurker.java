package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LilyLurker extends Monster {
    private static final EntityDataAccessor<Boolean> CAMOUFLAGED =
            SynchedEntityData.defineId(LilyLurker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions DISGUISED_SIZE = EntityDimensions.scalable(0.5F, 0.98F);
    private static final net.minecraft.world.phys.Vec3 RISE = new net.minecraft.world.phys.Vec3(0.0D, 0.05D, 0.0D);
    private static final int AMBUSH_INTERVAL = 5;
    private static final EntityDimensions EXPOSED_SIZE = EntityDimensions.scalable(0.5F, 0.5F);
    private static final int RECAMOUFLAGE_DELAY = 250;
    private static final int LAND_TOLERANCE = 100;

    private int calmTicks;
    private int landTicks;
    private int ambushCooldown;

    public LilyLurker(EntityType<? extends LilyLurker> type, Level level) {
        super(type, level);
        moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2F, 0.1F, true);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.LILY_PAD));
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 10));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Squid.class, false, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CAMOUFLAGED, true);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return isCamouflaged() ? DISGUISED_SIZE : EXPOSED_SIZE;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.25F;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return !isCamouflaged();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public void tick() {
        if (isCamouflaged()) {
            tickDisguised();
        } else {
            tickExposed();
        }
        super.tick();
    }

    private void tickDisguised() {
        if (!isInWater()) {
            setCamouflaged(false);
            return;
        }
        setNoGravity(true);
        setYRot(0.25F);
        yHeadRot = 0.25F;
        getNavigation().stop();
        calmTicks = 0;

        if (level().isClientSide) {
            return;
        }
        if (!level().getFluidState(blockPosition().above()).isEmpty()) {
            move(MoverType.SELF, RISE);
        }

        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            setCamouflaged(false);
            return;
        }
        if (--ambushCooldown > 0) {
            return;
        }
        ambushCooldown = AMBUSH_INTERVAL;
        AABB range = getBoundingBox().inflate(1.0D);
        List<LivingEntity> nearby = level().getEntitiesOfClass(LivingEntity.class, range,
                entity -> entity != this && !(entity instanceof Player player && player.isCreative()));
        if (!nearby.isEmpty()) {
            setTarget(nearby.get(0));
            setCamouflaged(false);
        }
    }

    private void tickExposed() {
        setNoGravity(false);
        if (level().isClientSide) {
            return;
        }
        LivingEntity target = getTarget();
        if (isInWater()) {
            landTicks = 0;
            if ((target == null || !target.isAlive()) && ++calmTicks > RECAMOUFLAGE_DELAY) {
                calmTicks = 0;
                setCamouflaged(true);
            }
        } else if (++landTicks > LAND_TOLERANCE) {
            hurt(damageSources().drown(), 3.0F);
            jumpFromGround();
            setDeltaMovement((random.nextFloat() - random.nextFloat()) / 2.0F, getDeltaMovement().y,
                    (random.nextFloat() - random.nextFloat()) / 2.0F);
            landTicks = 80;
        }
    }

    @Override
    protected void pushEntities() {
        if (!isCamouflaged()) {
            super.pushEntities();
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity.getBoundingBox().minY <= getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushable() {
        return !isCamouflaged();
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        setCamouflaged(false);
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        setCamouflaged(false);
        if (source.is(DamageTypes.IN_WALL) && isInWater()) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public boolean isCamouflaged() {
        return entityData.get(CAMOUFLAGED);
    }

    public void setCamouflaged(boolean camouflaged) {
        if (entityData.get(CAMOUFLAGED) != camouflaged) {
            entityData.set(CAMOUFLAGED, camouflaged);
            refreshDimensions();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (CAMOUFLAGED.equals(key)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Camouflaged", isCamouflaged());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setCamouflaged(tag.getBoolean("Camouflaged"));
    }

    public static boolean checkSpawnRules(EntityType<LilyLurker> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return pos.getY() > 45 && pos.getY() < level.getLevel().getSeaLevel()
                && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER);
    }
}
