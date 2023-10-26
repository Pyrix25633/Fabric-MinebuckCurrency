package net.rupyberstudios.minebuck_currency.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public abstract class BaseButtonWidget extends SwitchableWidget {
    protected final int u, v;
    protected final Identifier texture;
    protected boolean disabled;
    protected final TextRenderer textRenderer;

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
    public void renderButton(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        int u = this.u;
        int v = this.v;
        if(this.disabled) v += this.getHeight();
        else if(this.isMouseOver(mouseX, mouseY)) u += this.getWidth();
        context.drawTexture(texture, this.getX(), this.getY(), u, v,
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

    @Override
    public void playDownSound(SoundManager soundManager) {
        if(!this.disabled)
            super.playDownSound(soundManager);
    }
}