package com.misanthropy.primitive_mobs.entity.ai;

import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PrimitiveSitGoal<T extends Mob & PrimitiveTameable> extends Goal {
    private final T mob;

    public PrimitiveSitGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.isTamed() && mob.getFollowState() == PrimitiveTameable.FollowState.SITTING && mob.onGround();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.setTarget(null);
    }
}
