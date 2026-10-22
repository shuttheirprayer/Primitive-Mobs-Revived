package com.misanthropy.primitive_mobs.entity.passive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class PrimitiveMerchant extends AbstractVillager {
    private static final int TRADES_PER_LEVEL = 5;

    private int merchantLevel;
    private int tradesUntilLevelUp = TRADES_PER_LEVEL;

    protected PrimitiveMerchant(EntityType<? extends PrimitiveMerchant> type, Level level) {
        super(type, level);
    }

    protected abstract void addOffersForLevel(MerchantOffers offers, int level);

    protected abstract int maxMerchantLevel();

    @Override
    protected void updateTrades() {
        addOffersForLevel(getOffers(), merchantLevel);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (merchantLevel >= maxMerchantLevel() || --tradesUntilLevelUp > 0) {
            return;
        }
        merchantLevel++;
        tradesUntilLevelUp = TRADES_PER_LEVEL;
        addOffersForLevel(getOffers(), merchantLevel);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MerchantLevel", merchantLevel);
        tag.putInt("TradesUntilLevelUp", tradesUntilLevelUp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        merchantLevel = tag.getInt("MerchantLevel");
        tradesUntilLevelUp = tag.contains("TradesUntilLevelUp") ? tag.getInt("TradesUntilLevelUp") : TRADES_PER_LEVEL;
    }

    protected static MerchantOffer emeraldForItems(RandomSource random, Item item, int min, int max) {
        return new MerchantOffer(new ItemStack(item, price(random, min, max)), new ItemStack(Items.EMERALD), 12, 2, 0.05F);
    }

    protected static MerchantOffer itemsForCurrency(RandomSource random, ItemStack sold, Item currency, int min, int max) {
        int amount = price(random, min, max);
        ItemStack cost;
        ItemStack result;
        if (amount < 0) {
            cost = new ItemStack(currency);
            result = sold.copyWithCount(-amount);
        } else {
            cost = new ItemStack(currency, amount);
            result = sold.copyWithCount(1);
        }
        return new MerchantOffer(cost, result, 12, 2, 0.05F);
    }

    protected static MerchantOffer itemsForEmeralds(RandomSource random, ItemStack sold, int min, int max) {
        return itemsForCurrency(random, sold, Items.EMERALD, min, max);
    }

    protected static MerchantOffer itemsForGold(RandomSource random, ItemStack sold, int min, int max) {
        return itemsForCurrency(random, sold, Items.GOLD_INGOT, min, max);
    }

    private static int price(RandomSource random, int min, int max) {
        return min >= max ? min : min + random.nextInt(max - min + 1);
    }
}
