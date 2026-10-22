package com.misanthropy.primitive_mobs.entity.ai;

import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class PrimitiveFollowOwnerGoal<T extends Mob & PrimitiveTameable> extends Goal {
    private final T mob;
    private final double speed;
    private final float startDistance;
    private final float stopDistance;
    private final PathNavigation navigation;
    private LivingEntity owner;
    private int repathCooldown;

    public PrimitiveFollowOwnerGoal(T mob, double speed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.navigation = mob.getNavigation();
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.isTamed() || mob.getFollowState() != PrimitiveTameable.FollowState.FOLLOWING) {
            return false;
        }
        LivingEntity candidate = mob.getOwner(mob);
        if (candidate == null || candidate.isSpectator()) {
            return false;
        }
        if (mob.distanceToSqr(candidate) < startDistance * startDistance) {
            return false;
        }
        owner = candidate;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return owner != null
                && !navigation.isDone()
                && mob.isTamed()
                && mob.getFollowState() == PrimitiveTameable.FollowState.FOLLOWING
                && mob.distanceToSqr(owner) > stopDistance * stopDistance;
    }

    @Override
    public void start() {
        repathCooldown = 0;
    }

    @Override
    public void stop() {
        owner = null;
        navigation.stop();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(owner, 10.0F, mob.getMaxHeadXRot());
        if (--repathCooldown > 0) {
            return;
        }
        repathCooldown = adjustedTickDelay(10);
        navigation.moveTo(owner, speed);
    }
}
