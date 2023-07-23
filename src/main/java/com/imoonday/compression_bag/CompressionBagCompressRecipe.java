package com.imoonday.compression_bag;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class CompressionBagRecipe extends SpecialCraftingRecipe {
    public CompressionBagRecipe(Identifier id, CraftingRecipeCategory category) {
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
        return bag != null && itemStack != null && count > 0;
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
        if (bag != null && itemStack != null && count > 0) {
            Optional<ItemStack> optionalItemStack = CompressionBagItem.getItem(bag);
            if (optionalItemStack.isPresent()) {
                ItemStack stack = optionalItemStack.get();
                if (ItemStack.canCombine(stack, itemStack.copyWithCount(count))) {
                    stack.increment(count);
                    bag.getOrCreateNbt().put("Item", stack.writeNbt(new NbtCompound()));
                } else {
                    return ItemStack.EMPTY;
                }
            } else {
                bag.getOrCreateNbt().put("Item", itemStack.copyWithCount(count).writeNbt(new NbtCompound()));
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
        return CompressionBag.COMPRESSION_BAG_RECIPE;
    }
}
