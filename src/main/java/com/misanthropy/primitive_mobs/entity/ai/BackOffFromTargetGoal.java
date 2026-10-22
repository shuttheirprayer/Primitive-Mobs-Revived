package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BackOffFromTargetGoal extends Goal {
    private final PathfinderMob mob;
    private final double distance;
    private final double speed;
    private LivingEntity target;

    public BackOffFromTargetGoal(PathfinderMob mob, double distance, double speed) {
        this.mob = mob;
        this.distance = distance;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        target = mob.getTarget();
        return target != null && mob.distanceToSqr(target) < distance * distance;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && mob.distanceToSqr(target) < distance * distance && !mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public void start() {
        moveAway();
    }

    @Override
    public void tick() {
        if (mob.getNavigation().isDone()) {
            moveAway();
        }
    }

    private void moveAway() {
        Vec3 away = DefaultRandomPos.getPosAway(mob, 8, 4, target.position());
        if (away != null) {
            mob.getNavigation().moveTo(away.x, away.y, away.z, speed);
        }
    }
}
