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
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;
import net.rupyberstudios.minebuck_currency.config.ModConfig;
import net.rupyberstudios.minebuck_currency.database.Hash;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.database.Utils;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.networking.packet.DepositCashC2SPacket;
import net.rupyberstudios.minebuck_currency.networking.packet.GetCardBalancePacket;
import net.rupyberstudios.minebuck_currency.networking.packet.WithdrawCashC2SPacket;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import net.rupyberstudios.minebuck_currency.screen.widget.BaseButtonWidget;
import net.rupyberstudios.minebuck_currency.screen.widget.SwitchableWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class AutomatedTellerMachineScreen extends HandledScreen<AutomatedTellerMachineScreenHandler> {
    private Identifier texture;
    private int textColor;
    private static final Text PIN_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.pin");
    private static final Text AMOUNT_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.amount");
    private static final Text DEPOSIT_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.deposit");
    private static final Text WITHDRAW_TEXT = Text.translatable("container.minebuck_currency.automated_teller_machine.withdraw");
    private TextFieldWidget pinField, amountField;
    private boolean pinFieldEditable, amountFieldEditable;
    private final ArrayList<SwitchableWidget> buttons = new ArrayList<>();
    private final Position position;
    private long balance;
    private ID balanceId;

    public AutomatedTellerMachineScreen(AutomatedTellerMachineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
        this.backgroundHeight = 189;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.balanceId = null;
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
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine_classic.png") :
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine.png");
        textColor = ModConfig.INSTANCE.classicGui ? 0x404040 : 0xd6d6df;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.position.setXY((width - backgroundWidth) / 2, (height - backgroundHeight) / 2);
        pinField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 25,
                56, 12, PIN_TEXT);
        this.pinField.setDrawsBackground(false);
        this.pinField.setMaxLength(9);
        this.setPinFieldEditable(false);
        this.addSelectableChild(this.pinField);
        amountField = new TextFieldWidget(this.textRenderer, position.getX() + 112, position.getY() + 49,
                56, 12, AMOUNT_TEXT);
        this.amountField.setDrawsBackground(false);
        this.amountField.setMaxLength(9);
        this.setAmountFieldEditable(false);
        this.addSelectableChild(this.amountField);
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
        if(this.amountField.keyPressed(keyCode, scanCode, modifiers) || this.amountField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        pinField.mouseClicked(mouseX, mouseY, button);
        amountField.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForeground(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, textColor, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, textColor, false);
        context.drawText(this.textRenderer, PIN_TEXT,
                112 - this.textRenderer.getWidth(PIN_TEXT) - 2,
                25, textColor, false);
        context.drawText(this.textRenderer, AMOUNT_TEXT,
                112 - this.textRenderer.getWidth(AMOUNT_TEXT) - 2,
                49, textColor, false);
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
                (this.amountFieldEditable ? 0 : 16), 60, 16);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.pinField.render(context, mouseX, mouseY, delta);
        this.amountField.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        boolean pinFieldShouldBeEditable = this.pinFieldShouldBeEditable();
        if(this.pinFieldEditable != pinFieldShouldBeEditable) this.setPinFieldEditable(pinFieldShouldBeEditable);
        this.pinField.tick();
        boolean amountFieldShouldBeEditable = this.amountFieldShouldBeEditable();
        if(this.amountFieldEditable != amountFieldShouldBeEditable) this.setAmountFieldEditable(amountFieldShouldBeEditable);
        this.amountField.tick();
        int amount = parseAmountField();
        NbtCompound cardNbt = this.handler.getInput().getStack().getNbt();
        if(cardNbt != null && cardNbt.contains("id")) {
            ID cardId = new ID(cardNbt.getLong("id"));
            if(!cardId.equals(this.balanceId)) {
                this.balanceId = cardId;
                this.balance = GetCardBalancePacket.C2S.send(cardId).read();
            }
        }
        else {
            this.balance = -1;
            this.balanceId = null;
        }
        boolean depositShouldBeDisabled = amount == -1 || !amountFieldShouldBeEditable ||
                Utils.countCash(handler.getPlayerInventory()) < amount || balance < 0;
        if(this.buttons.get(0).isDisabled() != depositShouldBeDisabled)
            this.buttons.get(0).setDisabled(depositShouldBeDisabled);
        boolean withdrawShouldBeDisabled = amount == -1 || !amountFieldShouldBeEditable || balance < amount;
        if(this.buttons.get(1).isDisabled() != withdrawShouldBeDisabled)
            this.buttons.get(1).setDisabled(withdrawShouldBeDisabled);
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
        if(pin == null || pin.contains("-")) return -1;
        String string = SharedConstants.stripInvalidChars(pin);
        if(string.length() < 3 || string.length() > 9) return -1;
        try {return Integer.parseInt(pin);}
        catch(NumberFormatException e) {return -1;}
    }

    public int parseAmountField() {
        String amount = this.amountField.getText();
        if(amount == null || amount.contains("-")) return -1;
        String string = SharedConstants.stripInvalidChars(amount);
        if(string.isEmpty() || string.length() > 9) return -1;
        try {
            int parsed = Integer.parseInt(amount);
            return parsed > 0 ? parsed : -1;
        }
        catch(NumberFormatException e) {return -1;}
    }

    public boolean pinFieldShouldBeEditable() {
        ItemStack inputStack = this.handler.getSlot(AutomatedTellerMachineBlockEntity.INPUT_SLOT).getStack();
        ItemStack outputStack = this.handler.getSlot(AutomatedTellerMachineBlockEntity.OUTPUT_SLOT).getStack();
        return inputStack.getItem() == ModItems.CARD &&
                (inputStack.getNbt() != null && inputStack.getNbt().contains("id")) &&
                outputStack.isEmpty();
    }

    public boolean amountFieldShouldBeEditable() {
        return pinFieldShouldBeEditable() && parsePinField() != -1;
    }

    @Environment(value = EnvType.CLIENT)
    static class DepositButtonWidget extends BaseButtonWidget {
        private final AutomatedTellerMachineScreen screen;

        public DepositButtonWidget(@NotNull AutomatedTellerMachineScreen screen) {
            super(screen.position.getX() + 7, screen.position.getY() + 68, 60, 189, 79, 22,
                    DEPOSIT_TEXT, screen.texture, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {
            ItemStack card = screen.handler.getInventory().getStack(AutomatedTellerMachineBlockEntity.INPUT_SLOT);
            if(card.getNbt() == null || !card.getNbt().contains("id")) return;
            DepositCashC2SPacket.send(new ID(card.getNbt().getLong("id")),
                    Hash.digest(String.valueOf(screen.pinField.getText())), screen.parseAmountField());
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class WithdrawButtonWidget extends BaseButtonWidget {
        private final AutomatedTellerMachineScreen screen;

        public WithdrawButtonWidget(@NotNull AutomatedTellerMachineScreen screen) {
            super(screen.position.getX() + 90, screen.position.getY() + 68, 60, 189, 79, 22,
                    WITHDRAW_TEXT, screen.texture, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {
            ItemStack card = screen.handler.getInventory().getStack(AutomatedTellerMachineBlockEntity.INPUT_SLOT);
            if(card.getNbt() == null || !card.getNbt().contains("id")) return;
            WithdrawCashC2SPacket.send(new ID(card.getNbt().getLong("id")),
                    Hash.digest(String.valueOf(screen.pinField.getText())), screen.parseAmountField());
        }
    }
}