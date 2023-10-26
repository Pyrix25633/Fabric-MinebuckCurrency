package net.rupyberstudios.minebuck_currency.screen.widget;

import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public abstract class SwitchableWidget extends PressableWidget {
    public SwitchableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public abstract boolean isDisabled();

    public abstract void setDisabled(boolean disabled);
}