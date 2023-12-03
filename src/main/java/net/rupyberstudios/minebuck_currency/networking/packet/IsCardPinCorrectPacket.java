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

import java.util.UUID;

public class IsCardPinCorrectPacket {
    private static final Packet<Boolean> correct = new Packet<>();

    public static class C2S {
        public static Packet<Boolean> send(@NotNull ID cardId, String pinHash) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeLong(cardId.toLong());
            data.writeString(pinHash);
            correct.prepare();
            ClientPlayNetworking.send(ModMessages.IS_CARD_PIN_CORRECT, data);
            return correct;
        }

        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                   PacketByteBuf buf, PacketSender responseSender) {
            if(MinebuckCurrency.connection == null) return;
            try {
                ID cardId = new ID(buf.readLong());
                boolean correct = DatabaseManager.isPinCorrect(cardId, buf.readString());
                UUID owner = DatabaseManager.getCardOwner(cardId);
                if(owner != null && owner != player.getUuid()) correct = false;
                S2C.send(correct, player);
            } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        }
    }

    public static class S2C {
        public static void send(boolean correct, ServerPlayerEntity player) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBoolean(correct);
            ServerPlayNetworking.send(player, ModMessages.IS_CARD_PIN_CORRECT, data);
        }

        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                                   @NotNull PacketByteBuf buf, PacketSender responseSender) {
            correct.write(buf.readBoolean());
        }
    }
}