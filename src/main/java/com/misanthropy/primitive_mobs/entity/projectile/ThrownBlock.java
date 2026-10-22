package com.misanthropy.primitive_mobs.entity.projectile;

import com.misanthropy.primitive_mobs.core.ModEntities;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class ThrownBlock extends Entity {
    private static final EntityDataAccessor<Integer> BLOCK_STATE_ID =
            SynchedEntityData.defineId(ThrownBlock.class, EntityDataSerializers.INT);
    private static final float IMPACT_DAMAGE = 10.0F;

    @Nullable
    private LivingEntity owner;

    public ThrownBlock(EntityType<? extends ThrownBlock> type, Level level) {
        super(type, level);
        blocksBuilding = true;
    }

    public ThrownBlock(Level level, double x, double y, double z, LivingEntity owner, BlockState state) {
        this(ModEntities.THROWN_BLOCK.get(), level);
        setPos(x, y, z);
        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        setDeltaMovement(-Math.sin(angle) * 0.02D, 0.2D, -Math.cos(angle) * 0.02D);
        xo = x;
        yo = y;
        zo = z;
        this.owner = owner;
        setBlockState(state);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(BLOCK_STATE_ID, net.minecraft.world.level.block.Block.getId(Blocks.STONE.defaultBlockState()));
    }

    public void setBlockState(BlockState state) {
        entityData.set(BLOCK_STATE_ID, net.minecraft.world.level.block.Block.getId(state));
    }

    public BlockState getBlockState() {
        return net.minecraft.world.level.block.Block.stateById(entityData.get(BLOCK_STATE_ID));
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return isAlive();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void tick() {
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.98D));

        if (!onGround() && !horizontalCollision) {
            return;
        }
        if (!level().isClientSide) {
            impact();
        }
        discard();
    }

    private void impact() {
        AABB box = getBoundingBox().inflate(1.2D);
        List<Entity> hits = level().getEntities(this, box, entity -> entity != owner && entity.isAlive());
        for (Entity entity : hits) {
            entity.hurt(damageSources().thrown(this, owner), IMPACT_DAMAGE);
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()),
                    getX(), getY() + 0.5D, getZ(), 36, 0.5D, 0.5D, 0.5D, 0.15D);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(getBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setBlockState(NbtUtils.readBlockState(level().holderLookup(net.minecraft.core.registries.Registries.BLOCK), tag.getCompound("BlockState")));
    }

    @Override
    protected float getEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return 0.0F;
    }
}
