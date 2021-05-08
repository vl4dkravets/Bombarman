package org.bombermen.message;

import org.bombermen.network.Broker;
import org.bombermen.network.ConnectionPool;
import org.bombermen.services.GameService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class EventHandler extends TextWebSocketHandler implements WebSocketHandler {

//    private ConnectionPool connectionPool;
//    private Broker broker;
//    private GameService gameService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("Socket Connected: " + session);

        ConnectionPool connectionPool = ConnectionPool.getInstance();
        GameService gameService = GameService.getInstance();

        // use unique sessionId as player's ID, temporarily
        String playerId = session.getId();
        connectionPool.add(session,playerId);
        String gameID = retrieveGameIdFromQuery(session.getUri().getQuery());
        gameService.connect(playerId, gameID);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // session.sendMessage(new TextMessage("{ \"history\": [ \"ololo\", \"2\" ] }"));
        //System.out.println("Received " + message.toString());
        Broker broker = Broker.getInstance();
        broker.receive(session,message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Socket Closed: [" + closeStatus.getCode() + "] " + closeStatus.getReason());
        super.afterConnectionClosed(session, closeStatus);
    }

    private String retrieveGameIdFromQuery(String query) {
        return query.substring(query.indexOf("=")+1, query.indexOf("&"));
    }

}