package org.bombermen.game;

import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.services.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class GameSessionTest {
    private GameSession gameSession;
    private String gameId = "gameId1";
    private int numOfPlayers = 2;
    private String player1Name = "player1";
    private String player2Name = "player2";
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameSession = new GameSession(gameId, numOfPlayers);
        gameService = GameService.getInstance();
    }

    @Test
    void createPlayersTest() {
        gameSession.createPlayer(player1Name);
        gameSession.createPlayer(player2Name);
        assertTrue(gameSession.isGameReady());
        assertEquals(numOfPlayers, gameSession.getMAX_N_OF_PLAYERS());
        assertEquals(gameSession.getMAX_N_OF_PLAYERS(), StreamSupport.stream(gameSession.getPlayersAsSpliterator(), true).count());
        gameSession.setGameSessionFinished(true);
        assertTrue(gameSession.isGameSessionFinished());
    }

    @Test
    void pushMessageTest() throws InterruptedException {
        Message message1 = new Message(Topic.REPLICA, "data1");
        Message message2 = new Message(Topic.REPLICA, "data2");
        gameSession.pushMessage(message1);
        gameSession.pushMessage(message2);
        assertEquals(2, gameSession.getInputQueue().size());
        assertEquals(0, gameSession.getInputQueue().size());
    }

    @Test
    void getInputQueueTest() throws InterruptedException {
        assertEquals(0, gameSession.getInputQueue().size());
        Message message1 = new Message(Topic.REPLICA, "data1");
        Message message2 = new Message(Topic.REPLICA, "data2");
        Message message3 = new Message(Topic.REPLICA, "data3");
        gameSession.pushMessage(message1);
        gameSession.pushMessage(message2);
        gameSession.pushMessage(message3);
        assertEquals(3, gameSession.getInputQueue().size());
        assertEquals(0, gameSession.getInputQueue().size());
    }

    @Test
    void deleteSessionTest(){
        gameService.getGames().put(gameId, gameSession);
        gameSession.deleteSession();
        assertAll(
                () -> assertNull(gameService.getGames().get(gameId)),
                () -> assertThrows(NullPointerException.class, () -> gameSession.getPlayersAsSpliterator()),
                () -> assertThrows(NullPointerException.class, () -> gameSession.getInputQueue()),
                () -> assertNull(gameSession.getGameThread())
        );
    }

    @Test
    void saveMessagesForNextTickTest(){
        assertEquals(0, gameSession.getInputQueue().size());
        Message message1 = new Message(Topic.REPLICA, "data1");
        Message message2 = new Message(Topic.REPLICA, "data2");
        Message message3 = new Message(Topic.REPLICA, "data3");
        List<Message> list = new ArrayList<>();
        list.add(message1);
        list.add(message2);
        list.add(message3);

        gameSession.saveMessagesForNextTick(list);
        assertEquals(3, gameSession.getInputQueue().size());
        assertEquals(0, gameSession.getInputQueue().size());
    }

    @AfterEach
    void tearDown() {
        gameSession = null;
        gameService = null;
    }
}