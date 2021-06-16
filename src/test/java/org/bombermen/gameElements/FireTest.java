package org.bombermen.gameElements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FireTest {
    private Fire fire;
    private Position position;

    @BeforeEach
    void setUp() {
        position = new Position(100, 200);
        fire = new Fire(1, position);
    }

    @Test
    void checkState() {
        Position topLeftCornerOfFire = fire.getTopLeftPoint();
        Position bottomRightCornerOfFire = fire.getBottomRightPoint();
        Position topLeftTest = new Position(fire.getPosition().getX(), fire.getPosition().getY()+fire.getTileSize());
        Position bottomRightTest = new Position(fire.getPosition().getX()+fire.getTileSize(), fire.getPosition().getY());

        assertAll(() -> assertNotNull(position),
                () -> assertNotNull(fire),
                () -> assertEquals(100, position.getX()),
                () -> assertEquals(200, position.getY()),
                () -> assertEquals(2001,fire.getId()),
                () -> assertEquals(32, fire.getTileSize()),
                () -> assertTrue(topLeftCornerOfFire.equals(topLeftTest)),
                () -> assertTrue(bottomRightCornerOfFire.equals(bottomRightTest)),
                () -> assertFalse(topLeftTest.equals(bottomRightTest))
                );
    }

    @AfterEach
    void tearDown() {
        position = null;
        fire = null;
    }
}