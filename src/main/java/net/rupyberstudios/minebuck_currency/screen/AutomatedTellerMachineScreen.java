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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;
import net.rupyberstudios.minebuck_currency.config.ModConfigs;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import net.rupyberstudios.minebuck_currency.screen.widget.BaseButtonWidget;
import net.rupyberstudios.minebuck_currency.screen.widget.SwitchableWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class AutomatedTellerMachineScreen extends HandledScreen<AutomatedTellerMachineScreenHandler> {
    private static final Identifier TEXTURE = ModConfigs.classicGui ?
            new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine_classic.png") :
            new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine.png");
    private static final int TEXT_COLOR = ModConfigs.classicGui ? 0x404040 : 0xd6d6df;
    private static final Text PIN_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.pin");
    private static final Text AMOUNT_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.amount");
    private static final Text DEPOSIT_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.deposit");
    private static final Text WITHDRAW_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.withdraw");
    private TextFieldWidget pinField, amountField;
    private boolean pinFieldEditable, amountFieldEditable;
    private final ArrayList<SwitchableWidget> buttons = new ArrayList<>();
    private final Position position;

    public AutomatedTellerMachineScreen(AutomatedTellerMachineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
        this.backgroundHeight = 189;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        if(button instanceof SwitchableWidget baseButton)
            this.buttons.add(baseButton);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.position.setXY((width - backgroundWidth) / 2, (height - backgroundHeight) / 2);
        pinField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 25,
                56, 12, PIN_TEXT);
        this.pinField.setFocusUnlocked(false);
        this.pinField.setEditableColor(-1);
        this.pinField.setUneditableColor(-1);
        this.pinField.setDrawsBackground(false);
        this.pinField.setMaxLength(9);
        this.setPinFieldEditable(false);
        this.addSelectableChild(this.pinField);
        this.setInitialFocus(this.pinField);
        amountField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 25,
                56, 12, PIN_TEXT);
        this.amountField.setFocusUnlocked(false);
        this.amountField.setEditableColor(-1);
        this.amountField.setUneditableColor(-1);
        this.amountField.setDrawsBackground(false);
        this.amountField.setMaxLength(9);
        this.setAmountFieldEditable(false);
        this.addSelectableChild(this.amountField);
        this.setInitialFocus(this.amountField);
        this.buttons.clear();
        this.addButton(new DepositButtonWidget(this));
        this.addButton(new WithdrawButtonWidget(this));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String pin = this.pinField.getText();
        String amount = this.amountField.getText();
        this.init(client, width, height);
        this.pinField.setText(pin);
        this.amountField.setText(amount);
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
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void drawForeground(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, TEXT_COLOR, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, TEXT_COLOR, false);
        context.drawText(this.textRenderer, PIN_TEXT,
                112 - this.textRenderer.getWidth(PIN_TEXT) - 2,
                25, TEXT_COLOR, false);
        context.drawText(this.textRenderer, AMOUNT_TEXT,
                112 - this.textRenderer.getWidth(AMOUNT_TEXT) - 2,
                49, TEXT_COLOR, false);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, position.getX(), position.getY(), 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(TEXTURE, position.getX() + 109, position.getY() + 21, 0, this.backgroundHeight +
                (this.pinFieldEditable ? 0 : 16), 60, 16);
        context.drawTexture(TEXTURE, position.getX() + 109, position.getY() + 45, 0, this.backgroundHeight +
                (this.amountFieldEditable ? 0 : 16), 60, 16);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.pinField.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        boolean shouldBeEditable = this.pinFieldShouldBeEditable();
        if(this.pinFieldEditable != shouldBeEditable) this.setPinFieldEditable(shouldBeEditable);
        this.pinField.tick();
        boolean shouldBeDisabled = parsePinField() == -1 || !shouldBeEditable;
        if(this.buttons.get(1).isDisabled() != shouldBeDisabled) this.buttons.get(1).setDisabled(shouldBeDisabled);
    }

    private void setPinFieldEditable(boolean editable) {
        this.pinField.setEditable(editable);
        this.pinFieldEditable = editable;
    }

    private void setAmountFieldEditable(boolean editable) {
        this.amountField.setEditable(editable);
        this.amountFieldEditable = editable;
    }

    public int parsePinField() {
        String pin = this.pinField.getText();
        if(pin == null) return -1;
        String string = SharedConstants.stripInvalidChars(pin);
        if(string.length() < 3 || string.length() > 9) return -1;
        try {return Integer.parseInt(pin);}
        catch(NumberFormatException e) {return -1;}
    }

    public boolean pinFieldShouldBeEditable() {
        ItemStack inputStack = this.handler.getSlot(ComputerBlockEntity.INPUT_SLOT).getStack();
        ItemStack outputStack = this.handler.getSlot(ComputerBlockEntity.OUTPUT_SLOT).getStack();
        return inputStack.getItem() == ModItems.CARD &&
                (inputStack.getNbt() == null || !inputStack.getNbt().contains("id")) &&
                outputStack.isEmpty();
    }

    @Environment(value = EnvType.CLIENT)
    static class DepositButtonWidget extends BaseButtonWidget {
        private final AutomatedTellerMachineScreen screen;

        public DepositButtonWidget(@NotNull AutomatedTellerMachineScreen screen) {
            super(screen.position.getX() + 7, screen.position.getY() + 68, 60, 189, 79, 22,
                    DEPOSIT_TEXT, TEXTURE, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {

        }
    }

    @Environment(value = EnvType.CLIENT)
    static class WithdrawButtonWidget extends BaseButtonWidget {
        private final AutomatedTellerMachineScreen screen;

        public WithdrawButtonWidget(@NotNull AutomatedTellerMachineScreen screen) {
            super(screen.position.getX() + 90, screen.position.getY() + 68, 60, 189, 79, 22,
                    WITHDRAW_TEXT, TEXTURE, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {

        }
    }
}