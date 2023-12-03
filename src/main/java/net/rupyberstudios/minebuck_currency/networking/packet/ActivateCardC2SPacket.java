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
import net.rupyberstudios.minebuck_currency.screen.ComputerActivateCardScreenHandler;

public class ActivateCardC2SPacket {
    public static void send(String pinHash, boolean personal) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeString(pinHash);
        data.writeBoolean(personal);
        ClientPlayNetworking.send(ModMessages.ACTIVATE_CARD, data);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if(MinebuckCurrency.connection == null) return;
        try {
            ID cardId = DatabaseManager.activateCard(buf.readString(), buf.readBoolean() ? player.getUuid() : null);
            if(player.currentScreenHandler instanceof ComputerActivateCardScreenHandler screenHandler) {
                screenHandler.activateCard(cardId);
            }
        } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
    }
}