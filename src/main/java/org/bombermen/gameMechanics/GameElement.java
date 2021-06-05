package org.bombermen.gameMechanics;

public abstract class GameElement {
    private int id;
    private Position position;
    private final double tileSize = 28;

    public GameElement(Position position) {
        this.position = position;
    }

    public GameElement(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        position.setX(x);
        position.setY(y);
    }
    public void setPosition(Position pos) {
        position = pos;
    }

    public Position getTopLeftPoint() {
        return new Position(getPosition().getX(), getPosition().getY()+tileSize);
    }

    public Position getBottomRightPoint() {
        return new Position(getPosition().getX()+tileSize, getPosition().getY());
    }

    public double getTileSize() {
        return tileSize;
    }
}
