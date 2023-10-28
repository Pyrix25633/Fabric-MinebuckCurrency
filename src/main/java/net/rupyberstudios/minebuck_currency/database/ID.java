package net.rupyberstudios.minebuck_currency.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ID {
    private static final Random RANDOM = new Random();

    @Contract(" -> new")
    public static @NotNull ID random() {
        return new ID(RANDOM.nextLong());
    }

    private final long id;

    public ID(long id) {
        this.id = id;
    }

    public boolean equals(@Nullable ID other) {
        if(other == null) return false;
        return this.id == other.id;
    }

    public long toLong() {
        return id;
    }

    @Override
    public String toString() {
        String hexString = String.format("%016x", id);
        StringBuilder formatted = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            formatted.append(hexString, i * 4, i * 4 + 4);
            if(i < 3) formatted.append("-");
        }
        return formatted.toString();
    }
}