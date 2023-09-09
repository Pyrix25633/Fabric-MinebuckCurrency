package net.rupyberstudios.minebuck_currency.database;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagement {
    public static void createIDsTable() throws SQLException {
        Statement statement = MinebuckCurrency.connection.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS ids (
                table_name VARCHAR(16) NOT NULL,
                next_id INTEGER NOT NULL,
                PRIMARY KEY (table_name)
                );""");
    }
}