package com.misanthropy.primitive_mobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GoblinMaceItem extends SwordItem {
    public static final float ARMOR_DAMAGE_FRACTION = 0.1F;

    public GoblinMaceItem() {
        super(ModTiers.GOBLIN_METAL, 0, -3.0F, new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.primitive_mobs.goblin_mace.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
