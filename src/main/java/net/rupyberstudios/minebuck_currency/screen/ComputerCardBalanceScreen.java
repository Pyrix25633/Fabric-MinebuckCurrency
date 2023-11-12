package net.rupyberstudios.minebuck_currency.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.config.ModConfig;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.networking.packet.GetCardBalancePacket;
import net.rupyberstudios.minebuck_currency.networking.packet.GetPersonalCardsTotalBalancePacket;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import org.jetbrains.annotations.NotNull;

@Environment(value = EnvType.CLIENT)
public class ComputerCardBalanceScreen extends HandledScreen<ComputerCardBalanceScreenHandler> {
    private Identifier texture;
    private int textColor;
    private static final Text BALANCE_TEXT = Text.translatable("container.minebuck_currency.computer.card_balance.balance");
    private static final Text SYMBOL_TEXT = Text.translatable("symbol.minebuck_currency.minebuck");
    private static final Text PERSONAL_CARDS_TOTAL_BALANCE_TEXT =
            Text.translatable("container.minebuck_currency.computer.card_balance.personal_cards_total_balance");
    private static final Text DEFAULT_TEXT = Text.literal("?").append(SYMBOL_TEXT);
    private final Position position;
    private Text balance, personalCardsTotalBalance;
    private ID balanceId;

    public ComputerCardBalanceScreen(ComputerCardBalanceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
        this.balance = DEFAULT_TEXT.copy();
        this.personalCardsTotalBalance = DEFAULT_TEXT.copy();
        this.balanceId = null;
    }

    @Override
    protected void init() {
        super.init();
        texture = ModConfig.INSTANCE.classicGui ?
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/computer_card_balance_classic.png") :
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/computer_card_balance.png");
        textColor = ModConfig.INSTANCE.classicGui ? 0x404040 : 0xd6d6df;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.position.setXY((width - backgroundWidth) / 2, (height - backgroundHeight) / 2);
        if(personalCardsTotalBalance.equals(DEFAULT_TEXT))
            this.personalCardsTotalBalance = Text.literal(GetPersonalCardsTotalBalancePacket.C2S.send().read().toString())
                    .append(SYMBOL_TEXT);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    @Override
    protected void drawForeground(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, textColor, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, textColor, false);
        context.drawText(this.textRenderer, BALANCE_TEXT, 30, 25, textColor, false);
        context.drawText(this.textRenderer, balance,
                this.backgroundWidth - this.textRenderer.getWidth(balance) - 6, 25, textColor, false);
        context.drawText(this.textRenderer, PERSONAL_CARDS_TOTAL_BALANCE_TEXT,
                (this.backgroundWidth - this.textRenderer.getWidth(PERSONAL_CARDS_TOTAL_BALANCE_TEXT)) / 2,
                44, textColor, false);
        context.drawText(this.textRenderer, personalCardsTotalBalance,
                (this.backgroundWidth - this.textRenderer.getWidth(personalCardsTotalBalance)) / 2,
                58, textColor, false);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        context.drawTexture(texture, position.getX(), position.getY(), 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        NbtCompound cardNbt = this.handler.getInput().getStack().getNbt();
        if(cardNbt != null && cardNbt.contains("id")) {
            ID cardId = new ID(cardNbt.getLong("id"));
            if(!cardId.equals(this.balanceId)) {
                this.balanceId = cardId;
                long balance = GetCardBalancePacket.C2S.send(cardId).read();
                String balanceString = balance != -1 ? String.valueOf(balance) : "?";
                this.balance = Text.literal(balanceString).append(SYMBOL_TEXT);
            }
        }
        else {
            this.balance = DEFAULT_TEXT.copy();
            this.balanceId = null;
        }
    }
}