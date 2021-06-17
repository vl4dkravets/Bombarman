package org.bombermen.network;

import org.bombermen.services.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionPoolTest {

    @Mock WebSocketSession session1;
    @Mock WebSocketSession session2;
    @Mock GameService gameService;
    private String posssessTestMessage = "{\"topic\":\"POSSESS\",\"data\":\"0\"}";
    private ConnectionPool connectionPool;
    private IOException IOException;
    private String player1 = "player1";
    private String player2 = "player2";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        connectionPool = ConnectionPool.getInstance();
    }

    @AfterEach
    void tearDown() {
        connectionPool.getPool().clear();
        connectionPool = null;
    }

    @Test
    void successfulSendTest() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        doNothing().when(session1).sendMessage(new TextMessage(posssessTestMessage));
        connectionPool.send(session1, posssessTestMessage);
        verify(session1, times(1)).isOpen();
        verify(session1, times(1)).sendMessage(new TextMessage(posssessTestMessage));
    }

    @Test
    void notSuccessfulSendConnectionClosedTest() throws IOException {
        when(session1.isOpen()).thenReturn(false);
        connectionPool.send(session1, posssessTestMessage);
        verify(session1, never()).sendMessage(new TextMessage(posssessTestMessage));
    }

    @Test
    void shutdownTest() throws IOException {
        connectionPool.add(session1, "player1");
        connectionPool.add(session2, "player2");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        doNothing().when(session1).close();
        doNothing().when(session2).close();
        assertEquals(2, connectionPool.getPool().size());
        connectionPool.shutdown();
        verify(session1, times(1)).close();
        verify(session2, times(1)).close();
    }

    @Test
    void getPlayerTest() {
        String player1 = "player1";
        connectionPool.add(session1, player1);
        String somePlayer = connectionPool.getPlayer(session1);
        assertEquals(player1, somePlayer);
    }

    @Test
    void getSessionSuccessfullyTest() {
        String player1 = "player1";
        connectionPool.add(session1, player1);
        assertEquals(session1, connectionPool.getSession(player1));
    }

    @Test
    void getSessionNotSuccessfullyTest() {
        connectionPool.add(session1, player1);
        assertThrows(NullPointerException.class, () -> connectionPool.getSession(player2));
    }


    @Test
    void addTest() {
        connectionPool.add(session1, player1);
        assertEquals(1, connectionPool.getPool().size());

        connectionPool.add(session2, player2);
        assertEquals(2, connectionPool.getPool().size());

        connectionPool.add(session2, player1);
        assertEquals(2, connectionPool.getPool().size());
    }

    @Test
    void removeTest() {
        connectionPool.add(session1, player1);
        assertEquals(1, connectionPool.getPool().size());

        connectionPool.remove(session1);

        assertEquals(0, connectionPool.getPool().size());

    }

    @Test
    void getInstance() {
        ConnectionPool connectionPool1 = ConnectionPool.getInstance();
        ConnectionPool connectionPool2 = ConnectionPool.getInstance();
        assertEquals(connectionPool1, connectionPool2);
    }
}