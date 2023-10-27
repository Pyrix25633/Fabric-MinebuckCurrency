package net.rupyberstudios.minebuck_currency.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class ComputerCardBalanceScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final Slot input;

    public ComputerCardBalanceScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf packetByteBuf) {
        this(syncId, inventory, new SimpleInventory(2));
    }

    public ComputerCardBalanceScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.COMPUTER_CARD_BALANCE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.input = this.addSlot(new Slot(inventory, ComputerBlockEntity.INPUT_SLOT, 8, 21));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public Slot getInput() {
        return input;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if(slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if(slot == 1) {
                if(!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if(slot == 0 ?
                    !this.insertItem(itemStack2, 2, 38, false) :
                    (itemStack2.getItem() == ModItems.CARD ?
                            !this.insertItem(itemStack2, 0, 1, false) :
                            (slot >= 2 && slot < 29 ?
                                    !this.insertItem(itemStack2, 29, 38, false) :
                                    slot >= 29 && slot < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if(itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            }
            slot2.markDirty();
            if(itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if(player.getWorld().isClient) return;
        player.getInventory().offerOrDrop(inventory.getStack(ComputerBlockEntity.INPUT_SLOT));
        if(this.inventory instanceof ComputerBlockEntity computerBlockEntity)
            computerBlockEntity.getPropertyDelegate().set(0, 0);
        inventory.markDirty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for(int i = 0; i < 3; ++i)
            for(int l = 0; l < 9; ++l)
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for(int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
}