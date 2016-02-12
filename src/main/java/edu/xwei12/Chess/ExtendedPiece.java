package edu.xwei12.chess;

import java.util.HashSet;
import java.util.Set;

/**
 * Extension to default pieces for rectangle board and rectangle coordinates
 * @author Xinran Wei
 */
public enum ExtendedPiece {

    /**
     * Move behavior for Berolina Chess as a lambda function
     * (position, board, distance) -> positionSet
     */
    BEROLINA ((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        int ranks = board.getRanks();
        int playerDirection = board.getPiece(x, y).getTag();
        Set<RectanglePosition> positionSet = new HashSet<>();

        /* If at initial rank, advance by 2 along the file */
        if (distance == 1 || (distance == 2 && (x == 1 || x == ranks - 2))) {

            // Does not attack diagonally
            RectanglePosition diagonal1 = new RectanglePosition(x + playerDirection, y + 1);
            if (board.isValidPosition(diagonal1) && !board.pieceExists(diagonal1)) {
                positionSet.add(diagonal1);
            }
            RectanglePosition diagonal2 = new RectanglePosition(x + playerDirection, y - 1);
            if (board.isValidPosition(diagonal2) && !board.pieceExists(diagonal2)) {
                positionSet.add(diagonal2);
            }

            // Attacks forwards
            RectanglePosition forward = new RectanglePosition(x + distance * playerDirection, y);
            if (distance == 1 && board.isValidPosition(forward) && board.pieceExists(forward)
                              && !board.getPiece(forward).getTag().equals(board.getPiece(position).getTag())) {
                positionSet.add(forward);
            }
        }

        return positionSet;
    }),

    /**
     * Move behavior for Grasshopper as a lambda function
     * (position, board, distance) -> positionSet
     */
    GRASSHOPPER ((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;

        Set<RectanglePosition> positionSet = new HashSet<RectanglePosition>() {{
            add(new RectanglePosition(x + distance, y));
            add(new RectanglePosition(x, y + distance));
            add(new RectanglePosition(x - distance, y));
            add(new RectanglePosition(x, y - distance));
            add(new RectanglePosition(x + distance, y + distance));
            add(new RectanglePosition(x - distance, x - distance));
            add(new RectanglePosition(x - distance, x + distance));
            add(new RectanglePosition(x + distance, x - distance));
        }};

        // Remove destinations that don't require leaps
        positionSet.removeIf(dest -> board.findNearestLeap(position, dest) == null ||
                board.findNearestLeap(position, dest).distanceTo(dest) > 1);
        // Remove out-of-board position
        positionSet.removeIf(dest -> !board.isValidPosition(dest));
        // Remove cannibalism
        positionSet.removeIf(dest -> board.pieceExists(dest) &&
                                      board.getPiece(dest).getTag().equals(board.getPiece(position).getTag()));

        return positionSet;
    }),


    ;

    /** Chess piece **/
    private Piece.MoveFunction<RectangleBoard, RectanglePosition> mover;

    /**
     * Constructor, unused but required
     * @param mover mover function
     */
    ExtendedPiece(Piece.MoveFunction<RectangleBoard, RectanglePosition> mover) {
        this.mover = mover;
    }

    /**
     * Get kind identifier
     * @return lowercase of enum name as identifier
     */
    public String getKind() {
        return name().toLowerCase();
    }

    /**
     * Create a new piece with self behavior (don't forget the enum value is a lambda!)
     * @param tag tag that can be used as player tag
     * @return piece
     */
    public Piece<RectangleBoard, RectanglePosition> newPieceWithTag(int tag) {
        return new Piece<>(name().toLowerCase(), tag, mover);
    }

    /**
     * Get mover function
     * @return mover :: (position, board, distance) -> positionSet
     */
    public Piece.MoveFunction<RectangleBoard, RectanglePosition> getMover() {
        return mover;
    }
}
