package net.rupyberstudios.minebuck_currency.networking.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.database.DatabaseManager;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import org.jetbrains.annotations.NotNull;

public class GetCardBalancePacket {
    private static final Packet<Long> balance = new Packet<>();

    public static class C2S {
        public static Packet<Long> send(@NotNull ID cardId) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeLong(cardId.toLong());
            balance.prepare();
            ClientPlayNetworking.send(ModMessages.GET_CARD_BALANCE, data);
            return balance;
        }

        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                   PacketByteBuf buf, PacketSender responseSender) {
            if(MinebuckCurrency.connection == null) return;
            try {
                long balance = DatabaseManager.getCardBalance(new ID(buf.readLong()), player.getUuid());
                S2C.send(balance, player);
            } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        }
    }

    public static class S2C {
        public static void send(long balance, ServerPlayerEntity player) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeLong(balance);
            ServerPlayNetworking.send(player, ModMessages.GET_CARD_BALANCE, data);
        }

        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                                   @NotNull PacketByteBuf buf, PacketSender responseSender) {
            balance.write(buf.readLong());
        }
    }
}