package net.rupyberstudios.minebuck_currency.networking.packet;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

import java.util.HashMap;

public class PacketSequence<T> {
    private final HashMap<Integer, Packet<T>> packets;
    private int index;

    public PacketSequence() {
        this.packets = new HashMap<>();
        this.index = 0;
    }

    public Packet<T> get(int index) {
        return packets.get(index);
    }

    public int prepare() {
        try {
            Packet<T> packet = new Packet<>();
            packet.prepare();
            packets.put(index, packet);
            int temp = index;
            index++;
            return temp;
        } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        return -1;
    }

    public void write(int index, T data) {
        packets.get(index).write(data);
    }

    public T read(int index) {
        T temp = packets.get(index).read();
        packets.remove(index);
        return temp;
    }
}