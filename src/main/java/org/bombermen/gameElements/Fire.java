package org.bombermen.gameElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "startTime", "tileSize" })
public class Fire extends GameElement {
    private final String type = "Fire";
    // Unique index used by JS to store element in it array
    private static final int startIndex = 2000;

    public Fire(int id, Position position) {
        super(startIndex+id, new Position(position.getX(), position.getY()));
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}