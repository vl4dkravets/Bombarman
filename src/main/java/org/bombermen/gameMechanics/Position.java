package org.bombermen.gameMechanics;

public class Position {
    private double x;
    private double y;
    public int posChanged;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
        posChanged++;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
        posChanged++;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Position position = (Position) object;
        return ((Double.compare(getX(), position.getX())==0) && (Double.compare(getY(), position.getY())==0));
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
