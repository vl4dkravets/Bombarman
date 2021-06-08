package org.bombermen.gameElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "startTime", "tileSize" })
public class Bomb extends GameElement{
    private final String type =  "Bomb";
    private long startTime;

    public Bomb(Position position) {
        super(position);
    }

    public boolean updateBombTimerAndCheck(long elapsed) {
        startTime+=elapsed;
        return startTime >= 10_000;
    }
}
