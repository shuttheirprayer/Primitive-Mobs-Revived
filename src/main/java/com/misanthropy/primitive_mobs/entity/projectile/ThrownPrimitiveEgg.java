package com.misanthropy.primitive_mobs.entity.projectile;

import com.misanthropy.primitive_mobs.core.ModEntities;
import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.entity.PrimitiveTameable;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

public class ThrownPrimitiveEgg extends ThrowableItemProjectile {
    public enum Kind {
        DODO(() -> ModEntities.DODO.get(), () -> ModItems.DODO_EGG.get()),
        BABY_SPIDER(() -> ModEntities.BABY_SPIDER.get(), () -> ModItems.SPIDER_EGG.get()),
        FESTIVE_CREEPER(() -> ModEntities.FESTIVE_CREEPER.get(), () -> ModItems.MYSTERY_EGG_FESTIVE.get()),
        SUPPORT_CREEPER(() -> ModEntities.SUPPORT_CREEPER.get(), () -> ModItems.MYSTERY_EGG_SUPPORT.get()),
        ROCKET_CREEPER(() -> ModEntities.ROCKET_CREEPER.get(), () -> ModItems.MYSTERY_EGG_ROCKET.get());

        private static final Kind[] VALUES = values();
        private final Supplier<EntityType<? extends Mob>> type;
        private final Supplier<Item> item;

        Kind(Supplier<EntityType<? extends Mob>> type, Supplier<Item> item) {
            this.type = type;
            this.item = item;
        }

        public EntityType<? extends Mob> entityType() {
            return type.get();
        }

        public Item item() {
            return item.get();
        }

        public static Kind byId(int id) {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    private Kind kind = Kind.DODO;
    private int spawnChance = 8;

    public ThrownPrimitiveEgg(EntityType<? extends ThrownPrimitiveEgg> type, Level level) {
        super(type, level);
    }

    public ThrownPrimitiveEgg(Level level, LivingEntity thrower, Kind kind, int spawnChance) {
        super(ModEntities.PRIMITIVE_EGG.get(), thrower, level);
        this.kind = kind;
        this.spawnChance = spawnChance;
    }

    protected ThrownPrimitiveEgg(EntityType<? extends ThrownPrimitiveEgg> type, Level level, LivingEntity thrower, Kind kind, int spawnChance) {
        super(type, thrower, level);
        this.kind = kind;
        this.spawnChance = spawnChance;
    }

    @Override
    protected Item getDefaultItem() {
        return kind.item();
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(damageSources().thrown(this, getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }
        if (spawnChance <= 1 || random.nextInt(spawnChance) == 0) {
            spawnHatchling();
        }
        level().broadcastEntityEvent(this, (byte) 3);
        discard();
    }

    private void spawnHatchling() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity spawned = kind.entityType().create(serverLevel);
        if (!(spawned instanceof Mob mob)) {
            return;
        }
        mob.moveTo(getX(), getY(), getZ(), getYRot(), 0.0F);
        mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
        if (mob instanceof AgeableMob ageable) {
            ageable.setAge(-24000);
        }
        if (mob instanceof PrimitiveTameable tameable && getOwner() instanceof Player player) {
            PrimitiveTameable.tame((Mob & PrimitiveTameable) mob, player);
        }
        serverLevel.addFreshEntity(mob);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, particleStack());
            for (int i = 0; i < 8; i++) {
                level().addParticle(particle, getX(), getY(), getZ(),
                        (random.nextDouble() - 0.5D) * 0.08D,
                        (random.nextDouble() - 0.5D) * 0.08D,
                        (random.nextDouble() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("EggKind", (byte) kind.ordinal());
        tag.putByte("SpawnChance", (byte) spawnChance);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        kind = Kind.byId(tag.getByte("EggKind"));
        spawnChance = Math.max(1, tag.getByte("SpawnChance"));
    }

    private ItemStack particleStack() {
        return getItemRaw().isEmpty() ? new ItemStack(getDefaultItem()) : getItemRaw();
    }
}
