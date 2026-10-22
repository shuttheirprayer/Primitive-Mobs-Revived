package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModSounds;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.TrollagerAttackGoal;
import com.misanthropy.primitive_mobs.entity.projectile.ThrownBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Trollager extends Monster {
    public static final int ACTION_THROW = 0;
    public static final int ACTION_SMASH = 1;
    public static final int ACTION_MELEE = 2;

    private static final EntityDataAccessor<Integer> ANIMATION_STATE =
            SynchedEntityData.defineId(Trollager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> THROWN_BLOCK =
            SynchedEntityData.defineId(Trollager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STONE =
            SynchedEntityData.defineId(Trollager.class, EntityDataSerializers.BOOLEAN);

    private int previousAnimationState;
    private float animationProgress;
    private boolean supported;
    private int scannedX = Integer.MIN_VALUE;
    private int scannedY = Integer.MIN_VALUE;
    private int scannedZ = Integer.MIN_VALUE;

    public Trollager(EntityType<? extends Trollager> type, Level level) {
        super(type, level);
        xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(3, new TrollagerAttackGoal(this, 1.25D, 2.5F, 20.0F));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANIMATION_STATE, 0);
        entityData.define(THROWN_BLOCK, net.minecraft.world.level.block.Block.getId(Blocks.STONE.defaultBlockState()));
        entityData.define(STONE, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (random.nextInt(20) == 0) {
            Goblin goblin = ModEntities.GOBLIN.get().create(level.getLevel());
            if (goblin != null) {
                goblin.moveTo(getX(), getY(), getZ(), getYRot(), 0.0F);
                goblin.finalizeSpawn(level, difficulty, MobSpawnType.JOCKEY, null, null);
                goblin.startRiding(this);
            }
        }
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            tickRider();
            if (getTarget() != null && !getTarget().isAlive()) {
                setTarget(null);
            }
            if (getAnimationState() != 1 && getAnimationState() != 2 && tickCount % 5 == 0) {
                pickThrowableBlock();
            }
        }
        tickAnimation();
        if (isStone()) {
            tickPetrified();
        }
    }

    private void tickRider() {
        if (!isVehicle()) {
            return;
        }
        if (getFirstPassenger() instanceof Mob rider && rider.getTarget() != null) {
            setTarget(rider.getTarget());
        }
        if (horizontalCollision) {
            jumpFromGround();
        }
    }

    private void tickPetrified() {
        setNoAi(true);
        getNavigation().stop();
        setTarget(null);
        if (!level().isClientSide && isVehicle()) {
            ejectPassengers();
        }
    }

    private void tickAnimation() {
        if (isStone()) {
            return;
        }
        if (previousAnimationState != getAnimationState()) {
            previousAnimationState = getAnimationState();
            animationProgress = 0.0F;
        }
        animationProgress = Math.min(1.0F, animationProgress + 0.01F);
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide) {
            updatePetrification();
        }
        super.aiStep();
    }

    private void updatePetrification() {
        if (supported) {
            setStone(false);
            supported = false;
            return;
        }
        boolean exposed = level().isDay()
                && level().canSeeSky(BlockPos.containing(getX(), getEyeY(), getZ()))
                && getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        setStone(exposed);
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    private void pickThrowableBlock() {
        int floorY = Mth.floor(getBoundingBox().minY);
        if (getBlockX() == scannedX && getBlockZ() == scannedZ && floorY == scannedY) {
            return;
        }
        scannedX = getBlockX();
        scannedZ = getBlockZ();
        scannedY = floorY;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 1; i < 64; i++) {
            cursor.set(scannedX, floorY - i, scannedZ);
            BlockState state = level().getBlockState(cursor);
            if (state.isCollisionShapeFullBlock(level(), cursor)) {
                entityData.set(THROWN_BLOCK, net.minecraft.world.level.block.Block.getId(state));
                return;
            }
        }
    }

    public BlockState getThrownBlockState() {
        return net.minecraft.world.level.block.Block.stateById(entityData.get(THROWN_BLOCK));
    }

    public void performAction(LivingEntity target, int action) {
        switch (action) {
            case ACTION_THROW -> throwBlock(target);
            case ACTION_SMASH -> smash(target);
            case ACTION_MELEE -> melee();
            default -> {
            }
        }
    }

    private void throwBlock(LivingEntity target) {
        if (!level().isClientSide) {
            ThrownBlock block = new ThrownBlock(level(), getX(), getY() + 4.0D, getZ(), this, getThrownBlockState());
            block.setDeltaMovement(
                    (target.getX() - block.getX()) / 18.0D,
                    (target.getY() - block.getY()) / 18.0D + 0.5D,
                    (target.getZ() - getZ()) / 18.0D);
            level().addFreshEntity(block);
        }
        playAttackSound();
    }

    private void smash(LivingEntity target) {
        Vec3 look = getLookAngle();
        double dx = look.x;
        double dz = look.z;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length != 0.0D) {
            dx /= length;
            dz /= length;
        }
        double explosionX = getX() + dx * 2.0D;
        double explosionZ = getZ() + dz * 2.0D;
        boolean griefing = PrimitiveMobsConfig.CONFIG.trollagerDestruction.get()
                && level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        level().explode(this, explosionX, getEyeY(), explosionZ, 3.0F,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, explosionX, getY(), explosionZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        playAttackSound();
    }

    private void melee() {
        LivingEntity target = getTarget();
        if (target == null || isStone()) {
            return;
        }
        double reach = getMeleeReachSqr(target) + 4.0D;
        if (distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ()) <= reach && getSensing().hasLineOfSight(target)) {
            doHurtTarget(target);
        }
        playAttackSound();
    }

    public double getMeleeReachSqr(LivingEntity target) {
        return getBbWidth() * 2.0F * getBbWidth() * 2.0F + target.getBbWidth();
    }

    private void playAttackSound() {
        playSound(ModSounds.TROLLAGER_ATTACK.get(), getSoundVolume(),
                ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
    }

    public boolean hasClearArcTo(Entity target) {
        Vec3 to = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        for (int i = 0; i < 4; i++) {
            Vec3 from = new Vec3(getX(), getEyeY() + i, getZ());
            if (level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType()
                    != HitResult.Type.MISS) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        if (!isStone() || !(attacker instanceof LivingEntity living)) {
            return false;
        }
        ItemStack weapon = living.getMainHandItem();
        return !weapon.isCorrectToolForDrops(Blocks.STONE.defaultBlockState());
    }

    @Override
    public void push(Entity entity) {
        if (entity.getBoundingBox().minY <= getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushable() {
        return !isStone();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        if (isStone()) {
            spawnAtLocation(new ItemStack(Items.STONE, 32), 1.0F);
            return;
        }
        int drops = 1 + random.nextInt(2) + (looting > 0 ? random.nextInt(looting + 1) : 0);
        for (int i = 0; i < drops; i++) {
            spawnAtLocation(randomLoot(), 1.0F);
        }
    }

    private ItemStack randomLoot() {
        int roll = random.nextInt(100);
        if (roll > 50) {
            return new ItemStack(Items.OBSIDIAN);
        }
        return roll > 10 ? new ItemStack(Items.EMERALD) : new ItemStack(Items.GOLDEN_APPLE);
    }

    public int getAnimationState() {
        return entityData.get(ANIMATION_STATE);
    }

    public void setAnimationState(int state) {
        entityData.set(ANIMATION_STATE, state);
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public boolean isStone() {
        return entityData.get(STONE);
    }

    public void setStone(boolean stone) {
        if (entityData.get(STONE) == stone) {
            return;
        }
        entityData.set(STONE, stone);
        setNoAi(stone);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isStone() ? null : ModSounds.TROLLAGER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isStone() ? SoundEvents.STONE_BREAK : ModSounds.TROLLAGER_HIT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TROLLAGER_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public float getVoicePitch() {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public double getPassengersRidingOffset() {
        return getBbHeight() * 0.9D;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ThrownBlock", entityData.get(THROWN_BLOCK));
        tag.putBoolean("Stone", isStone());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ThrownBlock")) {
            entityData.set(THROWN_BLOCK, tag.getInt("ThrownBlock"));
        }
        setStone(tag.getBoolean("Stone"));
    }

    public static boolean checkSpawnRules(EntityType<Trollager> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        if (pos.getY() > 40 && level.canSeeSky(pos) && random.nextInt(5) != 0) {
            return false;
        }
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
