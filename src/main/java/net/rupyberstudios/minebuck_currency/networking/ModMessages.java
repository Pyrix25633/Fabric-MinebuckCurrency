package net.rupyberstudios.minebuck_currency.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.networking.packet.*;

public class ModMessages {
    public static final Identifier ITEM_STACK_SYNC = new Identifier(MinebuckCurrency.MOD_ID, "item_stack_sync");
    public static final Identifier ACTIVATE_CARD = new Identifier(MinebuckCurrency.MOD_ID, "activate_card");
    public static final Identifier IS_CARD_PIN_CORRECT = new Identifier(MinebuckCurrency.MOD_ID, "is_card_pin_correct");
    public static final Identifier GET_CARD_OWNER = new Identifier(MinebuckCurrency.MOD_ID, "get_card_owner");
    public static final Identifier GET_CARD_BALANCE = new Identifier(MinebuckCurrency.MOD_ID, "get_card_balance");
    public static final Identifier GET_PERSONAL_CARDS_TOTAL_BALANCE =
            new Identifier(MinebuckCurrency.MOD_ID, "get_personal_cars_total_balance");
    public static final Identifier WITHDRAW_CASH = new Identifier(MinebuckCurrency.MOD_ID, "withdraw_cash");
    public static final Identifier DEPOSIT_CASH = new Identifier(MinebuckCurrency.MOD_ID, "deposit_cash");
    public static final Identifier GET_RECEIPT_INFO = new Identifier(MinebuckCurrency.MOD_ID, "get_receipt_info");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ITEM_STACK_SYNC, ItemStackSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(IS_CARD_PIN_CORRECT, IsCardPinCorrectPacket.S2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(GET_CARD_OWNER, GetCardOwnerPacket.S2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(GET_CARD_BALANCE, GetCardBalancePacket.S2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(GET_PERSONAL_CARDS_TOTAL_BALANCE,
                GetPersonalCardsTotalBalancePacket.S2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(GET_RECEIPT_INFO, GetReceiptInfoPacket.S2C::receive);
    }

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVATE_CARD, ActivateCardC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(IS_CARD_PIN_CORRECT, IsCardPinCorrectPacket.C2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_CARD_OWNER, GetCardOwnerPacket.C2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_CARD_BALANCE, GetCardBalancePacket.C2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_PERSONAL_CARDS_TOTAL_BALANCE,
                GetPersonalCardsTotalBalancePacket.C2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(WITHDRAW_CASH, WithdrawCashC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(DEPOSIT_CASH, DepositCashC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_RECEIPT_INFO, GetReceiptInfoPacket.C2S::receive);
    }
}