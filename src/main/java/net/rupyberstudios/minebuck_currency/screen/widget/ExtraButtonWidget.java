package net.rupyberstudios.minebuck_currency.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.screen.util.ExtraEnum;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public class ExtraButtonWidget extends SwitchableWidget {
    protected final int u, v;
    protected final Identifier texture;
    protected boolean disabled;
    private ExtraEnum status;

    public ExtraButtonWidget(int x, int y, int u, int v, int width, int height, Identifier texture, ExtraEnum initial) {
        super(x, y, width, height, Text.literal(""));
        this.u = u;
        this.v = v;
        this.texture = texture;
        this.disabled = false;
        this.status = initial;
    }

    @Override
    public void renderButton(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        int u = this.u;
        int v = this.v;
        if(this.isMouseOver(mouseX, mouseY)) u += this.getWidth();
        else if(this.disabled) v += this.getHeight();
        context.drawTexture(texture, this.getX(), this.getY(), u, v,
                this.width, this.height);
        Position statusPosition = status.getPosition();
        context.drawTexture(texture, this.getX(), this.getY(), statusPosition.getX(), statusPosition.getY(),
                this.width, this.height);
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public ExtraEnum getStatus() {
        return status;
    }

    public void setStatus(ExtraEnum status) {
        this.status = status;
    }

    @Override
    public void onPress() {
        status = status.getNext();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}