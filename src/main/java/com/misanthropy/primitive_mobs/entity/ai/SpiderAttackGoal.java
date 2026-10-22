package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class SpiderAttackGoal extends MeleeAttackGoal {
    private final float reachBonus;

    public SpiderAttackGoal(PathfinderMob mob, float reachBonus) {
        super(mob, 1.0D, true);
        this.reachBonus = reachBonus;
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.getLightLevelDependentMagicValue() >= 0.5F && mob.getRandom().nextInt(100) == 0) {
            mob.setTarget(null);
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return reachBonus + target.getBbWidth();
    }
}
