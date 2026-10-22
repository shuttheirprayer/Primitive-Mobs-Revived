package com.misanthropy.primitive_mobs.item;

import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.entity.monster.BabySpider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpiderEggshellItem extends Item {
    public SpiderEggshellItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || !(target instanceof BabySpider spider) || spider.getGrowthLevel() != 0 || !spider.isAlive()) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemEntity egg = new ItemEntity(spider.level(), spider.getX(), spider.getY() + 0.5D, spider.getZ(),
                new ItemStack(ModItems.SPIDER_EGG.get()));
        egg.setDefaultPickUpDelay();
        spider.level().addFreshEntity(egg);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        spider.discard();
        return InteractionResult.CONSUME;
    }
}
