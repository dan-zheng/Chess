package edu.xwei12.chess;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * Created by xinranmsn on 2/4/16.
 */
public class PieceTest {

    StandardGame game;

    @Before
    public void setUp() throws Exception {
        game = new StandardGame();
        game.getBoard().print();
    }


    /** Default piece tests **/

    @Test
    public void testPawn() throws Exception {
        // Get a move function
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = DefaultPiece.PAWN.getMover();
        Set<RectanglePosition> moves;
        RectanglePosition pos;

        /* Player 1 */
        // Move one step
        moves = mover.apply(new RectanglePosition(1, 3), game.getBoard(), 1);
        Assert.assertEquals(moves.size(), 1);
        pos = moves.toArray(new RectanglePosition[1])[0];
        Assert.assertEquals(pos.rank, 2);
        Assert.assertEquals(pos.file, 3);
        // Move two steps
        moves = mover.apply(new RectanglePosition(1, 3), game.getBoard(), 2);
        Assert.assertEquals(moves.size(), 1);
        pos = moves.toArray(new RectanglePosition[1])[0];
        Assert.assertEquals(pos.rank, 3);
        Assert.assertEquals(pos.file, 3);

        /* Player 2 */
        // Move one step
        moves = mover.apply(new RectanglePosition(6, 4), game.getBoard(), 1);
        Assert.assertEquals(moves.size(), 1);
        pos = moves.toArray(new RectanglePosition[1])[0];
        Assert.assertEquals(pos.rank, 5);
        Assert.assertEquals(pos.file, 4);
        // Move two steps
        moves = mover.apply(new RectanglePosition(6, 4), game.getBoard(), 2);
        Assert.assertEquals(moves.size(), 1);
        pos = moves.toArray(new RectanglePosition[1])[0];
        Assert.assertEquals(pos.rank, 4);
        Assert.assertEquals(pos.file, 4);

    }

