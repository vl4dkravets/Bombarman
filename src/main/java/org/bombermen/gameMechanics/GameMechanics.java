package org.bombermen.gameMechanics;

import org.bombermen.game.GameSession;
import org.bombermen.game.Player;
import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.tick.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameMechanics implements Tickable, Comparable {
    private final int GAME_FIELD_W = 847 - 15;
    private final int GAME_FIELD_H = 527 - 15;

    private final double pawnStepSize = 2;

    private ArrayList<Pawn> pawns;
    private ArrayList<Wall> walls;
    private ArrayList<Wood> woods;
    private ArrayList<Bomb> bombs;
    private ArrayList<Fire> firesLeft;
    private ArrayList<Fire> fires;
    private ArrayList<Wood> destroyedWoods;
    private int nOfPawns;
    private GameSession gameSession;
    private Replica replica;
    private int TILE_SIZE;
    //private Pawn firstDeadPawn;
    private ArrayList<Pawn> deadPawns;
    private long GAME_END_PAUSE = 3000;
    private ArrayList<Position> firesDefaultPositions;
    private boolean isGameFinished;

    public GameMechanics(GameSession gameSession) {
        TILE_SIZE = 32;
        pawns = new ArrayList<>();
        walls = new ArrayList<>();
        woods = new ArrayList<>();
        bombs = new ArrayList<>();
        firesDefaultPositions = new ArrayList<>();
        firesLeft = new ArrayList<>();
        fires = new ArrayList<>();
        destroyedWoods = new ArrayList<>();
        this.gameSession = gameSession;
        this.nOfPawns = gameSession.getMAX_N_OF_PLAYERS();
        replica = new Replica(gameSession);
        deadPawns = new ArrayList<>();

        createWallsAndWoods();
        createPawnsAndBombs();

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
    }

    private void createPawnsAndBombs(){
        // create the pawns for each player to control
        Iterator<Player> players = gameSession.getPlayersAsIterator();
        for(int i = 0; i < nOfPawns; i++){
            pawns.add(new Pawn(i, players.next().getName(),"Pawn_"+i));
        }

        Pawn pawn1 = pawns.get(0);
        Pawn pawn2 = pawns.get(1);
        pawn1.setPosition(TILE_SIZE, TILE_SIZE);
        pawn2.setPosition(TILE_SIZE, TILE_SIZE*2);
        //pawn2.setPosition(GAME_FIELD_W - TILE_SIZE, GAME_FIELD_H - TILE_SIZE);
        pawn1.setBomb(new Bomb(pawn1.getPosition()));
        pawn2.setBomb(new Bomb(pawn2.getPosition()));
        bombs.add(pawn1.getBomb());
        bombs.add(pawn2.getBomb());
    }

    private void handleGameOver(long elapsed) {
        GAME_END_PAUSE -= elapsed;
        if (GAME_END_PAUSE <= 0 && !isGameFinished) {
            replica.writeReplicaGameOver(deadPawns);
            Thread.currentThread().interrupt();
            isGameFinished = true;
        }
    }

    private void setUpBomb(Bomb bomb, Pawn pawn, long elapsed) {
        bomb.setPosition(new Position(bomb.getPosition().getX(), bomb.getPosition().getY()));
        pawn.setBomb(new Bomb(pawn.getPosition()));
        bombs.add(pawn.getBomb());
        updatePlantedBombsTimers(elapsed);
    }

    //updates coordinates automatically, so first pawn walks by itself
    private void pawnRobot() {
        Pawn pawnAuto = pawns.get(0);
        double x = pawnAuto.getPosition().getX()+pawnStepSize;
        double y = pawnAuto.getPosition().getY();
        pawnAuto.setPosition(x,y);
    }

    private boolean hasAllPawnHaveDoneAMoveForThisTick() {
        //check if all the pawns already done their move for this tick; if yes then leave the method since we are done for now
        for(Pawn pawn: pawns) {
            if(!(pawn.movedPerTickX || pawn.movedPerTickY)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasTickTimeRanOut(long tickStartTime, long elapsed, List<Message> inputQueue, int index) {
        //check whether tick lasts for too long; then store the rest of messages for the next tick and break out
        if(System.currentTimeMillis() - tickStartTime >= elapsed) {
            gameSession.saveMessagesForNextTick(inputQueue.subList(index,inputQueue.size()));
            return true;
        }
        return false;
    }

    private void destroy() {
        pawns.forEach(item -> item = null);
        pawns = null;

        walls.forEach(item -> item = null);
        walls = null;

        woods.forEach(item -> item = null);
        woods = null;

        bombs.forEach(item -> item = null);
        bombs = null;

        firesLeft.forEach(item -> item = null);
        firesLeft = null;

        destroyedWoods.forEach(item -> item = null);
        destroyedWoods = null;

        firesDefaultPositions.forEach(item -> item = null);
        firesDefaultPositions = null;

        gameSession = null;
        replica = null;
        deadPawns = null;
    }

    @Override
    public void tick(long elapsed) {
        if(isGameFinished) {
            destroy();
            deadPawns.clear();
            return;
        }
        if (deadPawns.size() > 0) {
            handleGameOver(elapsed);
            return;
        }
        // Plus 15 extra milliseconds for the method to finish its work before the tick ends
        long tickStartTime = System.currentTimeMillis()+26;

        List<Message> inputQueue = gameSession.getInputQueue();
        updatePlantedBombsTimers(elapsed);

        System.out.println("Inputqueue size: " + inputQueue.size());

        for (Message message : inputQueue) {
            if(hasAllPawnHaveDoneAMoveForThisTick() || hasTickTimeRanOut(tickStartTime, elapsed, inputQueue, inputQueue.indexOf(message))) {
                System.out.println("Breaking out from tick()");
                break;
            }

            Pawn pawn = pawns.stream().filter(pawn1 -> pawn1.getPlayerName().equals(message.getPlayerName())).findFirst().get();

            //if(pawn == pawns.get(0)) {continue;}

            if (message.getTopic() == Topic.PLANT_BOMB) {
                setUpBomb(pawn.getBomb(), pawn, elapsed);
                continue;
            }

            Position pawnPosition = pawn.getPosition();
            String direction;
            String messageData = message.getData();
            direction = messageData.substring(messageData.indexOf(":") + 2, messageData.indexOf("}") - 1);

            //if a pawn already made a move during this tick - skip the rest of redundant MOVE commands
            System.out.println("Direction: " + direction);
            System.out.println(pawn.movedPerTickX + " " + direction.equals("LEFT") + " " + direction.equals("RIGHT"));
            System.out.println(pawn.movedPerTickY + " " + direction.equals("UP") + " " + direction.equals("DOWN"));
            if ((pawn.movedPerTickY && (direction.equals("UP") || direction.equals("DOWN"))) ||
                    (pawn.movedPerTickX && (direction.equals("LEFT") || direction.equals("RIGHT")))) {
                System.out.println("skip");
                continue;
            }

            System.out.println("\t" + pawn);

            double newX = pawnPosition.getX();
            double newY = pawnPosition.getY();

            switch (direction) {
                case "UP":
                    newY += pawnStepSize;
                    if (checkIfPawnDidntStuck(newX, newY, pawn)) {
                        pawnPosition.setY(newY);

                        pawn.movedPerTickY = true;
                        //System.out.println("\t" + pawn + ": " + direction);
                    }
                    break;
                case "DOWN":
                    newY -= pawnStepSize;
                    if (checkIfPawnDidntStuck(newX, newY, pawn)) {
                        pawnPosition.setY(newY);

                        pawn.movedPerTickY = true;
                        //System.out.println("\t" + pawn + ": " + direction);
                    }
                    break;
                case "LEFT":
                    newX -= pawnStepSize;
                    if (checkIfPawnDidntStuck(newX, newY, pawn)) {
                        pawnPosition.setX(newX);

                        pawn.movedPerTickX = true;
                        //System.out.println("\t" + pawn + ": " + direction);
                    }
                    break;
                case "RIGHT":
                    newX += pawnStepSize;
                    if (checkIfPawnDidntStuck(newX, newY, pawn)) {
                        pawnPosition.setX(newX);

                        pawn.movedPerTickX = true;
                        //System.out.println("\t" + pawn + ": " + direction);
                    }
                    break;
            }
            pawn.setDirection(direction);
        }

        //pawnRobot();

        replica.writeReplica(pawns, bombs, fires, destroyedWoods, Topic.REPLICA);
        cleanAndPrepareForTheNextTick();
    }

    private void cleanAndPrepareForTheNextTick() {
        destroyedWoods.clear();
        fires.clear();

        //reinitialize variables for the next tick
        for (Pawn p : pawns) {
            p.movedPerTickY = false;
            p.movedPerTickX = false;
        }
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
    }

    private void checkWhetherFireKillsPawn() {
        Position bombCenterExplosion = firesLeft.get(firesLeft.size()-1).getPosition();
        Pawn pawnClosestToExplosion = null;
        ArrayList<Pawn> sortedPawns = new ArrayList<>();
        double x_difference=1000;
        double y_difference=1000;
        //find the pawn which is closest to the bomb, since it will be first to die
        for(Pawn pawn: pawns) {
            Position pawnPos = pawn.getPosition();
            double x_distance_temp = Math.abs(bombCenterExplosion.getX() - pawnPos.getX());
            double y_distance_temp = Math.abs(bombCenterExplosion.getY() - pawnPos.getY());
            if(x_distance_temp < x_difference || y_distance_temp < y_difference) {
                x_difference = x_distance_temp;
                y_difference = y_distance_temp;
                pawnClosestToExplosion = pawn;
            }
        }

        sortedPawns.add(pawnClosestToExplosion);
        for(Pawn p: pawns) {
            if(p != pawnClosestToExplosion){
                sortedPawns.add(p);
            }
        }


            //check pawns
        for(Pawn pawn: sortedPawns) {
            for(Fire fire: firesLeft) {
                if(doOverlap(fire.getTopLeftPoint(), fire.getBottomRightPoint(), pawn.getTopLeftPoint(), pawn.getBottomRightPoint())) {
                    pawn.setAlive(false);
                    deadPawns.add(pawn);
                    break;
                }
            }
            //if(firstDeadPawn != null) {break;}
        }
    }

    private void leaveFireWhichDontStuckWithWalls(Position explosionPosition) {
        double x = explosionPosition.getX();
        double y = explosionPosition.getY();

        firesDefaultPositions.add(new Position(x,y+TILE_SIZE*2));
        firesDefaultPositions.add(new Position(x+TILE_SIZE*2,y));
        firesDefaultPositions.add(new Position(x,y-TILE_SIZE*2));
        firesDefaultPositions.add(new Position(x-TILE_SIZE*2,y));
        firesDefaultPositions.add(new Position(x,y+TILE_SIZE));
        firesDefaultPositions.add(new Position(x+TILE_SIZE,y));
        firesDefaultPositions.add(new Position(x,y-TILE_SIZE));
        firesDefaultPositions.add(new Position(x-TILE_SIZE,y));
        firesDefaultPositions.add(new Position(x,y));

        for(int i = 0; i < firesDefaultPositions.size(); i++) {
            boolean doOverlap = false;
            Position firePosition = firesDefaultPositions.get(i);
            if(i==8) {
                // explosion in the center will always happen - no need to check & remove it
                firesLeft.add(new Fire(firesLeft.size(), firePosition));
                continue;
            }
            for(Wall wall: walls) {
                if(i < 4) {
                    Position initialFirePosition = firesDefaultPositions.get(i+4);
                    if(doOverlap(firePosition.getTopLeftPoint(), firePosition.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint()) ||
                            doOverlap(initialFirePosition.getTopLeftPoint(), initialFirePosition.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint())) {
                        doOverlap=true;
                        break;
                    }
                }
                else {
                    if(doOverlap(firePosition.getTopLeftPoint(), firePosition.getBottomRightPoint(), wall.getTopLeftPoint(), wall.getBottomRightPoint())) {
                        doOverlap=true;
                        break;
                    }
                }
            }
            if(!doOverlap){
                firesLeft.add(new Fire(firesLeft.size(), firePosition));
            }
        }
    }

    private void leaveFireWhichDontStuckWithWoods() {
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
    }


    private void handleFires(Position explosionPosition){
        leaveFireWhichDontStuckWithWalls(explosionPosition);
        leaveFireWhichDontStuckWithWoods();
        checkWhetherFireKillsPawn();

        fires.addAll(firesLeft);
        firesLeft.clear();
        firesDefaultPositions.clear();
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


