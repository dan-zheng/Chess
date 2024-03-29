package edu.xwei12.chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Standard rectangle game
 * @author Xinran Wei
 */
public class StandardGame extends Game<RectangleBoard, RectanglePosition> {

    public static final int PLAYER_A = 1;
    public static final int PLAYER_B = -1;
    public static final int PLAYER_DEFAULT = 0;

    private static Set<Integer> defaultPlayers = new HashSet<>(Arrays.asList(PLAYER_A, PLAYER_B));

    public StandardGame() {
        // Initialize a 8x8 board
        super(new RectangleBoard(8, 8), DefaultPiece.KING.getKind(), defaultPlayers);
    }

    @Override
    protected void initialize() {
        // PAWN
        for (int i = 0; i < 8; i++) {
            board.addPiece(DefaultPiece.PAWN.newPieceWithTag(PLAYER_A), new RectanglePosition(1, i));
        }
        for (int i = 0; i < 8; i++) {
            board.addPiece(DefaultPiece.PAWN.newPieceWithTag(PLAYER_B), new RectanglePosition(6, i));
        }
        // KNIGHT
        board.addPiece(DefaultPiece.KNIGHT.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 1));
        board.addPiece(DefaultPiece.KNIGHT.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 6));
        board.addPiece(DefaultPiece.KNIGHT.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 1));
        board.addPiece(DefaultPiece.KNIGHT.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 6));

        // QUEEN
        board.addPiece(DefaultPiece.QUEEN.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 3));
        board.addPiece(DefaultPiece.QUEEN.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 3));

        // KING
        board.addPiece(DefaultPiece.KING.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 4));
        board.addPiece(DefaultPiece.KING.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 4));

        // BISHOP
        board.addPiece(DefaultPiece.BISHOP.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 2));
        board.addPiece(DefaultPiece.BISHOP.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 5));
        board.addPiece(DefaultPiece.BISHOP.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 2));
        board.addPiece(DefaultPiece.BISHOP.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 5));

        // ROOK
        board.addPiece(DefaultPiece.ROOK.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 0));
        board.addPiece(DefaultPiece.ROOK.newPieceWithTag(PLAYER_A), new RectanglePosition(0, 7));
        board.addPiece(DefaultPiece.ROOK.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 0));
        board.addPiece(DefaultPiece.ROOK.newPieceWithTag(PLAYER_B), new RectanglePosition(7, 7));
    }

    /**
     * Stepping helper with rectangle coordinates
     * @param player player tag
     * @param fromX source rank-coordinate
     * @param fromY source file-coordinate
     * @param toX destination rank-coordinate
     * @param toY destination file-coordinate
     * @return moved
     */
    public boolean stepWithMove(Integer player, int fromX, int fromY, int toX, int toY) {
        return stepWithMove(player, new RectanglePosition(fromX, fromY), new RectanglePosition(toX, toY));
    }

    /**
     * Update player turn
     */
    @Override
    public void updateTurn() {
        Integer lastPlayer = getLastMove().player;
        setPlayerTurn(-lastPlayer);
    }

}
