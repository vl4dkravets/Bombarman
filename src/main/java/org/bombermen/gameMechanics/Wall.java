package org.bombermen.gameMechanics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "tileSize" })
public class Wall extends GameElement{
    private static final int startIndex = 700;
    private static final String type =  "Wall";

    public Wall(int id, Position position) {
        super(startIndex+id,type, position);
    }

    @Override
    public void setId(int id) {
        super.setId(startIndex+id);
    }
}
