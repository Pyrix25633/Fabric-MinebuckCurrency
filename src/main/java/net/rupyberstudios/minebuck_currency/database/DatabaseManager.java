package net.rupyberstudios.minebuck_currency.database;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseManager {
    public static void createTables() throws SQLException {
        Statement statement = MinebuckCurrency.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS cards (
                    id LONG,
                    pinHash CHAR(64) NOT NULL,
                    balance LONG NOT NULL DEFAULT 0,
                    ownerId CHAR(36) NULL,
                    PRIMARY KEY (id),
                    FOREIGN KEY (ownerId) REFERENCES players(id)
                );""");
        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id CHAR(36),
                    username VARCHAR(16) NOT NULL,
                    PRIMARY KEY (id)
                );""");
        statement.execute("""
                CREATE TABLE IF NOT EXISTS receipts (
                    id LONG,
                    emitter CHAR(36),
                    recipientId CHAR(36),
                    FOREIGN KEY (emitterId) REFERENCES players(id),
                    FOREIGN KEY (recipientId) REFERENCES players(id),
                    PRIMARY KEY (id)
                );""");
    }

    public static @NotNull ID activateCard(@NotNull String pinHash, @Nullable UUID owner) throws SQLException {
        if(pinHash.length() != 64) return new ID(0);
        Statement statement = MinebuckCurrency.connection.createStatement();
        ID cardId;
        boolean alreadyExists;
        do {
            cardId = ID.random();
            ResultSet results = statement.executeQuery("SELECT id FROM cards WHERE id=" + cardId.toLong() + ";");
            alreadyExists = results.next();
        } while(alreadyExists);
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                INSERT INTO cards (id, pinHash, ownerId)
                VALUES (?, ?, ?);""");
        preparedStatement.setLong(1, cardId.toLong());
        preparedStatement.setString(2, pinHash);
        preparedStatement.setString(3, owner != null ? owner.toString() : null);
        preparedStatement.execute();
        return cardId;
    }

    public static @Nullable UUID getCardOwner(@NotNull ID cardId) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT ownerId FROM cards WHERE id=?;""");
        preparedStatement.setLong(1, cardId.toLong());
        ResultSet results = preparedStatement.executeQuery();
        if(!results.next()) return null;
        String owner = results.getString("ownerId");
        return owner == null ? null : UUID.fromString(owner);
    }

    public static long getCardBalance(@NotNull ID cardId, @NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT ownerId, balance FROM cards WHERE id=?;""");
        preparedStatement.setLong(1, cardId.toLong());
        ResultSet results = preparedStatement.executeQuery();
        if(!results.next()) return -1;
        long balance = results.getLong("balance");
        String ownerId = results.getString("ownerId");
        UUID owner = ownerId.isEmpty() ? null : UUID.fromString(ownerId);
        return owner == null | player.equals(owner) ? balance : -1;
    }

    public static @NotNull BigInteger getPersonalCardsTotalBalance(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT balance FROM cards WHERE ownerId=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet results = preparedStatement.executeQuery();
        BigInteger total = BigInteger.ZERO;
        while(results.next()) {
            long balance = results.getLong("balance");
            total = total.add(BigInteger.valueOf(balance));
        }
        return total;
    }

    public static void depositCash(@NotNull ID cardId, int amount) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                UPDATE cards SET balance=balance+? WHERE id=?;""");
        preparedStatement.setInt(1, amount);
        preparedStatement.setLong(2, cardId.toLong());
        preparedStatement.execute();

    }

    public static void insertOrUpdatePlayer(@NotNull UUID player, @NotNull String username) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT username FROM players WHERE id=?""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) {
            preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                INSERT INTO players (id, username)
                VALUES (?, ?);""");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        }
        else if(!result.getString("username").equals(username)) {
            preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                UPDATE players SET username=? WHERE id=?""");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, player.toString());
            preparedStatement.execute();
        }
    }

    public static @NotNull String getPlayerUsername(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT username FROM players WHERE id=?""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return "";
        return result.getString("username");
    }
}