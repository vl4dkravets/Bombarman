package org.bombermen.gameElements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    private Position position1;
    private Position position2;
    private Wall wall1;
    private Wall wall2;


    @BeforeEach
    void setUp() {
        position1 = new Position(50,50);
        position2 = new Position(550,550);
        wall1 = new Wall(1, position1);
        wall2 = new Wall(2, position2);
    }

    @Test
    void checkState() {
        assertAll(
                ()->assertNotNull(wall1),
                ()->assertNotNull(wall2),
                ()->assertNotNull(position1),
                ()->assertNotNull(position2),
                ()->assertNotNull(wall1.getPosition()),
                ()->assertNotNull(wall2.getPosition()),
                ()->assertEquals(3001, wall1.getId()),
                ()->assertEquals(3002, wall2.getId()),
                ()->assertEquals(wall1.getTopLeftPoint(), new Position(wall1.getPosition().getX(), wall1.getPosition().getY()+wall1.getTileSize())),
                ()->assertEquals(wall1.getBottomRightPoint(), new Position(wall1.getPosition().getX()+wall1.getTileSize(), wall1.getPosition().getY())),
                ()->assertEquals(wall2.getTopLeftPoint(), new Position(wall2.getPosition().getX(), wall2.getPosition().getY()+wall2.getTileSize())),
                ()->assertEquals(wall2.getBottomRightPoint(), new Position(wall2.getPosition().getX()+wall2.getTileSize(), wall2.getPosition().getY())),
                ()->assertNotEquals(wall1.getPosition(), wall2.getPosition())
        );
    }
}