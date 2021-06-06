package org.bombermen.game;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private final String name;
    private AtomicBoolean isConnected;

    public Player(String name) {
        this.name = name;
        isConnected = new AtomicBoolean(true);
    }

    public String getName() {
        return name;
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    public void setIsConnected(boolean value) {
       isConnected.set(value);
    }
}
