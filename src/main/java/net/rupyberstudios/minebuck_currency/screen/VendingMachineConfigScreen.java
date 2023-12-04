package net.rupyberstudios.minebuck_currency.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.entity.AutomatedTellerMachineBlockEntity;
import net.rupyberstudios.minebuck_currency.block.entity.VendingMachineBlockEntity;
import net.rupyberstudios.minebuck_currency.config.ModConfig;
import net.rupyberstudios.minebuck_currency.database.Hash;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.networking.packet.IsCardPinCorrectPacket;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import net.rupyberstudios.minebuck_currency.screen.widget.BaseButtonWidget;
import net.rupyberstudios.minebuck_currency.screen.widget.SwitchableWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class VendingMachineConfigScreen extends HandledScreen<VendingMachineConfigScreenHandler> {
    private Identifier texture;
    private int textColor;
    private static final Text PIN_TEXT = Text.translatable("container.minebuck_currency.vending_machine.config.pin");
    private static final Text PIECE_PRICE_TEXT = Text.translatable("container.minebuck_currency.vending_machine.config.piece_price");
    private static final Text QUANTITY_TEXT = Text.translatable("container.minebuck_currency.vending_machine.config.quantity");
    private static final Text CONFIGURE_TEXT = Text.translatable("container.minebuck_currency.vending_machine.config.configure");
    private TextFieldWidget pinField, piecePriceField, quantityField;
    private boolean pinFieldEditable, piecePriceFieldEditable, quantityFieldEditable;
    private final ArrayList<SwitchableWidget> buttons = new ArrayList<>();
    private final Position position;
    private boolean cardPinCorrect = false;
    private ID cardPinCorrectId;

    public VendingMachineConfigScreen(VendingMachineConfigScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
        this.backgroundHeight = 211;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.cardPinCorrectId = null;
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        if(button instanceof SwitchableWidget baseButton)
            this.buttons.add(baseButton);
    }

    @Override
    protected void init() {
        super.init();
        texture = ModConfig.INSTANCE.classicGui ?
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/vending_machine_config_classic.png") :
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/vending_machine_config.png");
        textColor = ModConfig.INSTANCE.classicGui ? 0x404040 : 0xd6d6df;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.position.setXY((width - backgroundWidth) / 2, (height - backgroundHeight) / 2);
        pinField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 25,
                56, 12, PIN_TEXT);
        this.pinField.setDrawsBackground(false);
        this.pinField.setMaxLength(9);
        this.setPinFieldEditable(false);
        this.addSelectableChild(this.pinField);
        piecePriceField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 49,
                56, 12, PIECE_PRICE_TEXT);
        this.piecePriceField.setDrawsBackground(false);
        this.piecePriceField.setMaxLength(9);
        this.setPiecePriceFieldEditable(false);
        this.addSelectableChild(this.piecePriceField);
        quantityField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 72,
                56, 12, QUANTITY_TEXT);
        this.quantityField.setDrawsBackground(false);
        this.quantityField.setMaxLength(9);
        this.setQuantityFieldEditable(false);
        this.addSelectableChild(this.quantityField);
        this.buttons.clear();
        this.addButton(new ConfigureButtonWidget(this));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String pin = this.pinField.getText();
        String piecePrice = this.piecePriceField.getText();
        String quantity = this.quantityField.getText();
        this.init(client, width, height);
        this.pinField.setText(pin);
        this.piecePriceField.setText(piecePrice);
        this.quantityField.setText(quantity);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            assert this.client != null;
            assert this.client.player != null;
            this.client.player.closeHandledScreen();
        }
        if(this.pinField.keyPressed(keyCode, scanCode, modifiers) || this.pinField.isActive()) {
            return true;
        }
        if(this.piecePriceField.keyPressed(keyCode, scanCode, modifiers) || this.piecePriceField.isActive()) {
            return true;
        }
        if(this.quantityField.keyPressed(keyCode, scanCode, modifiers) || this.quantityField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        pinField.mouseClicked(mouseX, mouseY, button);
        piecePriceField.mouseClicked(mouseX, mouseY, button);
        quantityField.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForeground(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, textColor, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, textColor, false);
        context.drawText(this.textRenderer, PIN_TEXT,
                112 - this.textRenderer.getWidth(PIN_TEXT) - 2,
                25, textColor, false);
        context.drawText(this.textRenderer, PIECE_PRICE_TEXT,
                112 - this.textRenderer.getWidth(PIECE_PRICE_TEXT) - 2,
                49, textColor, false);
        context.drawText(this.textRenderer, QUANTITY_TEXT,
                112 - this.textRenderer.getWidth(QUANTITY_TEXT) - 2,
                72, textColor, false);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        context.drawTexture(texture, position.getX(), position.getY(), 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(texture, position.getX() + 109, position.getY() + 21, 0, this.backgroundHeight +
                (this.pinFieldEditable ? 0 : 16), 60, 16);
        context.drawTexture(texture, position.getX() + 109, position.getY() + 45, 0, this.backgroundHeight +
                (this.piecePriceFieldEditable ? 0 : 16), 60, 16);
        context.drawTexture(texture, position.getX() + 109, position.getY() + 68, 0, this.backgroundHeight +
                (this.quantityFieldEditable ? 0 : 16), 60, 16);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.pinField.render(context, mouseX, mouseY, delta);
        this.piecePriceField.render(context, mouseX, mouseY, delta);
        this.quantityField.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        boolean pinFieldShouldBeEditable = this.pinFieldShouldBeEditable();
        if(this.pinFieldEditable != pinFieldShouldBeEditable) this.setPinFieldEditable(pinFieldShouldBeEditable);
        this.pinField.tick();
        NbtCompound cardNbt = this.handler.getItem().getStack().getNbt();
        int cardPin = parsePinField();
        if(cardPin != -1 && cardNbt != null && cardNbt.contains("id")) {
            ID cardId = new ID(cardNbt.getLong("id"));
            if(!cardId.equals(this.cardPinCorrectId)) {
                this.cardPinCorrectId = cardId;
                this.cardPinCorrect = IsCardPinCorrectPacket.C2S.send(cardId, Hash.digest(this.pinField.getText())).read();
            }
        }
        else {
            this.cardPinCorrect = false;
            this.cardPinCorrectId = null;
        }
        boolean piecePriceFieldShouldBeEditable = this.piecePriceFieldShouldBeEditable();
        if(this.piecePriceFieldEditable != piecePriceFieldShouldBeEditable)
            this.setPiecePriceFieldEditable(piecePriceFieldShouldBeEditable);
        this.piecePriceField.tick();
        boolean quantityFieldShouldBeEditable = this.quantityFieldShouldBeEditable();
        if(this.quantityFieldEditable != quantityFieldShouldBeEditable)
            this.setQuantityFieldEditable(quantityFieldShouldBeEditable);
        this.quantityField.tick();
        int quantity = parseQuantityField();
        ItemStack stack = this.handler.getItem().getStack();
        boolean configureShouldBeDisabled = quantity == -1 || !quantityFieldShouldBeEditable || this.cardPinCorrect ||
                stack.isEmpty();
        if(this.buttons.get(0).isDisabled() != configureShouldBeDisabled)
            this.buttons.get(0).setDisabled(configureShouldBeDisabled);
    }

    private void setPinFieldEditable(boolean editable) {
        this.pinField.setEditable(editable);
        this.pinFieldEditable = editable;
    }

    private void setPiecePriceFieldEditable(boolean editable) {
        this.piecePriceField.setEditable(editable);
        this.piecePriceFieldEditable = editable;
    }

    private void setQuantityFieldEditable(boolean editable) {
        this.quantityField.setEditable(editable);
        this.quantityFieldEditable = editable;
    }

    public int parsePinField() {
        String pin = this.pinField.getText();
        if(pin == null || pin.contains("-")) return -1;
        String string = SharedConstants.stripInvalidChars(pin);
        if(string.length() < 3 || string.length() > 9) return -1;
        try {return Integer.parseInt(pin);}
        catch(NumberFormatException e) {return -1;}
    }

    public int parsePiecePriceField() {
        String piecePrice = this.piecePriceField.getText();
        if(piecePrice == null || piecePrice.contains("-")) return -1;
        String string = SharedConstants.stripInvalidChars(piecePrice);
        if(string.isEmpty() || string.length() > 9) return -1;
        try {return Integer.parseInt(piecePrice);}
        catch(NumberFormatException e) {return -1;}
    }

    public int parseQuantityField() {
        String quantity = this.quantityField.getText();
        if(quantity == null || quantity.contains("-")) return -1;
        String string = SharedConstants.stripInvalidChars(quantity);
        if(string.isEmpty() || string.length() > 9) return -1;
        try {return Integer.parseInt(quantity);}
        catch(NumberFormatException e) {return -1;}
    }

    public boolean pinFieldShouldBeEditable() {
        ItemStack stack = this.handler.getCard().getStack();
        return stack.getItem() == ModItems.CARD && (stack.getNbt() != null && stack.getNbt().contains("id"));
    }

    public boolean piecePriceFieldShouldBeEditable() {
        return pinFieldShouldBeEditable() && cardPinCorrect;
    }

    public boolean quantityFieldShouldBeEditable() {
        return piecePriceFieldShouldBeEditable() && parsePiecePriceField() != -1;
    }

    @Environment(value = EnvType.CLIENT)
    static class ConfigureButtonWidget extends BaseButtonWidget {
        private final VendingMachineConfigScreen screen;

        public ConfigureButtonWidget(@NotNull VendingMachineConfigScreen screen) {
            super(screen.position.getX() + 43, screen.position.getY() + 90, 60, 211, 90, 22,
                    CONFIGURE_TEXT, screen.texture, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {
            if(this.disabled) return;
            ItemStack card = screen.handler.getInventory().getStack(AutomatedTellerMachineBlockEntity.INPUT_SLOT);
            if(card.getNbt() == null || !card.getNbt().contains("id")) return;
            /*WithdrawCashC2SPacket.send(new ID(card.getNbt().getLong("id")),
                    Hash.digest(String.valueOf(screen.pinField.getText())), screen.parseAmountField());*/
        }
    }
}