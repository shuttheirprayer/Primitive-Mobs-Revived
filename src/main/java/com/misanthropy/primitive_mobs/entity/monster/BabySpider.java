package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveFollowOwnerGoal;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveSitGoal;
import com.misanthropy.primitive_mobs.entity.ai.SpiderAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BabySpider extends Spider implements PrimitiveTameable, PlayerRideableJumping {
    private static final net.minecraft.network.syncher.EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(BabySpider.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Integer> GROWTH =
            SynchedEntityData.defineId(BabySpider.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> EYE_COLOR =
            SynchedEntityData.defineId(BabySpider.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FOLLOW_STATE =
            SynchedEntityData.defineId(BabySpider.class, EntityDataSerializers.BYTE);

    public static final int MAX_GROWTH = 5;
    private static final float[][] SIZES = {
            {0.5F, 0.3F}, {0.7F, 0.4F}, {0.9F, 0.5F}, {1.1F, 0.6F}, {1.3F, 0.7F}, {1.5F, 0.8F}
    };

    private int rideAttemptDelay;
    private boolean jumpPending;

    public BabySpider(EntityType<? extends BabySpider> type, Level level) {
        super(type, level);
        rideAttemptDelay = random.nextInt(100);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PrimitiveSitGoal<>(this));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new SpiderAttackGoal(this, 2.0F));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new PrimitiveFollowOwnerGoal<>(this, 1.0D, 8.0F, 2.0F));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(GROWTH, 0);
        entityData.define(OWNER, Optional.empty());
        entityData.define(EYE_COLOR, (byte) 0);
        entityData.define(FOLLOW_STATE, (byte) FollowState.FOLLOWING.ordinal());
    }

    public int getGrowthLevel() {
        return entityData.get(GROWTH);
    }

    public void setGrowthLevel(int growth) {
        entityData.set(GROWTH, Math.max(0, Math.min(MAX_GROWTH, growth)));
        refreshDimensions();
    }

    public DyeColor getEyeColor() {
        return DyeColor.byId(entityData.get(EYE_COLOR) & 15);
    }

    public void setEyeColor(DyeColor color) {
        entityData.set(EYE_COLOR, (byte) (entityData.get(EYE_COLOR) & 240 | color.getId() & 15));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (GROWTH.equals(key)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float[] size = SIZES[Math.min(getGrowthLevel(), MAX_GROWTH)];
        return EntityDimensions.scalable(size[0], size[1]);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height / 2.0F;
    }

    @Nullable
    public Entity getOwnerEntity() {
        UUID ownerId = getOwnerUuid();
        if (ownerId == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(ownerId);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (getOwnerUuid() == null) {
                level().addParticle(ParticleTypes.SPLASH,
                        getX() + (random.nextFloat() - random.nextFloat()),
                        getY() + 1.0D + (random.nextFloat() - random.nextFloat()),
                        getZ() + (random.nextFloat() - random.nextFloat()),
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }
        if (getVehicle() instanceof MotherSpider mother && mother.getTarget() != null) {
            stopRiding();
            setTarget(mother.getTarget());
            rideAttemptDelay = 0;
        }
        if (++rideAttemptDelay > 100) {
            rideAttemptDelay = 0;
            if (getOwnerEntity() instanceof MotherSpider mother && !mother.isVehicle() && mother.getTarget() == null && getTarget() == null) {
                startRiding(mother);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL) && isPassenger()) {
            return false;
        }
        if (getOwnerEntity() instanceof MotherSpider mother) {
            mother.setAngry(true);
            if (source.getEntity() instanceof LivingEntity attacker) {
                mother.setTarget(attacker);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Items.FERMENTED_SPIDER_EYE)) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            grow(player, stack);
            return InteractionResult.CONSUME;
        }

        if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != getEyeColor()) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            setEyeColor(dye.getDyeColor());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }

        if (isTamed() && isOwnedBy(player) && getGrowthLevel() >= MAX_GROWTH && !isVehicle() && !player.isSecondaryUseActive()) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.startRiding(this);
            return InteractionResult.CONSUME;
        }

        InteractionResult result = PrimitiveTameable.interact(this, player, hand);
        return result == InteractionResult.PASS ? super.mobInteract(player, hand) : result;
    }

    private void grow(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        setGrowthLevel(getGrowthLevel() + 1);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.0D, getZ(), 8, 0.5D, 0.5D, 0.5D, 0.0D);
        }
        if (getGrowthLevel() == MAX_GROWTH) {
            setEyeColor(DyeColor.byId(random.nextInt(DyeColor.values().length)));
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D + random.nextFloat() * 0.1D);
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
            setHealth(30.0F);
        }
    }

    @Override
    public boolean onClimbable() {
        if (super.onClimbable()) {
            return true;
        }
        BlockPos pos = blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        return isFullBlock(cursor.setWithOffset(pos, Direction.EAST)) || isFullBlock(cursor.setWithOffset(pos, Direction.WEST))
                || isFullBlock(cursor.setWithOffset(pos, Direction.SOUTH)) || isFullBlock(cursor.setWithOffset(pos, Direction.NORTH));
    }

    private boolean isFullBlock(BlockPos pos) {
        return level().getBlockState(pos).isCollisionShapeFullBlock(level(), pos);
    }

    @Override
    public boolean isPushable() {
        return !isVehicle();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return getGrowthLevel() >= MAX_GROWTH && getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float forward = player.zza;
        return new Vec3(player.xxa * 0.5F, 0.0D, forward <= 0.0F ? forward * 0.5F : forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) getAttributeValue(Attributes.MOVEMENT_SPEED) / 1.5F;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        setRot(player.getYRot(), player.getXRot() * 0.5F);
        yRotO = getYRot();
        yBodyRot = getYRot();
        yHeadRot = yBodyRot;
        if (jumpPending && onGround()) {
            jumpPending = false;
            jumpFromGround();
        }
    }

    @Override
    protected float getJumpPower() {
        return 0.62F;
    }

    @Override
    public void onPlayerJump(int power) {
        jumpPending = true;
    }

    @Override
    public boolean canJump() {
        return getGrowthLevel() >= MAX_GROWTH;
    }

    @Override
    public void handleStartJump(int power) {
        jumpPending = true;
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public boolean isTamingFood(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH);
    }

    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return entityData.get(OWNER).orElse(null);
    }

    @Override
    public void setOwnerUuid(@Nullable UUID uuid) {
        entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    @Override
    public FollowState getFollowState() {
        return FollowState.byId(entityData.get(FOLLOW_STATE));
    }

    @Override
    public void setFollowState(FollowState state) {
        entityData.set(FOLLOW_STATE, (byte) state.ordinal());
    }

    @Override
    public boolean isTamed() {
        return getOwnerUuid() != null && !(getOwnerEntity() instanceof MotherSpider);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PrimitiveTameable.save(this, tag);
        tag.putInt("Growth", getGrowthLevel());
        tag.putByte("EyeColor", (byte) getEyeColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PrimitiveTameable.load(this, tag);
        setGrowthLevel(tag.getInt("Growth"));
        setEyeColor(DyeColor.byId(tag.getByte("EyeColor")));
    }
}
