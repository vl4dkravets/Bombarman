package org.bombermen.game;

import org.bombermen.gameElements.*;
import org.bombermen.message.Message;
import org.bombermen.message.Topic;
import org.bombermen.replicas.Replica;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class GameMechanicsTest {
    @Mock
    private GameSession gameSession;
    @Mock
    private Replica replica;
    private GameMechanics gameMechanics;

    private ArrayList<Player> players;

    private long FRAME_TIME;
    private int bombTimer;
    private int gameEndingPause;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        players = new ArrayList<>();
        players.add(new Player("player0"));
        players.add(new Player("player1"));
        doNothing().when(replica).writeReplicaToInitializeGameField(new ArrayList<Pawn>(),new ArrayList<Bomb>(),new ArrayList<Wood>(),new ArrayList<Wall>(), Topic.START);
        when(gameSession.getPlayersAsIterator()).thenReturn(players.iterator());
        when(gameSession.getMAX_N_OF_PLAYERS()).thenReturn(2);
        gameMechanics = new GameMechanics(gameSession, replica);
        FRAME_TIME = 1000/60;
        bombTimer = 10_000;
        gameEndingPause = 3000;
    }

    @Test
    void gameMechanicsInitializationTest() {
        assertNotNull(gameMechanics.getPawns());
        assertNotNull(gameMechanics.getBombs());
        assertNotNull(gameMechanics.getFires());
        assertNotNull(gameMechanics.getWalls());
        assertNotNull(gameMechanics.getWoods());
        assertNotNull(gameMechanics.getFiresDefaultPositions());
        assertNotNull(gameMechanics.getFiresLeft());
        assertNotNull(gameMechanics.getDestroyedWoods());
        assertNotNull(gameMechanics.getDeadPawns());
        assertEquals(32, gameMechanics.getTILE_SIZE());
        assertEquals(847 - 15, gameMechanics.getGAME_FIELD_W());
        assertEquals(527 - 15, gameMechanics.getGAME_FIELD_H());
        assertEquals(2, gameMechanics.getPawnStepSize());
        assertEquals(3000, gameMechanics.getGAME_END_PAUSE());
        assertFalse(gameMechanics.isGameFinished());
        assertEquals(32, gameMechanics.getTILE_SIZE());
        assertEquals(gameSession.getMAX_N_OF_PLAYERS(), gameMechanics.getnOfPawns());
    }

    @Test
    void createWallsAndWoodsTest() {
        assertNotEquals(0, gameMechanics.getWoods().size());
        assertNotEquals(0, gameMechanics.getWalls().size());

        Position position1 = new Position(0,0);
        Position position2 = new Position(0,4*gameMechanics.getTILE_SIZE());
        Position position3 = new Position(0,gameMechanics.getGAME_FIELD_H());
        Position position4 = new Position(4*gameMechanics.getTILE_SIZE(),0);
        Position position5 = new Position(4*gameMechanics.getTILE_SIZE(), gameMechanics.getGAME_FIELD_H());
        Position position6 = new Position(gameMechanics.getGAME_FIELD_W(), 0);
        Position position7 = new Position(gameMechanics.getGAME_FIELD_W(), gameMechanics.getGAME_FIELD_H());
        Position position8 = new Position(gameMechanics.getGAME_FIELD_W(), 4*gameMechanics.getTILE_SIZE());
        Position position9 = new Position(gameMechanics.getTILE_SIZE(), gameMechanics.getTILE_SIZE());


        Wall wall1 = new Wall(1,position1);
        Wall wall2 = new Wall(2,position2);
        Wall wall3 = new Wall(3,position3);
        Wall wall4 = new Wall(4,position4);
        Wall wall5 = new Wall(5,position5);
        Wall wall6 = new Wall(6,position6);
        Wall wall7 = new Wall(7,position7);
        Wall wall8 = new Wall(8,position8);
        Wall notWall = new Wall(9,position9);

        Wood wood1 = new Wood(1,position1);
        Wood wood2 = new Wood(2,position2);
        Wood wood3 = new Wood(3,position3);
        Wood wood4 = new Wood(4,position4);
        Wood wood5 = new Wood(5,position5);
        Wood wood6 = new Wood(6,position6);
        Wood wood7 = new Wood(7,position7);
        Wood wood8 = new Wood(8,position8);
        Wood notWood = new Wood(9,position9);

        assertTrue(gameMechanics.getWalls().contains(wall1));
        assertTrue(gameMechanics.getWalls().contains(wall2));
        assertTrue(gameMechanics.getWalls().contains(wall3));
        assertTrue(gameMechanics.getWalls().contains(wall4));
        assertTrue(gameMechanics.getWalls().contains(wall5));
        assertTrue(gameMechanics.getWalls().contains(wall6));
        assertTrue(gameMechanics.getWalls().contains(wall7));
        assertTrue(gameMechanics.getWalls().contains(wall8));
        assertFalse(gameMechanics.getWalls().contains(notWall));

        assertFalse(gameMechanics.getWoods().contains(wood1));
        assertFalse(gameMechanics.getWoods().contains(wood2));
        assertFalse(gameMechanics.getWoods().contains(wood3));
        assertFalse(gameMechanics.getWoods().contains(wood4));
        assertFalse(gameMechanics.getWoods().contains(wood5));
        assertFalse(gameMechanics.getWoods().contains(wood6));
        assertFalse(gameMechanics.getWoods().contains(wood7));
        assertFalse(gameMechanics.getWoods().contains(wood8));
        assertFalse(gameMechanics.getWoods().contains(notWood));
        assertFalse(gameMechanics.getWoods().contains(new Wood(10, new Position(gameMechanics.getGAME_FIELD_W()-gameMechanics.getTILE_SIZE(),gameMechanics.getGAME_FIELD_H()-gameMechanics.getTILE_SIZE() ))));
    }

    @Test
    void createPawnsAndBombsTest() {
        ArrayList<Pawn> pawns = gameMechanics.getPawns();
        for(int i = 0; i < players.size(); i++) {
            Pawn pawn = pawns.get(i);
            assertEquals(players.get(i).getName(), pawn.getPlayerName());
            assertEquals("Pawn_"+i, pawn.toString());
            assertEquals("Pawn_"+i, pawn.toString());
            assertEquals(pawn.getBomb().getPosition(), pawn.getPosition());
            assertTrue(pawn.isAlive());
            assertEquals(10_000, pawn.getBomb().getBombTimer());
            assertEquals(i,pawn.getId());
            if(i == 0) {
                assertEquals(new Position(gameMechanics.getTILE_SIZE(),gameMechanics.getTILE_SIZE() ), pawn.getPosition());
            }
            else if(i == 1) {
                assertEquals(new Position(gameMechanics.getGAME_FIELD_W() - gameMechanics.getTILE_SIZE(),gameMechanics.getGAME_FIELD_H() - gameMechanics.getTILE_SIZE() ), pawn.getPosition());
            }
        }

        assertEquals(pawns.size(), gameMechanics.getBombs().size());
    }

    @Test
    void tickTest() {
        long tickStartTime = System.currentTimeMillis();
        gameMechanics.tick(FRAME_TIME);
        bombDidNotExplodeCheck(tickStartTime);
        //there was no bombs planted yet
        assertEquals(2, gameMechanics.getBombs().size());

        //there was not messages - so no movement
        gameMechanics.getPawns().forEach(pawn -> {
            assertFalse(pawn.isMovedPerTickX());
            assertFalse(pawn.isMovedPerTickY());
        });

        //check there was no movement among pawns - positions should be original
        assertEquals(gameMechanics.getPawns().get(0).getPosition(), new Position(gameMechanics.getTILE_SIZE(), gameMechanics.getTILE_SIZE()));
        assertEquals(gameMechanics.getPawns().get(1).getPosition(), new Position(gameMechanics.getGAME_FIELD_W()-gameMechanics.getTILE_SIZE(), gameMechanics.getGAME_FIELD_H()-gameMechanics.getTILE_SIZE()));

        cleanAndPrepareForTheNextTickTest();
    }

    @Test
    void tickTestMoveMessage_Stuck() throws InterruptedException {
        //create & push a message
        Topic topic = Topic.MOVE;
        String data = "{\"direction\":\"LEFT\"}";
        String playerName = players.get(0).getName();
        Message message = new Message(topic, data);
        message.setPlayerName(playerName);
        //gameSession.pushMessage(message);
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        when(gameSession.getInputQueue()).thenReturn(messages);

        long tickStartTime = System.currentTimeMillis();

        gameMechanics.tick(FRAME_TIME);
        bombDidNotExplodeCheck(tickStartTime);

        ArrayList<Pawn> pawns = gameMechanics.getPawns();
        //there was no bombs planted yet
        assertEquals(2, gameMechanics.getBombs().size());
        Pawn pawn = pawns.get(0);
        //pawn stuck & didn't move
        assertNotEquals(new Position(gameMechanics.getTILE_SIZE()-gameMechanics.getPawnStepSize(), gameMechanics.getTILE_SIZE()), pawn.getPosition());
        assertEquals(new Position(gameMechanics.getTILE_SIZE(), gameMechanics.getTILE_SIZE()), pawn.getPosition());
        assertFalse(pawn.isMovedPerTickX());
        assertEquals("LEFT", pawn.getDirection());
        cleanAndPrepareForTheNextTickTest();
    }

    @Test
    void tickTestMoveMessage_DoNotStuck() throws InterruptedException {
        //create & push a message
        Topic topic = Topic.MOVE;
        String data = "{\"direction\":\"RIGHT\"}";
        String playerName = players.get(0).getName();
        Message message = new Message(topic, data);
        message.setPlayerName(playerName);
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        when(gameSession.getInputQueue()).thenReturn(messages);

        long tickStartTime = System.currentTimeMillis();
        gameMechanics.tick(FRAME_TIME);
        bombDidNotExplodeCheck(tickStartTime);

        ArrayList<Pawn> pawns = gameMechanics.getPawns();
        Pawn pawn = pawns.get(0);
        //there was no bombs planted yet
        assertEquals(2, gameMechanics.getBombs().size());
        //pawn stuck & didn't move
        assertEquals(new Position(gameMechanics.getTILE_SIZE()+gameMechanics.getPawnStepSize(), gameMechanics.getTILE_SIZE()), pawn.getPosition());
        assertEquals("RIGHT", pawn.getDirection());
        cleanAndPrepareForTheNextTickTest();
    }

    @Test
    void tickTestPlantBombMessage() {
        //create & push a message
        Topic topic = Topic.PLANT_BOMB;
        String playerName = players.get(0).getName();
        Message message = new Message(topic, "");
        message.setPlayerName(playerName);
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        when(gameSession.getInputQueue()).thenReturn(messages);

        long tickStartTime = System.currentTimeMillis();
        gameMechanics.tick(FRAME_TIME);
        bombDidNotExplodeCheck(tickStartTime);

        ArrayList<Bomb> bombs = gameMechanics.getBombs();
        Bomb newBomb = bombs.get(2);
        assertEquals(3, bombs.size());
        assertNotSame(newBomb.getPosition().hashCode(), gameMechanics.getPawns().get(0).getPosition().hashCode());
        assertEquals(bombTimer-FRAME_TIME, newBomb.getBombTimer());

        cleanAndPrepareForTheNextTickTest();
    }

    @Test
    void tickTestBombExplodesAfter10SecondsAndKillsAllPawns() {
        //before
        assertEquals(2, gameMechanics.getBombs().size());
        ArrayList<Pawn> pawns = gameMechanics.getPawns();
        assertEquals(2, pawns.size());
        assertTrue(pawns.get(0).isAlive());
        assertTrue(pawns.get(1).isAlive());


        long counter = 0;
        while(counter <= bombTimer) {
            gameMechanics.tick(FRAME_TIME);
            counter+=FRAME_TIME;
        }
        // after 10 sec
        assertEquals(0, gameMechanics.getBombs().size());
        assertFalse(pawns.get(0).isAlive());
        assertFalse(pawns.get(1).isAlive());
        assertEquals(pawns, gameMechanics.getDeadPawns());
    }



    @Test
    void tickTestGameOver() {
        ArrayList<Pawn> pawns = gameMechanics.getPawns();
        ArrayList<Pawn> deadPawns = gameMechanics.getDeadPawns();

        //before
        assertEquals(2, pawns.size());
        assertEquals(0, deadPawns.size());

        deadPawns.add(pawns.get(0));

        while(gameMechanics.getGAME_END_PAUSE() >= 0) {
            gameMechanics.tick(FRAME_TIME);
        }

        assertTrue(gameMechanics.isGameFinished());

    }

    @Test
    void tickDestroy() {
        tickTestGameOver();
        gameMechanics.tick(FRAME_TIME);
        assertNull(gameMechanics.getDeadPawns());
        assertNull(gameMechanics.getPawns());
        assertNull(gameMechanics.getBombs());
        assertNull(gameMechanics.getFires());
        assertNull(gameMechanics.getFiresLeft());
        assertNull(gameMechanics.getWalls());
        assertNull(gameMechanics.getWoods());
        assertNull(gameMechanics.getFiresLeft());
        assertNull(gameMechanics.getDestroyedWoods());
        assertNull(gameMechanics.getFiresDefaultPositions());
    }

    @Test
    void frameTimeNotExceeded() {
        // The following assertion succeeds.
        assertTimeout(Duration.ofMillis(FRAME_TIME), () -> {
            // Perform task that takes less than 1000/60 milliseconds
            gameMechanics.tick(FRAME_TIME);
        });
    }

    private void bombDidNotExplodeCheck(long tickStartTime) {
        assertTrue(System.currentTimeMillis()-tickStartTime<FRAME_TIME);
        //isGameFinished wasn't touched - meaning the game isn't over
        assertFalse(gameMechanics.isGameFinished());
        //nobody died
        assertEquals(0, gameMechanics.getDeadPawns().size());
        //gameEndingPause wasn't decremented - meaning the game isn't over
        assertEquals(gameEndingPause, gameMechanics.getGAME_END_PAUSE());

        //bombs timers updated/decremented per one tick
        assertEquals(bombTimer-FRAME_TIME, gameMechanics.getBombs().get(0).getBombTimer());
        assertEquals(bombTimer-FRAME_TIME, gameMechanics.getBombs().get(1).getBombTimer());
    }

    private void cleanAndPrepareForTheNextTickTest() {
        assertEquals(0, gameMechanics.getDestroyedWoods().size());
        assertEquals(0, gameMechanics.getFires().size());
        gameMechanics.getPawns().forEach(pawn -> {
            assertFalse(pawn.isMovedPerTickX());
            assertFalse(pawn.isMovedPerTickY());
        });
    }

    @AfterEach
    void tearDown() {
        players.clear();
        players = null;
        gameMechanics = null;
    }
}