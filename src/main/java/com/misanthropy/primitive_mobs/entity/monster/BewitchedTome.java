package com.misanthropy.primitive_mobs.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BewitchedTome extends Monster {
    private float bob;
    private float bobOld;
    private float bobSpeed = 1.0F;
    private float bobSpeedOld;

    public BewitchedTome(EntityType<? extends BewitchedTome> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, false));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
            level().addParticle(ParticleTypes.ENCHANT,
                    getX() + (random.nextFloat() - random.nextFloat()),
                    getY() + 1.0D + (random.nextFloat() - random.nextFloat()),
                    getZ() + (random.nextFloat() - random.nextFloat()),
                    0.0D, 0.0D, 0.0D);
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
        if (random.nextBoolean()) {
            spawnAtLocation(new ItemStack(Items.BOOK), 1.0F);
            return;
        }
        BuiltInRegistries.ENCHANTMENT.getRandom(random).ifPresent(holder -> {
            Enchantment enchantment = holder.value();
            ItemStack book = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(enchantment, 1 + random.nextInt(enchantment.getMaxLevel())));
            spawnAtLocation(book, 1.0F);
        });
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
        return SoundEvents.WOOL_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOL_BREAK;
    }

    public static boolean checkSpawnRules(EntityType<BewitchedTome> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return pos.getY() < 40 && Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
