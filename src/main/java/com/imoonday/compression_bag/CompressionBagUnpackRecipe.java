package com.imoonday.compression_bag;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
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

public class CompressionBagUnpackRecipe extends SpecialCraftingRecipe {
    public CompressionBagUnpackRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack bag = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (bag != null) {
                return false;
            }
            if (stack.isOf(CompressionBag.COMPRESSION_BAG)) {
                bag = stack.copy();
            }
        }
        return bag != null && !bag.isEmpty() && CompressionBagItem.getItem(bag).isPresent();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack bag = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (bag != null) {
                return ItemStack.EMPTY;
            }
            if (stack.isOf(CompressionBag.COMPRESSION_BAG)) {
                bag = stack.copy();
            }
        }
        if (bag != null && !bag.isEmpty()) {
            Optional<Pair<ItemStack, Integer>> optional = CompressionBagItem.getItem(bag);
            if (optional.isPresent()) {
                ItemStack itemStack = optional.get().getLeft().copy();
                int count = CompressionBagItem.decrementMax(bag.copy());
                return itemStack.copyWithCount(count);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            if (item == CompressionBag.COMPRESSION_BAG) {
                CompressionBagItem.decrementMax(stack);
                defaultedList.set(i, stack.copy());
                continue;
            }
            if (!item.hasRecipeRemainder()) continue;
            if (item.getRecipeRemainder() != null) {
                defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
            }
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompressionBag.COMPRESSION_BAG_UNPACK_RECIPE;
    }
}
