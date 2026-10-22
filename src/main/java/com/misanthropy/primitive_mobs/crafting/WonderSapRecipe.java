package com.misanthropy.primitive_mobs.crafting;

import com.misanthropy.primitive_mobs.core.ModItems;
import com.misanthropy.primitive_mobs.core.ModRecipes;
import com.misanthropy.primitive_mobs.item.WonderSapItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WonderSapRecipe extends CustomRecipe {
    public WonderSapRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return !assemble(container, level.registryAccess()).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registries) {
        ItemStack sap = ItemStack.EMPTY;
        ItemStack log = ItemStack.EMPTY;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.WONDER_SAP.get()) && sap.isEmpty()) {
                sap = stack;
            } else if (stack.is(ItemTags.LOGS) && log.isEmpty()) {
                log = stack;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (sap.isEmpty() || log.isEmpty()) {
            return ItemStack.EMPTY;
        }
        BlockState stored = WonderSapItem.getLog(sap);
        return stored == null ? ItemStack.EMPTY : new ItemStack(stored.getBlock());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WONDER_SAP_CONVERT.get();
    }
}
