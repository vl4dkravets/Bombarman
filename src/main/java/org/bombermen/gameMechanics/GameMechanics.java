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


    private final double pawnStepSize = 2.5;

    public long start = System.currentTimeMillis();
    public boolean showPosStats = true;

    private final ArrayList<Pawn> pawns;
    private final ArrayList<Player> players;
    private final ArrayList<Wall> walls;
    private final ArrayList<Wood> woods;
    private final ArrayList<Bomb> bombs;
    private final ArrayList<Fire> fires;
    private final ArrayList<Fire> firesLeft;
    private final ArrayList<Wood> destroyedWoods;
    private final int nOfPawns;
    private final GameSession gameSession;
    private final Replica replica;
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
        replica.writeReplicaToInitializeGameField(pawns, bombs, woods, walls, Topic.START);
    }

    private void createWallsAndWoods() {
//        Random random = new Random();
//        for(int x = 0, column = 0; x <= GAME_FIELD_W; x+=TILE_SIZE, column++) {
//            for(int y = 0, row = 0; y <= GAME_FIELD_H; y+=TILE_SIZE, row++) {
//                if(x==0) {
//                    walls.add(new Wall(walls.size()+1, new Position(x, y)));
//                }
//                else if(x == GAME_FIELD_W) {
//                    walls.add(new Wall(walls.size()+1, new Position(x, y)));
//                }
//                else if (column % 2 != 0) {
//                    if(y==0 || y==GAME_FIELD_H) {
//                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
//                    }
//                    else {
//                        if(random.nextBoolean()){
//                            if((x > TILE_SIZE*4 || y > TILE_SIZE*3) && (x < (GAME_FIELD_W - TILE_SIZE*3) || y < (GAME_FIELD_H - TILE_SIZE*4))) {
//                                woods.add(new Wood(woods.size()+1, new Position(x + 2, y - 2)));
//                            }
//                        }
//                    }
//
//                }
//                else {
//                    if(y==0 || y==GAME_FIELD_H) {
//                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
//                    }
//                    else if(row % 2 != 0){
//                        if(random.nextBoolean()){
//                            if((x > TILE_SIZE*4 || y > TILE_SIZE*4) ) {
//                                woods.add(new Wood(woods.size()+1, new Position(x + 2, y - 2)));
//                            }
//                        }
//                    }
//                    else {
//                        walls.add(new Wall(walls.size()+1, new Position(x, y)));
//                    }
//                }
//            }
//        }
        walls.add(new Wall(0, new Position(TILE_SIZE+800, TILE_SIZE)));
        walls.add(new Wall(1, new Position(TILE_SIZE+800, TILE_SIZE*2)));
        //replica.writeReplicaToInitializeGameField(woods, walls, Topic.START);
    }

    private void createPawns(){
        // create the pawns for each player to control
        for(int i = 0; i < nOfPawns; i++){
            pawns.add(new Pawn(i, players.get(i).getName(),"Pawn_"+i));
        }

        Pawn pawn1 = pawns.get(0);
        Pawn pawn2 = pawns.get(1);
        pawn1.setPosition(TILE_SIZE, TILE_SIZE);
        //pawn2.setPosition(GAME_FIELD_W-TILE_SIZE, GAME_FIELD_H-TILE_SIZE);
        pawn2.setPosition(TILE_SIZE, TILE_SIZE*2);
        pawn1.setBomb(new Bomb(pawn1.getPosition()));
        pawn2.setBomb(new Bomb(pawn2.getPosition()));
        bombs.add(pawn1.getBomb());
        bombs.add(pawn2.getBomb());

        //replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
    }

    private void handleGameOver(long elapsed) {
        GAME_END_PAUSE -= elapsed;
        if (GAME_END_PAUSE <= 0) {
            replica.writeReplicaGameOver(firstDeadPawn);
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void tick(long elapsed) {
        if (firstDeadPawn != null) {
            handleGameOver(elapsed);
        }

        List<Message> inputQueue = gameSession.getInputQueue();
        updatePlantedBombsTimers(elapsed);

        boolean breakOut = false;

//        if(showPosStats) {
//            System.out.println("Entering tick loop");
//        }
        System.out.println("Inputqueue size: " + inputQueue.size());

        for (Message message : inputQueue) {

            Topic topic = message.getTopic();
            String messageData = message.getData();
            String playerName = message.getPlayerName();
            Pawn pawn = pawns.stream().filter(pawn1 -> pawn1.getPlayerName().equals(playerName)).findFirst().get();
            Position pawnPosition = pawn.getPosition();
            String direction;

            if (topic == Topic.PLANT_BOMB) {
                Bomb bomb = pawn.getBomb();
                bomb.setPosition(new Position(bomb.getPosition().getX(), bomb.getPosition().getY()));

                pawn.setBomb(new Bomb(pawn.getPosition()));
                bombs.add(pawn.getBomb());
                updatePlantedBombsTimers(elapsed);
                continue;
            }

            direction = messageData.substring(messageData.indexOf(":") + 2, messageData.indexOf("}") - 1);


            //System.out.println(pawn.movedPerTickY + " " + direction.equals("UP") + " " + direction.equals("DOWN"));
//            if (pawn.movedPerTickY && ((direction.equals("UP") || direction.equals("DOWN")))) {
//                //System.out.println("skip");
//                continue;
//            }
//
//            //System.out.println(pawn.movedPerTickX + " " + direction.equals("LEFT") + " " + direction.equals("RIGHT"));
//            if (pawn.movedPerTickX && (direction.equals("LEFT") || direction.equals("RIGHT"))) {
//                //System.out.println("skip");
//                continue;
//            }

            //if a pawn already made a move during this tick - skip the rest of redundant MOVE commands
            if ((pawn.movedPerTickY && (direction.equals("UP") || direction.equals("DOWN"))) ||
                    (pawn.movedPerTickX && (direction.equals("LEFT") || direction.equals("RIGHT")))) {
                System.out.println("skip");
                continue;
            }

            System.out.println("\t" + pawn);

//            if(pawn.movedPerTickY && pawn.movedPerTickX) {
//                break;
//            }

            double newX = pawnPosition.getX();
            double newY = pawnPosition.getY();
            boolean canMove = false;

            switch (direction) {
                case "UP":
                    newY += pawnStepSize;
                    canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                    if (canMove) {
                        pawnPosition.setY(newY);

                        pawn.movedPerTickY = true;
                        //System.out.println("\t" + pawn + ": " + pawn.getPosition());
                    }
                    break;
                case "DOWN":
                    newY -= pawnStepSize;
                    canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                    if (canMove) {
                        pawnPosition.setY(newY);

                        pawn.movedPerTickY = true;
                        //System.out.println("\t" + pawn + ": " + pawn.getPosition());
                    }
                    break;
                case "LEFT":
                    newX -= pawnStepSize;
                    canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                    if (canMove) {
                        pawnPosition.setX(newX);

                        pawn.movedPerTickX = true;
                        //System.out.println("\t" + pawn + ": " + pawn.getPosition());
                    }
                    break;
                case "RIGHT":
                    newX += pawnStepSize;
                    canMove = checkIfPawnDidntStuck(newX, newY, pawn);
                    if (canMove) {
                        pawnPosition.setX(newX);

                        pawn.movedPerTickX = true;
                        //System.out.println("\t" + pawn + ": " + pawn.getPosition());
                    }
                    break;
            }
            pawn.setDirection(direction);
        }
//
//        if(inputQueue.size() > 0 || bombs.size() > 0) {
//            replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
//            System.out.println("Replica was sent");
//        }

        replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
        fires.clear();
        firesLeft.clear();
        destroyedWoods.clear();
        //System.out.println("Replica was sent");

//        fires.clear();
//        firesLeft.clear();
//        destroyedWoods.clear();

//        if(showPosStats) {
//            System.out.println("Ending tick loop\n");
//        }

        for (Pawn p : pawns) {
            p.movedPerTickY = false;
            p.movedPerTickX = false;
        }

//        long val = System.currentTimeMillis() - start;
//        if (val > 5_000 && showPosStats) {
//            pawns.forEach(p -> {
//                System.out.println(p + ": " + p.getPosition().posChanged);
//            });
//            showPosStats = false;
//        }

    }



    private void updatePlantedBombsTimers(long elapsed) {
        //  increment timer for the the rest of bombs which were planted
        Iterator<Bomb> iterator = bombs.iterator();
        while(iterator.hasNext()) {
            Bomb bomb = iterator.next();

            if (bomb.updateBombTimerAndCheck(elapsed)) {
                // System.out.println(bomb + " HAS EXPLODED!!");
                iterator.remove();
                handleFires(bomb.getPosition());
            }
        }

//        Pawn pawnAuto = pawns.get(1);
//        double x = pawnAuto.getPosition().getX()+pawnStepSize;
//        double y = pawnAuto.getPosition().getY();
//        pawnAuto.setPosition(x,y);

        //replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
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

        // check woods
        Iterator<Wood> iterator1;
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

        //replica.writeReplica(pawns, bombs, firesLeft, destroyedWoods, Topic.REPLICA);
//        fires.clear();
//        firesLeft.clear();
//        destroyedWoods.clear();
    }

    private boolean checkIfPawnDidntStuck(double currentX, double currentY, Pawn currentPawn) {
        Position currPos_TopLeft = new Position(currentX,currentY+ 32);
        Position currPos_BottomRight = new Position(currentX+32,currentY);

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
        return !(l1.getY() <= r2.getY()) && !(l2.getY() <= r1.getY());
    }
}
