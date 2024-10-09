package com.ppnt.client;

import java.io.Serializable;

public class Packet implements Serializable {
    private int id;
    private int delay;
    private long timestamp;

    public Packet(int id, int delay, long timestamp) {
        this.id = id;
        this.delay = delay;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
