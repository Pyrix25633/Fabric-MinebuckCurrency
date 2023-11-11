package net.rupyberstudios.minebuck_currency.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "minebuck_currency")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip()
    @Comment("If set to true a Minecraft-Style GUI will be used instead of the dark theme one")
    public boolean classicGui = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If set to true 1x2Ⓜ Coins will be preferred over 2x1Ⓜ Coins when withdrawing 2Ⓜ")
    public boolean preferHigherValueCash = false;
}