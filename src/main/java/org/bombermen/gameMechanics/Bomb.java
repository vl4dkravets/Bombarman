package org.bombermen.gameMechanics;

public class Bomb extends GameElement{
    private static final String type =  "Bomb";
    private long startTime;
    private static final int startIndex = 100;
    private boolean possessed;

    public Bomb(Position position) {
        super(type, position);
    }

    public boolean updateBombTimerAndCheck(long elapsed) {
        startTime+=elapsed;
        //System.out.println(startTime);
        if(startTime>= 5_000){
            return true;
        }
        return false;
    }

}
