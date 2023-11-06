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
import net.rupyberstudios.minebuck_currency.database.Hash;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.networking.packet.ActivateCardC2SPacket;
import net.rupyberstudios.minebuck_currency.screen.util.BooleanExtra;
import net.rupyberstudios.minebuck_currency.screen.util.ExtraEnum;
import net.rupyberstudios.minebuck_currency.screen.util.Position;
import net.rupyberstudios.minebuck_currency.screen.widget.BaseButtonWidget;
import net.rupyberstudios.minebuck_currency.screen.widget.ExtraButtonWidget;
import net.rupyberstudios.minebuck_currency.screen.widget.SwitchableWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class ComputerActivateCardScreen extends HandledScreen<ComputerActivateCardScreenHandler> {
    private Identifier texture;
    private int textColor;
    private static final Text PIN_TEXT = Text.translatable("container.minebuck_currency.computer.activate_card.pin");
    private static final Text ACTIVATE_TEXT = Text.translatable("container.minebuck_currency.computer.activate_card.activate");
    private static final Text PERSONAL_TEXT = Text.translatable("container.minebuck_currency.computer.activate_card.personal");
    private TextFieldWidget pinField;
    private boolean pinFieldEditable;
    private final ArrayList<SwitchableWidget> buttons = new ArrayList<>();
    private final Position position;

    public ComputerActivateCardScreen(ComputerActivateCardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.position = new Position(0, 0);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        if(button instanceof SwitchableWidget baseButton)
            this.buttons.add(baseButton);
    }

    @Override
    protected void init() {
        super.init();
        texture = ModConfigs.classicGui ?
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine_classic.png") :
                new Identifier(MinebuckCurrency.MOD_ID, "textures/gui/container/automated_teller_machine.png");
        textColor = ModConfigs.classicGui ? 0x404040 : 0xd6d6df;
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
        this.buttons.clear();
        this.addButton(new ExtraButtonWidget(this.position.getX() + 153, this.position.getY() + 48, 60, 166,
                16, 16, texture, BooleanExtra.TRUE));
        this.addButton(new ActivateCardButtonWidget(this));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String pin = this.pinField.getText();
        ExtraEnum personalCard = ((ExtraButtonWidget)this.buttons.get(0)).getStatus();
        this.init(client, width, height);
        this.pinField.setText(pin);
        ((ExtraButtonWidget)this.buttons.get(0)).setStatus(personalCard);
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
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, textColor, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, textColor, false);
        context.drawText(this.textRenderer, PIN_TEXT,
                112 - this.textRenderer.getWidth(PIN_TEXT) - 2,
                25, textColor, false);
        context.drawText(this.textRenderer, PERSONAL_TEXT,
                156 - this.textRenderer.getWidth(PERSONAL_TEXT) - 2,
                52, textColor, false);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        context.drawTexture(texture, position.getX(), position.getY(), 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(texture, position.getX() + 109, position.getY() + 21, 0, this.backgroundHeight +
                (this.pinFieldEditable ? 0 : 16), 60, 16);
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
    static class ActivateCardButtonWidget extends BaseButtonWidget {
        private final ComputerActivateCardScreen screen;
        
        public ActivateCardButtonWidget(@NotNull ComputerActivateCardScreen screen) {
            super(screen.position.getX() + 7, screen.position.getY() + 45, 0, 198, 90, 22,
                    ACTIVATE_TEXT, screen.texture, screen.textColor, screen.textRenderer);
            this.screen = screen;
        }

        @Override
        public void onPress() {
            int parsed = this.screen.parsePinField();
            if(this.disabled || parsed == -1 || !this.screen.pinFieldShouldBeEditable()) return;
            if(this.screen.buttons.get(0) instanceof ExtraButtonWidget extraButtonWidget) {
                if(extraButtonWidget.getStatus() instanceof BooleanExtra status)
                    ActivateCardC2SPacket.send(Hash.digest(screen.pinField.getText()), status.toBoolean());
            }
        }
    }
}