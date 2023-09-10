package net.rupyberstudios.minebuck_currency.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.networking.packet.ItemStackSyncS2CPacket;

public class ModMessages {
    public static final Identifier ITEM_STACK_SYNC_ID = new Identifier(MinebuckCurrency.MOD_ID, "item_stack_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ITEM_STACK_SYNC_ID, ItemStackSyncS2CPacket::receive);
    }
}