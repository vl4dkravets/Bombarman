package org.bombermen.gameMechanics;

import org.bombermen.game.GameSession;
import org.bombermen.game.Player;
import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.tick.Tickable;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameMechanics implements Tickable, Comparable {
    private final int GAME_FIELD_W = 847 - 15;
    private final int GAME_FIELD_H = 527 - 15;

    private ArrayList<Pawn> pawns;
    private ArrayList<Player> players;
    private ArrayList<Wall> walls;
    private ArrayList<Wood> woods;
    private ArrayList<Bomb> bombs;
    private ArrayList<Fire> fires;
    private ArrayList<Fire> firesLeft;
    private ArrayList<Wood> destroyedWoods;
    private final int nOfPawns;
    private final GameSession gameSession;
    private Replica replica;
    private final int TILE_SIZE;
    private Pawn firstDeadPawn;
    private long GAME_END_PAUSE = 3000;

    public GameMechanics(GameSession gameSession) {
        TILE_SIZE = 32;
        pawns = new ArrayList<>();
        walls = new ArrayList<>();
        woods = new ArrayList<>();
        bombs = new ArrayList<>();
        fires = new ArrayList<>();
        firesLeft = new ArrayList<>();
        destroyedWoods = new ArrayList<>();
        this.gameSession = gameSession;
        this.nOfPawns = gameSession.getMAX_N_OF_PLAYERS();
        players = gameSession.getPlayers();
        replica = new Replica(gameSession.getPlayers());

        createWallsAndWoods();
        createPawns();
    }

    private void createWallsAndWoods() {
        Random random = new Random();
        for(int x = 0, column = 0; x <= GAME_FIELD_W; x+=TILE_SIZE, column++) {
            for(int y = 0, row = 0; y <= GAME_FIELD_H; y+=TILE_SIZE, row++) {
                if(x==0) {
                    walls.add(new Wall(walls.size()+1, new Position(x, y)));
                }
                else if(x == GAME_FIELD_W) {
                    walls.add(new Wall(walls.size()+1, new Position(x, y)));
                }
                else if (column % 2 != 0) {
                    if(y==0 || y==GAME_FIELD_H) {
                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
                    }
                    else {
                        if(random.nextBoolean()){
                            if((x > TILE_SIZE*4 || y > TILE_SIZE*3) && (x < (GAME_FIELD_W - TILE_SIZE*3) || y < (GAME_FIELD_H - TILE_SIZE*4))) {
                                woods.add(new Wood(woods.size()+1, new Position(x + 2, y - 2)));
                            }
                        }
                    }

                }
                else if(column % 2 == 0) {
                    if(y==0 || y==GAME_FIELD_H) {
                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
                    }
                    else if(row % 2 != 0){
                        if(random.nextBoolean()){
                            if((x > TILE_SIZE*4 || y > TILE_SIZE*4) ) {
                                woods.add(new Wood(woods.size()+1, new Position(x + 2, y - 2)));
                            }
                        }
                    }
                    else if(row % 2 == 0) {
                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
                    }
                }
            }
        }
//        walls.add(new Wall(1,new Position(36, 250)));
//        walls.add(new Wall(2,new Position(36, 282)));
//        walls.add(new Wall(3,new Position(36, 314)));
//        //walls.add(new Wall(4,new Position(68, 314)));
//        walls.add(new Wall(5,new Position(68, 250)));
//        walls.add(new Wall(6,new Position(100, 250)));
//        walls.add(new Wall(7,new Position(132, 250)));
//        walls.add(new Wall(8,new Position(132, 282)));
//        walls.add(new Wall(9,new Position(132, 314)));
//        walls.add(new Wall(10,new Position(100, 314)));

        //woods.add(new Wood(fires.size(),new Position(100 + 32 + 2, 250 - 2)));

        replica.writeReplicaWall(walls, Topic.START);
        replica.writeReplicaWoods(woods, Topic.START);
    }

    private void createPawns(){
        // create the pawns for each player to control
        for(int i = 0; i < nOfPawns; i++){
            pawns.add(new Pawn(i, players.get(i).getName()));
        }

        Pawn pawn1 = pawns.get(0);
        Pawn pawn2 = pawns.get(1);
        pawn1.setPosition(TILE_SIZE, TILE_SIZE);
        pawn2.setPosition(GAME_FIELD_W-TILE_SIZE, GAME_FIELD_H-TILE_SIZE);
        pawn1.setBomb(new Bomb(pawn1.getPosition()));
        pawn2.setBomb(new Bomb(pawn2.getPosition()));
        bombs.add(pawn1.getBomb());
        bombs.add(pawn2.getBomb());

        replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
    }

    @Override
    public void tick(long elapsed) {
        List<Message> inputQueue = gameSession.getInputQueue();
        handlePlantedBomb(elapsed);

        if(firstDeadPawn != null) {
            GAME_END_PAUSE-=elapsed;
            if(GAME_END_PAUSE <= 0) {
                handleGameOver();
                Thread.currentThread().interrupt();
            }
            return;
        }

        for(Message message: inputQueue) {
            Topic topic = message.getTopic();
            String messageData = message.getData();
            String playerName = message.getPlayerName();
            Pawn pawn = pawns.stream().filter(pawn1 -> pawn1.getPlayerName() == playerName).findFirst().get();
            Position pawnPosition = pawn.getPosition();
            String direction;

            if(topic == Topic.PLANT_BOMB) {
                //bombs.add(new Bomb(pawnPosition));
                //System.out.println("bomb added");
                Bomb bomb = pawn.getBomb();
                bomb.setPosition(new Position(bomb.getPosition().getX(), bomb.getPosition().getY()));

                pawn.setBomb(new Bomb(pawn.getPosition()));
                bombs.add(pawn.getBomb());
                handlePlantedBomb(elapsed);
                continue;
            }
            else {
                direction = messageData.substring(messageData.indexOf(":")+2, messageData.indexOf("}")-1);
            }

            double newX = pawnPosition.getX();
            double newY = pawnPosition.getY();
            boolean canMove = false;

            if(direction.equals("UP")) {
                newY+=0.6;
                canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                if(canMove) {
                    pawnPosition.setY(newY);
                }
            }
            else if(direction.equals("DOWN")) {
                newY-=0.6;
                canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                if(canMove) {
                    pawnPosition.setY(newY);
                }
            }
            else if(direction.equals("LEFT")) {
                newX-=0.6;
                canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                if(canMove) {
                    pawnPosition.setX(newX);
                }
            }
            else if(direction.equals("RIGHT")) {
                newX+=0.6;
                canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                if(canMove) {
                    pawnPosition.setX(newX);
                }
            }
            pawn.setDirection(direction);

            if(canMove || topic == Topic.PLANT_BOMB) {
                replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
            }
        }
    }

    private void handleGameOver() {
        replica.writeReplicaGameOver(firstDeadPawn);
    }

    private void handlePlantedBomb(long elapsed) {
        //  increment timer for the the rest of bombs which were planted
        Iterator<Bomb> iterator = bombs.iterator();
        while(iterator.hasNext()) {
            Bomb bomb = iterator.next();
            boolean exploded = bomb.updateBombTimerAndCheck(elapsed);

            if (exploded) {
                // System.out.println(bomb + " HAS EXPLODED!!");
                iterator.remove();
                handleFires(bomb.getPosition());

            }
        }
        replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
    }

    private void handleFires(Position explosionPosition){
        double x = explosionPosition.getX();
        double y = explosionPosition.getY();
        fires.add(0, new Fire(fires.size(), new Position(x,y+TILE_SIZE*2)));
        fires.add(1,new Fire(fires.size(), new Position(x+TILE_SIZE*2,y)));
        fires.add(2,new Fire(fires.size(), new Position(x,y-TILE_SIZE*2)));
        fires.add(3, new Fire(fires.size(), new Position(x-TILE_SIZE*2,y)));
        fires.add(4, new Fire(fires.size(), new Position(x,y+TILE_SIZE)));
        fires.add(5,new Fire(fires.size(), new Position(x+TILE_SIZE,y)));
        fires.add(6,new Fire(fires.size(), new Position(x,y-TILE_SIZE)));
        fires.add(7,new Fire(fires.size(), new Position(x-TILE_SIZE,y)));
        fires.add(8,new Fire(fires.size(), new Position(x,y)));

        for(int i = 0; i < fires.size(); i++) {
            boolean doOverlap = false;
            Fire fire = fires.get(i);
            if(i==8) {
                // explosion in the center will always happen - no need to check & remove it
                firesLeft.add(fire);
                continue;
            }
            for(Wall wall: walls) {
                if(i < 4){
                    Fire initialFire = fires.get(i+4);
                    if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint()) ||
                            doOverlap(initialFire.getTopLeftPoint(), initialFire.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint())) {
                        doOverlap=true;
                        break;
                    }
                }
                else {
                    if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint())) {
                        doOverlap=true;
                        break;
                    }
                }
            }
            if(!doOverlap){
                firesLeft.add(fire);
            }
        }


        // check walls
