package org.bombermen.network;

import org.bombermen.game.Player;
import org.bombermen.services.GameService;
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
    private static ConnectionPool instance;
    private final int PARALLELISM_LEVEL;
    private final AtomicInteger playerNumber;
    private final ConcurrentHashMap<WebSocketSession, String> pool;

    //public HashMap<String, Integer> pressesSent = new HashMap<>();

    private ConnectionPool() {
        pool = new ConcurrentHashMap<>();
        playerNumber = new AtomicInteger(1);
        PARALLELISM_LEVEL = 2;
    }

    public void send(@NotNull WebSocketSession session, @NotNull String msg) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(msg));
//                if(pressesSent.containsKey(session.getId())) {
//                    pressesSent.compute(session.getId(), (key, value) -> value+1);
//                }
//                else {
//                    pressesSent.put(session.getId(), 0);
//                }
            } catch (IOException | NullPointerException ignored) {
                System.out.println("caught " + ignored);
            }
        }
    }

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
        if(GameService.getInstance().haveAllThePlayersDisconnectedFromGame(session.getId())) {
            pool.remove(session);
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new ConnectionPool();
                }

            }
        }
        return instance;
    }

    public ConcurrentHashMap<WebSocketSession, String> getPool() {
        return pool;
    }
}
