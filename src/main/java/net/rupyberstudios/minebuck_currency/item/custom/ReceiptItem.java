package net.rupyberstudios.minebuck_currency.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.database.ID;
import net.rupyberstudios.minebuck_currency.database.ReceiptInfo;
import net.rupyberstudios.minebuck_currency.networking.packet.GetReceiptInfoPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReceiptItem extends Item implements DyeableItem {
    private static final MutableText ID_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.id");
    private static final MutableText EMITTER_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.emitter_player");
    private static final Text UNKNOWN_EMITTER_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.emitter_player.unknown");
    private static final MutableText SOURCE_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.source_player");
    private static final Text UNKNOWN_SOURCE_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.source_player.unknown");
    private static final MutableText DESTINATION_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.destination_player");
    private static final Text UNKNOWN_DESTINATION_PLAYER_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.destination_player.unknown");
    private static final Text SOURCE_CARD_ID_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.source_card_id");
    private static final Text DESTINATION_CARD_ID_TEXT =
            Text.translatable("item.minebuck_currency.receipt.tooltip.destination_card_id");
    private static final Text AMOUNT_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.amount");
    private static final Text SYMBOL_TEXT = Text.translatable("symbol.minebuck_currency.minebuck");
    private static final Text ITEM_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.item");
    private static final Text QUANTITY_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.quantity");
    private static final Text SERVICE_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.service");
    private static final Text DESCRIPTION_TEXT = Text.translatable("item.minebuck_currency.receipt.tooltip.description");

    public ReceiptItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getColor(@NotNull ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : 0x66D088;
    }

    public @Nullable ID getId(@NotNull ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        boolean containsIdNbt = nbt != null && nbt.contains("id");
        return containsIdNbt ? new ID(nbt.getLong("id")) : null;
    }

    public String getIdString(@Nullable ID id) {
        return id != null ? id.toString() : "????-????-????-????";
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ID id = getId(stack);
        tooltip.add(ID_TEXT.copy().append(getIdString(id)));
        if(id != null) {
            ReceiptInfo info = GetReceiptInfoPacket.C2S.send(id).read();
            if(info == null) return;
            if(info.getEmitterPlayer().isEmpty())
                tooltip.add(UNKNOWN_EMITTER_PLAYER_TEXT);
            else
                tooltip.add(EMITTER_PLAYER_TEXT.copy().append(info.getEmitterPlayer()));
            if(info.getSourcePlayer().isEmpty())
                tooltip.add(UNKNOWN_SOURCE_PLAYER_TEXT);
            else
                tooltip.add(SOURCE_PLAYER_TEXT.copy().append(info.getSourcePlayer()));
            if(info.getDestinationPlayer().isEmpty())
                tooltip.add(UNKNOWN_DESTINATION_PLAYER_TEXT);
            else
                tooltip.add(DESTINATION_PLAYER_TEXT.copy().append(info.getDestinationPlayer()));
            if(info.getSourceCardId() != null)
                tooltip.add(SOURCE_CARD_ID_TEXT.copy().append(info.getSourceCardId().toString()));
            if(info.getDestinationCardId() != null)
                tooltip.add(DESTINATION_CARD_ID_TEXT.copy().append(info.getDestinationCardId().toString()));
            tooltip.add(AMOUNT_TEXT.copy().append(String.valueOf(info.getAmount())).append(SYMBOL_TEXT));
            if(info.getItem() != null)
                tooltip.add(ITEM_TEXT.copy().append(info.getItem()));
            if(info.getQuantity() != null)
                tooltip.add(QUANTITY_TEXT.copy().append(String.valueOf(info.getQuantity())));
            if(info.getService() != null)
                tooltip.add(SERVICE_TEXT.copy().append(info.getService()));
            if(info.getDescription() != null)
                tooltip.add(DESCRIPTION_TEXT.copy().append(info.getDescription()));
        }
    }
}