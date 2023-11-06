package net.rupyberstudios.minebuck_currency.config;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

public class ModConfigs {
    public static JsonConfig config;
    public static final String CLASSIC_GUI_KEY = "gui.classic";
    public static final boolean CLASSIC_GUI_DEFAULT = false;
    public static boolean classicGui;

    public static void registerConfigs() {
        config = new JsonConfig(MinebuckCurrency.MOD_ID + "_config.json");
        createConfigs();
        assignConfigs();
    }

    public static void createConfigs() {
        config.setDefault(CLASSIC_GUI_KEY, CLASSIC_GUI_DEFAULT);
    }

    public static void assignConfigs() {
        classicGui = config.getBoolean(CLASSIC_GUI_KEY);
        config.saveIfModified();
        MinebuckCurrency.LOGGER.info("Configs loaded");
    }
}