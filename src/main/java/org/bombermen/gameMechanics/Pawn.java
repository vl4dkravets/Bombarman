package org.bombermen.gameMechanics;

public class Pawn extends GameElement{
    private static final String type = "Pawn";
    private final String playerName;
    private final String pawnName;
    private Bomb bomb;
    public boolean movedPerTickX;
    public boolean movedPerTickY;
    public int counter;

    public Pawn(int id, String playerName, String pawnName) {
        super(id, type, new Position(0,0));
        this.playerName = playerName;
        this.pawnName = pawnName;
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
}
