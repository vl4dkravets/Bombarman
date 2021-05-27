package org.bombermen.gameMechanics;

import org.bombermen.game.Player;
import org.bombermen.message.Topic;
import org.bombermen.network.Broker;
import java.util.ArrayList;

public class Replica {
    private final ArrayList<Player> players;
    private final Broker broker;

    public Replica(ArrayList<Player> players) {
        this.players = players;
        broker = Broker.getInstance();
    }

    public void writeReplicaGameOver(Pawn deadPawn){
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(deadPawn.getId() == i) {
                broker.send(player.getName(), Topic.GAME_OVER, "You lost :(");
            }
            else{
                broker.send(player.getName(), Topic.GAME_OVER, "You won!!");
            }
        }
    }

    public void writeReplica(ArrayList<Pawn> pawns, ArrayList<Bomb> bombs, ArrayList<Fire> fires, ArrayList<Wood> destroyedWoods,  Topic topic) {
        ArrayList<GameElement> gameElements = new ArrayList<>();

        gameElements.addAll(pawns);
        gameElements.addAll(fires);
        gameElements.addAll(destroyedWoods);
        bombs.forEach(bomb -> {
            gameElements.add(bomb);
            bomb.setId(gameElements.indexOf(bomb));
        });

//        for(Player player: players) {
//            broker.send(player.getName(), topic, gameElements);
//        }

        players.stream().parallel().forEach(player -> broker.send(player.getName(), topic, gameElements));
        // broker.broadcast1(topic, gameElements, players);
        //broker.broadcast2(topic, gameElements, players);
    }

    public void writeReplicaToInitializeGameField(ArrayList<Wood> woods, ArrayList<Wall> walls, Topic topic) {
        ArrayList<GameElement> gameElements = new ArrayList<>();

        gameElements.addAll(woods);
        gameElements.addAll(walls);

        players.forEach(player -> broker.send(player.getName(), topic, gameElements));
    }
}
