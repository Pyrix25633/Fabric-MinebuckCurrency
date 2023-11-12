package net.rupyberstudios.minebuck_currency.database;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseManager {
    private static final String BANK_UUID = "00000000-0000-0000-0000-000000000000";

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
                    emitterPlayerId CHAR(36),
                    sourcePlayerId CHAR(36),
                    destinationPlayerId CHAR(36),
                    sourceCardId LONG NULL DEFAULT NULL,
                    destinationCardId LONG NULL DEFAULT NULL,
                    amount INT NOT NULL,
                    item VARCHAR(64) NULL DEFAULT NULL,
                    service VARCHAR(32) NULL DEFAULT NULL,
                    description VARCHAR(128) NULL DEFAULT NULL,
                    FOREIGN KEY (emitterPlayerId) REFERENCES players(id),
                    FOREIGN KEY (sourcePlayerId) REFERENCES players(id),
                    FOREIGN KEY (destinationPlayerId) REFERENCES players(id),
                    FOREIGN KEY (sourceCardId) REFERENCES cards(id),
                    FOREIGN KEY (destinationCardId) REFERENCES cards(id),
                    PRIMARY KEY (id)
                );""");
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                REPLACE INTO players (id, username) VALUES (?, ?)""");
        preparedStatement.setString(1, BANK_UUID);
        preparedStatement.setString(2, "Ⓜinebuck Currency Ⓑank");
        preparedStatement.execute();
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

    public static boolean isPinCorrect(@NotNull ID cardId, String pinHash) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT pinHash FROM cards WHERE id=?;""");
        preparedStatement.setLong(1, cardId.toLong());
        ResultSet results = preparedStatement.executeQuery();
        if(!results.next()) return false;
        System.out.println(results.getString("pinHash"));
        return results.getString("pinHash").equals(pinHash);
    }

    public static @NotNull ID depositCash(@NotNull ID cardId, int amount, @NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                UPDATE cards SET balance=balance+? WHERE id=?;""");
        preparedStatement.setInt(1, amount);
        preparedStatement.setLong(2, cardId.toLong());
        preparedStatement.execute();
        Statement statement = MinebuckCurrency.connection.createStatement();
        ID receiptId;
        boolean alreadyExists;
        do {
            receiptId = ID.random();
            ResultSet results = statement.executeQuery("SELECT id FROM receipts WHERE id=" + cardId.toLong() + ";");
            alreadyExists = results.next();
        } while(alreadyExists);
        preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                INSERT INTO receipts
                (id, emitterPlayerId, sourcePlayerId, destinationPlayerId, destinationCardId, amount, service)
                VALUES (?, ?, ?, ?, ?, ?, ?);""");
        preparedStatement.setLong(1, receiptId.toLong());
        preparedStatement.setString(2, BANK_UUID);
        preparedStatement.setString(3, player.toString());
        preparedStatement.setString(4, player.toString());
        preparedStatement.setLong(5, cardId.toLong());
        preparedStatement.setInt(6, amount);
        preparedStatement.setString(7, "Cash Deposit");
        preparedStatement.execute();
        return receiptId;
    }

    @Contract("_ -> new")
    public static @NotNull ReceiptInfo getReceiptInfo(@NotNull ID receiptId) throws SQLException {
        Statement statement = MinebuckCurrency.connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM receipts WHERE id=" + receiptId.toLong() + ";");
        if(!results.next()) return new ReceiptInfo();
        String emitterPlayer, sourcePlayer, destinationPlayer;
        PreparedStatement preparedStatement = MinebuckCurrency.connection.prepareStatement("""
                SELECT username FROM players WHERE id=?;""");
        preparedStatement.setString(1, results.getString("emitterPlayerId"));
        ResultSet playerResults = preparedStatement.executeQuery();
        if(!playerResults.next()) emitterPlayer = "";
        else emitterPlayer = playerResults.getString("username");
        preparedStatement.setString(1, results.getString("sourcePlayerId"));
        playerResults = preparedStatement.executeQuery();
        if(!playerResults.next()) sourcePlayer = "";
        else sourcePlayer = playerResults.getString("username");
        preparedStatement.setString(1, results.getString("destinationPlayerId"));
        playerResults = preparedStatement.executeQuery();
        if(!playerResults.next()) destinationPlayer = "";
        else destinationPlayer = playerResults.getString("username");
        return new ReceiptInfo(emitterPlayer, sourcePlayer, destinationPlayer,
                new ID(results.getLong("sourceCardId")), new ID(results.getLong("destinationCardId")),
                results.getInt("amount"), results.getString("item"),
                results.getString("service"), results.getString("description"));
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