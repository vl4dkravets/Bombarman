package org.bombermen.gameMechanics;

import org.jetbrains.annotations.NotNull;

public class Pawn extends GameElement{
    private static final String type = "Pawn";
    private final String playerName;
    private Bomb bomb;

    public Pawn(int id, String playerName) {
        super(id, type, new Position(0,0));
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
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
}
