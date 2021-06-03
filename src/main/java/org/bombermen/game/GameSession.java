package org.bombermen.game;

import org.bombermen.message.Message;
import org.bombermen.services.GameService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameSession {
    //private final CopyOnWriteArrayList<Message> inputQueue;
    private final BlockingQueue<Message> inputQueue;
    private final ArrayList<Player> players;
    private final int MAX_N_OF_PLAYERS;
    private boolean gameReady;
    private final String gameID;

    public GameSession(String gameID, int numOfPlayers) {
        this.gameID = gameID;
        inputQueue = new LinkedBlockingQueue<>();
        players = new ArrayList<>();
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getMAX_N_OF_PLAYERS() {
        return MAX_N_OF_PLAYERS;
    }

    public void deleteSession() {
        GameService.getInstance().getGames().remove(gameID);
    }

    public void saveMessagesForNextTick(List<Message> messages) {
        messages.forEach(m -> inputQueue.offer(m));
    }
}
