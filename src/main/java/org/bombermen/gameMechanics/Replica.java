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

//    public void writeReplicaPawns(ArrayList<Pawn> gameElements, Topic topic) {
//        for(Player player: players) {
//            broker.send(player.getName(), topic, gameElements);
//        }
//    }
    public void writeReplicaGameOver(Pawn deadPawn){
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(deadPawn.getId() == i) {
                broker.send(player.getName(), Topic.GAME_OVER, "You lost!");
            }
            else{
                broker.send(player.getName(), Topic.GAME_OVER, "You won!");
            }
        }
    }

    public void writeReplica(ArrayList<Pawn> pawns, ArrayList<Bomb> bombs, ArrayList<Fire> fires, ArrayList<Wood> destroyedWoods,  Topic topic) {
        ArrayList<GameElement> gameElements = new ArrayList<>();
        for(Pawn pawn: pawns) {
            gameElements.add((GameElement) pawn);
        }
        for(Bomb bomb: bombs) {
            gameElements.add((GameElement) bomb);
            bomb.setId(gameElements.indexOf(bomb));
        }
        for(Fire fire: fires) {
            gameElements.add((GameElement) fire);
        }

        for(Wood wood: destroyedWoods) {
            gameElements.add((GameElement) wood);
        }

        for(Player player: players) {
            broker.send(player.getName(), topic, gameElements);
        }
    }

    public void writeReplicaWall(ArrayList<Wall> gameElements, Topic topic) {
        for(Player player: players) {
            broker.send(player.getName(), topic, gameElements);
        }
    }

    public void writeReplicaWoods(ArrayList<Wood> gameElements, Topic topic) {
        for(Player player: players) {
            broker.send(player.getName(), topic, gameElements);
        }
    }
}
