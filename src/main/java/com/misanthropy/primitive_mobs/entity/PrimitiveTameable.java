package com.misanthropy.primitive_mobs.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public interface PrimitiveTameable {
    float TAMED_HEALTH = 20.0F;

    enum FollowState {
        FOLLOWING("following"),
        WANDERING("wandering"),
        SITTING("sitting");

        private static final FollowState[] VALUES = values();
        private final String key;

        FollowState(String key) {
            this.key = key;
        }

        public FollowState next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public Component message(Mob mob) {
            return Component.translatable("primitive_mobs.tamed." + key, mob.getDisplayName());
        }

        public static FollowState byId(int id) {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    @Nullable
    UUID getOwnerUuid();

    void setOwnerUuid(@Nullable UUID uuid);

    FollowState getFollowState();

    void setFollowState(FollowState state);

    boolean isTamingFood(ItemStack stack);

    default boolean isTamed() {
        return getOwnerUuid() != null;
    }

    @Nullable
    default LivingEntity getOwner(Mob mob) {
        UUID uuid = getOwnerUuid();
        return uuid == null ? null : mob.level().getPlayerByUUID(uuid);
    }

    default boolean isOwnedBy(Player player) {
        return player.getUUID().equals(getOwnerUuid());
    }

    static <T extends Mob & PrimitiveTameable> InteractionResult interact(T mob, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Level level = mob.level();

        if (!mob.isTamed()) {
            if (!mob.isTamingFood(stack)) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            tame(mob, player);
            return InteractionResult.CONSUME;
        }

        if (!mob.isOwnedBy(player)) {
            return InteractionResult.PASS;
        }

        if (stack.is(Items.MELON_SLICE) && mob.getHealth() < mob.getMaxHealth()) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            mob.heal(4.0F);
            spawnHearts(mob);
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        FollowState next = mob.getFollowState().next();
        mob.setFollowState(next);
        player.displayClientMessage(next.message(mob), true);
        return InteractionResult.CONSUME;
    }

    static <T extends Mob & PrimitiveTameable> void tame(T mob, Player player) {
        mob.setOwnerUuid(player.getUUID());
        mob.setFollowState(FollowState.FOLLOWING);
        mob.setTarget(null);
        mob.setPersistenceRequired();
        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        mob.setHealth(TAMED_HEALTH);
        spawnHearts(mob);
        mob.playSound(SoundEvents.PLAYER_LEVELUP, 0.6F, 1.6F);
    }

    static void spawnHearts(Mob mob) {
        if (mob.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, mob.getX(), mob.getY() + mob.getBbHeight(), mob.getZ(),
                    7, mob.getBbWidth(), 0.5D, mob.getBbWidth(), 0.0D);
        }
    }

    static void save(PrimitiveTameable tameable, CompoundTag tag) {
        UUID owner = tameable.getOwnerUuid();
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        tag.putByte("FollowState", (byte) tameable.getFollowState().ordinal());
    }

    static void load(PrimitiveTameable tameable, CompoundTag tag) {
        tameable.setOwnerUuid(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
        tameable.setFollowState(FollowState.byId(tag.getByte("FollowState")));
    }
}
