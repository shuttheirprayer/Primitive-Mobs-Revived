package com.misanthropy.primitive_mobs.entity.ai;

import com.misanthropy.primitive_mobs.entity.passive.GroveSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ManageSaplingsGoal extends MoveToBlockGoal {
    private final GroveSprite sprite;
    private int delay;
    private boolean planting;
    private BlockState plantState;

    public ManageSaplingsGoal(GroveSprite sprite) {
        super(sprite, 0.7D, 16, 2);
        this.sprite = sprite;
        this.delay = sprite.getRandom().nextInt(100) + 100;
    }

    @Override
    public boolean canUse() {
        if (sprite.isCinder()) {
            return false;
        }
        if (--delay > 0) {
            return false;
        }
        ItemStack sapling = carriedSapling();
        planting = sprite.plantsSaplings() && sprite.getSaplings() > 0 && !sapling.isEmpty();
        plantState = planting ? ((BlockItem) sapling.getItem()).getBlock().defaultBlockState() : null;
        return super.canUse();
    }

    @Override
    public double acceptedDistance() {
        return 3.5D;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        if (planting) {
            return plantState != null && level.getBlockState(pos).isAir() && plantState.canSurvive(level, pos);
        }
        return level.getBlockState(pos).is(BlockTags.SAPLINGS);
    }

    @Override
    public void tick() {
        super.tick();
        sprite.getLookControl().setLookAt(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D,
                10.0F, sprite.getMaxHeadXRot());
        if (!isReachedTarget()) {
            return;
        }
        if (planting) {
            plant();
        } else {
            fertilize();
        }
    }

    private void plant() {
        ItemStack sapling = carriedSapling();
        if (sapling.isEmpty()) {
            return;
        }
        Block block = ((BlockItem) sapling.getItem()).getBlock();
        sprite.level().setBlockAndUpdate(blockPos, block.defaultBlockState());
        sprite.level().playSound(null, blockPos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        sprite.setSaplings(sprite.getSaplings() - 1);
        delay = sprite.getRandom().nextInt(300) + 300;
    }

    private void fertilize() {
        BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), sprite.level(), blockPos);
        delay = sprite.getRandom().nextInt(200) + 200;
    }

    private ItemStack carriedSapling() {
        ItemStack held = sprite.getMainHandItem();
        return held.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(BlockTags.SAPLINGS) ? held : ItemStack.EMPTY;
    }
}
