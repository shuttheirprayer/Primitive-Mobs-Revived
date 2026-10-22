package com.misanthropy.primitive_mobs.entity.ai;

import com.misanthropy.primitive_mobs.entity.passive.GroveSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GroveSpriteTemptGoal extends TemptWithPredicateGoal {
    private final GroveSprite sprite;

    public GroveSpriteTemptGoal(GroveSprite sprite, double speed) {
        super(sprite, speed, stack -> wanted(sprite, stack), false);
        this.sprite = sprite;
    }

    private static boolean wanted(GroveSprite sprite, ItemStack stack) {
        if (stack.isEmpty() || sprite.isCinder()) {
            return false;
        }
        ItemStack held = sprite.getMainHandItem();
        return stack.is(Items.BONE_MEAL) || (!held.isEmpty() && stack.is(held.getItem()));
    }

    @Override
    public void start() {
        super.start();
        sprite.setBegging(true);
    }

    @Override
    public void stop() {
        super.stop();
        sprite.setBegging(false);
    }
}
