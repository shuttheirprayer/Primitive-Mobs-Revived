package com.misanthropy.primitive_mobs.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PrimitiveMobs.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("primitive_mobs", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.primitive_mobs"))
            .icon(() -> new ItemStack(ModItems.MIMIC_ORB.get()))
            .displayItems((params, output) -> ModItems.tabOrder().forEach(item -> output.accept(item.get())))
            .build());

    private ModCreativeTab() {
    }
}
