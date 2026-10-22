package com.misanthropy.primitive_mobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class WonderSapItem extends Item {
    private static final String LOG_TAG = "LogState";
    private static final String COLOR_TAG = "SapColor";
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    public WonderSapItem() {
        super(new Properties());
    }

    public static void setLog(ItemStack stack, BlockState state) {
        stack.getOrCreateTag().putInt(LOG_TAG, Block.getId(state));
    }

    @Nullable
    public static BlockState getLog(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(LOG_TAG)) {
            return null;
        }
        BlockState state = Block.stateById(tag.getInt(LOG_TAG));
        return state.isAir() ? null : state;
    }

    public static void setColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt(COLOR_TAG, color);
    }

    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(COLOR_TAG) ? tag.getInt(COLOR_TAG) : DEFAULT_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        BlockState log = getLog(stack);
        tooltip.add(Component.translatable("item.primitive_mobs.wonder_sap.type",
                log == null ? Component.translatable("item.primitive_mobs.wonder_sap.unknown") : log.getBlock().getName())
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
