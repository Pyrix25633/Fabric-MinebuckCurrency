package net.rupyberstudios.minebuck_currency;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.WorldSavePath;
import net.rupyberstudios.minebuck_currency.item.ModItemGroups;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class MinebuckCurrency implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "minebuck_currency";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItemGroups.buildItemGroups();

		ModItems.registerModItems();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			Path worldPath = Path.of(System.getProperty("user.dir")).relativize(server.getSavePath(WorldSavePath.ROOT));
			System.out.println("World path: " + worldPath);
		});

		LOGGER.info("Initializing main");
	}
}