package com.misanthropy.primitive_mobs.entity.passive;

import com.misanthropy.primitive_mobs.core.ModLootTables;
import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.ai.GrabItemFromFloorGoal;
import com.misanthropy.primitive_mobs.entity.ai.StealFromPlayerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class FilchLizard extends PathfinderMob {
    private static final EntityDimensions CARRYING_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.75F);
    private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.3F);

    @Nullable
    private Set<Item> desiredItems;

    public FilchLizard(EntityType<? extends FilchLizard> type, Level level) {
        super(type, level);
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        goalSelector.addGoal(2, new GrabItemFromFloorGoal(this, 1.2D, this::wants, 8.0D));
        goalSelector.addGoal(3, new StealFromPlayerGoal(this, 0.8D, this::wants, 12.0D));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class,
                player -> !getMainHandItem().isEmpty(), 16.0F, 1.0D, 1.33D, entity -> true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    private boolean wants(ItemStack stack) {
        return !stack.isEmpty() && desiredItems().contains(stack.getItem());
    }

    private Set<Item> desiredItems() {
        if (desiredItems == null) {
            desiredItems = new HashSet<>();
            if (level() instanceof ServerLevel serverLevel) {
                for (ItemStack stack : ModLootTables.roll(serverLevel, ModLootTables.FILCH_LIZARD_STEAL)) {
                    desiredItems.add(stack.getItem());
                }
            }
            if (desiredItems.isEmpty()) {
                desiredItems.add(Items.IRON_INGOT);
            }
        }
        return desiredItems;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getMainHandItem().isEmpty() ? EMPTY_DIMENSIONS : CARRYING_DIMENSIONS;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            refreshDimensions();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable net.minecraft.nbt.CompoundTag tag) {
        int chance = PrimitiveMobsConfig.CONFIG.filchLizardLootChance.get();
        if (chance > 0 && random.nextInt(100) < chance) {
            setItemSlot(EquipmentSlot.MAINHAND,
                    ModLootTables.rollOne(level.getLevel(), random, ModLootTables.FILCH_LIZARD_SPAWN, new ItemStack(Items.IRON_INGOT)));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        ItemStack carried = getMainHandItem();
        if (!carried.isEmpty() && !level().isClientSide) {
            spawnAtLocation(carried.copy(), 1.0F);
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        return super.hurt(source, amount);
    }

    public static boolean checkSpawnRules(EntityType<FilchLizard> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.SAND) && level.getRawBrightness(pos, 0) > 8;
    }
}
