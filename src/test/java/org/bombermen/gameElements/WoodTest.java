package org.bombermen.gameElements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WoodTest {

    private Position position1;
    private Position position2;
    private Wood wood1;
    private Wood wood2;
    private final static int TILE_SIZE_DIFFERENCE = 2;


    @BeforeEach
    void setUp() {
        position1 = new Position(150+TILE_SIZE_DIFFERENCE,150-TILE_SIZE_DIFFERENCE);
        position2 = new Position(250+TILE_SIZE_DIFFERENCE,250-TILE_SIZE_DIFFERENCE);
        wood1 = new Wood(1, position1);
        wood2 = new Wood(2, position2);
    }

    @Test
    void checkState() {
        assertAll(
                ()->assertNotNull(wood1),
                ()->assertNotNull(wood2),
                ()->assertNotNull(position1),
                ()->assertNotNull(position2),
                ()->assertNotNull(wood1.getPosition()),
                ()->assertNotNull(wood2.getPosition()),
                ()->assertEquals(4001, wood1.getId()),
                ()->assertEquals(4002, wood2.getId()),
                ()->assertEquals(wood1.getTopLeftPoint(), new Position(wood1.getPosition().getX()-TILE_SIZE_DIFFERENCE, wood1.getPosition().getY()+wood1.getTileSize()+TILE_SIZE_DIFFERENCE)),
                ()->assertEquals(wood1.getBottomRightPoint(), new Position(wood1.getPosition().getX()+wood1.getTileSize()-TILE_SIZE_DIFFERENCE, wood1.getPosition().getY()+TILE_SIZE_DIFFERENCE)),
                ()->assertEquals(wood2.getTopLeftPoint(), new Position(wood2.getPosition().getX()-TILE_SIZE_DIFFERENCE, wood2.getPosition().getY()+wood2.getTileSize()+TILE_SIZE_DIFFERENCE)),
                ()->assertEquals(wood2.getBottomRightPoint(), new Position(wood2.getPosition().getX()+wood2.getTileSize()-TILE_SIZE_DIFFERENCE, wood2.getPosition().getY()+TILE_SIZE_DIFFERENCE)),
                ()->assertNotEquals(wood1.getPosition(), wood2.getPosition())
        );
    }
}