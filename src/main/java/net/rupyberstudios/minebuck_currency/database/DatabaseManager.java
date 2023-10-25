package net.rupyberstudios.minebuck_currency.database;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                id INTEGER,
                pinHash CHAR(64) NOT NULL,
                balance INTEGER NOT NULL DEFAULT 0,
                ownerId CHAR(36) NULL,
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
        String owner = results.getString(1);
        return owner == null ? null : UUID.fromString(owner);
    }
}