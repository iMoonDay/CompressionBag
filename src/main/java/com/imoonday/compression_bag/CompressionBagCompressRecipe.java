package com.imoonday.compression_bag;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class CompressionBagCompressRecipe extends SpecialCraftingRecipe {
    public CompressionBagCompressRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack bag = null;
        ItemStack itemStack = null;
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.isOf(CompressionBag.COMPRESSION_BAG)) {
                if (bag != null) {
                    return false;
                }
                bag = stack.copy();
            } else {
                if (itemStack != null && !ItemStack.canCombine(itemStack, stack)) {
                    return false;
                }
                itemStack = stack.copy();
                count++;
            }
        }
        return bag != null && itemStack != null && CompressionBagItem.canMerge(bag, itemStack) && count > 0;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack bag = null;
        ItemStack itemStack = null;
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.isOf(CompressionBag.COMPRESSION_BAG)) {
                if (bag != null) {
                    return ItemStack.EMPTY;
                }
                bag = stack.copy();
            } else {
                if (itemStack != null && !ItemStack.canCombine(itemStack, stack)) {
                    return ItemStack.EMPTY;
                }
                itemStack = stack.copy();
                count++;
            }
        }
        if (bag != null && itemStack != null && CompressionBagItem.canMerge(bag, itemStack) && count > 0) {
            Optional<Pair<ItemStack, Integer>> optional = CompressionBagItem.getItem(bag);
            if (optional.isPresent()) {
                ItemStack stack = optional.get().getLeft();
                if (ItemStack.canCombine(stack, itemStack)) {
                    CompressionBagItem.increment(bag, count);
                } else {
                    return ItemStack.EMPTY;
                }
            } else if (!CompressionBagItem.setItem(bag, itemStack, count)) {
                return ItemStack.EMPTY;
            }
            return bag;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        return super.getRemainder(inventory);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompressionBag.COMPRESSION_BAG_COMPRESS_RECIPE;
    }
}
