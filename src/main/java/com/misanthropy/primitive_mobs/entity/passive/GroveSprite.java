package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.core.ModSounds;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.GroveSpriteTemptGoal;
import com.misanthropy.primitive_mobs.entity.ai.ManageSaplingsGoal;
import com.misanthropy.primitive_mobs.item.WonderSapItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class GroveSprite extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> CINDER =
            SynchedEntityData.defineId(GroveSprite.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BEGGING =
            SynchedEntityData.defineId(GroveSprite.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SAPLINGS =
            SynchedEntityData.defineId(GroveSprite.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEAVES_STATE =
            SynchedEntityData.defineId(GroveSprite.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LOG_STATE =
            SynchedEntityData.defineId(GroveSprite.class, EntityDataSerializers.INT);

    private static final int CINDER_LEAF_COLOR = 0xB16400;
    private static final int CINDER_LOG_COLOR = 0x5A5650;

    private int saplingTimer;

    public GroveSprite(EntityType<? extends GroveSprite> type, Level level) {
        super(type, level);
        setDropChance(EquipmentSlot.MAINHAND, 1.0F);
        setDropChance(EquipmentSlot.OFFHAND, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(2, new ManageSaplingsGoal(this));
        goalSelector.addGoal(3, new GroveSpriteTemptGoal(this, 1.1D));
        goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0D));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> isCinder()));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CINDER, false);
        entityData.define(BEGGING, false);
        entityData.define(SAPLINGS, 0);
        entityData.define(LEAVES_STATE, net.minecraft.world.level.block.Block.getId(Blocks.OAK_LEAVES.defaultBlockState()));
        entityData.define(LOG_STATE, net.minecraft.world.level.block.Block.getId(Blocks.OAK_LOG.defaultBlockState()));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        adoptNearbyTree(level);
        ItemStack sapling = saplingFromLeaves();
        if (getMainHandItem().isEmpty() && !sapling.isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, sapling);
        }
        setItemSlot(EquipmentSlot.OFFHAND, makeWonderSap(1));
        setSaplings(1 + random.nextInt(4));
        saplingTimer = random.nextInt(1000) + 1000;
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    private void adoptNearbyTree(LevelAccessor level) {
        BlockPos origin = blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dy = -2; dy <= 8; dy++) {
            for (int dx = -10; dx <= 10; dx++) {
                for (int dz = -10; dz <= 10; dz++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    BlockState state = readBlock(level, cursor);
                    if (state == null || !state.is(BlockTags.LOGS)) {
                        continue;
                    }
                    BlockState leaves = findLeavesAbove(level, cursor);
                    if (leaves != null) {
                        setLog(state);
                        setLeaves(leaves);
                        return;
                    }
                }
            }
        }
    }

    @Nullable
    private BlockState findLeavesAbove(LevelAccessor level, BlockPos logPos) {
        BlockPos.MutableBlockPos cursor = logPos.mutable();
        for (int i = 0; i < 12; i++) {
            cursor.move(0, 1, 0);
            BlockState state = readBlock(level, cursor);
            if (state != null && state.is(BlockTags.LEAVES)) {
                return state;
            }
        }
        return null;
    }

    @Nullable
    private static BlockState readBlock(LevelAccessor level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) {
            return null;
        }
        if (!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
            return null;
        }
        return level.getBlockState(pos);
    }

    private ItemStack saplingFromLeaves() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return ItemStack.EMPTY;
        }
        BlockState leaves = getLeaves();
        for (int attempt = 0; attempt < 40; attempt++) {
            List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(leaves, serverLevel, blockPosition(), null);
            for (ItemStack drop : drops) {
                if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTags.SAPLINGS)) {
                    return new ItemStack(drop.getItem());
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack makeWonderSap(int count) {
        ItemStack sap = new ItemStack(ModItems.WONDER_SAP.get(), count);
        WonderSapItem.setLog(sap, getLog());
        WonderSapItem.setColor(sap, getLogColor());
        return sap;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (isOnFire() && !isCinder()) {
                becomeCinder();
            }
            if (!isCinder() && --saplingTimer <= 0) {
                setSaplings(getSaplings() + 1);
                saplingTimer = random.nextInt(1000) + 1000;
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.0D, getZ(), 8, 0.5D, 0.5D, 0.5D, 0.0D);
                }
            }
        } else if (isCinder()) {
            level().addParticle(ParticleTypes.FLAME, getX(), getY() + getBbHeight() + 0.2D, getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    private void becomeCinder() {
        setCinder(true);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TORCH));
        setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    @Override
    public boolean fireImmune() {
        return isCinder() || super.fireImmune();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isCinder() && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.LAVA) || source.is(DamageTypes.IN_WALL))) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && isCinder()) {
            target.setSecondsOnFire(8);
        }
        return hurt;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isCinder()) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && !getMainHandItem().isEmpty() && stack.is(getMainHandItem().getItem())) {
            return acceptSapling(player, stack);
        }
        if (stack.is(Items.WHEAT_SEEDS)) {
            return acceptSeeds(player, stack);
        }
        if (stack.is(Items.BONE_MEAL) && getSaplings() > 0) {
            return tradeSap(player, stack);
        }
        return super.mobInteract(player, hand);
    }

    private InteractionResult acceptSapling(Player player, ItemStack stack) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        consume(player, stack);
        setPersistenceRequired();
        setSaplings(getSaplings() + 1);
        celebrate(ParticleTypes.HAPPY_VILLAGER);
        return InteractionResult.CONSUME;
    }

    private InteractionResult acceptSeeds(Player player, ItemStack stack) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        consume(player, stack);
        setHealth(getMaxHealth());
        celebrate(ParticleTypes.HEART);
        return InteractionResult.CONSUME;
    }

    private InteractionResult tradeSap(Player player, ItemStack stack) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        consume(player, stack);
        setSaplings(getSaplings() - 1);
        celebrate(ParticleTypes.ENCHANTED_HIT);
        ItemEntity dropped = spawnAtLocation(makeWonderSap(random.nextInt(4) + 1), 1.0F);
        if (dropped != null) {
            dropped.setDefaultPickUpDelay();
        }
        return InteractionResult.CONSUME;
    }

    private void consume(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private void celebrate(net.minecraft.core.particles.ParticleOptions particle) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, getX(), getY() + 1.0D, getZ(), 8, 0.5D, 0.5D, 0.5D, 0.0D);
        }
        playSound(ModSounds.GROVE_SPRITE_THANKS.get(), 1.0F, 1.0F);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        if (isCinder()) {
            spawnAtLocation(new ItemStack(Items.COAL, random.nextInt(5) + looting), 1.0F);
            return;
        }
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GROVE_SPRITE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GROVE_SPRITE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GROVE_SPRITE_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return isCinder()
                ? (random.nextFloat() - random.nextFloat()) * 0.2F + 0.5F
                : (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    public boolean isCinder() {
        return entityData.get(CINDER);
    }

    public void setCinder(boolean cinder) {
        entityData.set(CINDER, cinder);
    }

    public boolean isBegging() {
        return entityData.get(BEGGING);
    }

    public void setBegging(boolean begging) {
        entityData.set(BEGGING, begging);
    }

    public int getSaplings() {
        return entityData.get(SAPLINGS);
    }

    public void setSaplings(int saplings) {
        entityData.set(SAPLINGS, Math.max(0, saplings));
    }

    public BlockState getLeaves() {
        return net.minecraft.world.level.block.Block.stateById(entityData.get(LEAVES_STATE));
    }

    public void setLeaves(BlockState state) {
        entityData.set(LEAVES_STATE, net.minecraft.world.level.block.Block.getId(state));
    }

    public BlockState getLog() {
        return net.minecraft.world.level.block.Block.stateById(entityData.get(LOG_STATE));
    }

    public void setLog(BlockState state) {
        entityData.set(LOG_STATE, net.minecraft.world.level.block.Block.getId(state));
    }

    public int getLeavesColor() {
        return isCinder() ? CINDER_LEAF_COLOR : getLeaves().getMapColor(level(), blockPosition()).col;
    }

    public int getLogColor() {
        return isCinder() ? CINDER_LOG_COLOR : getLog().getMapColor(level(), blockPosition()).col;
    }

    public boolean plantsSaplings() {
        return PrimitiveMobsConfig.CONFIG.groveSpritesPlant.get()
                && level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Cinder", isCinder());
        tag.putInt("Saplings", getSaplings());
        tag.putInt("SaplingTimer", saplingTimer);
        tag.putInt("LeavesState", entityData.get(LEAVES_STATE));
        tag.putInt("LogState", entityData.get(LOG_STATE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setCinder(tag.getBoolean("Cinder"));
        setSaplings(tag.getInt("Saplings"));
        saplingTimer = tag.getInt("SaplingTimer");
        if (tag.contains("LeavesState")) {
            entityData.set(LEAVES_STATE, tag.getInt("LeavesState"));
        }
        if (tag.contains("LogState")) {
            entityData.set(LOG_STATE, tag.getInt("LogState"));
        }
    }

    public static boolean checkSpawnRules(EntityType<GroveSprite> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        return (below.is(BlockTags.ANIMALS_SPAWNABLE_ON) || below.is(BlockTags.DIRT)) && level.getRawBrightness(pos, 0) > 8;
    }
}
