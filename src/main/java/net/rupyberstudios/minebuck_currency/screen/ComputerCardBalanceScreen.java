package net.rupyberstudios.minebuck_currency.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.networking.packet.GetPersonalCardsTotalBalancePacket;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import org.jetbrains.annotations.NotNull;

@Environment(value = EnvType.CLIENT)
public class ComputerCardBalanceScreen extends HandledScreen<ComputerCardBalanceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MinebuckCurrency.MOD_ID,
            "textures/gui/container/computer_card_balance.png");
    private static final Text BALANCE_TEXT = Text.translatable("container.minebuck_currency.computer.card_balance.balance");
    private static final Text SYMBOL_TEXT = Text.translatable("symbol.minebuck_currency.minebuck");
    private static final Text PERSONAL_CARDS_TOTAL_BALANCE =
            Text.translatable("container.minebuck_currency.computer.card_balance.personal_cards_total_balance");
    private Text balance = Text.literal("?").append(SYMBOL_TEXT);
    private Text personalCardsTotalBalance = Text.literal("?").append(SYMBOL_TEXT);
    private final Position position;

    public ComputerCardBalanceScreen(ComputerCardBalanceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.position.setXY((width - backgroundWidth) / 2, (height - backgroundHeight) / 2);
        if(personalCardsTotalBalance.equals(Text.literal("?").append(SYMBOL_TEXT)))
            this.personalCardsTotalBalance = Text.literal(GetPersonalCardsTotalBalancePacket.C2S.send().read().toString())
                    .append(SYMBOL_TEXT);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    protected void drawForeground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, BALANCE_TEXT,
                position.getX() + 30,
                position.getY() + 25, 0x404040, false);
        context.drawText(this.textRenderer, PERSONAL_CARDS_TOTAL_BALANCE,
                (this.width - this.textRenderer.getWidth(PERSONAL_CARDS_TOTAL_BALANCE)) / 2,
                position.getY() + 45, 0x404040, false);
        context.drawText(this.textRenderer, personalCardsTotalBalance,
                (this.width - this.textRenderer.getWidth(personalCardsTotalBalance)) / 2,
                position.getY() + 56, 0x404040, false);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, position.getX(), position.getY(), 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawForeground(context, delta, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();

    }
}