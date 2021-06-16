package org.bombermen.gameElements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BombTest {
    private Bomb bomb1;
    private Bomb bomb2;
    private Position position1;
    private Position position2;

    @BeforeEach
    void setUp() {
        position1 = new Position(1,1);
        position2 = new Position(400,399);
        bomb1= new Bomb(position1);
        bomb2= new Bomb(position1);
    }

    @Test
    void testBombPosition() {
        assertEquals(1,position1.getX());
        assertEquals(1,position1.getY());
        assertEquals(400,position2.getX());
        assertEquals(399,position2.getY());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 2, 5000,9999 })
    void testBombDoNotExplodes(int elapsed) {
        assertFalse(bomb1.updateBombTimerAndCheck(elapsed));
    }

    @ParameterizedTest
    @ValueSource(ints = { 10000, 10001, 20000})
    void testBombExplodes(int elapsed) {
        assertTrue(bomb1.updateBombTimerAndCheck(elapsed));
    }

    @Test
    void checkState() {
        Position topLeftCornerOfFire = bomb1.getTopLeftPoint();
        Position bottomRightCornerOfFire = bomb1.getBottomRightPoint();
        Position topLeftTest = new Position(bomb1.getPosition().getX(), bomb1.getPosition().getY()+bomb1.getTileSize());
        Position bottomRightTest = new Position(bomb1.getPosition().getX()+bomb1.getTileSize(), bomb1.getPosition().getY());

        assertAll(() -> assertNotNull(position1),
                () -> assertNotNull(position2),
                () -> assertNotNull(bomb1),
                () -> assertNotNull(bomb2),
                () -> assertEquals(32, bomb1.getTileSize()),
                () -> assertTrue(topLeftCornerOfFire.equals(topLeftTest)),
                () -> assertTrue(bottomRightCornerOfFire.equals(bottomRightTest)),
                () -> assertFalse(topLeftTest.equals(bottomRightTest))
        );
    }

    @AfterEach
    void tearDown() {
        position1 = null;
        position2 = null;
        bomb1= null;
        bomb2= null;
    }
}