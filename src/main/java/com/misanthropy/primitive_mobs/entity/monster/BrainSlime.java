package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BrainSlime extends Slime {
    private static final EntityDataAccessor<Integer> SATURATION =
            SynchedEntityData.defineId(BrainSlime.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_DELAY =
            SynchedEntityData.defineId(BrainSlime.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> VICTIM =
            SynchedEntityData.defineId(BrainSlime.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final int FULL_SATURATION = 10;
    private static final int DIGESTION_TICKS = 100;
    private static final int MAX_GROWN_SIZE = 4;

    private float suck;
    private float suckOld;
    private float suckSpeed = 1.0F;

    public BrainSlime(EntityType<? extends BrainSlime> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SATURATION, 0);
        entityData.define(ATTACK_DELAY, 0);
        entityData.define(VICTIM, Optional.empty());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        setSize(1 + random.nextInt(3), true);
        return data;
    }

    @Override
    public void tick() {
        suckOld = suck;

        if (!level().isClientSide) {
            tickDigestion();
            int attackDelay = getAttackDelay();
            if (attackDelay > 0) {
                setAttackDelay(attackDelay - 1);
            }
            LivingEntity target = getTarget();
            if (target != null) {
                setVictimId(target.getUUID());
            }
            if (!isPassenger()) {
                tryMountVictim();
            } else {
                tickDraining();
            }
        }

        if (isPassenger()) {
            suckSpeed = Math.max(0.2F, suckSpeed * 0.9F);
            suck += suckSpeed * 2.0F;
        } else {
            suck = 0.0F;
            suckSpeed = 1.0F;
        }

        super.tick();
    }

    private void tickDigestion() {
        int saturation = getSaturation();
        if (saturation < FULL_SATURATION) {
            return;
        }
        saturation++;
        if (saturation < DIGESTION_TICKS) {
            setSaturation(saturation);
            return;
        }
        setSize(Math.min(MAX_GROWN_SIZE, getSize() + 1), true);
        setSaturation(0);
        setAttackDelay(50);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 0.5D, getZ(), 10, 0.5D, 0.5D, 0.5D, 0.0D);
        }
    }

    private void tryMountVictim() {
        LivingEntity victim = getVictim();
        if (victim == null || getSaturation() >= FULL_SATURATION || getAttackDelay() > 0 || distanceToSqr(victim) >= 4.0D) {
            return;
        }
        Entity top = topPassenger(victim);
        if (top != null && top.isAlive()) {
            startRiding(top, false);
            setAttackDelay(5);
        }
    }

    private void tickDraining() {
        if (getAttackDelay() > 0) {
            return;
        }
        Entity host = bottomVehicle(this);
        if (!(host instanceof LivingEntity living) || !living.isAlive() || !canDrain(living)) {
            dismountVictim();
            setAttackDelay(20);
            return;
        }
        drain(living);
        setSaturation(getSaturation() + 1);
        setAttackDelay(20);
    }

    public boolean canDrain(LivingEntity entity) {
        if (getSaturation() >= FULL_SATURATION) {
            return false;
        }
        if (entity instanceof TamableAnimal tamable && tamable.isTame()) {
            return false;
        }
        if (entity.getMobType() == MobType.UNDEAD) {
            return false;
        }
        return !(entity instanceof Slime);
    }

    private void drain(LivingEntity victim) {
        ItemStack helmet = victim.getItemBySlot(EquipmentSlot.HEAD);
        int strength = getDrainStrength();
        if (helmet.isEmpty() || !helmet.isDamageableItem()) {
            if (victim.hurt(damageSources().mobAttack(this), Math.max(2.0F, strength))) {
                playSound(SoundEvents.SLIME_ATTACK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                doEnchantDamageEffects(this, victim);
            }
        } else {
            helmet.hurtAndBreak(strength, victim, entity -> entity.broadcastBreakEvent(EquipmentSlot.HEAD));
        }
    }

    private void dismountVictim() {
        setVictimId(null);
        setAttackDelay(20);
        stopRiding();
    }

    @Nullable
    private static Entity topPassenger(Entity entity) {
        Entity current = entity;
        while (current.isVehicle()) {
            List<Entity> passengers = current.getPassengers();
            if (passengers.isEmpty()) {
                break;
            }
            current = passengers.get(0);
        }
        return current;
    }

    private static Entity bottomVehicle(Entity entity) {
        Entity current = entity;
        while (current.isPassenger() && current.getVehicle() != null) {
            current = current.getVehicle();
        }
        return current;
    }

    private int getDrainStrength() {
        return (getSize() + 1) / 3;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isPassenger()) {
            dismountVictim();
            setAttackDelay(10);
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isTiny() {
        return false;
    }

    @Override
    public void playerTouch(Player player) {
    }

    @Override
    public double getMyRidingOffset() {
        return getVehicle() instanceof Player ? 0.25D : 0.0D;
    }

    public float getSuck(float partialTick) {
        return suckOld + (suck - suckOld) * partialTick;
    }

    public int getSaturation() {
        return entityData.get(SATURATION);
    }

    public void setSaturation(int saturation) {
        entityData.set(SATURATION, saturation);
    }

    public int getAttackDelay() {
        return entityData.get(ATTACK_DELAY);
    }

    public void setAttackDelay(int delay) {
        entityData.set(ATTACK_DELAY, delay);
    }

    @Nullable
    public UUID getVictimId() {
        return entityData.get(VICTIM).orElse(null);
    }

    public void setVictimId(@Nullable UUID uuid) {
        entityData.set(VICTIM, Optional.ofNullable(uuid));
    }

    @Nullable
    public LivingEntity getVictim() {
        UUID uuid = getVictimId();
        if (uuid == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(uuid) instanceof LivingEntity living ? living : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AttackDelay", getAttackDelay());
        tag.putInt("Saturation", getSaturation());
        UUID victim = getVictimId();
        if (victim != null) {
            tag.putUUID("Victim", victim);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAttackDelay(tag.getInt("AttackDelay"));
        setSaturation(tag.getInt("Saturation"));
        setVictimId(tag.hasUUID("Victim") ? tag.getUUID("Victim") : null);
    }

    public static boolean checkSpawnRules(EntityType<BrainSlime> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.SAND);
    }
}
