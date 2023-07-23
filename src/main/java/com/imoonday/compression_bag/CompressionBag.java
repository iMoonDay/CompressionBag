package com.imoonday.compression_bag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CompressionBag implements ModInitializer {

    public static final Item COMPRESSION_BAG = Registry.register(Registries.ITEM, new Identifier("compression_bag", "compression_bag"), new CompressionBagItem(new FabricItemSettings().maxCount(1)));
    public static final RecipeSerializer<CompressionBagCompressRecipe> COMPRESSION_BAG_COMPRESS_RECIPE = RecipeSerializer.register("compress_into_bag", new SpecialRecipeSerializer<>(CompressionBagCompressRecipe::new));
    public static final RecipeSerializer<CompressionBagUnpackRecipe> COMPRESSION_BAG_UNPACK_RECIPE = RecipeSerializer.register("unpack_from_bag", new SpecialRecipeSerializer<>(CompressionBagUnpackRecipe::new));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(COMPRESSION_BAG));
    }
}
