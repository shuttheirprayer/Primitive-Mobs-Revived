package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.TemptWithPredicateGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TravelingMerchant extends PrimitiveMerchant {
    private static final EntityDataAccessor<Boolean> SETTLED =
            SynchedEntityData.defineId(TravelingMerchant.class, EntityDataSerializers.BOOLEAN);

    private static final Item[] FOOD_PURCHASES = {
            Items.CARROT, Items.BREAD, Items.APPLE, Items.COOKED_BEEF,
            Items.COOKED_CHICKEN, Items.COOKED_COD, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT
    };
    private static final int[][] FOOD_PRICES = {
            {10, 14}, {4, 6}, {4, 6}, {4, 6}, {5, 7}, {3, 5}, {4, 6}, {3, 5}
    };

    public TravelingMerchant(EntityType<? extends TravelingMerchant> type, Level level) {
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
        goalSelector.addGoal(3, new TemptWithPredicateGoal(this, 1.1D, stack -> stack.is(Items.EMERALD), false));
        goalSelector.addGoal(4, new MoveThroughVillageGoal(this, 0.45D, false, 4, () -> false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        if (PrimitiveMobsConfig.CONFIG.merchantVisitsHouses.get()) {
            goalSelector.addGoal(4, new OpenDoorGoal(this, true));
            setCanPickUpLoot(false);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SETTLED, false);
    }

    @Override
    protected int maxMerchantLevel() {
        return 2;
    }

    @Override
    protected void addOffersForLevel(MerchantOffers offers, int level) {
        if (level == 0) {
            int food = random.nextInt(FOOD_PURCHASES.length);
            offers.add(emeraldForItems(random, FOOD_PURCHASES[food], FOOD_PRICES[food][0], FOOD_PRICES[food][1]));
        }
        List<Supplier<MerchantOffer>> pool = switch (level) {
            case 0 -> tier2();
            case 1 -> tier3();
            default -> tier4();
        };
        addDistinct(offers, pool, 2);
    }

    private void addDistinct(MerchantOffers offers, List<Supplier<MerchantOffer>> pool, int amount) {
        List<Supplier<MerchantOffer>> remaining = new ArrayList<>(pool);
        for (int i = 0; i < amount && !remaining.isEmpty(); i++) {
            offers.add(remaining.remove(random.nextInt(remaining.size())).get());
        }
    }

    private List<Supplier<MerchantOffer>> tier2() {
        return List.of(
                () -> itemsForEmeralds(random, new ItemStack(Items.PUMPKIN_SEEDS), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.MELON_SEEDS), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.GOLD_INGOT), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.ARROW), -15, -10),
                () -> itemsForEmeralds(random, new ItemStack(Items.SLIME_BALL), -7, -4),
                () -> itemsForEmeralds(random, new ItemStack(Items.LIGHT_BLUE_DYE), -8, -6),
                () -> itemsForEmeralds(random, new ItemStack(Items.BEETROOT), -16, -10),
                () -> itemsForEmeralds(random, new ItemStack(Items.PAPER), -38, -26),
                () -> itemsForEmeralds(random, new ItemStack(Items.FLINT), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.SNOWBALL), -16, -14)
        );
    }

    private List<Supplier<MerchantOffer>> tier3() {
        return List.of(
                () -> itemsForEmeralds(random, new ItemStack(Items.BONE), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.FIRE_CHARGE), -10, -8),
                () -> itemsForEmeralds(random, new ItemStack(Items.IRON_INGOT), -10, -7),
                () -> itemsForEmeralds(random, new ItemStack(Items.ARROW), -15, -10),
                () -> itemsForEmeralds(random, new ItemStack(Items.STRING), -20, -15),
                () -> itemsForEmeralds(random, new ItemStack(Items.CLAY_BALL), -10, -6),
                () -> itemsForEmeralds(random, new ItemStack(Items.RABBIT_FOOT), -3, -2),
                () -> itemsForEmeralds(random, new ItemStack(Items.REDSTONE), -6, -3),
                () -> itemsForEmeralds(random, new ItemStack(Items.BLAZE_POWDER), -6, -4),
                () -> itemsForEmeralds(random, new ItemStack(Items.BOOK), -6, -4)
        );
    }

    private List<Supplier<MerchantOffer>> tier4() {
        return List.of(
                () -> itemsForEmeralds(random, new ItemStack(Items.ENDER_PEARL), -1, -1),
                () -> itemsForEmeralds(random, new ItemStack(Items.EXPERIENCE_BOTTLE), 1, 4),
                () -> itemsForEmeralds(random, new ItemStack(Items.DIAMOND), 2, 3),
                () -> itemsForEmeralds(random, new ItemStack(Items.GHAST_TEAR), 2, 4),
                () -> itemsForEmeralds(random, new ItemStack(Items.PRISMARINE_CRYSTALS), -7, -4),
                () -> itemsForEmeralds(random, new ItemStack(Items.NAME_TAG), -10, -5),
                () -> itemsForEmeralds(random, PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), Potions.POISON), -15, -10),
                () -> itemsForEmeralds(random, new ItemStack(Items.SADDLE), 4, 6),
                () -> itemsForEmeralds(random, new ItemStack(Items.CHORUS_FRUIT), -3, -2),
                () -> itemsForEmeralds(random, new ItemStack(Items.GOLDEN_APPLE), 1, 1)
        );
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.EMERALD_BLOCK) && !isSettled() && PrimitiveMobsConfig.CONFIG.merchantCanSettle.get()) {
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
        return isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Settled", isSettled());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSettled(tag.getBoolean("Settled"));
    }

    public static boolean checkSpawnRules(EntityType<TravelingMerchant> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        if (pos.getY() <= 20 || level.getRawBrightness(pos, 0) <= 8) {
            return false;
        }
        BlockPos groundPos = pos.below();
        return level.getBlockState(groundPos).isValidSpawn(level, groundPos, type);
    }
}
