package net.rupyberstudios.minebuck_currency.block.property;

import net.minecraft.state.property.EnumProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ComputerOpenScreenProperty extends EnumProperty<ComputerOpenScreen> {
    protected ComputerOpenScreenProperty(String name, Collection<ComputerOpenScreen> values) {
        super(name, ComputerOpenScreen.class, values);
    }

    @Contract("_ -> new")
    public static @NotNull ComputerOpenScreenProperty of(String name) {
        return ComputerOpenScreenProperty.of(name, (ComputerOpenScreen computerOpenScreen) -> true);
    }

    @Contract("_, _ -> new")
    public static @NotNull ComputerOpenScreenProperty of(String name, Predicate<ComputerOpenScreen> filter) {
        return ComputerOpenScreenProperty.of(name, Arrays.stream(ComputerOpenScreen.values()).filter(filter).collect(Collectors.toList()));
    }

    @Contract("_, _ -> new")
    public static @NotNull ComputerOpenScreenProperty of(String name, Collection<ComputerOpenScreen> values) {
        return new ComputerOpenScreenProperty(name, values);
    }
}