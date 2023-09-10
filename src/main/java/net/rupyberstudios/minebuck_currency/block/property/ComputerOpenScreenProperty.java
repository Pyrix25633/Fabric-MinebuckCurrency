package net.rupyberstudios.minebuck_currency.block.property;

import com.google.common.collect.Lists;
import net.minecraft.state.property.EnumProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ComputerOpenScreenProperty extends EnumProperty<ComputerOpenScreen> {
    protected ComputerOpenScreenProperty(String name, Collection<ComputerOpenScreen> values) {
        super(name, ComputerOpenScreen.class, values);
    }

    public static ComputerOpenScreenProperty of(String name) {
        return ComputerOpenScreenProperty.of(name, (ComputerOpenScreen computerOpenScreen) -> true);
    }

    public static ComputerOpenScreenProperty of(String name, Predicate<ComputerOpenScreen> filter) {
        return ComputerOpenScreenProperty.of(name, Arrays.stream(ComputerOpenScreen.values()).filter(filter).collect(Collectors.toList()));
    }

    public static ComputerOpenScreenProperty of(String name, ComputerOpenScreen ... values) {
        return ComputerOpenScreenProperty.of(name, Lists.newArrayList(values));
    }

    public static ComputerOpenScreenProperty of(String name, Collection<ComputerOpenScreen> values) {
        return new ComputerOpenScreenProperty(name, values);
    }
}