package org.bombermen.gameMechanics;

import org.bombermen.game.GameSession;
import org.bombermen.game.Player;
import org.bombermen.message.Topic;
import org.bombermen.network.Broker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class Replica {
    private final GameSession gameSession;
    private final Broker broker;

    public Replica(GameSession gameSession) {
        this.gameSession = gameSession;
        broker = Broker.getInstance();
    }

    public void writeReplicaPossessed(ArrayList<Pawn> pawns) {
        Iterator<Player> iterator = gameSession.getPlayersAsIterator();
        int index = 0;
        while(iterator.hasNext()) {
            broker.send(iterator.next().getName(), Topic.POSSESS, new Integer(index++));
        }
    }


    public void writeReplicaGameOver(ArrayList<Pawn> deadPawns) {
        if (deadPawns.size() == 1) {
            StreamSupport.stream(gameSession.getPlayersAsSpliterator(), true).
                    forEach(player -> {
                        if (deadPawns.get(0).getPlayerName().equals(player.getName()) && player.isConnected()) {
                            broker.send(player.getName(), Topic.GAME_OVER, "You lost :(");
                        } else if (player.isConnected()){
                            broker.send(player.getName(), Topic.GAME_OVER, "You won!!");
                        }
                    });
        }
        else if(deadPawns.size() == 2) {
            StreamSupport.stream(gameSession.getPlayersAsSpliterator(), true).
                    forEach(player -> {
                        if(player.isConnected()) {
                            broker.send(player.getName(), Topic.GAME_OVER, "It's a draw!!");
                        }
                    });
        }
    }

    public void writeReplica(ArrayList<Pawn> pawns, ArrayList<Bomb> bombs, ArrayList<Fire> fires, ArrayList<Wood> destroyedWoods,  Topic topic) {
        ArrayList<GameElement> gameElements = new ArrayList<>();

        gameElements.addAll(pawns);
        bombs.forEach(bomb -> {
            gameElements.add(bomb);
            bomb.setId(gameElements.size()-1);
        });
        gameElements.addAll(fires);
        gameElements.addAll(destroyedWoods);

        StreamSupport.stream(gameSession.getPlayersAsSpliterator(), true).forEach(player -> {
            if(player.isConnected()) {
                broker.send(player.getName(), topic, gameElements);
            }
        });
    }

    public void writeReplicaToInitializeGameField(ArrayList<Pawn> pawns, ArrayList<Bomb> bombs, ArrayList<Wood> woods, ArrayList<Wall> walls, Topic topic) {
        ArrayList<GameElement> gameElements = new ArrayList<>();

        gameElements.addAll(pawns);
        bombs.forEach(bomb -> {
            gameElements.add(bomb);
            bomb.setId(gameElements.size()-1);
        });
        gameElements.addAll(woods);
        gameElements.addAll(walls);


        StreamSupport.stream(gameSession.getPlayersAsSpliterator(), true).forEach(player -> {
            if(player.isConnected()) {
                broker.send(player.getName(), topic, gameElements);
            }
        });
    }
}
