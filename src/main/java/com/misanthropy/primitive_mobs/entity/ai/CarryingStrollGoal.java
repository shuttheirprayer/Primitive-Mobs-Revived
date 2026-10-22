package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;

public class CarryingStrollGoal extends WaterAvoidingRandomStrollGoal {
    public CarryingStrollGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
    }

    @Override
    public boolean canUse() {
        if (!mob.isVehicle()) {
            return super.canUse();
        }
        if (mob.getNoActionTime() >= 100 || mob.getRandom().nextInt(reducedTickDelay(interval)) != 0) {
            return false;
        }
        Vec3 pos = getPosition();
        if (pos == null) {
            return false;
        }
        wantedX = pos.x;
        wantedY = pos.y;
        wantedZ = pos.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !mob.getNavigation().isDone();
    }
}
