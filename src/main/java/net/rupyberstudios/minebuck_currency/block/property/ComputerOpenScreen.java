package net.rupyberstudios.minebuck_currency.block.property;

import net.minecraft.util.StringIdentifiable;

public enum ComputerOpenScreen implements StringIdentifiable {
    OFF,
    ACTIVATE_CARD;

    @Override
    public String asString() {
        return switch(this) {
            case OFF -> "off";
            case ACTIVATE_CARD -> "activate_card";
        };
    }
}