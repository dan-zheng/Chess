package edu.xwei12.chess;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by xinranmsn on 2/4/16.
 */
public class GameTest extends TestCase implements GameObserver<RectangleBoard, RectanglePosition> {
    StandardGame game;

    @Before
    public void setUp() throws Exception {

        game = new StandardGame();
        game.setObserver(this);
    }

    @Test
    public void testStepWithMove() throws Exception {
        boolean success;
        // Move P1.Knight as P2
        success = game.stepWithMove(StandardGame.PLAYER_B, 0, 1, 2, 0);
        assertFalse(success);
        assertEquals(Game.State.NORMAL, game.getState());

        // Move P1.Knight as P1
        success = game.stepWithMove(StandardGame.PLAYER_A, 0, 1, 2, 0);
        assertTrue(success);
        assertEquals(Game.State.NORMAL, game.getState());

        // Move P2.Knight as P1
        success = game.stepWithMove(StandardGame.PLAYER_A, 7, 6, 5, 5);
        assertFalse(success);
        assertEquals(Game.State.NORMAL, game.getState());

        // Move P2.Knight as P2
        success = game.stepWithMove(StandardGame.PLAYER_B, 7, 6, 5, 5);
        assertTrue(success);
        assertEquals(Game.State.NORMAL, game.getState());

        // Move empty cell
        success = game.stepWithMove(StandardGame.PLAYER_A, 4, 4, 5, 4);
        assertFalse(success);
    }

    @Test
    public void testAttack() throws Exception {
        boolean success;

        RectangleBoard board = game.getBoard();

        // P1.Pawn attacks P1.Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 1, 5, 3, 5);
        game.stepWithMove(StandardGame.PLAYER_A, 3, 5, 4, 5);
        game.stepWithMove(StandardGame.PLAYER_A, 4, 5, 5, 5);
        game.stepWithMove(StandardGame.PLAYER_A, 5, 5, 6, 5);
        assertEquals(Game.State.NORMAL, game.getState());
        Piece p = board.getPiece(6, 5);
        assertTrue(p.getKind().equals(DefaultPiece.PAWN.getKind()) && p.getTag() == StandardGame.PLAYER_A);
        assertEquals(15, board.getPiecesByKind(DefaultPiece.PAWN.getKind()).size());

    }

    @Test
    public void testCheckmate() throws Exception {
        boolean success;

        // P1.Pawn checkmates P2.King
        game.stepWithMove(StandardGame.PLAYER_A, 1, 4, 3, 4);
        game.stepWithMove(StandardGame.PLAYER_A, 3, 4, 4, 4);
        game.stepWithMove(StandardGame.PLAYER_A, 4, 4, 5, 4);
        game.stepWithMove(StandardGame.PLAYER_A, 5, 4, 6, 4);
        assertEquals(Game.State.CHECKMATE, game.getState());

        // P2.Knight checkmates P1.King
        game.stepWithMove(StandardGame.PLAYER_B, 7, 1, 5, 2);
        game.stepWithMove(StandardGame.PLAYER_B, 5, 2, 4, 4);
        game.stepWithMove(StandardGame.PLAYER_B, 6, 1, 5, 1);
        assertEquals(Game.State.CHECKMATE, game.getState());

        // Test move after checkmate
        success = game.stepWithMove(StandardGame.PLAYER_B, 4, 4, 2, 3);
        assertFalse(success);
        assertEquals(Game.State.CHECKMATE, game.getState());
    }

    @Override
    public void onChessGameStateUpdate(Game<RectangleBoard, RectanglePosition> game, Game<RectangleBoard, RectanglePosition>.Move move) {
        game.getBoard().print();
        if (game.getState() == Game.State.CHECKMATE)
            System.out.println("Checkmate!");
    }
}