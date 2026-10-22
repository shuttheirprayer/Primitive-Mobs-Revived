package com.misanthropy.primitive_mobs.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

public final class ModLootTables {
    public static final ResourceLocation HAUNTED_TOOL_WEAPONS = PrimitiveMobs.id("entities/special/haunted_tool");
    public static final ResourceLocation TREASURE_SLIME_TREASURE = PrimitiveMobs.id("entities/special/treasure_slime");
    public static final ResourceLocation FILCH_LIZARD_SPAWN = PrimitiveMobs.id("entities/special/filch_lizard_spawn");
    public static final ResourceLocation FILCH_LIZARD_STEAL = PrimitiveMobs.id("entities/special/filch_lizard_steal");
    public static final ResourceLocation MIMIC_TREASURE = PrimitiveMobs.id("chests/mimic_treasure");
    public static final ResourceLocation MIMIC_TRAP = PrimitiveMobs.id("chests/mimic_trap");

    public static List<ItemStack> roll(ServerLevel level, ResourceLocation table) {
        LootTable loot = level.getServer().getLootData().getLootTable(table);
        return loot.getRandomItems(new LootParams.Builder(level).create(LootContextParamSets.EMPTY));
    }

    public static ItemStack rollOne(ServerLevel level, RandomSource random, ResourceLocation table, ItemStack fallback) {
        LootTable loot = level.getServer().getLootData().getLootTable(table);
        List<ItemStack> items = loot.getRandomItems(new LootParams.Builder(level).create(LootContextParamSets.EMPTY), random.nextLong());
        if (items.isEmpty()) {
            return fallback.copy();
        }
        return items.get(random.nextInt(items.size()));
    }

    private ModLootTables() {
    }
}
