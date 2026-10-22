package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class SkeletonWarrior extends AbstractSkeleton {
    private static final ItemStack BOW = unbreakable(new ItemStack(Items.BOW));
    private static final ItemStack SWORD = unbreakable(new ItemStack(Items.IRON_SWORD));
    private static final double MELEE_RANGE = 5.0D;
    private static final double BOW_RANGE = 6.0D;

    private static ItemStack unbreakable(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("Unbreakable", true);
        return stack;
    }

    public SkeletonWarrior(EntityType<? extends SkeletonWarrior> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 25.0D);
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide) {
            updateCombatWeapon();
        }
        super.aiStep();
    }

    private void updateCombatWeapon() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        double distance = distanceTo(target);
        ItemStack wanted;
        if (distance < MELEE_RANGE) {
            wanted = SWORD;
        } else if (distance > BOW_RANGE) {
            wanted = BOW;
        } else {
            return;
        }
        if (getMainHandItem().is(wanted.getItem())) {
            return;
        }
        setItemSlot(EquipmentSlot.MAINHAND, wanted.copy());
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        reassessWeaponGoal();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }
        if (getMainHandItem().isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
        reassessWeaponGoal();
        return result;
    }

    public ItemStack getSheathedWeapon() {
        return getMainHandItem().is(Items.IRON_SWORD) ? BOW : SWORD;
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrowStack, float velocity) {
        AbstractArrow arrow = super.getArrow(arrowStack, velocity);
        if (arrow instanceof Arrow tipped) {
            tipped.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
        }
        return arrow;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }
}
