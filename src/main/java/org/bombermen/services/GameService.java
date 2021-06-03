package org.bombermen.services;

import org.bombermen.exceptions.InvalidGameIdException;
import org.bombermen.game.GameSession;
import org.bombermen.game.GameThread;
import org.bombermen.tick.Ticker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//@Service
public class GameService {

	private static final GameService gameService = new GameService();

	private static final int MAX_N_OF_PLAYERS = 2;
	private final ConcurrentHashMap<String, GameSession> games;
	private final AtomicInteger playersConnectedToLastCreatedSession;
	private final AtomicInteger gameSessionCounter;
	private String lastCreateGameID;
	
	private GameService() {
		games = new ConcurrentHashMap<>();
		playersConnectedToLastCreatedSession = new AtomicInteger(0);
		gameSessionCounter = new AtomicInteger(0);
	}
	
	public void start(GameSession gameSession) {
		Thread t = new Thread(new GameThread(gameSession), "gameThread-"+gameSessionCounter.get());
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

	public static GameService getInstance() {return gameService;}

}