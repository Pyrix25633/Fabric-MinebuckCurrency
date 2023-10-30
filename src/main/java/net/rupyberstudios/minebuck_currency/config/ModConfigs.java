package net.rupyberstudios.minebuck_currency.config;

import com.mojang.datafixers.util.Pair;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

public class ModConfigs {
    public static SimpleConfig config;
    private static ModConfigProvider configs;
    public static final String CLASSIC_GUI_KEY = "gui.classic";
    public static final boolean CLASSIC_GUI_DEFAULT = false;
    public static boolean classicGui;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();
        config = SimpleConfig.of(MinebuckCurrency.MOD_ID + "_config").provider(configs).request();
        assignConfigs();
    }

    public static void createConfigs() {
        configs.addKeyValuePair(new Pair<>(CLASSIC_GUI_KEY, CLASSIC_GUI_DEFAULT),
                "If set to true a Minecraft-Style GUI will be displayed");
    }

    public static void assignConfigs() {
        classicGui = config.getOrDefault(CLASSIC_GUI_KEY, CLASSIC_GUI_DEFAULT);

        MinebuckCurrency.LOGGER.info("All " + configs.getConfigList().size() + " configs have been set propery");
    }
}