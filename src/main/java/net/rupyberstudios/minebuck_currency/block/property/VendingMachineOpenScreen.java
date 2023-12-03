package net.rupyberstudios.minebuck_currency.block.property;

import net.minecraft.util.StringIdentifiable;

public enum VendingMachineOpenScreen implements StringIdentifiable {
    OFF,
    CONFIG,
    VENDING_MACHINE;

    @Override
    public String asString() {
        return switch(this) {
            case OFF -> "off";
            case CONFIG -> "config";
            case VENDING_MACHINE -> "vending_machine";
        };
    }
}