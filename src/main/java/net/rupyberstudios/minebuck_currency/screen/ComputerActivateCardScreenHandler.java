package net.rupyberstudios.minebuck_currency.screen;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.block.custom.ComputerBlock;
import net.rupyberstudios.minebuck_currency.block.property.ComputerOpenScreen;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.screen.slot.OutputSlot;

public class ComputerActivateCardScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private World world;
    private BlockPos pos;
    private BlockState state;

    public ComputerActivateCardScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf packetByteBuf) {
        this(syncId, inventory, new SimpleInventory(2));
    }

    public ComputerActivateCardScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.COMPUTER_ACTIVATE_CARD_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 8, 52));
        this.addSlot(new OutputSlot(inventory, 1, 62, 52));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public ComputerActivateCardScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                             World world, BlockPos pos, BlockState state) {
        this(syncId, playerInventory, inventory);
        this.world = world;
        this.pos = pos;
        this.state = state;
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
        player.getInventory().offerOrDrop(inventory.getStack(0));
        player.getInventory().offerOrDrop(inventory.getStack(1));
        if(this.world != null) {
            world.setBlockState(pos, state.with(ComputerBlock.ON, false)
                    .with(ComputerBlock.OPEN_SCREEN, ComputerOpenScreen.OFF));
            inventory.markDirty();
        }
        super.onClosed(player);
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