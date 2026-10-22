package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModLootTables;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TreasureSlime extends Slime {
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(TreasureSlime.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(TreasureSlime.class, EntityDataSerializers.BOOLEAN);

    private static final int BLEND_STEP = 10;
    private static final float TAMED_HEALTH = 20.0F;
    private static final float WILD_HEALTH = 10.0F;

    private int targetColor = 0xFFFFFF;
    private int displayRed = 0xFF;
    private int displayGreen = 0xFF;
    private int displayBlue = 0xFF;

    public TreasureSlime(EntityType<? extends TreasureSlime> type, Level level) {
        super(type, level);
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, WILD_HEALTH);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SitStillGoal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER, Optional.empty());
        entityData.define(SITTING, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        int tameableChance = PrimitiveMobsConfig.CONFIG.tameableSlimeChance.get();
        boolean tameable = tameableChance > 0 && random.nextInt(100) < tameableChance;
        if (!tameable && getMainHandItem().isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND,
                    ModLootTables.rollOne(level.getLevel(), random, ModLootTables.TREASURE_SLIME_TREASURE, new ItemStack(Items.SLIME_BALL)));
        }
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            blendColor();
            if (isTame() && random.nextInt(200) == 0) {
                level().addParticle(ParticleTypes.HEART,
                        getX() + (random.nextFloat() - random.nextFloat()),
                        getY() + random.nextFloat() + 1.0D,
                        getZ() + (random.nextFloat() - random.nextFloat()),
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }
        if (isTame()) {
            if (isSitting()) {
                setTarget(null);
            } else if (getTarget() == null) {
                setTarget(getOwner());
            }
        }
    }

    public void setColorTarget(int color) {
        targetColor = color;
    }

    private void blendColor() {
        displayRed = approach(displayRed, (targetColor >> 16) & 0xFF);
        displayGreen = approach(displayGreen, (targetColor >> 8) & 0xFF);
        displayBlue = approach(displayBlue, targetColor & 0xFF);
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
        if (hand != InteractionHand.MAIN_HAND) {
            return super.mobInteract(player, hand);
        }
        ItemStack stack = player.getItemInHand(hand);

        if (!isTame()) {
            if (!stack.is(Items.SLIME_BALL) || !getMainHandItem().isEmpty()) {
                return super.mobInteract(player, hand);
            }
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            tame(player, stack);
            return InteractionResult.CONSUME;
        }

        if (stack.is(Items.SLIME_BALL)) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            consume(player, stack);
            heal(20.0F);
            spawnEffect(ParticleTypes.HEART);
            return InteractionResult.CONSUME;
        }

        if (!isOwnedBy(player)) {
            return super.mobInteract(player, hand);
        }
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (stack.isEmpty()) {
            setSitting(!isSitting());
            spawnEffect(ParticleTypes.NOTE);
        } else {
            if (!getMainHandItem().isEmpty()) {
                spawnAtLocation(getMainHandItem().copy(), 0.0F);
            }
            setItemSlot(EquipmentSlot.MAINHAND, stack.copyWithCount(1));
            consume(player, stack);
        }
        return InteractionResult.CONSUME;
    }

    private void tame(Player player, ItemStack stack) {
        consume(player, stack);
        setOwnerUuid(player.getUUID());
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        setHealth(TAMED_HEALTH);
        setPersistenceRequired();
        getNavigation().stop();
        spawnEffect(ParticleTypes.HEART);
    }

    private void consume(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private void spawnEffect(net.minecraft.core.particles.ParticleOptions particle) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, getX(), getY() + 0.5D + getBbHeight(), getZ(), 7, getBbWidth(), 0.5D, getBbWidth(), 0.02D);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        int size = getSize();
        if (!level().isClientSide && size > 1 && isDeadOrDying()) {
            splitInto(size);
            if (isTame() && !getMainHandItem().isEmpty()) {
                spawnAtLocation(getMainHandItem().copy(), 1.0F);
            }
        }
        super.remove(reason);
    }

    private void splitInto(int size) {
        Component name = getCustomName();
        boolean noAi = isNoAi();
        float spread = size / 4.0F;
        int childSize = size / 2;
        int count = 2 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            float offsetX = ((i % 2) - 0.5F) * spread;
            float offsetZ = ((i / 2) - 0.5F) * spread;
            TreasureSlime child = (TreasureSlime) getType().create(level());
            if (child == null) {
                return;
            }
            if (isPersistenceRequired()) {
                child.setPersistenceRequired();
            }
            child.setCustomName(name);
            child.setNoAi(noAi);
            child.setInvulnerable(isInvulnerable());
            child.setSize(childSize, true);
            if (isTame()) {
                child.setOwnerUuid(getOwnerUuid());
                child.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
                child.setHealth(TAMED_HEALTH);
            } else {
                child.setItemSlot(EquipmentSlot.MAINHAND, getMainHandItem().copy());
            }
            child.moveTo(getX() + offsetX, getY() + 0.5D, getZ() + offsetZ, random.nextFloat() * 360.0F, 0.0F);
            level().addFreshEntity(child);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        if (getSize() == 1 && !getMainHandItem().isEmpty()) {
            spawnAtLocation(getMainHandItem().copy(), 1.0F);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!isOwnedBy(player)) {
            super.playerTouch(player);
        }
    }

    @Override
    protected boolean isDealsDamage() {
        return !isTame() && super.isDealsDamage();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isTame() && (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN))) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !isTame();
    }

    public boolean isTame() {
        return getOwnerUuid() != null;
    }

    @Nullable
    public UUID getOwnerUuid() {
        return entityData.get(OWNER).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(getOwnerUuid());
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUuid();
        return uuid == null ? null : level().getPlayerByUUID(uuid);
    }

    public boolean isSitting() {
        return entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        entityData.set(SITTING, sitting);
    }

    private class SitStillGoal extends net.minecraft.world.entity.ai.goal.Goal {
        SitStillGoal() {
            setFlags(java.util.EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return isTame() && isSitting();
        }

        @Override
        public void start() {
            setZza(0.0F);
            getNavigation().stop();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID owner = getOwnerUuid();
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        tag.putBoolean("Sitting", isSitting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setOwnerUuid(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
        setSitting(tag.getBoolean("Sitting"));
        if (isTame()) {
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        }
    }
}
