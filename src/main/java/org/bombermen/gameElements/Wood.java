package org.bombermen.gameElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "tileSize" })
public class Wood extends GameElement{
    private static final int startIndex = 4000;
    private final String type =  "Wood";
    private final static int TILE_SIZE_DIFFERENCE = 2;

    public Wood(int id, Position position) {
        super(startIndex+id, position);
    }

    @Override
    public Position getTopLeftPoint() {
        return new Position(getPosition().getX() - TILE_SIZE_DIFFERENCE, getPosition().getY() + TILE_SIZE_DIFFERENCE + getTileSize());
    }

    @Override
    public Position getBottomRightPoint() {
        return new Position(getPosition().getX() - TILE_SIZE_DIFFERENCE + getTileSize(), getPosition().getY() + TILE_SIZE_DIFFERENCE);
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}
