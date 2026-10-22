package com.misanthropy.primitive_mobs.core;

import com.misanthropy.primitive_mobs.crafting.CamouflageToggleRecipe;
import com.misanthropy.primitive_mobs.crafting.WonderSapRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PrimitiveMobs.MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<CamouflageToggleRecipe>> CAMOUFLAGE_TOGGLE =
            SERIALIZERS.register("camouflage_toggle", () -> new SimpleCraftingRecipeSerializer<>(CamouflageToggleRecipe::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<WonderSapRecipe>> WONDER_SAP_CONVERT =
            SERIALIZERS.register("wonder_sap_convert", () -> new SimpleCraftingRecipeSerializer<>(WonderSapRecipe::new));

    private ModRecipes() {
    }
}
