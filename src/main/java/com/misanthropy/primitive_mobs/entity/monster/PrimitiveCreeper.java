package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveFollowOwnerGoal;
import com.misanthropy.primitive_mobs.entity.ai.PrimitiveSitGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PrimitiveCreeper extends Creeper implements PrimitiveTameable {
    private static final net.minecraft.network.syncher.EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(PrimitiveCreeper.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Boolean> BABY =
            SynchedEntityData.defineId(PrimitiveCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> FOLLOW_STATE =
            SynchedEntityData.defineId(PrimitiveCreeper.class, EntityDataSerializers.BYTE);
    private static final int GROW_UP_TICKS = 24000;

    private int growUpTimer;

    protected PrimitiveCreeper(EntityType<? extends PrimitiveCreeper> type, Level level) {
        super(type, level);
    }

    protected void addTamedGoals() {
        goalSelector.addGoal(0, new PrimitiveSitGoal<>(this));
        goalSelector.addGoal(4, new PrimitiveFollowOwnerGoal<>(this, 1.0D, 8.0F, 3.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BABY, false);
        entityData.define(OWNER, Optional.empty());
        entityData.define(FOLLOW_STATE, (byte) FollowState.WANDERING.ordinal());
    }

    @Override
    public boolean isBaby() {
        return entityData.get(BABY);
    }

    public void setBaby(boolean baby) {
        entityData.set(BABY, baby);
        if (baby) {
            growUpTimer = GROW_UP_TICKS;
        }
        refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (BABY.equals(key)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(isBaby() ? 0.5F : 1.0F);
    }

    @Override
    public void tick() {
        if (isTamed() && getFollowState() == FollowState.SITTING) {
            setSwellDir(-1);
            setTarget(null);
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide && isBaby() && --growUpTimer <= 0) {
            setBaby(false);
        }
        super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isTamed() && (source.is(DamageTypes.FIREWORKS) || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN))) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = PrimitiveTameable.interact(this, player, hand);
        return result == InteractionResult.PASS ? super.mobInteract(player, hand) : result;
    }

    @Override
    public boolean isTamingFood(ItemStack stack) {
        return stack.is(Items.GUNPOWDER);
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
    public boolean removeWhenFarAway(double distance) {
        return !isTamed() && super.removeWhenFarAway(distance);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PrimitiveTameable.save(this, tag);
        tag.putBoolean("Baby", isBaby());
        tag.putInt("GrowUpTimer", growUpTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PrimitiveTameable.load(this, tag);
        setBaby(tag.getBoolean("Baby"));
        if (tag.contains("GrowUpTimer")) {
            growUpTimer = tag.getInt("GrowUpTimer");
        }
    }
}
