package org.bombermen.services;

import org.bombermen.exceptions.InvalidGameIdException;
import org.bombermen.game.GameSession;
import org.bombermen.game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;

    @Mock
    private GameSession gameSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = GameService.getInstance();
    }

    @AfterEach
    void tearDown() {
        gameService.getGames().clear();
        gameService = null;
    }

    @Test
    void start() {
    }

    @Test
    void createTest() {
        String gameId1 = "gameId1";
        String gameId2 = "gameId2";
        String gameId3 = "gameId3";

        String result = gameService.create(gameId1);
        assertEquals(gameId1, result);
        assertEquals(1, gameService.getGames().size());

        result = gameService.create(gameId2);
        assertEquals(gameId1, result);
        assertEquals(1, gameService.getGames().size());

        result = gameService.create(gameId3);
        assertEquals(gameId3, result);
        assertEquals(2, gameService.getGames().size());
    }

    @Test
    void connectTest() throws InvalidGameIdException {
        doNothing().when(gameSession).createPlayer("");
        when(gameSession.isGameReady()).thenReturn(false);

        String gameIdTest = "gameIdTest";
        String player1 = "player1";
        String player2 = "player2";
        gameService.getGames().put(gameIdTest, gameSession);

        gameService.connect(player1, gameIdTest);
        gameService.connect(player2, gameIdTest);
        verify(gameSession, times(2)).isGameReady();

        assertThrows(InvalidGameIdException.class, () -> gameService.connect(player1, ""));
    }

    @Test
    void getGamesTest() {
        assertNotNull(gameService.getGames());
    }

    @Test
    void notAllPlayersDisconnectedFromGameTest() {
        String player1Name = "player1Name";
        String player2Name = "player2Name";
        Player player1 = new Player(player1Name);
        Player player2 = new Player(player2Name);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        when(gameSession.getPlayersAsIterator()).thenReturn(players.iterator());
        gameService.getGames().put("gameIdTest", gameSession);

        assertFalse(gameService.haveAllThePlayersDisconnectedFromGame(player1Name));
        assertFalse(player1.isConnected());
    }

    @Test
    void AllPlayersDisconnectedFromGameTest() {
        String player1Name = "player1Name";
        String player2Name = "player2Name";
        Player player1 = new Player(player1Name);
        player1.setIsConnected(false);
        Player player2 = new Player(player2Name);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        when(gameSession.getPlayersAsIterator()).thenReturn(players.iterator());
        when(gameSession.getGameThread()).thenReturn(new Thread());
        doNothing().when(gameSession).setGameSessionFinished(true);

        gameService.getGames().put("gameIdTest", gameSession);

        assertTrue(gameService.haveAllThePlayersDisconnectedFromGame(player2Name));
        assertFalse(player2.isConnected());
    }

    @Test
    void getInstance() {
        GameService gameService1 = GameService.getInstance();
        GameService gameService2 = GameService.getInstance();
        assertEquals(gameService1, gameService2);
    }
}