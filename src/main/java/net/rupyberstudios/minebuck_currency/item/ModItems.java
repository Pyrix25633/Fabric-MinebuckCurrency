package net.rupyberstudios.minebuck_currency.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.item.custom.CardItem;

public class ModItems {
    public static final Item COIN_1 = registerItem("coin_1", new Item(new FabricItemSettings()));
    public static final Item COIN_2 = registerItem("coin_2", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_5 = registerItem("banknote_5", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_10 = registerItem("banknote_10", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_20 = registerItem("banknote_20", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_50 = registerItem("banknote_50", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_100 = registerItem("banknote_100", new Item(new FabricItemSettings()));
    public static final Item BANKNOTE_200 = registerItem("banknote_200", new Item(new FabricItemSettings()));

    public static final Item CARD = registerItem("card", new CardItem(new FabricItemSettings().maxCount(1)));

    public static final Item RECEIPT = registerItem("receipt", new Item(new FabricItemSettings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(MinebuckCurrency.MOD_ID, name), item);
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.MINEBUCK_CURRENCY).register(entries -> entries.add(registered));
        return registered;
    }

    public static void registerModItems() {
        System.out.println("Registering ModItems for " + MinebuckCurrency.MOD_ID);
    }
}