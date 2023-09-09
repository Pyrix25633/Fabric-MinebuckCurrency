package net.rupyberstudios.minebuck_currency.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

public class ModItemGroups {
    private static final Identifier MINEBUCK_CURRENCY_ID = new Identifier(MinebuckCurrency.MOD_ID, "minebuck_currency");

    public static final RegistryKey<ItemGroup> MINEBUCK_CURRENCY = RegistryKey.of(RegistryKeys.ITEM_GROUP, MINEBUCK_CURRENCY_ID);

    public static void buildItemGroups() {
        Registry.register(Registries.ITEM_GROUP, MINEBUCK_CURRENCY_ID,
                FabricItemGroup.builder().displayName(Text.translatable("item_group.minebuck_currency.minebuck_currency"))
                        .icon(() -> new ItemStack(ModItems.COIN_1)).build());
    }
}