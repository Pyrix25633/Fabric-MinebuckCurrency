package net.rupyberstudios.minebuck_currency.block.property;

import net.minecraft.state.property.EnumProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VendingMachineOpenScreenProperty extends EnumProperty<VendingMachineOpenScreen> {
    protected VendingMachineOpenScreenProperty(String name, Collection<VendingMachineOpenScreen> values) {
        super(name, VendingMachineOpenScreen.class, values);
    }

    @Contract("_ -> new")
    public static @NotNull VendingMachineOpenScreenProperty of(String name) {
        return VendingMachineOpenScreenProperty.of(name, (VendingMachineOpenScreen computerOpenScreen) -> true);
    }

    @Contract("_, _ -> new")
    public static @NotNull VendingMachineOpenScreenProperty of(String name, Predicate<VendingMachineOpenScreen> filter) {
        return VendingMachineOpenScreenProperty.of(name, Arrays.stream(VendingMachineOpenScreen.values()).filter(filter).collect(Collectors.toList()));
    }

    @Contract("_, _ -> new")
    public static @NotNull VendingMachineOpenScreenProperty of(String name, Collection<VendingMachineOpenScreen> values) {
        return new VendingMachineOpenScreenProperty(name, values);
    }
}