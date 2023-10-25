package net.rupyberstudios.minebuck_currency.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public abstract class BaseButtonWidget extends PressableWidget {
    protected int u, v;
    protected Identifier texture;
    protected boolean disabled;
    protected TextRenderer textRenderer;

    public BaseButtonWidget(int x, int y, int u, int v, int width, int height, Text message,
                            Identifier texture, TextRenderer textRenderer) {
        super(x, y, width, height, message);
        this.u = u;
        this.v = v;
        this.texture = texture;
        this.disabled = false;
        this.textRenderer = textRenderer;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(texture, this.getX(), this.getY(), u, v + (this.disabled ? this.height : 0),
                this.width, this.height);
        context.drawTextWithShadow(this.textRenderer, getMessage(),
                this.getX() + (getWidth() - this.textRenderer.getWidth(getMessage())) / 2,
                this.getY() + (getHeight() - 8) / 2, 0xFFFFFF);
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}