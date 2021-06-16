package org.bombermen.services;

import org.bombermen.exceptions.InvalidGameIdException;
import org.bombermen.game.GameSession;
import org.bombermen.game.GameThread;
import org.bombermen.game.Player;
import org.bombermen.network.Broker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class GameService {

	private static GameService gameService;

	private static final int MAX_N_OF_PLAYERS = 2;
	private final ConcurrentHashMap<String, GameSession> games;
	private final AtomicInteger playersConnectedToLastCreatedSession;
	private final AtomicInteger gameSessionCounter;
	private String lastCreateGameID;
	private ArrayList<GameThread> gameThreads = new ArrayList<>();
	
	private GameService() {
		games = new ConcurrentHashMap<>();
		playersConnectedToLastCreatedSession = new AtomicInteger(0);
		gameSessionCounter = new AtomicInteger(0);
	}
	
	public void start(GameSession gameSession) {
		Thread t = new Thread(new GameThread(gameSession), "gameThread-"+gameSessionCounter.get());
		gameSession.setGameThread(t);
		t.start();
	}
	
	// return the client the gameID to connect to
	public synchronized String create(String clientGeneratedID) {
		if(playersConnectedToLastCreatedSession.get() == 0) {
			lastCreateGameID = null;
		}
		int playersConnected = playersConnectedToLastCreatedSession.incrementAndGet();
		if(playersConnected < MAX_N_OF_PLAYERS) {
			if(lastCreateGameID == null) {
				lastCreateGameID = clientGeneratedID;
				games.put(lastCreateGameID, new GameSession(lastCreateGameID, MAX_N_OF_PLAYERS));
			}
		}
		else {
			playersConnectedToLastCreatedSession.set(0);
		}
		
		return lastCreateGameID;
	}
	
	public void connect(String playerName, String gameId) throws InvalidGameIdException {
		GameSession gameSession;
		if(games.containsKey(gameId)) {
			gameSession = games.get(gameId);
			gameSession.createPlayer(playerName);
			if(gameSession.isGameReady()) {
				this.start(gameSession);
			}
		}
		else {
			// throw some websocket exception to cause error on client
			throw new InvalidGameIdException("Game with gameId " + gameId + " doesn't exists");
		}
	}

	public ConcurrentHashMap<String, GameSession> getGames() {
		return games;
	}

	public boolean haveAllThePlayersDisconnectedFromGame(String playerID) {

		for(GameSession gs: games.values()) {
			boolean playerDisconnectedFromSession = false;
			Iterator<Player> players = gs.getPlayersAsIterator();
			while(players.hasNext()) {
				Player p = players.next();
				if(p.getName().equals(playerID) && p.isConnected()) {
					p.setIsConnected(false);
					//reset the iterator
					players = gs.getPlayersAsIterator();
					playerDisconnectedFromSession = true;
				}
				else if(playerDisconnectedFromSession) {
					if(p.isConnected()) {
						//meaning, there's still one player live
						return false;
					}
				}
			}
			//if got till this point & it's true, then all the players in the session have disconnected
			if(playerDisconnectedFromSession) {
				System.out.println("All the players left the session");
				gs.getGameThread().interrupt();
				gs.setGameSessionFinished(true);
				//games.remove(games.entrySet().stream().filter(pair -> pair.getValue().isGameSessionFinished()).findFirst().get());
				return true;
			}
		}
		return true;
	}

	public static GameService getInstance()
	{
		if (gameService == null)
		{
			//synchronized block to remove overhead
			synchronized (GameService.class)
			{
				if(gameService==null)
				{
					// if instance is null, initialize
					gameService = new GameService();
				}

			}
		}
		return gameService;
	}

}