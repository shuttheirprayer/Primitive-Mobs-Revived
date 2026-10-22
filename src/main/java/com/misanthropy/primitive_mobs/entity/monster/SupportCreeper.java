package com.misanthropy.primitive_mobs.entity.monster;

import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SupportCreeper extends PrimitiveCreeper {
    private AvoidEntityGoal<Player> avoidPlayerGoal;
    private boolean cornered;

    public SupportCreeper(EntityType<? extends SupportCreeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        avoidPlayerGoal = new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D);
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new BuffAlliesGoal(this));
        goalSelector.addGoal(3, avoidPlayerGoal);
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        addTamedGoals();
    }

    @Override
    public void tick() {
        if (!cornered && !level().isClientSide && getHealth() < getMaxHealth() / 2.0F) {
            cornered = true;
            goalSelector.removeGoal(avoidPlayerGoal);
            goalSelector.addGoal(2, new SwellGoal(this));
            targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }
        super.tick();
    }

    private static class BuffAlliesGoal extends Goal {
        private static final int EFFECT_DURATION = 60;

        private final SupportCreeper creeper;
        @Nullable
        private LivingEntity ally;
        private int searchCooldown;

        BuffAlliesGoal(SupportCreeper creeper) {
            this.creeper = creeper;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (creeper.isTamed() && creeper.getFollowState() == PrimitiveTameable.FollowState.SITTING) {
                return false;
            }
            ally = findAlly();
            return ally != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (creeper.isTamed() && creeper.getFollowState() == PrimitiveTameable.FollowState.SITTING) {
                return false;
            }
            return ally != null && ally.isAlive() && creeper.distanceToSqr(ally) <= 64.0D;
        }

        @Override
        public void stop() {
            ally = null;
        }

        @Nullable
        private LivingEntity findAlly() {
            if (--searchCooldown > 0) {
                return ally;
            }
            searchCooldown = reducedTickDelay(20);

            if (creeper.isTamed()) {
                LivingEntity owner = creeper.getOwner(creeper);
                return owner != null && owner.distanceToSqr(creeper) < 30.0D ? owner : null;
            }

            List<Monster> nearby = creeper.level().getEntitiesOfClass(Monster.class,
                    creeper.getBoundingBox().inflate(6.0D, 3.0D, 6.0D),
                    mob -> mob != creeper && !(mob instanceof SupportCreeper) && mob.getActiveEffects().isEmpty());
            LivingEntity closest = null;
            double best = Double.MAX_VALUE;
            for (Monster mob : nearby) {
                double distance = creeper.distanceToSqr(mob);
                if (distance < best) {
                    best = distance;
                    closest = mob;
                }
            }
            return closest;
        }

        @Override
        public void tick() {
            if (ally == null) {
                return;
            }
            int amplifier = creeper.isPowered() ? 1 : 0;
            if (creeper.distanceTo(ally) > 2.0F) {
                creeper.getNavigation().moveTo(ally, 1.0D);
            }
            if (creeper.distanceToSqr(ally) > 16.0D) {
                return;
            }

            if (ally instanceof Creeper allyCreeper) {
                if (!allyCreeper.isPowered() && creeper.level() instanceof ServerLevel serverLevel) {
                    charge(serverLevel, allyCreeper);
                }
                apply(allyCreeper, MobEffects.FIRE_RESISTANCE, amplifier);
            } else {
                if (ally instanceof Trollager trollager) {
                    trollager.setSupported(true);
                }
                apply(ally, MobEffects.DAMAGE_BOOST, amplifier);
                apply(ally, MobEffects.MOVEMENT_SPEED, amplifier);
                apply(ally, MobEffects.FIRE_RESISTANCE, amplifier);
            }
            apply(creeper, MobEffects.FIRE_RESISTANCE, amplifier);
            apply(creeper, MobEffects.MOVEMENT_SPEED, amplifier);
        }

        private static void charge(ServerLevel level, Creeper target) {
            net.minecraft.world.entity.LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt == null) {
                return;
            }
            bolt.moveTo(target.position());
            bolt.setVisualOnly(true);
            target.thunderHit(level, bolt);
        }

        private static void apply(LivingEntity entity, MobEffect effect, int amplifier) {
            if (!entity.hasEffect(effect)) {
                entity.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier));
            }
        }
    }
}
