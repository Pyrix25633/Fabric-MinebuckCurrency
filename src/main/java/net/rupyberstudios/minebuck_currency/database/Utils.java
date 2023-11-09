package net.rupyberstudios.minebuck_currency.database;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Utils {
    public static int countCash(@NotNull Inventory inventory) {
        int total = 0;
        for(int i = 0; i < inventory.size(); i++) {
            Item item = inventory.getStack(i).getItem();
            if(item == ModItems.COIN_1) total += 1;
            else if(item == ModItems.COIN_2) total += 2;
            else if(item == ModItems.BANKNOTE_5) total += 5;
            else if(item == ModItems.BANKNOTE_10) total += 10;
            else if(item == ModItems.BANKNOTE_20) total += 20;
            else if(item == ModItems.BANKNOTE_50) total += 50;
            else if(item == ModItems.BANKNOTE_100) total += 100;
            else if(item == ModItems.BANKNOTE_200) total += 200;
        }
        return total;
    }
    
    public static void removeCash(PlayerInventory inventory, int amount) {
        if(countCash(inventory) < amount || amount <= 0) return;
        HashMap<Integer, HashMap<Integer, Integer>> cash = new HashMap<>();
        cash.put(1, new HashMap<>());
        cash.put(2, new HashMap<>());
        cash.put(5, new HashMap<>());
        cash.put(10, new HashMap<>());
        cash.put(20, new HashMap<>());
        cash.put(50, new HashMap<>());
        cash.put(100, new HashMap<>());
        cash.put(200, new HashMap<>());
        for(int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            int value = getValue(stack);
            cash.get(value).put(i, stack.getCount() * value);
        }
        HashMap<Integer, Pair<Integer, Integer>> remaining = new HashMap<>();
        remaining.put(2, calculateRemaining(cash, 2, amount));
        remaining.put(5, calculateRemaining(cash, 5, amount));
        remaining.put(10, calculateRemaining(cash, 10, amount));
        remaining.put(20, calculateRemaining(cash, 20, amount));
        remaining.put(50, calculateRemaining(cash, 50, amount));
        remaining.put(100, calculateRemaining(cash, 100, amount));
        remaining.put(200, calculateRemaining(cash, 200, amount));
        Pair<Integer, Pair<Integer, Integer>> best = new Pair<>(1, calculateRemaining(cash, 1, amount));
        for(int value : remaining.keySet()) {
            if(Math.abs(remaining.get(value).getRight()) < best.getRight().getRight())
                best = new Pair<>(value, remaining.get(value));
        }
        for(Integer slot : cash.get(best.getLeft()).keySet()) {
            ItemStack stack = inventory.getStack(slot);
            if(stack.getCount() > best.getRight().getLeft())
                stack.setCount(stack.getCount() - best.getRight().getLeft());
            else {
                best.getRight().setLeft(best.getRight().getLeft() - stack.getCount());
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        }
        long change = best.getRight().getRight();
        if(change == 0) return;
        if(best.getRight().getRight() > 0)
            removeCash(inventory, best.getRight().getRight());
        else
            addCash(inventory, -best.getRight().getRight());
    }

    public static void addCash(PlayerInventory inventory, int amount) {
        if(amount <= 0) return;
        int needed = amount / 200;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_200, needed));
        amount %= 200;
        needed = amount / 100;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_100, needed));
        amount %= 100;
        needed = amount / 50;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_50, needed));
        amount %= 50;
        needed = amount / 20;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_20, needed));
        amount %= 20;
        needed = amount / 10;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_10, needed));
        amount %= 10;
        needed = amount / 5;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.BANKNOTE_50, needed));
        amount %= 5;
        needed = amount / 2;
        if(needed > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.COIN_2, needed));
        amount %= 2;
        if(amount > 0)
            inventory.offerOrDrop(new ItemStack(ModItems.COIN_1, amount));
    }

    @Contract("_, _, _ -> new")
    private static @NotNull Pair<Integer, Integer> calculateRemaining(@NotNull HashMap<Integer, HashMap<Integer, Integer>> cash, int value, int amount) {
        HashMap<Integer, Integer> stacks = cash.get(value);
        int total = 0;
        for(int subtotal : stacks.values())
            total += subtotal;
        int needed = amount / value;
        if((total - needed * value) > 0) return new Pair<>(needed, amount - needed * value);
        else {
            int available = total / value;
            return new Pair<>(available, amount - total);
        }
    }

    @Contract(pure = true)
    private static int getValue(@NotNull ItemStack stack) {
        Item item = stack.getItem();
        int value = 0;
        if(item == ModItems.COIN_1) value = 1;
        else if(item == ModItems.COIN_2) value = 2;
        else if(item == ModItems.BANKNOTE_5) value = 5;
        else if(item == ModItems.BANKNOTE_10) value = 10;
        else if(item == ModItems.BANKNOTE_20) value = 20;
        else if(item == ModItems.BANKNOTE_50) value = 50;
        else if(item == ModItems.BANKNOTE_100) value = 100;
        else if(item == ModItems.BANKNOTE_200) value = 200;
        return value;
    }
}