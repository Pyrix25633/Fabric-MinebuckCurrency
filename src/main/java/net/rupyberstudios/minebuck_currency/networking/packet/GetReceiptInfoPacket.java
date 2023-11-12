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
import net.rupyberstudios.minebuck_currency.database.ReceiptInfo;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import org.jetbrains.annotations.NotNull;

public class GetReceiptInfoPacket {
    private static final PacketSequence<ReceiptInfo> info = new PacketSequence<>();

    public static class C2S {
        public static Packet<ReceiptInfo> send(@NotNull ID receiptId) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeLong(receiptId.toLong());
            int id = info.prepare();
            data.writeInt(id);
            ClientPlayNetworking.send(ModMessages.GET_RECEIPT_INFO, data);
            return info.get(id);
        }

        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                   PacketByteBuf buf, PacketSender responseSender) {
            if(MinebuckCurrency.connection == null) return;
            try {
                ID receiptId = new ID(buf.readLong());
                ReceiptInfo info = DatabaseManager.getReceiptInfo(receiptId);
                S2C.send(info, buf.readInt(), player);
            } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        }
    }

    public static class S2C {
        public static void send(@NotNull ReceiptInfo info, int id, ServerPlayerEntity player) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeString(info.getEmitterPlayer());
            data.writeString(info.getSourcePlayer());
            data.writeString(info.getDestinationPlayer());
            ID sourceCardId = info.getSourceCardId();
            ID destinationCardId = info.getDestinationCardId();
            data.writeBoolean(sourceCardId != null);
            data.writeLong(sourceCardId != null ? sourceCardId.toLong() : 0);
            data.writeBoolean(destinationCardId != null);
            data.writeLong(destinationCardId != null ? destinationCardId.toLong() : 0);
            data.writeInt(info.getAmount());
            String item = info.getItem();
            String service = info.getService();
            String description = info.getDescription();
            data.writeString(item != null ? item : "");
            data.writeString(service != null ? service : "");
            data.writeString(description != null ? description : "");
            data.writeInt(id);
            ServerPlayNetworking.send(player, ModMessages.GET_RECEIPT_INFO, data);
        }

        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                                   @NotNull PacketByteBuf buf, PacketSender responseSender) {
            String emitterPlayer = buf.readString();
            String sourcePlayer = buf.readString();
            String destinationPlayer = buf.readString();
            long sourceCardLong = buf.readLong();
            ID sourceCardId = buf.readBoolean() ? new ID(sourceCardLong) : null;
            long destinationCardLong = buf.readLong();
            ID destinationCardId = buf.readBoolean() ? new ID(destinationCardLong) : null;
            int amount = buf.readInt();
            String item = buf.readString();
            item = item.isEmpty() ? null : item;
            String service = buf.readString();
            service = service.isEmpty() ? null : service;
            String description = buf.readString();
            description = description.isEmpty() ? null : description;
            ReceiptInfo received = new ReceiptInfo(emitterPlayer, sourcePlayer, destinationPlayer,
                    sourceCardId, destinationCardId, amount, item, service, description);
            int id = buf.readInt();
            info.write(id, received.get());
        }
    }
}