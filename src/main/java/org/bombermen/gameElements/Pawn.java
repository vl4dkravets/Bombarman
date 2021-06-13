package org.bombermen.gameElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "bomb", "movedPerTickX", "movedPerTickY", "pawnName", "playerName", "tileSize" })
public class Pawn extends GameElement{
    private final String type = "Pawn";
    private final String playerName;
    private final String pawnName;
    private Bomb bomb;
    private boolean movedPerTickX;
    private boolean movedPerTickY;
    private boolean alive;
    private String direction;

    public Pawn(int id, String playerName, String pawnName) {
        super(id, new Position(0,0));
        this.playerName = playerName;
        this.pawnName = pawnName;
        alive = true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Pawn pawn1 = (Pawn) object;
        return getPosition().equals(pawn1.getPosition());
    }

    public Bomb getBomb() {
        return bomb;
    }
    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String toString() {
        return pawnName;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isMovedPerTickX() {
        return movedPerTickX;
    }

    public void setMovedPerTickX(boolean movedPerTickX) {
        this.movedPerTickX = movedPerTickX;
    }

    public boolean isMovedPerTickY() {
        return movedPerTickY;
    }

    public void setMovedPerTickY(boolean movedPerTickY) {
        this.movedPerTickY = movedPerTickY;
    }
}
