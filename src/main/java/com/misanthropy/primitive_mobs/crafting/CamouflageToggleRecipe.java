package com.misanthropy.primitive_mobs.crafting;

import com.misanthropy.primitive_mobs.core.ModRecipes;
import com.misanthropy.primitive_mobs.item.CamouflageArmorItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CamouflageToggleRecipe extends CustomRecipe {
    public CamouflageToggleRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return findArmor(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registries) {
        ItemStack armor = findArmor(container);
        if (armor == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = armor.copy();
        CamouflageArmorItem.setDisabled(result, !CamouflageArmorItem.isDisabled(armor));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CAMOUFLAGE_TOGGLE.get();
    }

    private static ItemStack findArmor(CraftingContainer container) {
        ItemStack found = null;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (found != null || !(stack.getItem() instanceof CamouflageArmorItem)) {
                return null;
            }
            found = stack;
        }
        return found;
    }
}
