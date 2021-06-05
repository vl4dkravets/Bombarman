package org.bombermen.gameMechanics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "tileSize" })
public class Wall extends GameElement{
    private static final int startIndex = 3000;
    private final String type =  "Wall";

    public Wall(int id, Position position) {
        super(startIndex+id, position);
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}
