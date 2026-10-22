package com.misanthropy.primitive_mobs.entity.projectile;

import com.misanthropy.primitive_mobs.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FlameSpit extends Fireball {
    private static final int MAX_LIFETIME = 30;

    public FlameSpit(EntityType<? extends FlameSpit> type, Level level) {
        super(type, level);
    }

    public FlameSpit(Level level, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.FLAME_SPIT.get(), shooter, accelX, accelY, accelZ, level);
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > MAX_LIFETIME) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = getOwner();
        boolean hurt = target.hurt(damageSources().fireball(this, owner), 5.0F);
        if (hurt) {
            if (target.isAlive()) {
                target.setSecondsOnFire(5);
            } else if (owner instanceof LivingEntity living) {
                doEnchantDamageEffects(living, target);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide) {
            return;
        }
        Entity owner = getOwner();
        if (owner == null || !(owner instanceof net.minecraft.world.entity.Mob) || level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            if (level().isEmptyBlock(pos)) {
                level().setBlockAndUpdate(pos, BaseFireBlock.getState(level(), pos));
            }
        }
    }

    @Override
    protected void onHit(net.minecraft.world.phys.HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
