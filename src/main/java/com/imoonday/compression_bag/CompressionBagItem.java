package com.imoonday.compression_bag;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class CompressionBagItem extends Item {

    public CompressionBagItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(CompressionBag.COMPRESSION_BAG, new Identifier("compressed"), (stack, world, entity, seed) -> getItem(stack).isPresent() ? 1.0f : 0.0f);
    }

    @Override
    public Text getName(ItemStack stack) {
        return getItem(stack).isPresent() ? getItem(stack).get().getLeft().getName().copy().append(super.getName(stack)) : super.getName(stack);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        getItem(stack).ifPresent(itemStackIntegerPair -> defaultedList.add(itemStackIntegerPair.getLeft().copyWithCount(1)));
        return Optional.of(new BundleTooltipData(defaultedList, 0));
    }

    public static Optional<Pair<ItemStack, Integer>> getItem(ItemStack bag) {
        if (!bag.isOf(CompressionBag.COMPRESSION_BAG)) {
            return Optional.empty();
        }
        if (bag.getNbt() != null && bag.getNbt().contains("Item", NbtElement.COMPOUND_TYPE) && bag.getNbt().contains("Count", NbtElement.INT_TYPE)) {
            NbtCompound nbtCompound = bag.getNbt().getCompound("Item");
            int count = bag.getNbt().getInt("Count");
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                return Optional.of(new Pair<>(itemStack, count));
            } else {
                removeItem(bag);
            }
        }
        return Optional.empty();
    }

    public static void removeItem(ItemStack bag) {
        if (bag.getNbt() != null) {
            bag.getOrCreateNbt().remove("Item");
            bag.getOrCreateNbt().remove("Count");
        }
    }

    public static boolean setItem(ItemStack bag, ItemStack stack) {
        return setItem(bag, stack, stack.getCount());
    }

    public static boolean setItem(ItemStack bag, ItemStack stack, int count) {
        if (stack.isOf(CompressionBag.COMPRESSION_BAG)) {
            return false;
        }
        bag.getOrCreateNbt().put("Item", stack.copyWithCount(1).writeNbt(new NbtCompound()));
        setCount(bag, count);
        return true;
    }

    public static void increment(ItemStack bag, int amount) {
        getItem(bag).ifPresent(pair -> setCount(bag, pair.getRight() + amount));
    }

    public static int decrement(ItemStack bag, int amount) {
        if (getItem(bag).isPresent()) {
            Pair<ItemStack, Integer> pair = getItem(bag).get();
            int resultCount = pair.getRight() - amount;
            if (resultCount <= 0) {
                removeItem(bag);
                return 0;
            }
            setCount(bag, resultCount);
            return resultCount;
        }
        return 0;
    }

    public static void setCount(ItemStack bag, int count) {
        bag.getOrCreateNbt().putInt("Count", count);
    }

    public static int decrementMax(ItemStack bag) {
        if (getItem(bag).isPresent()) {
            Pair<ItemStack, Integer> pair = getItem(bag).get();
            int count = pair.getRight();
            ItemStack itemStack = pair.getLeft();
            int maxCount = itemStack.getMaxCount();
            if (count > maxCount) {
                decrement(bag, maxCount);
                return maxCount;
            } else {
                removeItem(bag);
                return count;
            }
        }
        return 0;
    }

    public static boolean canMerge(ItemStack bag, ItemStack stack) {
        return getItem(bag).isEmpty() || ItemStack.canCombine(getItem(bag).get().getLeft(), stack);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
            if (getItem(stack).isPresent()) {
                ItemStack itemStack1 = getItem(stack).get().getLeft().copy();
                int decrement = decrementMax(stack);
                this.playRemoveOneSound(player);
                slot.insertStack(itemStack1.copyWithCount(decrement));
            } else {
                return false;
            }
        } else {
            if (getItem(stack).isPresent()) {
                Pair<ItemStack, Integer> pair = getItem(stack).get();
                if (ItemStack.canCombine(pair.getLeft(), itemStack)) {
                    increment(stack, itemStack.getCount());
                } else {
                    return true;
                }
            } else if (!setItem(stack, itemStack)) {
                return true;
            }
            slot.takeStack(itemStack.getCount());
            this.playInsertSound(player);
        }
        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        if (otherStack.isEmpty()) {
            if (getItem(stack).isPresent()) {
                ItemStack itemStack = getItem(stack).get().getLeft().copy();
                int decrement = decrementMax(stack);
                this.playRemoveOneSound(player);
                cursorStackReference.set(itemStack.copyWithCount(decrement));
            } else {
                return false;
            }
        } else {
            if (getItem(stack).isPresent()) {
                Pair<ItemStack, Integer> pair = getItem(stack).get();
                if (ItemStack.canCombine(pair.getLeft(), otherStack)) {
                    increment(stack, otherStack.getCount());
                } else {
                    return true;
                }
            } else if (!setItem(stack, otherStack)) {
                return true;
            }
            this.playInsertSound(player);
            otherStack.decrement(otherStack.getCount());
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.isSneaking() ? dropAllItems(user, itemStack) : dropItem(user, itemStack)) {
            this.playDropContentsSound(user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemStack stack = entity.getStack();
        dropAllItems(entity, stack);
    }

    public static boolean dropItem(Entity entity, ItemStack stack) {
        if (getItem(stack).isPresent()) {
            ItemStack itemStack = getItem(stack).get().getLeft().copy();
            int decrement = decrementMax(stack);
            entity.dropStack(itemStack.copyWithCount(decrement));
            return true;
        }
        return false;
    }

    public static boolean dropAllItems(Entity entity, ItemStack stack) {
        boolean dropped = false;
        while (getItem(stack).isPresent()) {
            ItemStack itemStack = getItem(stack).get().getLeft().copy();
            int decrement = decrementMax(stack);
            entity.dropStack(itemStack.copyWithCount(decrement));
            dropped = true;
        }
        return dropped;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }
}
