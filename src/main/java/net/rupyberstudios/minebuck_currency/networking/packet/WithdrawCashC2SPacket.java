package net.rupyberstudios.minebuck_currency.networking.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.database.DatabaseManager;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import net.rupyberstudios.minebuck_currency.screen.AutomatedTellerMachineScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WithdrawCashC2SPacket {
    public static void send(@NotNull ID cardId, String pinHash, int amount) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeLong(cardId.toLong());
        data.writeString(pinHash);
        data.writeInt(amount);
        ClientPlayNetworking.send(ModMessages.WITHDRAW_CASH, data);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if(MinebuckCurrency.connection == null) return;
        try {
            ID cardId = new ID(buf.readLong());
            String pinHash = buf.readString();
            int amount = buf.readInt();
            if(player.currentScreenHandler instanceof AutomatedTellerMachineScreenHandler screenHandler) {
                UUID cardOwner = DatabaseManager.getCardOwner(cardId);
                if(cardOwner != null && !cardOwner.equals(player.getUuid())) return;
                if(!DatabaseManager.isPinCorrect(cardId, pinHash)) return;
                ID receiptId = DatabaseManager.withdrawCash(cardId, amount, player.getUuid());
                screenHandler.withdrawCash(amount);
                screenHandler.printReceipt(receiptId);
            }
        } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
    }
}