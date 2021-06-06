package org.bombermen.game;

import org.bombermen.message.Message;
import org.bombermen.services.GameService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameSession {
    private CopyOnWriteArrayList<Player> players;
    private BlockingQueue<Message> inputQueue;
    private final int MAX_N_OF_PLAYERS;
    private boolean gameReady;
    private final String gameID;
    private Thread gameThread;
    private boolean gameSessionFinished;

    public GameSession(String gameID, int numOfPlayers) {
        this.gameID = gameID;
        inputQueue = new LinkedBlockingQueue<>();
        players = new CopyOnWriteArrayList<>();
        MAX_N_OF_PLAYERS = numOfPlayers;
    }

    public void createPlayer(String name){
        players.add(new Player(name));
        if(players.size() == MAX_N_OF_PLAYERS) {
            gameReady = true;
        }
    }

    public void pushMessage(Message message) throws InterruptedException {
        inputQueue.put(message);
    }

    public boolean isGameReady() {
        return gameReady;
    }

    public ArrayList<Message> getInputQueue() {
        ArrayList<Message> copy = new ArrayList<>();
        inputQueue.drainTo(copy);
        return copy;
    }

    public Spliterator<Player> getPlayersAsSpliterator() {
        return players.spliterator();
    }

    public Iterator<Player> getPlayersAsIterator() {
        return players.iterator();
    }

    public int getMAX_N_OF_PLAYERS() {
        return MAX_N_OF_PLAYERS;
    }

    public void deleteSession() {
        GameService.getInstance().getGames().remove(gameID);
        destroy();
    }

    public void saveMessagesForNextTick(List<Message> messages) {
        messages.forEach(m -> inputQueue.offer(m));
    }

    public Thread getGameThread() {
        return gameThread;
    }

    public void setGameThread(Thread gameThread) {
        this.gameThread = gameThread;
    }

    public boolean isGameSessionFinished() {
        return gameSessionFinished;
    }

    public void setGameSessionFinished(boolean gameSessionFinished) {
        this.gameSessionFinished = gameSessionFinished;
    }

    private void destroy() {
        players.forEach(item -> item = null);
        players = null;

        inputQueue.forEach(item -> item = null);
        inputQueue = null;

        gameThread = null;
    }
}
