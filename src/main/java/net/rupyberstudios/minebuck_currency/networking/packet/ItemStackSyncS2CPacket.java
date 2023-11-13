package net.rupyberstudios.minebuck_currency.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.block.entity.AutomatedTellerMachineBlockEntity;
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import org.jetbrains.annotations.NotNull;

public class ItemStackSyncS2CPacket {
    public static void send(World world, @NotNull DefaultedList<ItemStack> items, BlockPos pos) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(items.size());
        for(ItemStack item : items) data.writeItemStack(item);
        data.writeBlockPos(pos);
        for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)world, pos))
            ServerPlayNetworking.send(player, ModMessages.ITEM_STACK_SYNC_ID, data);
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        int size = buf.readInt();
        DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for(int i = 0; i < size; i++)
            list.set(i, buf.readItemStack());
        BlockPos position = buf.readBlockPos();
        assert client.world != null;
        if(client.world.getBlockEntity(position) instanceof ComputerBlockEntity computerBlockEntity)
            computerBlockEntity.setInventory(list);
        else if(client.world.getBlockEntity(position) instanceof AutomatedTellerMachineBlockEntity automatedTellerMachineBlockEntity)
            automatedTellerMachineBlockEntity.setInventory(list);
    }
}