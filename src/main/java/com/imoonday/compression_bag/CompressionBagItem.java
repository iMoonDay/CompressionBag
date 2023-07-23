package com.imoonday.compressionbag;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompressionBagItem extends Item {

    public CompressionBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        MutableText name = Text.empty();
        boolean hasItem = getItem(stack).isPresent();
        if (hasItem) {
            name = name.append(getItem(stack).get().getName());
        }
        return name.append(super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        getItem(stack).ifPresent(stack1 -> {
            tooltip.add(Text.literal("——————"));
            List<Text> tooltip1 = new ArrayList<>(stack1.getTooltip(null, context));
            tooltip1.set(0, Text.empty().append(tooltip1.get(0)).append(" " + stack1.getCount()));
            tooltip.addAll(tooltip1);
            tooltip.add(Text.literal("——————"));
        });
    }

    public static Optional<ItemStack> getItem(ItemStack bag) {
        if (bag.getNbt() != null && bag.getNbt().contains("Item", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = bag.getNbt().getCompound("Item");
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                return Optional.of(itemStack);
            }
        }
        return Optional.empty();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Optional<ItemStack> optional = getItem(user.getStackInHand(hand));
        if (optional.isPresent()) {
            return optional.get().use(world, user, hand);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Optional<ItemStack> optional = getItem(stack);
        if (optional.isPresent()) {
            return optional.get().useOnEntity(user, entity, hand);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Optional<ItemStack> optional = getItem(context.getStack());
        if (optional.isPresent()) {
            return optional.get().useOnBlock(new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), optional.get(), new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock())));
        }
        return super.useOnBlock(context);
    }
}
