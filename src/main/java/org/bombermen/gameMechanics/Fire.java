package org.bombermen.gameMechanics;

public class Fire extends GameElement {
    private static final String type = "Fire";
    // Unique index used by JS to store element in it array
    private static final int startIndex = 400;

    public Fire(int id, Position position) {
        super(startIndex+id, type, new Position(position.getX(), position.getY()));
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}