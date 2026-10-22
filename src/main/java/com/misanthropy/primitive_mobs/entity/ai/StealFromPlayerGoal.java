package com.misanthropy.primitive_mobs.entity.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.function.Predicate;

public class StealFromPlayerGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private final Predicate<ItemStack> wanted;
    private final TargetingConditions targeting;
    private Player victim;
    private int searchCooldown;
    private int recheckCooldown;

    public StealFromPlayerGoal(PathfinderMob mob, double speed, Predicate<ItemStack> wanted, double range) {
        this.mob = mob;
        this.speed = speed;
        this.wanted = wanted;
        this.targeting = TargetingConditions.forNonCombat().range(range).selector(entity -> carriesLoot((Player) entity));
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
        victim = mob.level().getNearestPlayer(targeting, mob);
        return victim != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (victim == null || !victim.isAlive() || !mob.getMainHandItem().isEmpty()) {
            return false;
        }
        if (--recheckCooldown > 0) {
            return true;
        }
        recheckCooldown = reducedTickDelay(10);
        return carriesLoot(victim);
    }

    @Override
    public void stop() {
        victim = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(victim, 30.0F, 30.0F);
        mob.getNavigation().moveTo(victim, speed);
        if (mob.distanceToSqr(victim) < 2.25D) {
            steal();
        }
    }

    private boolean carriesLoot(Player player) {
        return findSlot(player) >= 0;
    }

    private int findSlot(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (wanted.test(player.getInventory().getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private void steal() {
        int slot = findSlot(victim);
        if (slot < 0) {
            return;
        }
        ItemStack stolen = victim.getInventory().getItem(slot).split(1);
        mob.setItemSlot(EquipmentSlot.MAINHAND, stolen);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        mob.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 1.6F);
    }
}
