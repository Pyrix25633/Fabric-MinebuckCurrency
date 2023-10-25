package net.rupyberstudios.minebuck_currency.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull String digest(@NotNull String in) {
        return String.format("%064x", new BigInteger(digest.digest(in.getBytes())));
    }
}