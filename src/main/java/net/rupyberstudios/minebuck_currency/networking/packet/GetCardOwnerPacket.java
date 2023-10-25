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

public class GetCardOwnerPacket {
    private static final PacketSequence<String> owners = new PacketSequence<>();

    public static class C2S {
        public static Packet<String> send(@NotNull ID cardId) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeLong(cardId.toLong());
            int id = owners.prepare();
            data.writeInt(id);
            ClientPlayNetworking.send(ModMessages.GET_CARD_OWNER, data);
            return owners.get(id);
        }

        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                   PacketByteBuf buf, PacketSender responseSender) {
            if(MinebuckCurrency.connection == null) return;
            try {
                UUID ownerId = DatabaseManager.getCardOwner(new ID(buf.readLong()));
                String owner = "";
                boolean personal = false;
                if(ownerId != null) {
                    ServerPlayerEntity ownerPlayer = server.getPlayerManager().getPlayer(ownerId);
                    if(ownerPlayer != null) {
                        owner = ownerPlayer.getGameProfile().getName();
                    }
                    personal = true;
                }
                S2C.send(owner, personal, buf.readInt(), player);
            } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        }
    }

    public static class S2C {
        public static void send(String owner, boolean personal, int id, ServerPlayerEntity player) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeString(owner);
            data.writeBoolean(personal);
            data.writeInt(id);
            ServerPlayNetworking.send(player, ModMessages.GET_CARD_OWNER, data);
        }

        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                                   @NotNull PacketByteBuf buf, PacketSender responseSender) {
            String owner = buf.readString();
            boolean personal = buf.readBoolean();
            owner = personal ? owner : null;
            int id = buf.readInt();
            owners.write(id, owner);
        }
    }
}