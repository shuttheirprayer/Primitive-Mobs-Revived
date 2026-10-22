package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.entity.ai.BackOffFromTargetGoal;
import com.misanthropy.primitive_mobs.entity.projectile.PrimitiveTnt;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FestiveCreeper extends PrimitiveCreeper {
    public FestiveCreeper(EntityType<? extends FestiveCreeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new ThrowTntGoal(this));
        goalSelector.addGoal(3, new BackOffFromTargetGoal(this, 7.5D, 1.0D));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        addTamedGoals();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void tick() {
        setSwellDir(-1);
        super.tick();
    }

    private static class ThrowTntGoal extends Goal {
        private static final int COOLDOWN = 60;

        private final FestiveCreeper creeper;
        private int attackCooldown;

        ThrowTntGoal(FestiveCreeper creeper) {
            this.creeper = creeper;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = creeper.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double distanceSqr = creeper.distanceToSqr(target);
            return distanceSqr > 4.0D && distanceSqr < 144.0D && creeper.getSensing().hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void stop() {
            attackCooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = creeper.getTarget();
            if (target == null || --attackCooldown > 0) {
                return;
            }
            attackCooldown = COOLDOWN;
            if (!creeper.level().isClientSide) {
                float power = creeper.isPowered() ? 3.0F : 1.5F;
                PrimitiveTnt tnt = new PrimitiveTnt(creeper.level(), creeper.getX(), creeper.getY(), creeper.getZ(),
                        creeper, power, 30);
                tnt.setDeltaMovement(
                        (target.getX() - tnt.getX()) / 18.0D,
                        (target.getY() - tnt.getY()) / 18.0D + 0.5D,
                        (target.getZ() - tnt.getZ()) / 18.0D);
                creeper.level().addFreshEntity(tnt);
            }
            creeper.playSound(SoundEvents.TNT_PRIMED, creeper.getSoundVolume(), creeper.getVoicePitch());
        }
    }
}
