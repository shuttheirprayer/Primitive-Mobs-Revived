package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.core.ModLootTables;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collection;

public class HauntedTool extends Monster {
    private float bob;
    private float bobOld;
    private float bobSpeed = 1.0F;
    private float bobSpeedOld;

    public HauntedTool(EntityType<? extends HauntedTool> type, Level level) {
        super(type, level);
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3D, false));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (getMainHandItem().isEmpty()) {
            ItemStack tool = ModLootTables.rollOne(level.getLevel(), random, ModLootTables.HAUNTED_TOOL_WEAPONS, new ItemStack(Items.WOODEN_SWORD));
            setItemSlot(EquipmentSlot.MAINHAND, tool);
            applyToolStats(tool);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    private void applyToolStats(ItemStack tool) {
        if (!tool.isDamageableItem()) {
            return;
        }
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(healthFromTool(tool));
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damageFromTool(tool));
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speedFromTool(tool));
        setHealth(getMaxHealth());
    }

    private static double healthFromTool(ItemStack tool) {
        return Math.min(100.0D, tool.getMaxDamage() / 10.0D);
    }

    private static double damageFromTool(ItemStack tool) {
        double modifier = firstModifier(tool, Attributes.ATTACK_DAMAGE);
        return Mth.clamp(modifier / 2.0D, 1.0D, 8.0D);
    }

    private static double speedFromTool(ItemStack tool) {
        Collection<AttributeModifier> modifiers =
                tool.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED);
        if (modifiers.isEmpty()) {
            return 0.2D;
        }
        double amount = modifiers.iterator().next().getAmount();
        return Mth.clamp(0.5D + amount * 0.1D, 0.1D, 0.3D);
    }

    private static double firstModifier(ItemStack tool, Attribute attribute) {
        Collection<AttributeModifier> modifiers = tool.getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute);
        return modifiers.isEmpty() ? 0.0D : modifiers.iterator().next().getAmount();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void aiStep() {
        bobOld = bob;
        bobSpeedOld = bobSpeed;
        bobSpeed = Math.max(0.2F, bobSpeed * 0.9F);
        bob += bobSpeed;

        if (level().isClientSide) {
            level().addParticle(ParticleTypes.SMOKE,
                    getX() + (random.nextFloat() - random.nextFloat()) / 2.0D,
                    getY() + 1.0D + (random.nextFloat() - random.nextFloat()) / 2.0D,
                    getZ() + (random.nextFloat() - random.nextFloat()) / 2.0D,
                    0.0D, 0.0D, 0.0D);
        } else if (getMainHandItem().isEmpty()) {
            discard();
            return;
        }
        super.aiStep();
    }

    public float getBob(float partialTick) {
        return bobOld + (bob - bobOld) * partialTick;
    }

    public float getBobSpeed(float partialTick) {
        return bobSpeedOld + (bobSpeed - bobSpeedOld) * partialTick;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        ItemStack tool = getMainHandItem().copy();
        if (tool.isEmpty()) {
            return;
        }
        if (!PrimitiveMobsConfig.CONFIG.hauntedToolFullDurability.get() && tool.isDamageableItem()) {
            int clamped = Math.min(looting, 3);
            int max = tool.getMaxDamage();
            int min = max / 10 * (clamped + 1);
            int upper = max / 2 * (clamped + 2);
            tool.setDamageValue(max - Mth.nextInt(random, Math.min(min, max), Math.min(upper, max)));
        }
        spawnAtLocation(tool, 1.0F);
    }

    @Override
    public boolean onClimbable() {
        return horizontalCollision;
    }

    @Override
    protected void jumpFromGround() {
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ITEM_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ITEM_BREAK;
    }
}
