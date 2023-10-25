package net.rupyberstudios.minebuck_currency.networking.packet;

import net.rupyberstudios.minebuck_currency.MinebuckCurrency;

import java.util.concurrent.Semaphore;

public class Packet<T> {
    private final Semaphore write, read;
    private T data;

    public Packet() {
        this.write = new Semaphore(1);
        this.read = new Semaphore(0);
        this.data = null;
    }

    public void prepare() {
        try {
            write.acquire();
        } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
    }

    public void write(T data) {
        this.data = data;
        read.release();
    }

    public T read() {
        T temp = null;
        try {
            read.acquire();
            temp = data;
            write.release();
        } catch(Exception e) {MinebuckCurrency.LOGGER.error(e.toString());}
        return temp;
    }
}