package net.rupyberstudios.minebuck_currency.config;

import net.fabricmc.loader.api.FabricLoader;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class JsonConfig {
    private final Gson gson;
    private final Path path;
    private HashMap<String, Object> config;
    private final HashMap<String, Object> defaults;
    private boolean modified;

    @SuppressWarnings("unchecked")
    public JsonConfig(String filename) {
        this.path = Path.of(FabricLoader.getInstance().getConfigDir().toString(), filename);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.modified = false;
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            config = gson.fromJson(reader, HashMap.class);
            reader.close();
            MinebuckCurrency.LOGGER.warn(config.toString());
        } catch (Exception e) {
            config = new HashMap<>();
        }
        this.defaults = new HashMap<>();
    }

    public void setDefault(String key, Object def) {
        defaults.put(key, def);
    }

    public boolean getBoolean(String key) {
        Object value = config.get(key);
        if(value instanceof Boolean b)
            return b;
        else {
            Object def = defaults.get(key);
            if(def instanceof Boolean bDef) {
                config.put(key, bDef);
                modified = true;
                return bDef;
            }
        }
        return false;
    }

    public int getInt(String key) {
        Object value = config.get(key);
        if(value instanceof Integer i)
            return i;
        else {
            Object def = defaults.get(key);
            if(def instanceof Integer iDef) {
                config.put(key, iDef);
                modified = true;
                return iDef;
            }
        }
        return 0;
    }

    public long getLong(String key) {
        Object value = config.get(key);
        if(value instanceof Long l)
            return l;
        else {
            Object def = defaults.get(key);
            if(def instanceof Long lDef) {
                config.put(key, lDef);
                modified = true;
                return lDef;
            }
        }
        return 0L;
    }

    public float getFloat(String key) {
        Object value = config.get(key);
        if(value instanceof Float f)
            return f;
        else {
            Object def = defaults.get(key);
            if(def instanceof Integer fDef) {
                config.put(key, fDef);
                modified = true;
                return fDef;
            }
        }
        return 0F;
    }

    public double getDouble(String key) {
        Object value = config.get(key);
        if(value instanceof Double d)
            return d;
        else {
            Object def = defaults.get(key);
            if(def instanceof Double dDef) {
                config.put(key, dDef);
                modified = true;
                return dDef;
            }
        }
        return 0D;
    }

    public String getString(String key) {
        Object value = config.get(key);
        if(value instanceof String s)
            return s;
        else {
            Object def = defaults.get(key);
            if(def instanceof String sDef) {
                config.put(key, sDef);
                modified = true;
                return sDef;
            }
        }
        return "";
    }

    public void set(String key, Object value) {
        if(value instanceof Boolean || value instanceof Integer || value instanceof Long
                || value instanceof Float || value instanceof Double || value instanceof String) {
            config.put(key, value);
            modified = true;
        }
    }

    public void saveIfModified() {
        if(!modified) return;
        save();
    }

    public void save() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(path);
            gson.toJson(config, writer);
            writer.close();
            modified = false;
        } catch(Exception e) {
            System.err.println("Exception saving config file: " + e.getMessage());
        }
    }
}