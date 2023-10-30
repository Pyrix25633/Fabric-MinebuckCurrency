package net.rupyberstudios.minebuck_currency.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

public class SimpleConfig {
    private static final Logger LOGGER = LogManager.getLogger("SimpleConfig");
    private final HashMap<String, String> config = new HashMap<>();
    private final ConfigRequest request;
    private boolean broken = false;

    public interface DefaultConfig {
        String get( String namespace );

        @Contract(pure = true)
        static @NotNull String empty(String namespace) {
            return "";
        }
    }

    public static class ConfigRequest {
        private final File file;
        private final String filename;
        private DefaultConfig provider;

        private ConfigRequest(File file, String filename) {
            this.file = file;
            this.filename = filename;
            this.provider = DefaultConfig::empty;
        }

        public ConfigRequest provider(DefaultConfig provider) {
            this.provider = provider;
            return this;
        }

        public SimpleConfig request() {
            return new SimpleConfig(this);
        }

        private @NotNull String getConfig() {
            return provider.get(filename) + "\n";
        }

    }

    public static @NotNull ConfigRequest of(String filename) {
        Path path = FabricLoader.getInstance().getConfigDir();
        return new ConfigRequest(path.resolve(filename + ".properties").toFile(), filename );
    }

    private void createConfig() throws IOException {
        request.file.getParentFile().mkdirs();
        Files.createFile( request.file.toPath() );

        PrintWriter writer = new PrintWriter(request.file, StandardCharsets.UTF_8);
        writer.write( request.getConfig() );
        writer.close();
    }

    private void loadConfig() throws IOException {
        Scanner reader = new Scanner(request.file);
        for(int line = 1; reader.hasNextLine(); line ++) {
            parseConfigEntry( reader.nextLine(), line );
        }
    }

    private void parseConfigEntry(@NotNull String entry, int line ) {
        if(!entry.isEmpty() && !entry.startsWith( "#" )) {
            String[] parts = entry.split("=", 2);
            if( parts.length == 2 ) {
                // Recognizes comments after a value
                String temp = parts[1].split(" #")[0];
                config.put( parts[0], temp );
            }
            else
                throw new RuntimeException("Syntax error in config file on line " + line + "!");
        }
    }

    private SimpleConfig(@NotNull ConfigRequest request) {
        this.request = request;
        String identifier = "Config '" + request.filename + "'";

        if(!request.file.exists()) {
            LOGGER.info(identifier + " is missing, generating default one...");

            try {
                createConfig();
            } catch(IOException e) {
                LOGGER.error(identifier + " failed to generate!");
                LOGGER.trace(e);
                broken = true;
            }
        }

        if(!broken) {
            try {
                loadConfig();
            } catch(Exception e) {
                LOGGER.error(identifier + " failed to load!" );
                LOGGER.trace(e);
                broken = true;
            }
        }
    }

    @Deprecated
    public String get(String key) {
        return config.get(key);
    }

    public String getOrDefault(String key, String def) {
        String val = get(key);
        return val == null ? def : val;
    }

    public int getOrDefault(String key, int def) {
        try {
            return Integer.parseInt(get(key));
        } catch(Exception e) {
            return def;
        }
    }

    public boolean getOrDefault(String key, boolean def) {
        String val = get(key);
        if(val != null)
            return val.equalsIgnoreCase("true");
        return def;
    }

    public double getOrDefault(String key, double def) {
        try {
            return Double.parseDouble(get(key));
        } catch(Exception e) {
            return def;
        }
    }

    public boolean isBroken() {
        return broken;
    }

    public boolean delete() {
        LOGGER.warn("Config '" + request.filename + "' was deleted! Restart the game to regenerate it.");
        return request.file.delete();
    }
}