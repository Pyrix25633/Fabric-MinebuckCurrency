package net.rupyberstudios.minebuck_currency.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ComputerActivateCardScreenHandler> COMPUTER_ACTIVATE_CARD_SCREEN_HANDLER =
            new ExtendedScreenHandlerType<>(ComputerActivateCardScreenHandler::new);

    public static void registerScreenHandlers() {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(MinebuckCurrency.MOD_ID, "computer_activate_card"),
                COMPUTER_ACTIVATE_CARD_SCREEN_HANDLER);
    }
}