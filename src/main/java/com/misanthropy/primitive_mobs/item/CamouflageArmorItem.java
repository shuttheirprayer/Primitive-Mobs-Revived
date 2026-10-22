package com.misanthropy.primitive_mobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class CamouflageArmorItem extends DyeableArmorItem {
    private static final String DISABLED_TAG = "CamouflageDisabled";
    private static final String BLOCK_TAG = "CamouflageBlock";
    private static final int UPDATE_INTERVAL = 10;

    public CamouflageArmorItem(ArmorItem.Type type) {
        super(ArmorMaterials.LEATHER, type, new Properties());
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide || isDisabled(stack) || (player.tickCount % UPDATE_INTERVAL) != 0) {
            return;
        }
        BlockState state = sampleGround(level, player);
        if (state == null) {
            return;
        }
        int stateId = Block.getId(state);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt(BLOCK_TAG) == stateId) {
            return;
        }
        tag.putInt(BLOCK_TAG, stateId);
        setColor(stack, state.getMapColor(level, player.blockPosition()).col);
    }

    @Nullable
    private static BlockState sampleGround(Level level, LivingEntity wearer) {
        BlockPos pos = wearer.blockPosition();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }
        return state.isAir() ? null : state;
    }

    public static boolean isDisabled(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(DISABLED_TAG);
    }

    public static void setDisabled(ItemStack stack, boolean disabled) {
        stack.getOrCreateTag().putBoolean(DISABLED_TAG, disabled);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(isDisabled(stack)
                        ? "item.primitive_mobs.camouflage.disabled"
                        : "item.primitive_mobs.camouflage.enabled")
                .withStyle(ChatFormatting.GRAY));
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(BLOCK_TAG)) {
            BlockState state = Block.stateById(tag.getInt(BLOCK_TAG));
            tooltip.add(Component.translatable("item.primitive_mobs.camouflage.block",
                    state.getBlock().getName()).withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
