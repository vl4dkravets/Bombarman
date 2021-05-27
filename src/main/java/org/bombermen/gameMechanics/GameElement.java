package org.bombermen.gameMechanics;

public abstract class GameElement {
    private int id;
    private String type;
    private Position position;
    private boolean alive;
    private String direction;
    private final double tileSize = 28;

    public GameElement(String type, Position position) {
        this.type = type;
        this.alive = true;
        this.position = position;
    }

    public GameElement(int id, String type, Position position) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.alive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
