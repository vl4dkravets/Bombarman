package org.bombermen.game;

import org.bombermen.gameElements.*;
import org.bombermen.message.Topic;
import org.bombermen.replicas.Replica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
class GameMechanicsTest {
    @Mock
    private GameSession gameSession;
    @Mock
    private Replica replica;
    private GameMechanics gameMechanics;

    private ArrayList<Player> players;


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
        long FRAME_TIME = 1000/60;
        long bombTimer = 10_000;
        int gameEndingPause = 3000;

        long tickStartTime = System.currentTimeMillis();
        gameMechanics.tick(FRAME_TIME);
        // tick() finishes in one tick frame
        assertTrue(System.currentTimeMillis()-tickStartTime<FRAME_TIME);
        //isGameFinished wasn't touched - meaning the game isn't over
        assertFalse(gameMechanics.isGameFinished());
        //nobody died
        assertEquals(0, gameMechanics.getDeadPawns().size());
        //gameEndingPause wasn't decremented - meaning the game isn't over
        assertEquals(gameEndingPause, gameMechanics.getGAME_END_PAUSE());
        //there was no bombs planted yet
        assertEquals(2, gameMechanics.getBombs().size());
        //bombs timers updated/decremented per one tick
        assertEquals(bombTimer-FRAME_TIME, gameMechanics.getBombs().get(0).getBombTimer());
        assertEquals(bombTimer-FRAME_TIME, gameMechanics.getBombs().get(1).getBombTimer());

        //there was not messages - so no movement
        gameMechanics.getPawns().forEach(pawn -> {
            assertFalse(pawn.movedPerTickX);
            assertFalse(pawn.movedPerTickY);
        });

        //check there was no movement among pawns - positions should be original
        assertEquals(gameMechanics.getPawns().get(0).getPosition(), new Position(gameMechanics.getTILE_SIZE(), gameMechanics.getTILE_SIZE()));
        assertEquals(gameMechanics.getPawns().get(1).getPosition(), new Position(gameMechanics.getGAME_FIELD_W()-gameMechanics.getTILE_SIZE(), gameMechanics.getGAME_FIELD_H()-gameMechanics.getTILE_SIZE()));

        assertEquals(0, gameMechanics.getDestroyedWoods().size());
        assertEquals(0, gameMechanics.getFires().size());
    }

    @Test
    void tickTestMoveMessage_Stuck() {

    }

    @Test
    void tickTestMoveMessage_DoNotStuck() {

    }

    @Test
    void tickTestPlantBombMessage() {

    }

    @Test
    void tickTestBombExlodes() {

    }

    @Test
    void tickTestGameOver() {

    }
}