package net.rupyberstudios.minebuck_currency.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.networking.packet.ActivateCardC2SPacket;
import net.rupyberstudios.minebuck_currency.networking.packet.GetCardOwnerPacket;
import net.rupyberstudios.minebuck_currency.networking.packet.ItemStackSyncS2CPacket;

public class ModMessages {
    public static final Identifier ITEM_STACK_SYNC_ID = new Identifier(MinebuckCurrency.MOD_ID, "item_stack_sync");
    public static final Identifier ACTIVATE_CARD_ID = new Identifier(MinebuckCurrency.MOD_ID, "activate_card");
    public static final Identifier GET_CARD_OWNER = new Identifier(MinebuckCurrency.MOD_ID, "get_card_owner");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ITEM_STACK_SYNC_ID, ItemStackSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(GET_CARD_OWNER, GetCardOwnerPacket.S2C::receive);
    }

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVATE_CARD_ID, ActivateCardC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_CARD_OWNER, GetCardOwnerPacket.C2S::receive);
    }
}