package org.bombermen.network;

import org.bombermen.exceptions.InvalidGameIdException;
import org.bombermen.network.Broker;
import org.bombermen.network.ConnectionPool;
import org.bombermen.services.GameService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EventHandler extends TextWebSocketHandler implements WebSocketHandler {

    private HashMap<String, Integer> pressCounter = new HashMap<>();
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("Socket Connected: " + session);

        ConnectionPool connectionPool = ConnectionPool.getInstance();
        GameService gameService = GameService.getInstance();

        // use unique sessionId as player's ID, temporarily
        String playerId = session.getId();
        connectionPool.add(session,playerId);
        String gameID = retrieveGameIdFromQuery(Objects.requireNonNull(session.getUri()).getQuery());

        pressCounter.put(playerId, 0);

        try {
            gameService.connect(playerId, gameID);
        } catch (InvalidGameIdException e) {
            throw new Exception(e);
        }
    }


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        //session.sendMessage(new TextMessage("{ \"history\": [ \"ololo\", \"2\" ] }"));
        //System.out.println("Received from " + session.getId());

        pressCounter.computeIfPresent(session.getId(), (key, value) -> value+1);
        System.out.println(pressCounter);
        Broker broker = Broker.getInstance();
        broker.receive(session,message.getPayload());
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Socket Closed: [" + closeStatus.getCode() + "] " + closeStatus.getReason());
        super.afterConnectionClosed(session, closeStatus);

    }

    private String retrieveGameIdFromQuery(String query) {
        return query.substring(query.indexOf("=")+1, query.indexOf("&"));
    }

}