package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public SpiderTargetGoal(Mob mob, Class<T> targetType) {
        super(mob, targetType, true);
    }

    @Override
    public boolean canUse() {
        return mob.getLightLevelDependentMagicValue() < 0.5F && super.canUse();
    }
}
