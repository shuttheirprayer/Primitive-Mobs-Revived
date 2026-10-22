package com.misanthropy.primitive_mobs.world;

import com.misanthropy.primitive_mobs.core.PrimitiveMobsConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MimicChestFeature extends Feature<NoneFeatureConfiguration> {
    public static final String MIMIC_TAG = "PrimitiveMobsMimic";

    public MimicChestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        int rarity = PrimitiveMobsConfig.CONFIG.mimicChestRarity.get();
        if (rarity <= 0 || context.random().nextInt(rarity) != 0) {
            return false;
        }
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        if (!level.isEmptyBlock(pos) || !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
            return false;
        }
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            chest.getPersistentData().putInt(MIMIC_TAG, 1);
        }
        return true;
    }
}
