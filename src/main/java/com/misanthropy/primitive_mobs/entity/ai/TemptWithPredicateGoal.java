package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.function.Predicate;

public class TemptWithPredicateGoal extends Goal {
    private static final TargetingConditions TEMPT_TARGETING =
            TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();

    protected final PathfinderMob mob;
    private final double speed;
    private final Predicate<ItemStack> tempting;
    private final boolean canScare;
    private final TargetingConditions targetingConditions;
    protected Player player;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;
    private int calmDown;

    public TemptWithPredicateGoal(PathfinderMob mob, double speed, Predicate<ItemStack> tempting, boolean canScare) {
        this.mob = mob;
        this.speed = speed;
        this.tempting = tempting;
        this.canScare = canScare;
        this.targetingConditions = TEMPT_TARGETING.copy().selector(this::shouldFollow);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (calmDown > 0) {
            calmDown--;
            return false;
        }
        player = mob.level().getNearestPlayer(targetingConditions, mob);
        return player != null;
    }

    private boolean shouldFollow(net.minecraft.world.entity.LivingEntity entity) {
        return tempting.test(entity.getMainHandItem()) || tempting.test(entity.getOffhandItem());
    }

    @Override
    public boolean canContinueToUse() {
        if (canScare()) {
            if (mob.distanceToSqr(player) < 36.0D) {
                if (player.distanceToSqr(px, py, pz) > 0.010000000000000002D) {
                    return false;
                }
                if (Math.abs(player.getXRot() - pRotX) > 5.0D || Math.abs(player.getYRot() - pRotY) > 5.0D) {
                    return false;
                }
            } else {
                px = player.getX();
                py = player.getY();
                pz = player.getZ();
            }
            pRotX = player.getXRot();
            pRotY = player.getYRot();
        }
        return canUse();
    }

    protected boolean canScare() {
        return canScare;
    }

    @Override
    public void start() {
        px = player.getX();
        py = player.getY();
        pz = player.getZ();
    }

    @Override
    public void stop() {
        player = null;
        mob.getNavigation().stop();
        calmDown = reducedTickDelay(100);
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(player, mob.getMaxHeadYRot() + 20.0F, mob.getMaxHeadXRot());
        if (mob.distanceToSqr(player) < 6.25D) {
            mob.getNavigation().stop();
        } else {
            mob.getNavigation().moveTo(player, speed);
        }
    }
}
