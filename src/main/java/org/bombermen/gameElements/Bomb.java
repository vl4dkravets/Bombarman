package org.bombermen.gameElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "bombTimer", "tileSize" })
public class Bomb extends GameElement{
    private final String type =  "Bomb";
    private int bombTimer = 10_000;

    public Bomb(Position position) {
        super(position);
    }

    public boolean updateBombTimerAndCheck(long elapsed) {
        bombTimer-=elapsed;
        return bombTimer <= 0;
    }

    public int getBombTimer() {
        return bombTimer;
    }
}
