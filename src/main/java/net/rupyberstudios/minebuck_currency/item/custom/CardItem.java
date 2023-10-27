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
import net.rupyberstudios.minebuck_currency.networking.packet.GetCardOwnerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CardItem extends Item implements DyeableItem {
    private static final MutableText ID = Text.translatable("item.minebuck_currency.card.tooltip.id");
    private static final MutableText OWNER_ONLINE = Text.translatable("item.minebuck_currency.card.tooltip.owner.online");
    private static final Text OWNER_OFFLINE = Text.translatable("item.minebuck_currency.card.tooltip.owner.offline");

    public CardItem(Settings settings) {
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
        tooltip.add(ID.copy().append(getIdString(id)));
        if(id != null) {
            String owner = GetCardOwnerPacket.C2S.send(id).read();
            if(owner == null) return;
            if(owner.isEmpty())
                tooltip.add(OWNER_OFFLINE);
            else
                tooltip.add(OWNER_ONLINE.copy().append(owner));
        }
    }
}