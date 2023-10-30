package net.rupyberstudios.minebuck_currency.config;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {
    private String configContents = "";
    public final List<Pair<String, ?>> configList = new ArrayList<>();

    public List<Pair<String, ?>> getConfigList() {
        return configList;
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #" +
                comment + " | default: " + keyValuePair.getSecond() + "\n";
    }

    @Override
    public String get(String namespace) {
        return configContents;
    }
}