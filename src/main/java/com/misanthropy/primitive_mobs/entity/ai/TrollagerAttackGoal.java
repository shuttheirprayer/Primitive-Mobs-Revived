package com.misanthropy.primitive_mobs.entity.ai;

import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.misanthropy.primitive_mobs.entity.monster.Trollager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TrollagerAttackGoal extends Goal {
    private static final int IDLE = 0;
    private static final int THROW_WINDUP = 1;
    private static final int THROWING = 2;
    private static final int SMASH_WINDUP = 3;
    private static final int SMASHING = 4;
    private static final int SMASH_RECOVER = 5;
    private static final int MELEE_SWING = 6;
    private static final int MELEE_RECOVER = 7;

    private final Trollager trollager;
    private final double moveSpeed;
    private final float meleeRangeSqr;
    private final float maxRangeSqr;

    private int attackTime = -1;
    private int animationTime = -1;
    private boolean attacking;

    public TrollagerAttackGoal(Trollager trollager, double moveSpeed, float meleeRange, float maxRange) {
        this.trollager = trollager;
        this.moveSpeed = moveSpeed;
        this.meleeRangeSqr = meleeRange * meleeRange;
        this.maxRangeSqr = maxRange * maxRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return trollager.getTarget() != null && !trollager.isStone();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        attackTime = -1;
        animationTime = -1;
        attacking = false;
    }

    @Override
    public void stop() {
        trollager.setAnimationState(IDLE);
        attackTime = -1;
        animationTime = -1;
        attacking = false;
    }

    @Override
    public void tick() {
        LivingEntity target = trollager.getTarget();
        if (target == null) {
            return;
        }

        advanceAttack(target);
        advanceAnimation();
        if (!attacking) {
            chooseAttack(target);
        }

        trollager.getNavigation().moveTo(target, moveSpeed);
        trollager.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    private void advanceAttack(LivingEntity target) {
        if (--attackTime > 0 || !attacking) {
            return;
        }
        switch (trollager.getAnimationState()) {
            case SMASH_WINDUP -> {
                trollager.performAction(target, Trollager.ACTION_SMASH);
                trollager.setAnimationState(SMASHING);
                animationTime = 20;
            }
            case IDLE -> {
                trollager.performAction(target, Trollager.ACTION_MELEE);
                trollager.setAnimationState(MELEE_SWING);
                animationTime = 7;
            }
            default -> {
                trollager.performAction(target, Trollager.ACTION_THROW);
                trollager.setAnimationState(THROWING);
                animationTime = 20;
            }
        }
        attackTime = Integer.MAX_VALUE;
    }

    private void advanceAnimation() {
        if (animationTime < 0 || --animationTime != 0) {
            return;
        }
        switch (trollager.getAnimationState()) {
            case SMASHING -> {
                trollager.setAnimationState(SMASH_RECOVER);
                animationTime = 10;
            }
            case MELEE_SWING -> {
                trollager.setAnimationState(MELEE_RECOVER);
                animationTime = 7;
            }
            default -> {
                animationTime = -1;
                attacking = false;
            }
        }
    }

    private void chooseAttack(LivingEntity target) {
        double distanceSqr = trollager.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        boolean canSee = trollager.getSensing().hasLineOfSight(target);

        if (distanceSqr <= meleeRangeSqr && canSee) {
            trollager.setAnimationState(IDLE);
            attackTime = 10;
            attacking = true;
            return;
        }

        boolean ridden = trollager.isVehicle();
        boolean clearShot = canSee && !ridden && trollager.hasClearArcTo(target);
        boolean canSmash = PrimitiveMobsConfig.CONFIG.trollagerDestruction.get() && !ridden
                && (distanceSqr <= maxRangeSqr && !clearShot || trollager.getRandom().nextInt(4) == 0);
        if (canSmash) {
            trollager.setAnimationState(SMASH_WINDUP);
            attackTime = 40;
            attacking = true;
            return;
        }

        if (clearShot) {
            trollager.setAnimationState(THROW_WINDUP);
            attackTime = 30;
            attacking = true;
            return;
        }

        trollager.setAnimationState(IDLE);
        attackTime = Integer.MAX_VALUE;
    }
}
