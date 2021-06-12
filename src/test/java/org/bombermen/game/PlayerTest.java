package org.bombermen.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player1;
    private Player player2;
    private String player1Name = "player1";
    private String player2Name = "player2";


    @BeforeEach
    void setUp() {
        player1 = new Player(player1Name);
        player2 = new Player(player2Name);
    }

    @Test
    void checkState() {
        assertAll(
                () -> assertNotNull(player1),
                () -> assertNotNull(player2),
                () -> assertEquals(player1Name, player1.getName()),
                () -> assertEquals(player2Name, player2.getName()),
                () -> assertNotEquals(player1Name, player2.getName()),
                () -> assertTrue(player1.isConnected()),
                () -> assertTrue(player2.isConnected()),
                () -> assertFalse(!player2.isConnected())
                );
        player1.setIsConnected(false);
        player2.setIsConnected(false);
        assertFalse(player1.isConnected());
        assertFalse(player2.isConnected());
    }
}