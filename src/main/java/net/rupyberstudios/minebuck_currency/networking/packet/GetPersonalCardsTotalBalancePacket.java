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
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class GetPersonalCardsTotalBalancePacket {
    private static final Packet<BigInteger> total = new Packet<>();

    public static class C2S {
        public static Packet<BigInteger> send() {
            PacketByteBuf data = PacketByteBufs.create();
            total.prepare();
            ClientPlayNetworking.send(ModMessages.GET_PERSONAL_CARDS_TOTAL_BALANCE, data);
            return total;
        }

        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                   PacketByteBuf buf, PacketSender responseSender) {
            if(MinebuckCurrency.connection == null) return;
            try {
                BigInteger total = DatabaseManager.getPersonalCardsTotalBalance(player.getUuid());
                S2C.send(total, player);
            } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        }
    }

    public static class S2C {
        public static void send(@NotNull BigInteger total, ServerPlayerEntity player) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeByteArray(total.toByteArray());
            ServerPlayNetworking.send(player, ModMessages.GET_PERSONAL_CARDS_TOTAL_BALANCE, data);
        }

        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                                   @NotNull PacketByteBuf buf, PacketSender responseSender) {
            total.write(new BigInteger(buf.readByteArray()));
        }
    }
}