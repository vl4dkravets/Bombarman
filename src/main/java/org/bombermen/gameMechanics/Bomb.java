package org.bombermen.gameMechanics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "startTime", "tileSize" })
public class Bomb extends GameElement{
    private final String type =  "Bomb";
    private long startTime;
    private static final int startIndex = 1000;

    public Bomb(Position position) {
        super(position);
    }

    public boolean updateBombTimerAndCheck(long elapsed) {
        startTime+=elapsed;
        return startTime >= 10_000;
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}
