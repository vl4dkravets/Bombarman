package org.bombermen.game;

import org.bombermen.replicas.Replica;
import org.bombermen.tick.Ticker;

public class GameThread implements Runnable {
    private final GameSession gameSession;
    private Ticker ticker;

    public GameThread(GameSession gameSession) {
        this.gameSession = gameSession;
        ticker = new Ticker();
    }

    @Override
    public void run() {
        GameMechanics gameMechanics = new GameMechanics(gameSession, new Replica(gameSession));
        ticker.registerTickable(gameMechanics);
        ticker.gameLoop();
        finishGameSession();
    }

    private void finishGameSession(){
        ticker = null;
        gameSession.deleteSession();
    }
}
