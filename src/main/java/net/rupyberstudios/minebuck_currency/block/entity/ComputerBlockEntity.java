package net.rupyberstudios.minebuck_currency.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.rupyberstudios.minebuck_currency.block.custom.ComputerBlock;
import net.rupyberstudios.minebuck_currency.networking.packet.ItemStackSyncS2CPacket;
import net.rupyberstudios.minebuck_currency.screen.ComputerActivateCardScreenHandler;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return switch(getCachedState().get(ComputerBlock.OPEN_SCREEN)) {
            case OFF -> Text.literal("");
            case ACTIVATE_CARD -> Text.translatable("container.minebuck_currency.computer.activate_card");
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ComputerActivateCardScreenHandler(syncId, playerInventory, this, world, pos, getCachedState());
    }

    public ItemStack getRenderStack() {
        return items.get(0);
    }

    public void setInventory(DefaultedList<ItemStack> list) {
        for(int i = 0; i < items.size(); i++)
            items.set(i, list.get(i));
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