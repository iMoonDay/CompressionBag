package com.imoonday.compression_bag.mixin;

import com.imoonday.compression_bag.CompressionBagItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void appendTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> tooltip) {
        ItemStack stack = (ItemStack) (Object) this;
        CompressionBagItem.getItem(stack).ifPresent(pair -> {
            tooltip.add(Text.literal("——————"));
            List<Text> tooltip1 = pair.getLeft().getTooltip(player, context);
            tooltip1.set(0, tooltip1.get(0).copy().append(Text.literal(" " + pair.getRight()).formatted(Formatting.WHITE)));
            tooltip.addAll(tooltip1);
            tooltip.add(Text.literal("——————"));
        });
    }
}
