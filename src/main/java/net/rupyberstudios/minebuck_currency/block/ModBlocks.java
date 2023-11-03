package net.rupyberstudios.minebuck_currency.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.custom.AutomatedTellerMachineBlock;
import net.rupyberstudios.minebuck_currency.block.custom.ComputerBlock;
import net.rupyberstudios.minebuck_currency.block.custom.VendingMachineBlock;
import net.rupyberstudios.minebuck_currency.item.ModItemGroups;

public class ModBlocks {
    public static final Block COMPUTER = registerBlock("computer",
            new ComputerBlock(FabricBlockSettings.copyOf(Blocks.STONE).breakInstantly().nonOpaque()));
    public static final Block AUTOMATED_TELLER_MACHINE = registerBlock("automated_teller_machine",
            new AutomatedTellerMachineBlock(FabricBlockSettings.copyOf(Blocks.STONE).breakInstantly().nonOpaque()));
    public static final Block VENDING_MACHINE = registerBlock("vending_machine",
            new VendingMachineBlock(FabricBlockSettings.copyOf(Blocks.STONE).breakInstantly().nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(MinebuckCurrency.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(MinebuckCurrency.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.MINEBUCK_CURRENCY).register(entries -> entries.add(registered));
    }

    public static void registerModBlocks() {
        System.out.println("Registering ModBlocks for " + MinebuckCurrency.MOD_ID);
    }
}