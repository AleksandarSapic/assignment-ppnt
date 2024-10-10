package com.ppnt.client;

import java.io.Serializable;

public class Packet implements Serializable {
    private long id;
    private int delay;
    private long timestamp;

    public Packet(long id, int delay, long timestamp) {
        this.id = id;
        this.delay = delay;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
