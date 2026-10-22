package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.client.ClientSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PrimitiveMobs.MODID)
public class PrimitiveMobs {
    public static final String MODID = "primitive_mobs";

    public PrimitiveMobs() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModFeatures.FEATURES.register(modBus);

        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(ModEntities::registerSpawnPlacements);

        MinecraftForge.EVENT_BUS.register(PrimitiveMobsEvents.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PrimitiveMobsConfig.SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.init(modBus));
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
