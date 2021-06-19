package org.bombermen.game;

import org.bombermen.gameElements.Bomb;
import org.bombermen.gameElements.Pawn;
import org.bombermen.gameElements.Wall;
import org.bombermen.gameElements.Wood;
import org.bombermen.message.Topic;
import org.bombermen.replicas.Replica;
import org.bombermen.tick.Ticker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class GameThreadTest {

    private Thread thread;
    private GameThread gameThread;
    @Mock private GameSession gameSession;
    @Mock private Ticker ticker;
    @Mock private Replica replica;
    @Mock private GameMechanics gameMechanics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        gameThread = new GameThread(gameSession);
        thread = new Thread(gameThread, "testThread");
        doNothing().when(ticker).registerTickable(gameMechanics);
        doNothing().when(ticker).gameLoop();
        doNothing().when(gameSession).deleteSession();
    }

    @Test
    void checkState() {
        assertNotNull(thread);
        assertNotNull(gameThread);
        assertNotNull(gameSession);
        assertNotNull(ticker);
        assertNotNull(replica);
        assertNotNull(gameMechanics);
    }

//    @Test
//    void runTest(){
//        thread.start();
//        thread.interrupt();
//    }

    @AfterEach
    void tearDown() {
        gameThread = null;
        thread.interrupt();
        thread = null;
    }
}