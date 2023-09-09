package net.rupyberstudios.minebuck_currency;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.item.custom.CardItem;
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

        LOGGER.info("Initializing client");
    }
}