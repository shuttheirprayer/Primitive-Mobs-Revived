package com.misanthropy.primitive_mobs.item;

import com.misanthropy.primitive_mobs.entity.projectile.ThrownPrimitiveEgg;
import com.misanthropy.primitive_mobs.entity.projectile.ThrownSpiderEgg;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownEggItem extends Item {
    private final ThrownPrimitiveEgg.Kind kind;
    private final int spawnChance;
    private final boolean glowing;

    public ThrownEggItem(ThrownPrimitiveEgg.Kind kind, int spawnChance, boolean glowing) {
        super(new Item.Properties().stacksTo(16));
        this.kind = kind;
        this.spawnChance = spawnChance;
        this.glowing = glowing;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return glowing || super.isFoil(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            ThrownPrimitiveEgg egg = kind == ThrownPrimitiveEgg.Kind.BABY_SPIDER
                    ? new ThrownSpiderEgg(level, player, spawnChance)
                    : new ThrownPrimitiveEgg(level, player, kind, spawnChance);
            egg.setItem(stack);
            egg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(egg);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