//        Iterator<Fire> iterator = fires.iterator();
//        while(iterator.hasNext()){
//            Fire fire = iterator.next();
//            for(Wall wall: walls) {
//                if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint())) {
//                    iterator.remove();
//                    break;
//                }
//            }
//        }

        // check woods
        Iterator<Wood> iterator1 = null;
        for(Fire fire: firesLeft){
            iterator1 = woods.iterator();
            while(iterator1.hasNext()) {
                Wood wood = iterator1.next();
                if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), wood.getTopLeftPoint(), wood.getBottomRightPoint())) {
                    destroyedWoods.add(wood);
                    iterator1.remove();
                }
            }
        }

        //check pawns
        for(Fire fire: firesLeft){
            for(Pawn pawn: pawns){
                if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), pawn.getTopLeftPoint(), pawn.getBottomRightPoint())) {
                    pawn.setAlive(false);
                    firstDeadPawn = pawn;
                    break;
                }
            }
            if(firstDeadPawn != null) {break;}
        }

        replica.writeReplica(pawns, bombs, firesLeft, destroyedWoods, Topic.REPLICA);
        fires.clear();
        firesLeft.clear();
        destroyedWoods.clear();
    }

    private boolean checkIfPawnDidntStuck(double currentX, double currentY, Pawn currentPawn) {
//        Position currPos_TopLeft = new Position(currentX,currentY+ currentPawn.getTileSize());
//        Position currPos_BottomRight = new Position(currentX+currentPawn.getTileSize(),currentY);
        Position currPos_TopLeft = new Position(currentX,currentY+ 32);
        Position currPos_BottomRight = new Position(currentX+32,currentY);

        // first, check against game filed bounds
        //if(currentX > GAME_FIELD_W || currentY > GAME_FIELD_H || currentX < 5 || currentY < 5) { return false; }

        // second, make sure pawn pawns don't step on each other
        for(Pawn pawn: pawns) {
            if(pawn == currentPawn) {
                continue;
            }
            if(doOverlap(currPos_TopLeft, currPos_BottomRight, pawn.getTopLeftPoint(), pawn.getBottomRightPoint())) {
                return false;
            }
        }

        //third, check whether current pawn step on any wall or wood
        for(Wall wall: walls) {
            if(doOverlap(currPos_TopLeft, currPos_BottomRight, wall.getTopLeftPoint(), wall.getBottomRightPoint())) {return false;}
        }

        for(Wood wood: woods) {
            if(doOverlap(currPos_TopLeft, currPos_BottomRight, wood.getTopLeftPoint(), wood.getBottomRightPoint())) {return false;}
        }

        return true;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    private boolean doOverlap(Position l1, Position r1, Position l2, Position r2) {
        // To check if either rectangle is actually a line
        // For example :  l1 ={-1,0}  r1={1,1}  l2={0,-1}  r2={0,1}
        if (l1.getX() == r1.getX() || l1.getY() == r2.getY() || l2.getX() == r2.getX() || l2.getY() == r2.getY())
        {
            // the line cannot have positive overlap
            return false;
        }

        // If one rectangle is on left side of other
        if (l1.getX() >= r2.getX() || l2.getX() >= r1.getX()) {
            return false;
        }

        // If one rectangle is above other
        if (l1.getY() <= r2.getY() || l2.getY() <= r1.getY()) {
            return false;
        }

        return true;
    }
}
