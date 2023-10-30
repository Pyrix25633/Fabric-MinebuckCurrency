package net.rupyberstudios.minebuck_currency;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.WorldSavePath;
import net.rupyberstudios.minebuck_currency.block.ModBlocks;
import net.rupyberstudios.minebuck_currency.block.entity.ModBlockEntities;
import net.rupyberstudios.minebuck_currency.config.ModConfigs;
import net.rupyberstudios.minebuck_currency.database.DatabaseManager;
import net.rupyberstudios.minebuck_currency.item.ModItemGroups;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import net.rupyberstudios.minebuck_currency.networking.ModMessages;
import net.rupyberstudios.minebuck_currency.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MinebuckCurrency implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "minebuck_currency";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Connection connection;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModConfigs.registerConfigs();

		ModItemGroups.buildItemGroups();

		ModBlocks.registerModBlocks();

		ModItems.registerModItems();

		ModBlockEntities.registerBlockEntities();

		ModScreenHandlers.registerScreenHandlers();

		ModMessages.registerC2SPackets();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			Path worldPath = server.getSavePath(WorldSavePath.ROOT);
			String url = "jdbc:sqlite:" + worldPath + "minebuck.db";

			try {
				MinebuckCurrency.connection = DriverManager.getConnection(url);
				if(connection != null) {
					LOGGER.info("Connected to the database");
					DatabaseManager.createTables();
				}
				else throw new IllegalStateException("Not connected to the minebuck database!");
			} catch(Exception e) {
				LOGGER.error("Error: " + e.getMessage());
				throw new IllegalStateException("Error operating the minebuck database!");
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			try {
				LOGGER.info("Closing minebuck database connection");
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			try {
				DatabaseManager.insertOrUpdatePlayer(handler.player.getUuid(), handler.player.getGameProfile().getName());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		LOGGER.info("Initializing main");
	}
}