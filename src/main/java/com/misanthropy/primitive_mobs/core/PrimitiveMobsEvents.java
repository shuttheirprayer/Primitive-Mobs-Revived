package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.entity.monster.Goblin;
import com.misanthropy.primitive_mobs.entity.monster.Harpy;
import com.misanthropy.primitive_mobs.entity.monster.Mimic;
import com.misanthropy.primitive_mobs.entity.passive.Sheepman;
import com.misanthropy.primitive_mobs.item.CamouflageArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class PrimitiveMobsEvents {
    private static final String MIMIC_TAG = com.misanthropy.primitive_mobs.world.MimicChestFeature.MIMIC_TAG;
    private static final int MIMIC_RANDOM = 1;
    private static final int MIMIC_FORCED = 2;
    private static final EquipmentSlot[] ARMOR_SLOTS =
            {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
        int ratePercent = PrimitiveMobsConfig.CONFIG.naturalSpawnRatePercent.get();
        if (ratePercent >= 100 || event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (id != null && id.getNamespace().equals(PrimitiveMobs.MODID)
                && event.getLevel().getRandom().nextInt(100) >= ratePercent) {
            event.setSpawnCancelled(true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Villager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, Goblin.class, 8.0F, 0.6D, 0.6D));
        }
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getNewTarget();

        if (target instanceof Sheepman
                && (attacker instanceof Zombie || attacker instanceof AbstractIllager || attacker instanceof Goblin)) {
            event.setCanceled(true);
            return;
        }

        if (target instanceof Player player && attacker instanceof Mob mob
                && mob.distanceToSqr(player) > 36.0D
                && mob.getLastHurtByMob() != player
                && hasFullCamouflage(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)
                || !attacker.getMainHandItem().is(ModItems.GOBLIN_MACE.get())) {
            return;
        }
        LivingEntity victim = event.getEntity();
        ItemStack picked = ItemStack.EMPTY;
        int seen = 0;
        for (ItemStack piece : victim.getArmorSlots()) {
            if (!piece.isEmpty() && piece.isDamageableItem() && victim.getRandom().nextInt(++seen) == 0) {
                picked = piece;
            }
        }
        if (picked.isEmpty()) {
            return;
        }
        picked.hurtAndBreak((int) (picked.getMaxDamage() * 0.1F), victim,
                entity -> entity.broadcastBreakEvent(EquipmentSlot.CHEST));
    }

    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (event.isDismounting()
                && event.getEntityBeingMounted() instanceof Harpy
                && event.getEntityMounting() instanceof Player player
                && player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide || !event.getEntity().isCreative()) {
            return;
        }
        if (!event.getEntity().getMainHandItem().is(ModItems.MIMIC_ORB.get())) {
            return;
        }
        ChestBlockEntity chest = singleEmptyChest(level, event.getPos());
        if (chest == null) {
            return;
        }
        consume(event.getEntity());
        chest.getPersistentData().putInt(MIMIC_TAG, MIMIC_RANDOM);
        spawnMarkParticles((ServerLevel) level, event.getPos(), ParticleTypes.SMOKE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(event.getPos());
        if (!(blockEntity instanceof ChestBlockEntity chest) || !isSingleChest(level.getBlockState(event.getPos()))) {
            return;
        }
        CompoundTag data = chest.getPersistentData();
        if (data.contains(MIMIC_TAG) && data.getInt(MIMIC_TAG) != 0) {
            triggerMimic(event, (ServerLevel) level, chest, data);
            return;
        }
        if (isEmpty(chest) && event.getEntity().getMainHandItem().is(ModItems.MIMIC_ORB.get())) {
            consume(event.getEntity());
            data.putInt(MIMIC_TAG, MIMIC_FORCED);
            spawnMarkParticles((ServerLevel) level, event.getPos(), ParticleTypes.CLOUD);
            event.setCanceled(true);
        }
    }

    private static void triggerMimic(PlayerInteractEvent.RightClickBlock event, ServerLevel level,
                                     ChestBlockEntity chest, CompoundTag data) {
        BlockPos pos = event.getPos();
        int roll = level.random.nextInt(3);
        boolean becomeMimic = roll == 0 || data.getInt(MIMIC_TAG) == MIMIC_FORCED;
        boolean becomeTrap = roll == 1;

        if (becomeMimic) {
            Mimic mimic = ModEntities.MIMIC.get().create(level);
            if (mimic != null) {
                mimic.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 180.0F, 0.0F);
                mimic.setChestState(level.getBlockState(pos));
                mimic.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null, null);
                if (data.getInt(MIMIC_TAG) != MIMIC_FORCED && level.random.nextInt(10) == 0) {
                    mimic.prime();
                }
                level.addFreshEntity(mimic);
            }
            level.removeBlock(pos, false);
            spawnMarkParticles(level, pos, ParticleTypes.CLOUD);
            event.setCanceled(true);
            return;
        }

        if (becomeTrap) {
            spawnTrap(level, pos);
            chest.setLootTable(ModLootTables.MIMIC_TRAP, level.random.nextLong());
            data.putInt(MIMIC_TAG, 0);
            spawnMarkParticles(level, pos, ParticleTypes.CLOUD);
            event.setCanceled(true);
            return;
        }

        chest.setLootTable(ModLootTables.MIMIC_TREASURE, level.random.nextLong());
        data.putInt(MIMIC_TAG, 0);
    }

    private static void spawnTrap(ServerLevel level, BlockPos pos) {
        int option = level.random.nextInt(4);
        if (option == 0) {
            spawnAt(level, ModEntities.SKELETON_WARRIOR.get(), pos);
        } else if (option == 1) {
            for (int i = 0; i < 3; i++) {
                spawnAt(level, EntityType.BAT, pos);
            }
        } else {
            spawnAt(level, ModEntities.HAUNTED_TOOL.get(), pos);
        }
    }

    private static void spawnAt(ServerLevel level, EntityType<? extends Mob> type, BlockPos pos) {
        Mob mob = type.create(level);
        if (mob == null) {
            return;
        }
        mob.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 180.0F, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null, null);
        level.addFreshEntity(mob);
    }

    private static void spawnMarkParticles(ServerLevel level, BlockPos pos, net.minecraft.core.particles.ParticleOptions particle) {
        level.sendParticles(particle, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10, 0.25D, 0.25D, 0.25D, 0.01D);
    }

    private static ChestBlockEntity singleEmptyChest(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChestBlockEntity chest) || !isSingleChest(level.getBlockState(pos))) {
            return null;
        }
        return isEmpty(chest) ? chest : null;
    }

    private static boolean isSingleChest(BlockState state) {
        return state.getBlock() instanceof ChestBlock
                && state.getValue(ChestBlock.TYPE) == ChestType.SINGLE;
    }

    private static boolean isEmpty(ChestBlockEntity chest) {
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            if (!chest.getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void consume(Player player) {
        if (!player.getAbilities().instabuild) {
            player.getMainHandItem().shrink(1);
        }
    }

    public static boolean hasFullCamouflage(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!(player.getItemBySlot(slot).getItem() instanceof CamouflageArmorItem)) {
                return false;
            }
        }
        return true;
    }

    private PrimitiveMobsEvents() {
    }
}
