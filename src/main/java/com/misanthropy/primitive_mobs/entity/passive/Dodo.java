package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.TemptWithPredicateGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

public class Dodo extends Chicken {
    private static final ItemStack[] BREEDING_ITEMS = {
            new ItemStack(Items.LIGHT_BLUE_DYE),
            new ItemStack(Items.BEETROOT_SEEDS),
            new ItemStack(Items.BROWN_MUSHROOM),
            new ItemStack(Items.RED_MUSHROOM),
            new ItemStack(Items.PUMPKIN_SEEDS),
            new ItemStack(Items.MELON_SEEDS),
            new ItemStack(Items.WHEAT_SEEDS),
            new ItemStack(Items.APPLE),
            new ItemStack(Items.BEETROOT),
            new ItemStack(Items.CARROT),
            new ItemStack(Items.MELON_SLICE),
            new ItemStack(Items.WHEAT),
            new ItemStack(Items.SUGAR_CANE),
            new ItemStack(Items.POTATO),
            new ItemStack(Items.PUMPKIN),
            new ItemStack(Items.MELON)
    };

    private int shedTime;
    private int dodoEggTime;

    public Dodo(EntityType<? extends Dodo> type, Level level) {
        super(type, level);
        shedTime = random.nextInt(6000) + 6000;
        dodoEggTime = random.nextInt(10000) + 10000;
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(3, new TemptWithPredicateGoal(this, 1.0D, this::isFood, false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        ItemStack wanted = getMainHandItem();
        return !wanted.isEmpty() && stack.is(wanted.getItem());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            return;
        }
        if (isInLove()) {
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else if (getMainHandItem().isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, BREEDING_ITEMS[random.nextInt(BREEDING_ITEMS.length)].copy());
        }

        if (isBaby() || isChickenJockey()) {
            return;
        }

        if (PrimitiveMobsConfig.CONFIG.dodoMycelium.get() && --shedTime <= 0) {
            playSound(SoundEvents.GRASS_BREAK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            convertGrassBelow();
            shedTime = random.nextInt(6000) + 6000;
        }

        if (--dodoEggTime <= 0) {
            playSound(SoundEvents.CHICKEN_EGG, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            spawnAtLocation(ModItems.DODO_EGG.get());
            dodoEggTime = random.nextInt(10000) + 10000;
        }
    }

    private void convertGrassBelow() {
        BlockPos pos = blockPosition().below();
        if (level().getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
            level().setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState());
        }
    }

    @Nullable
    @Override
    public Chicken getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob partner) {
        return ModEntities.DODO.get().create(level);
    }

    @Override
    public float getVoicePitch() {
        return isBaby()
                ? (random.nextFloat() - random.nextFloat()) * 0.2F + 1.1F
                : (random.nextFloat() - random.nextFloat()) * 0.2F + 0.6F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShedTime", shedTime);
        tag.putInt("DodoEggTime", dodoEggTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ShedTime")) {
            shedTime = tag.getInt("ShedTime");
        }
        if (tag.contains("DodoEggTime")) {
            dodoEggTime = tag.getInt("DodoEggTime");
        }
    }

    public static boolean checkSpawnRules(EntityType<Dodo> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        net.minecraft.world.level.block.state.BlockState below = level.getBlockState(pos.below());
        return (below.is(net.minecraft.tags.BlockTags.MOOSHROOMS_SPAWNABLE_ON) || below.is(net.minecraft.tags.BlockTags.DIRT))
                && isBrightEnoughToSpawn(level, pos);
    }

    private static boolean isBrightEnoughToSpawn(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) > 8;
    }
}
