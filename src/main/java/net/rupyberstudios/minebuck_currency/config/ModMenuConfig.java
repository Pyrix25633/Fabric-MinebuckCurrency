package net.rupyberstudios.minebuck_currency.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuConfig implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new Screen(Text.translatable("config_screen.minebuck_currency.title")) {
            @Override
            protected void init() {
                //TODO: Config Screen
            }
        };
    }
}