    @Test
    public void testKnight() throws Exception {
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = DefaultPiece.KNIGHT.getMover();

        Set<RectanglePosition> moves;
        RectanglePosition pos;

        /* Player 1 */
        // Move
        moves = mover.apply(new RectanglePosition(0, 1), game.getBoard(), 1);
        Assert.assertEquals(moves.size(), 2);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 0))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 2))));

        /* Player 2 */
        // Move
        moves = mover.apply(new RectanglePosition(7, 6), game.getBoard(), 1);
        Assert.assertEquals(moves.size(), 2);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(5, 5))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(5, 7))));
    }

    @Test
    public void testRook() throws Exception {
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = DefaultPiece.ROOK.getMover();

        Set<RectanglePosition> moves;
        RectanglePosition pos;

        RectangleBoard board = game.getBoard();

        /* Set up positions for player 1 and 2 */
        game.stepWithMove(StandardGame.PLAYER_A, 1, 7, 3, 7); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 3, 7, 4, 7); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 1, 5, 2, 5); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 0, 7, 2, 7); // Rook
        Assert.assertEquals(board.getPiece(2, 7).getKind(), DefaultPiece.ROOK.getKind());
        game.stepWithMove(StandardGame.PLAYER_B, 6, 0, 4, 0); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 4, 0, 3, 0); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 6, 3, 5, 3); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 7, 0, 5, 0); // Rook
        Assert.assertEquals(board.getPiece(5, 0).getKind(), DefaultPiece.ROOK.getKind());

        // Print board
        board.print();

        /* Player 1 */
        // Move by 1 square
        moves = mover.apply(new RectanglePosition(2, 7), board, 1);
        Assert.assertEquals(moves.size(), 3);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 6))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(1, 7))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 7))));
        // Move by 2 squares
        moves = mover.apply(new RectanglePosition(2, 7), board, 2);
        Assert.assertEquals(moves.size(), 1);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(0, 7))));

        /* Player 2 */
        // Move by 2 square
        moves = mover.apply(new RectanglePosition(5, 0), board, 1);
        Assert.assertEquals(moves.size(), 3);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(5, 1))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(6, 0))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(4, 0))));
        // Move by 2 square
        moves = mover.apply(new RectanglePosition(5, 0), board, 2);
        Assert.assertEquals(moves.size(), 2);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(5, 2))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(7, 0))));
    }

    @Test
    public void testBishop() throws Exception {
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = DefaultPiece.BISHOP.getMover();

        Set<RectanglePosition> moves;
        RectanglePosition pos;

        RectangleBoard board = game.getBoard();

        /* Set up positions for bishop */
        game.stepWithMove(StandardGame.PLAYER_A, 1, 2, 3, 2); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 1, 3, 3, 3); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 0, 2, 2, 4); // Bishop
        Assert.assertEquals(board.getPiece(2, 4).getKind(), DefaultPiece.BISHOP.getKind());
        game.stepWithMove(StandardGame.PLAYER_B, 6, 2, 4, 2); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 6, 3, 4, 3); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 7, 2, 4, 5); // Bishop
        game.stepWithMove(StandardGame.PLAYER_A, 3, 2, 4, 3); // Pawn

        // Print board
        board.print();

        // Move by 1 square
        moves = mover.apply(new RectanglePosition(2, 4), board, 1);
        Assert.assertEquals(2, moves.size());
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(1, 3))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 5))));
        // Move by 2 squares
        moves = mover.apply(new RectanglePosition(2, 4), board, 2);
        Assert.assertEquals(3, moves.size());
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(4, 6))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(0, 2))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(4, 2))));
    }

    @Test
    public void testKing() throws Exception {
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = DefaultPiece.KING.getMover();

        Set<RectanglePosition> moves;
        RectanglePosition pos;

        RectangleBoard board = game.getBoard();

        /* Set up positions for King */
        game.stepWithMove(StandardGame.PLAYER_A, 1, 4, 3, 4); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 3, 4, 4, 4); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 0, 4, 1, 4); // King
        game.stepWithMove(StandardGame.PLAYER_A, 1, 4, 2, 4); // King
        board.print();
        Assert.assertEquals(board.getPiece(2, 4).getKind(), DefaultPiece.KING.getKind());
        game.stepWithMove(StandardGame.PLAYER_B, 6, 3, 5, 3); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 5, 3, 4, 4); // Pawn
        game.stepWithMove(StandardGame.PLAYER_B, 4, 4, 3, 4); // Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 2, 4, 3, 4); // P1.King eats P2.Pawn
        game.stepWithMove(StandardGame.PLAYER_A, 3, 4, 2, 4); // P1.King moves back
        board.print();

        // Print board
        board.print();

        // Move by 1 square
        moves = mover.apply(new RectanglePosition(2, 4), board, 1);
        Assert.assertEquals(moves.size(), 6);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 3))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 5))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(1, 4))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 4))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 3))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 5))));
        // Move by 2 squares
        moves = mover.apply(new RectanglePosition(2, 7), board, 2);
        Assert.assertTrue(moves.isEmpty());
    }

    @Test
    public void testGrasshopper() throws Exception {
        // Configure board
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = ExtendedPiece.GRASSHOPPER.getMover();

        Set<RectanglePosition> moves;
        RectanglePosition pos;

        RectangleBoard board = game.getBoard();

        board.addPiece(ExtendedPiece.GRASSHOPPER.newPieceWithTag(StandardGame.PLAYER_B), new RectanglePosition(3, 3));

        game.stepWithMove(StandardGame.PLAYER_A, 1, 5, 3, 5); // Pawn

        // Print board
        board.print();

        // Move by 1 square
        moves = mover.apply(new RectanglePosition(3, 3), board, 1);
        Assert.assertEquals(moves.size(), 0);
        // Move by 2 square
        moves = mover.apply(new RectanglePosition(3, 3), board, 2);
        Assert.assertEquals(moves.size(), 0);
        // Move by 3 square
        moves = mover.apply(new RectanglePosition(3, 3), board, 3);
        Assert.assertEquals(moves.size(), 2);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(3, 6))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(0, 3)))); // Attacks the Queen

    }


    @Test
    public void testBerolina() throws Exception {
        // Get a move function
        Piece.MoveFunction<RectangleBoard, RectanglePosition> mover = ExtendedPiece.BEROLINA.getMover();
        Set<RectanglePosition> moves;
        RectanglePosition pos;
        RectangleBoard board = game.getBoard();

        board.addPiece(ExtendedPiece.BEROLINA.newPieceWithTag(StandardGame.PLAYER_B), new RectanglePosition(3, 3));

        // Move one step
        moves = mover.apply(new RectanglePosition(3, 3), game.getBoard(), 1);
        Assert.assertEquals(moves.size(), 2);
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 4))));
        Assert.assertTrue(moves.stream().anyMatch(x -> x.sameAs(new RectanglePosition(2, 2))));

    }
}