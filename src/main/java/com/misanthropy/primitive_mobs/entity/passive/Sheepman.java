package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.entity.ai.TemptWithPredicateGoal;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Sheepman extends PrimitiveMerchant implements IForgeShearable {
    public enum Career {
        SCAVENGER(DyeColor.RED),
        ALCHEMIST(DyeColor.ORANGE),
        THIEF(DyeColor.BLACK);

        private final DyeColor fleece;

        Career(DyeColor fleece) {
            this.fleece = fleece;
        }

        public DyeColor fleece() {
            return fleece;
        }
    }

    private static final Map<DyeColor, ItemLike> WOOL_BY_DYE = Util.make(new EnumMap<>(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        map.put(DyeColor.LIME, Blocks.LIME_WOOL);
        map.put(DyeColor.PINK, Blocks.PINK_WOOL);
        map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        map.put(DyeColor.RED, Blocks.RED_WOOL);
        map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });

    private static final Map<DyeColor, ResourceLocation> LOOT_BY_DYE = Util.make(new EnumMap<>(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, BuiltInLootTables.SHEEP_WHITE);
        map.put(DyeColor.ORANGE, BuiltInLootTables.SHEEP_ORANGE);
        map.put(DyeColor.MAGENTA, BuiltInLootTables.SHEEP_MAGENTA);
        map.put(DyeColor.LIGHT_BLUE, BuiltInLootTables.SHEEP_LIGHT_BLUE);
        map.put(DyeColor.YELLOW, BuiltInLootTables.SHEEP_YELLOW);
        map.put(DyeColor.LIME, BuiltInLootTables.SHEEP_LIME);
        map.put(DyeColor.PINK, BuiltInLootTables.SHEEP_PINK);
        map.put(DyeColor.GRAY, BuiltInLootTables.SHEEP_GRAY);
        map.put(DyeColor.LIGHT_GRAY, BuiltInLootTables.SHEEP_LIGHT_GRAY);
        map.put(DyeColor.CYAN, BuiltInLootTables.SHEEP_CYAN);
        map.put(DyeColor.PURPLE, BuiltInLootTables.SHEEP_PURPLE);
        map.put(DyeColor.BLUE, BuiltInLootTables.SHEEP_BLUE);
        map.put(DyeColor.BROWN, BuiltInLootTables.SHEEP_BROWN);
        map.put(DyeColor.GREEN, BuiltInLootTables.SHEEP_GREEN);
        map.put(DyeColor.RED, BuiltInLootTables.SHEEP_RED);
        map.put(DyeColor.BLACK, BuiltInLootTables.SHEEP_BLACK);
    });

    private static final EntityDataAccessor<Byte> WOOL =
            SynchedEntityData.defineId(Sheepman.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> CAREER =
            SynchedEntityData.defineId(Sheepman.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> SETTLED =
            SynchedEntityData.defineId(Sheepman.class, EntityDataSerializers.BOOLEAN);

    public Sheepman(EntityType<? extends Sheepman> type, Level level) {
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
        goalSelector.addGoal(3, new TemptWithPredicateGoal(this, 1.1D, stack -> stack.is(Items.GOLD_INGOT), false));
        goalSelector.addGoal(4, new net.minecraft.world.entity.ai.goal.AvoidEntityGoal<>(this,
                net.minecraft.world.entity.monster.ZombifiedPiglin.class, 12.0F, 0.8D, 0.8D));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(WOOL, (byte) 0);
        entityData.define(CAREER, (byte) 0);
        entityData.define(SETTLED, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        Career career = Career.values()[random.nextInt(Career.values().length)];
        setCareer(career);
        setFleeceColor(career.fleece());
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    @Override
    protected int maxMerchantLevel() {
        return 3;
    }

    @Override
    protected void addOffersForLevel(MerchantOffers offers, int level) {
        switch (getCareer()) {
            case SCAVENGER -> addScavengerOffers(offers, level);
            case ALCHEMIST -> addAlchemistOffers(offers, level);
            case THIEF -> addThiefOffers(offers, level);
        }
    }

    private void addScavengerOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> offers.add(itemsForGold(random, new ItemStack(Items.NETHER_BRICK), -6, -4));
            case 1 -> {
                offers.add(itemsForGold(random, new ItemStack(Items.FLINT), -4, -2));
                offers.add(itemsForGold(random, new ItemStack(Items.STICK), -10, -8));
            }
            case 2 -> {
                offers.add(itemsForGold(random, new ItemStack(Items.FLINT_AND_STEEL), 1, 2));
                offers.add(itemsForGold(random, new ItemStack(Items.QUARTZ), -5, -3));
            }
            case 3 -> offers.add(itemsForGold(random, new ItemStack(Items.OBSIDIAN), 12, 15));
            default -> {
            }
        }
    }

    private void addAlchemistOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> {
                offers.add(itemsForGold(random, new ItemStack(Items.NETHER_WART), 2, 4));
                offers.add(itemsForGold(random, new ItemStack(Items.GLASS_BOTTLE), 1, 2));
            }
            case 1 -> offers.add(itemsForGold(random, potion(Potions.AWKWARD), 4, 6));
            case 2 -> {
                offers.add(itemsForGold(random, potion(Potions.REGENERATION), 12, 15));
                offers.add(itemsForGold(random, potion(Potions.FIRE_RESISTANCE), 10, 13));
                offers.add(itemsForGold(random, potion(Potions.HEALING), 15, 18));
            }
            case 3 -> offers.add(itemsForGold(random, potion(Potions.LONG_FIRE_RESISTANCE), 32, 40));
            default -> {
            }
        }
    }

    private void addThiefOffers(MerchantOffers offers, int level) {
        switch (level) {
            case 0 -> offers.add(itemsForGold(random, new ItemStack(Items.GLOWSTONE_DUST), -10, -8));
            case 1 -> offers.add(itemsForGold(random, new ItemStack(Items.BLAZE_POWDER), 2, 3));
            case 2 -> {
                offers.add(itemsForGold(random, new ItemStack(Items.MAGMA_CREAM), 5, 8));
                offers.add(itemsForGold(random, new ItemStack(Items.BLAZE_ROD), 5, 8));
            }
            case 3 -> offers.add(itemsForGold(random, new ItemStack(Items.GHAST_TEAR), 35, 40));
            default -> {
            }
        }
    }

    private static ItemStack potion(net.minecraft.world.item.alchemy.Potion type) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), type);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.GOLD_BLOCK) && !isSettled()) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            setSettled(true);
            setPersistenceRequired();
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.0D, getZ(), 8, 0.5D, 0.5D, 0.5D, 0.0D);
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isShearable(ItemStack item, Level level, BlockPos pos) {
        return !isSheared() && !isBaby();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos, int fortune) {
        setSheared(true);
        int count = 1 + random.nextInt(3);
        List<ItemStack> wool = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            wool.add(new ItemStack(WOOL_BY_DYE.get(getFleeceColor())));
        }
        playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
        return wool;
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        Sheep sheep = EntityType.SHEEP.create(level);
        if (sheep == null) {
            super.thunderHit(level, bolt);
            return;
        }
        sheep.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        sheep.finalizeSpawn(level, level.getCurrentDifficultyAt(sheep.blockPosition()), MobSpawnType.CONVERSION, null, null);
        sheep.setColor(getFleeceColor());
        sheep.setNoAi(isNoAi());
        if (hasCustomName()) {
            sheep.setCustomName(getCustomName());
            sheep.setCustomNameVisible(isCustomNameVisible());
        }
        sheep.setInvulnerable(isInvulnerable());
        level.addFreshEntity(sheep);
        discard();
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return isSheared() ? EntityType.SHEEP.getDefaultLootTable() : LOOT_BY_DYE.get(getFleeceColor());
    }

    public DyeColor getFleeceColor() {
        return DyeColor.byId(entityData.get(WOOL) & 15);
    }

    public void setFleeceColor(DyeColor color) {
        byte packed = entityData.get(WOOL);
        entityData.set(WOOL, (byte) (packed & 240 | color.getId() & 15));
    }

    public boolean isSheared() {
        return (entityData.get(WOOL) & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte packed = entityData.get(WOOL);
        entityData.set(WOOL, (byte) (sheared ? packed | 16 : packed & -17));
    }

    public Career getCareer() {
        return Career.values()[Math.floorMod(entityData.get(CAREER), Career.values().length)];
    }

    public void setCareer(Career career) {
        entityData.set(CAREER, (byte) career.ordinal());
    }

    public boolean isSettled() {
        return entityData.get(SETTLED);
    }

    public void setSettled(boolean settled) {
        entityData.set(SETTLED, settled);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !isSettled();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean success) {
        return success ? SoundEvents.SHEEP_AMBIENT : SoundEvents.SHEEP_HURT;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Wool", entityData.get(WOOL));
        tag.putByte("Career", (byte) getCareer().ordinal());
        tag.putBoolean("Settled", isSettled());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(WOOL, tag.getByte("Wool"));
        entityData.set(CAREER, tag.getByte("Career"));
        setSettled(tag.getBoolean("Settled"));
    }

    public static boolean checkSpawnRules(EntityType<Sheepman> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isValidSpawn(level, pos.below(), type)
                && !level.getBlockState(pos.below()).is(Blocks.BEDROCK);
    }
}
