package net.rupyberstudios.minebuck_currency.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<ComputerBlockEntity> COMPUTER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MinebuckCurrency.MOD_ID, "computer"),
            FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, ModBlocks.COMPUTER).build());

    public static void registerBlockEntities() {
        MinebuckCurrency.LOGGER.info("Registering ModBlockEntities for " + MinebuckCurrency.MOD_ID);
    }
}