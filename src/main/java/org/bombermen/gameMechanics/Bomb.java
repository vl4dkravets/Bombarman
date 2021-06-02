package org.bombermen.gameMechanics;

public class Bomb extends GameElement{
    private static final String type =  "Bomb";
    private long startTime;
    private static final int startIndex = 100;

    public Bomb(Position position) {
        super(type, position);
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
