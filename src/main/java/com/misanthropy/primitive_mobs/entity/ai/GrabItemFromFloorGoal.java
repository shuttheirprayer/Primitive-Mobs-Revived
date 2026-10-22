package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class GrabItemFromFloorGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private final Predicate<ItemStack> wanted;
    private final double searchRange;
    private ItemEntity target;
    private int searchCooldown;

    public GrabItemFromFloorGoal(PathfinderMob mob, double speed, Predicate<ItemStack> wanted, double searchRange) {
        this.mob = mob;
        this.speed = speed;
        this.wanted = wanted;
        this.searchRange = searchRange;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.getMainHandItem().isEmpty()) {
            return false;
        }
        if (--searchCooldown > 0) {
            return false;
        }
        searchCooldown = reducedTickDelay(20);
        target = findItem();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && mob.getMainHandItem().isEmpty();
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        mob.getNavigation().moveTo(target, speed);
        if (mob.distanceToSqr(target) < 1.5D) {
            ItemStack stack = target.getItem().split(1);
            mob.setItemSlot(EquipmentSlot.MAINHAND, stack);
            mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            mob.take(target, 1);
            if (target.getItem().isEmpty()) {
                target.discard();
            }
            mob.swing(InteractionHand.MAIN_HAND);
        }
    }

    private ItemEntity findItem() {
        AABB box = mob.getBoundingBox().inflate(searchRange, 4.0D, searchRange);
        List<ItemEntity> items = mob.level().getEntitiesOfClass(ItemEntity.class, box, item -> item.isAlive() && wanted.test(item.getItem()));
        ItemEntity closest = null;
        double best = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double dist = mob.distanceToSqr(item);
            if (dist < best) {
                best = dist;
                closest = item;
            }
        }
        return closest;
    }
}
