package org.bombermen.gameElements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    private Position position1;
    private Position position2;
    private Position position3;

    @BeforeEach
    void setUp() {
        position1 = new Position(1,1);
        position2 = new Position(1,1);
        position3 = new Position(111,111);
    }

    @Test
    void checkState() {
        assertAll(() -> assertEquals(position1,position2),
                () -> assertNotEquals(position1,position3),
                () -> assertEquals(position2,position1),
                () -> assertNotEquals(position3,position1),
                () -> assertNotEquals(position3,position2),
                () -> assertNotEquals(position2,position3),
                () -> assertEquals(1, position1.getX()),
                () -> assertEquals(1, position1.getY()),
                () -> assertEquals(1, position2.getX()),
                () -> assertEquals(new Position(position1.getX(), position1.getY()+position1.getTileSize()), position1.getTopLeftPoint()),
                () -> assertEquals(new Position(position1.getX()+position1.getTileSize(), position1.getY()), position1.getBottomRightPoint()),
                () -> assertEquals(new Position(position2.getX(), position2.getY()+position2.getTileSize()), position2.getTopLeftPoint()),
                () -> assertEquals(new Position(position2.getX()+position2.getTileSize(), position2.getY()), position2.getBottomRightPoint()),
                () -> assertEquals(new Position(position1.getX(), position1.getY()+position1.getTileSize()), position2.getTopLeftPoint()),
                () -> assertEquals(new Position(position2.getX()+position2.getTileSize(), position2.getY()), position1.getBottomRightPoint())
        );
    }
}