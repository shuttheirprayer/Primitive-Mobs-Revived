package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveFollowOwnerGoal;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveSitGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Chameleon extends Animal implements PrimitiveTameable {
    private static final net.minecraft.network.syncher.EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(Chameleon.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Integer> SKIN_COLOR =
            SynchedEntityData.defineId(Chameleon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> FOLLOW_STATE =
            SynchedEntityData.defineId(Chameleon.class, EntityDataSerializers.BYTE);
    private static final int DEFAULT_COLOR = 0x007D19;
    private static final int COLOR_SAMPLE_INTERVAL = 10;
    private static final int BLEND_STEP = 4;

    private int displayRed = 0x00;
    private int displayGreen = 0x7D;
    private int displayBlue = 0x19;

    public Chameleon(EntityType<? extends Chameleon> type, Level level) {
        super(type, level);
        setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PrimitiveSitGoal<>(this));
        goalSelector.addGoal(2, new PanicGoal(this, 1.25D));
        goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        goalSelector.addGoal(5, new PrimitiveFollowOwnerGoal<>(this, 1.1D, 8.0F, 3.0F));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SKIN_COLOR, DEFAULT_COLOR);
        entityData.define(OWNER, Optional.empty());
        entityData.define(FOLLOW_STATE, (byte) FollowState.WANDERING.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            blendTowardsTargetColor();
        } else if (tickCount % COLOR_SAMPLE_INTERVAL == 0) {
            sampleGroundColor();
        }
    }

    private void sampleGroundColor() {
        BlockPos pos = blockPosition();
        BlockState state = level().getBlockState(pos);
        if (state.isAir()) {
            pos = pos.below();
            state = level().getBlockState(pos);
        }
        if (state.isAir()) {
            return;
        }
        int color = state.getMapColor(level(), pos).col;
        if (color != 0 && color != entityData.get(SKIN_COLOR)) {
            entityData.set(SKIN_COLOR, color);
        }
    }

    private void blendTowardsTargetColor() {
        int target = entityData.get(SKIN_COLOR);
        displayRed = approach(displayRed, (target >> 16) & 0xFF);
        displayGreen = approach(displayGreen, (target >> 8) & 0xFF);
        displayBlue = approach(displayBlue, target & 0xFF);
    }

    private static int approach(int current, int goal) {
        if (current < goal) {
            return Math.min(goal, current + BLEND_STEP);
        }
        return Math.max(goal, current - BLEND_STEP);
    }

    public int getDisplayColor() {
        return (displayRed << 16) | (displayGreen << 8) | displayBlue;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isFood(stack) && !isTamed()) {
            return super.mobInteract(player, hand);
        }
        InteractionResult result = PrimitiveTameable.interact(this, player, hand);
        return result == InteractionResult.PASS ? super.mobInteract(player, hand) : result;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.FERMENTED_SPIDER_EYE);
    }

    @Override
    public boolean isTamingFood(ItemStack stack) {
        return stack.is(Items.SPIDER_EYE);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.CHAMELEON.get().create(level);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PrimitiveTameable.save(this, tag);
        tag.putInt("SkinColor", entityData.get(SKIN_COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PrimitiveTameable.load(this, tag);
        if (tag.contains("SkinColor")) {
            entityData.set(SKIN_COLOR, tag.getInt("SkinColor"));
        }
    }

    public static boolean checkSpawnRules(EntityType<Chameleon> type, ServerLevelAccessor level,
                                          net.minecraft.world.entity.MobSpawnType spawnType, BlockPos pos,
                                          net.minecraft.util.RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }
}
