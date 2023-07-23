package com.imoonday.compressionbag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CompressionBag implements ModInitializer {

    public static final Item COMPRESSION_BAG = Registry.register(Registries.ITEM, new Identifier("compression_bag", "compression_bag"), new CompressionBagItem(new FabricItemSettings().maxCount(1)));
    public static final RecipeSerializer<CompressionBagRecipe> COMPRESSION_BAG_RECIPE = RecipeSerializer.register("compress_into_bag", new SpecialRecipeSerializer<>(CompressionBagRecipe::new));

    @Override
    public void onInitialize() {

    }
}
