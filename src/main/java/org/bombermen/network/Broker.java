package org.bombermen.network;

import org.bombermen.game.GameSession;
import org.bombermen.game.Player;
import org.bombermen.services.GameService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.util.JsonHelper;

import java.util.ArrayList;
import java.util.Objects;

public class Broker {
    //private static final Logger log = LoggerFactory.getLogger(Broker.class);
    private static final Broker instance = new Broker();
    private final ConnectionPool connectionPool;
    public static Broker getInstance() {
        return instance;
    }
    private Broker() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    public void receive(@NotNull WebSocketSession session, @NotNull String msg) throws InterruptedException {
        //log.info("RECEIVED: " + msg);
        //TODO TASK2 implement message processing
        GameService gameService = GameService.getInstance();

        String gameID = retrieveGameIdFromQuery(Objects.requireNonNull(session.getUri()).getQuery());
        GameSession gameSession1 = gameService.getGames().get(gameID);
        if(gameSession1 != null) {
            Message message1 = JsonHelper.fromJson(msg, Message.class);
            message1.setPlayerName(session.getId());
            gameSession1.pushMessage(message1);
        }

    }

    public void send(@NotNull String player, @NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
        WebSocketSession session = connectionPool.getSession(player);
        connectionPool.send(session, message);
        // System.out.println(message);
    }

//    public void broadcast(@NotNull Topic topic, @NotNull Object object) {
//        String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
//        connectionPool.broadcast(message);
//    }

//    public void broadcast1(@NotNull Topic topic, @NotNull Object object, ArrayList<Player> players) {
//        String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
//        connectionPool.broadcast1(message, players);
//    }
//
//    public void broadcast2(@NotNull Topic topic, @NotNull Object object, ArrayList<Player> players) {
//        String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
//        connectionPool.broadcast2(message, players);
//    }

    private String retrieveGameIdFromQuery(String query) {
        return query.substring(query.indexOf("=")+1, query.indexOf("&"));
    }

}
