package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.world.MimicChestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, PrimitiveMobs.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MIMIC_CHEST =
            FEATURES.register("mimic_chest", () -> new MimicChestFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {
    }
}
