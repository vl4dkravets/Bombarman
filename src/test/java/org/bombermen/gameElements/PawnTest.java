package org.bombermen.gameElements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {
    private Pawn pawn0;
    private Pawn pawn1;
    private Pawn pawn2;
    private String playerName0;
    private String playerName1;
    private String playerName2;
    private String pawnName0;
    private String pawnName1;
    private String pawnName2;
    private String direction0;
    private String direction1;
    private String direction2;

    @BeforeEach
    void setUp() {
        direction0 = "UP";
        direction1 = "DOWN";
        direction2 = "LEFT";
        playerName0 = "playerName_0";
        playerName1 = "playerName_1";
        playerName2 = "playerName_2";
        pawnName0 = "pawnName_0";
        pawnName1 = "pawnName_1";
        pawnName2 = "pawnName_2";
        pawn0 = new Pawn(0,playerName0 ,pawnName0 );
        pawn1 = new Pawn(1, playerName1, pawnName1);
        pawn2 = new Pawn(2, playerName2, pawnName2);
        pawn0.setPosition(100,200);
        pawn1.setPosition(100,200);
        pawn2.setPosition(200,300);
        pawn0.setDirection(direction0);
        pawn1.setDirection(direction1);
        pawn2.setDirection(direction2);
    }

    @Test
    void checkState() {
        assertAll(
                () -> assertNotNull(pawn0),
                () -> assertNotNull(pawn1),
                () -> assertEquals(pawn0.getPosition(), pawn1.getPosition()),
                () -> assertEquals(pawn1.getPosition(), pawn0.getPosition()),
                () -> assertNotEquals(pawn1.getPosition(), pawn2.getPosition()),
                () -> assertNotEquals(pawn0.getPosition(), pawn2.getPosition()),
                () -> assertEquals(pawn0, pawn1),
                () -> assertEquals(pawn1, pawn0),
                () -> assertNotEquals(pawn1, pawn2),
                () -> assertNotEquals(pawn2, pawn1),
                () -> assertNotEquals(pawn2, pawn0),
                () -> assertNotEquals(pawn0, pawn2),
                () -> assertEquals(playerName0, pawn0.getPlayerName()),
                () -> assertEquals(playerName1, pawn1.getPlayerName()),
                () -> assertEquals(playerName2, pawn2.getPlayerName()),
                () -> assertNotEquals(playerName2, pawn1.getPlayerName()),
                () -> assertEquals(pawnName0, pawn0.toString()),
                () -> assertEquals(pawnName1, pawn1.toString()),
                () -> assertEquals(pawnName2, pawn2.toString()),
                () -> assertNotEquals(pawnName2, pawn1.toString()),
                () -> assertEquals(direction0, pawn0.getDirection()),
                () -> assertEquals(direction1, pawn1.getDirection()),
                () -> assertEquals(direction2, pawn2.getDirection()),
                () -> assertNotEquals(direction0, pawn2.getDirection()),
                () -> assertTrue(pawn0.isAlive()),
                () -> assertTrue(pawn1.isAlive()),
                () -> assertTrue(pawn2.isAlive()),
                () -> assertEquals(0, pawn0.getId()),
                () -> assertEquals(1, pawn1.getId()),
                () -> assertEquals(2, pawn2.getId())
                );
    }

}