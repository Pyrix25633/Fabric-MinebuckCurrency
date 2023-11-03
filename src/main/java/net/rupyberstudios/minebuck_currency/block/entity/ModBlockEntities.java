package net.rupyberstudios.minebuck_currency.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.ModBlocks;
import net.rupyberstudios.minebuck_currency.block.custom.AutomatedTellerMachineBlock;

public class ModBlockEntities {
    public static final BlockEntityType<ComputerBlockEntity> COMPUTER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MinebuckCurrency.MOD_ID, "computer"),
            FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, ModBlocks.COMPUTER).build());
    public static final BlockEntityType<AutomatedTellerMachineBlockEntity> AUTOMATED_TELLER_MACHINE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MinebuckCurrency.MOD_ID, "automated_teller_machine"),
            FabricBlockEntityTypeBuilder.create(AutomatedTellerMachineBlockEntity::new, ModBlocks.AUTOMATED_TELLER_MACHINE).build());

    public static void registerBlockEntities() {
        MinebuckCurrency.LOGGER.info("Registering ModBlockEntities for " + MinebuckCurrency.MOD_ID);
    }
}