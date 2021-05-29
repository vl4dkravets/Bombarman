package org.bombermen.network;

import org.bombermen.game.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);
    private static final ConnectionPool instance = new ConnectionPool();
    private static final int PARALLELISM_LEVEL = 2;
    private final AtomicInteger playerNumber;
    private final ConcurrentHashMap<WebSocketSession, String> pool;

    private HashMap<String, Integer> pressCounter = new HashMap<>();

    public static ConnectionPool getInstance() {
        return instance;
    }

    private ConnectionPool() {
        pool = new ConcurrentHashMap<>();
        playerNumber = new AtomicInteger(1);
    }

    public void send(@NotNull WebSocketSession session, @NotNull String msg) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(msg));
                if(pressCounter.containsKey(session.getId())) {
                    pressCounter.computeIfPresent(session.getId(), (key, value) -> value+1);
                }
                else {
                    pressCounter.put(session.getId(), 0);
                }
                System.out.println("Sent: " + pressCounter);

                // System.out.println("message was sent:\n");
                // System.out.println(msg);
            } catch (IOException ignored) {
            }
        }
    }

    public void clearPressCounter(){
        pressCounter.clear();
    }

//    public void broadcast(@NotNull String msg) {
//        pool.forEachKey(PARALLELISM_LEVEL, session -> send(session, msg));
//    }

//    public void broadcast1(@NotNull String msg, ArrayList<Player> players) {
//        pool.forEachKey(PARALLELISM_LEVEL, session -> {
//            int i = 0;
//             for(Player p: players) {
//                 if (p.getName().equals(session.getId())) {
//                     send(session, msg);
//                     i++;
//                 }
//                 if(i > 1) break;
//             }
//        });
//    }
//
//        public void broadcast2(@NotNull String msg, ArrayList<Player> players) {
//            //pool.forEachKey(PARALLELISM_LEVEL, session -> send(session, msg));
//            ArrayList<WebSocketSession> targetSessions = new ArrayList<>();
//            players.forEach(player -> targetSessions.add(getSession(player.getName())));
//            targetSessions.stream().parallel().forEach(session -> send(session, msg));
//    }

    public void shutdown() {
        pool.forEachKey(PARALLELISM_LEVEL, session -> {
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException ignored) {
                }
            }
        });
    }

    public String getPlayer(WebSocketSession session) {
        return pool.get(session);
    }

    public WebSocketSession getSession(String player) {
        return pool.entrySet().stream()
                .filter(entry -> entry.getValue().equals(player))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(null);
    }

    public void add(WebSocketSession session, String player) {
        if (pool.putIfAbsent(session, player) == null) {
            log.info("{} joined", player);
        }
    }

    public void remove(WebSocketSession session) {
        pool.remove(session);
    }
}
