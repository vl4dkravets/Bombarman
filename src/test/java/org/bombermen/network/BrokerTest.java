package org.bombermen.network;

import org.bombermen.game.GameSession;
import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.services.GameService;
import org.bombermen.util.JsonHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@JsonTest
class BrokerTest {
//    private Broker broker;
//    @Mock private ConnectionPool connectionPool;
//    @Mock private GameService gameService;
//    @Mock private WebSocketSession session;
//    private String moveMessage = "{\"topic\":\"MOVE\",\"data\":{\"direction\":\"DOWN\"}}";
//    private String plantBombMessage = "{\"topic\":\"PLANT_BOMB\",\"data\":{}}";
//    private String url = "gameId=9646&name=NKOHA";
//    private String gameID = "9646";
//    private GameSession gameSession;
    @Autowired
    private JacksonTester<Message> jsonTest;

    private String posssessMessage = "{\"topic\":\"POSSESS\",\"data\":\"0\"}";

    private String startDataMessage = "[{\"id\":0,\"position\":{\"x\":32.0,\"y\":32.0},\"type\":\"Pawn\",\"alive\":true,\"direction\":null}]";
    private String startMessage = "{\"topic\":\"START\",\"data\":\"[{\\\"id\\\":0,\\\"position\\\":{\\\"x\\\":32.0,\\\"y\\\":32.0},\\\"type\\\":\\\"Pawn\\\",\\\"alive\\\":true,\\\"direction\\\":null}]\"}";

    private String replicaData = "[{\"id\":0,\"position\":{\"x\":32.0,\"y\":32.0},\"type\":\"Pawn\",\"alive\":true,\"direction\":null}]";
    private String replicaMessage = "{\"topic\":\"REPLICA\",\"data\":\"[{\\\"id\\\":0,\\\"position\\\":{\\\"x\\\":32.0,\\\"y\\\":32.0},\\\"type\\\":\\\"Pawn\\\",\\\"alive\\\":true,\\\"direction\\\":null}]\"}";


//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        gameSession = new GameSession(gameID, 2);
//        when(session.getUri().getQuery()).thenReturn(url);
//        when(session.getId()).thenReturn("1234");
//        when(gameService.getGames().get(gameID)).thenReturn(gameSession);
//    }

//    @Test
//    void receiveTest() throws InterruptedException {
//        //broker = Broker.getInstance();
//        //broker.receive(session, moveMessage);
//        //ArrayList<Message> messages = gameSession.getInputQueue();
////        assertAll(
////                () -> assertEquals(1, messages.size()),
////                () -> assertEquals(Topic.MOVE, messages.get(0).getTopic()),
////                () -> assertEquals("{\"direction\":\"DOWN\"}", messages.get(0).getData()),
////                () -> assertEquals(session.getId(), messages.get(0).getTopic())
////        );
//
//        Message message1 = JsonHelper.fromJson(msg, Message.class);
//    }

    @Test
    void sendTest() throws IOException {
        Message message = new Message(Topic.POSSESS, "0");
        String json = jsonTest.write(message).getJson();
        assertEquals(json, posssessMessage);


        message = new Message(Topic.REPLICA, replicaData);
        json = jsonTest.write(message).getJson();
        assertEquals(json, replicaMessage);

        message = new Message(Topic.START, startDataMessage);
        json = jsonTest.write(message).getJson();
        assertEquals(json, startMessage);
    }
}