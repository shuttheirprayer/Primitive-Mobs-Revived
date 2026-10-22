package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LostMiner extends PrimitiveMerchant {
    public enum Career {
        ORE, STONE, GEM
    }

    private static final EntityDataAccessor<Boolean> RESCUED =
            SynchedEntityData.defineId(LostMiner.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CAREER =
            SynchedEntityData.defineId(LostMiner.class, EntityDataSerializers.BYTE);

    @Nullable
    private Player beckoningPlayer;

    public LostMiner(EntityType<? extends LostMiner> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractVillager.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 0.5D));
        goalSelector.addGoal(2, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.4D));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(RESCUED, false);
        entityData.define(CAREER, (byte) 0);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        setCareer(Career.values()[random.nextInt(Career.values().length)]);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRescued() || level().isClientSide) {
            return;
        }
        if (tickCount % 5 == 0) {
            updateBeckoning();
        }
        if (beckoningPlayer != null) {
            getNavigation().moveTo(beckoningPlayer, 0.65D);
        }
    }

    private void updateBeckoning() {
        Player nearby = level().getNearestPlayer(this, 8.0D);
        beckoningPlayer = nearby != null && nearby.distanceToSqr(this) > 4.0D ? nearby : null;

        if (level() instanceof ServerLevel serverLevel) {
            ParticleOptions particle = canBeRescued() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.SPLASH;
            double angle = getYRot() * ((float) Math.PI / 180F);
            serverLevel.sendParticles(particle,
                    getX() - Math.sin(angle) / 3.0D,
                    getY() + 1.8D + random.nextDouble() / 3.0D,
                    getZ() + Math.cos(angle) / 3.0D,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isRescued() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
            if (!canBeRescued()) {
                return InteractionResult.PASS;
            }
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            rescue();
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    private void rescue() {
        setRescued(true);
        setPersistenceRequired();
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.0D, getZ(), 8, 0.5D, 0.5D, 0.5D, 0.0D);
        }
        int reward = PrimitiveMobsConfig.CONFIG.lostMinerRewardBase.get();
        int bonus = PrimitiveMobsConfig.CONFIG.lostMinerRewardBonus.get();
        int total = reward + (bonus > 0 ? random.nextInt(bonus) : 0);
        for (int i = 0; i < total; i++) {
            spawnAtLocation(Items.EMERALD);
        }
    }

    public boolean canBeRescued() {
        return getY() > 50.0D && level().canSeeSky(BlockPos.containing(getX(), getEyeY(), getZ()));
    }

    public boolean isRescued() {
        return entityData.get(RESCUED);
    }

    public void setRescued(boolean rescued) {
        entityData.set(RESCUED, rescued);
    }

    public Career getCareer() {
        return Career.values()[Math.floorMod(entityData.get(CAREER), Career.values().length)];
    }

    public void setCareer(Career career) {
        entityData.set(CAREER, (byte) career.ordinal());
    }

    @Override
    protected int maxMerchantLevel() {
        return getCareer() == Career.STONE ? 3 : 2;
    }

    @Override
    protected void addOffersForLevel(MerchantOffers offers, int level) {
        switch (getCareer()) {
            case ORE -> addOreOffers(offers, level);
            case STONE -> addStoneOffers(offers, level);
            case GEM -> addGemOffers(offers, level);
        }
    }

    private void addOreOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.COAL), -22, -14));
            case 1 -> {
                offers.add(itemsForEmeralds(random, new ItemStack(Items.RAW_IRON), -8, -6));
                offers.add(itemsForEmeralds(random, new ItemStack(Items.RAW_GOLD), -9, -7));
            }
            case 2 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.OBSIDIAN), -3, -1));
            default -> {
            }
        }
    }

    private void addStoneOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.COBBLESTONE), -60, -45));
            case 1 -> {
                offers.add(itemsForEmeralds(random, new ItemStack(Items.GRANITE), -15, -10));
                offers.add(itemsForEmeralds(random, new ItemStack(Items.DIORITE), -15, -10));
                offers.add(itemsForEmeralds(random, new ItemStack(Items.ANDESITE), -15, -10));
            }
            case 2 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.STONE), -20, -15));
            case 3 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.OBSIDIAN), -3, -1));
            default -> {
            }
        }
    }

    private void addGemOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.LAPIS_LAZULI), -4, -3));
            case 1 -> {
                offers.add(itemsForEmeralds(random, new ItemStack(Items.REDSTONE), -6, -3));
                offers.add(itemsForEmeralds(random, new ItemStack(Items.QUARTZ), -6, -3));
            }
            case 2 -> offers.add(itemsForEmeralds(random, new ItemStack(Items.DIAMOND), 2, 3));
            default -> {
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!PrimitiveMobsConfig.CONFIG.lostMinerSounds.get()) {
            return null;
        }
        return isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return PrimitiveMobsConfig.CONFIG.lostMinerSounds.get() ? SoundEvents.VILLAGER_HURT : SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PrimitiveMobsConfig.CONFIG.lostMinerSounds.get() ? SoundEvents.VILLAGER_DEATH : SoundEvents.GENERIC_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean success) {
        return success ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !isRescued();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Rescued", isRescued());
        tag.putByte("Career", (byte) getCareer().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRescued(tag.getBoolean("Rescued"));
        entityData.set(CAREER, tag.getByte("Career"));
    }

    public static boolean checkSpawnRules(EntityType<LostMiner> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        if (pos.getY() >= 55 || pos.getY() < level.getMinBuildHeight() + 5) {
            return false;
        }
        BlockState below = level.getBlockState(pos.below());
        return below.isValidSpawn(level, pos.below(), type) && !below.is(Blocks.BEDROCK);
    }
}
