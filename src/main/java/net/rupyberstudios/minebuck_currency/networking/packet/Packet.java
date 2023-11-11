package net.rupyberstudios.minebuck_currency.networking.packet;

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
        } catch(Exception e) {throw new RuntimeException(e);}
    }

    public void write(T data) {
        this.data = data;
        read.release();
    }

    public T read() {
        T temp;
        try {
            read.acquire();
            temp = data;
            write.release();
        } catch(Exception e) {throw new RuntimeException(e);}
        return temp;
    }
}