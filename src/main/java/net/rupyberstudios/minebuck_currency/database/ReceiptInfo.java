package net.rupyberstudios.minebuck_currency.database;

import org.jetbrains.annotations.NotNull;

public class ReceiptInfo {
    private final String emitterPlayer, sourcePlayer, destinationPlayer;
    private final ID sourceCardId, destinationCardId;
    private final int amount;
    private final String item, service, description;

    public ReceiptInfo(@NotNull String emitterPlayer, @NotNull String sourcePlayer, @NotNull String destinationPlayer,
                       ID sourceCardId, ID destinationCardId, int amount, String item, String service, String description) {
        this.emitterPlayer = emitterPlayer;
        this.sourcePlayer = sourcePlayer;
        this.destinationPlayer = destinationPlayer;
        this.sourceCardId = sourceCardId;
        this.destinationCardId = destinationCardId;
        this.amount = amount;
        this.item = item;
        this.service = service;
        this.description = description;
    }

    public ReceiptInfo() {
        this("", "", "", null, null, -1,
                null, null, null);
    }

    public ReceiptInfo get() {
        if(emitterPlayer.isEmpty() && sourcePlayer.isEmpty() && destinationPlayer.isEmpty()) return null;
        return this;
    }

    public String getEmitterPlayer() {
        return emitterPlayer;
    }

    public String getSourcePlayer() {
        return sourcePlayer;
    }

    public String getDestinationPlayer() {
        return destinationPlayer;
    }

    public ID getSourceCardId() {
        return sourceCardId;
    }

    public ID getDestinationCardId() {
        return destinationCardId;
    }

    public int getAmount() {
        return amount;
    }

    public String getItem() {
        return item;
    }

    public String getService() {
        return service;
    }

    public String getDescription() {
        return description;
    }
}