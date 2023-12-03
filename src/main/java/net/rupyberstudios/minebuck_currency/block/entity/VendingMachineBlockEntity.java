package net.rupyberstudios.minebuck_currency.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.rupyberstudios.minebuck_currency.block.custom.ComputerBlock;
import net.rupyberstudios.minebuck_currency.block.custom.VendingMachineBlock;
import net.rupyberstudios.minebuck_currency.block.property.ComputerOpenScreen;
import net.rupyberstudios.minebuck_currency.block.property.VendingMachineOpenScreen;
import net.rupyberstudios.minebuck_currency.networking.packet.ItemStackSyncS2CPacket;
import net.rupyberstudios.minebuck_currency.screen.ComputerActivateCardScreenHandler;
import net.rupyberstudios.minebuck_currency.screen.ComputerCardBalanceScreenHandler;
import org.jetbrains.annotations.Nullable;

public class VendingMachineBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);
    public static final int CARD_SLOT = 0, ITEM_SLOT = 1, RECEIPT_SLOT = 2;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return 0;
        }

        @Override
        public void set(int index, int value) {
            assert VendingMachineBlockEntity.this.world != null;
            VendingMachineBlockEntity.this.world.setBlockState(VendingMachineBlockEntity.this.getPos(),
                    VendingMachineBlockEntity.this.getCachedState()
                            .with(VendingMachineBlock.OPEN_SCREEN, VendingMachineOpenScreen.OFF)
                            .with(VendingMachineBlock.ON, false));
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public VendingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VENDING_MACHINE, pos, state);
    }

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public Text getDisplayName() {
        return switch(getCachedState().get(VendingMachineBlock.OPEN_SCREEN)) {
            case OFF -> Text.literal("");
            case CONFIG -> Text.translatable("container.minebuck_currency.vending_machine.config");
            case VENDING_MACHINE -> Text.translatable("container.minebuck_currency.vending_machine.vending_machine");
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return switch(getCachedState().get(ComputerBlock.OPEN_SCREEN)) {
            case OFF -> null;
            case ACTIVATE_CARD -> new VendingMachineConfigScreenHandler(syncId, playerInventory, this);
            case CARD_BALANCE -> new VendingMachineScreenHandler(syncId, playerInventory, this);
        };
    }

    public ItemStack getRenderStack() {
        return items.get(CARD_SLOT);
    }

    public void setInventory(DefaultedList<ItemStack> list) {
        for(int i = 0; i < items.size(); i++)
            items.set(i, list.get(i));
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
    }

    @Override
    public void markDirty() {
        ItemStackSyncS2CPacket.send(world, items, pos);
        super.markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {}
}