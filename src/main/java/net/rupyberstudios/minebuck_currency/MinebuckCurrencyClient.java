package net.rupyberstudios.minebuck_currency;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.rupyberstudios.minebuck_currency.block.entity.ModBlockEntities;
import net.rupyberstudios.minebuck_currency.block.entity.client.ComputerBlockEntityRenderer;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.item.custom.CardItem;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import net.rupyberstudios.minebuck_currency.screen.ComputerActivateCardScreen;
import net.rupyberstudios.minebuck_currency.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.rupyberstudios.minebuck_currency.MinebuckCurrency.MOD_ID;

public class MinebuckCurrencyClient implements ClientModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                tintIndex != 0 ? -1 : ((CardItem)stack.getItem()).getColor(stack), ModItems.CARD
        );

        HandledScreens.register(ModScreenHandlers.COMPUTER_ACTIVATE_CARD_SCREEN_HANDLER, ComputerActivateCardScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.COMPUTER, ComputerBlockEntityRenderer::new);

        ModMessages.registerS2CPackets();

        LOGGER.info("Initializing client");
    }
}