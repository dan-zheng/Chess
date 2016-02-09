package edu.xwei12.Chess;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xinranmsn on 1/30/16.
 */
public enum DefaultPiece {

    /**
     * Move behavior for King
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the King's possible behaviors (positionSet)
     *     |                   from (position, board, distance)
     */
    KING((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        Set<RectanglePosition> positionSet = new HashSet<>();

        // Reject movement with distance != 1
        if (distance != 1) return positionSet;

        // Add to set
        addIfCellValid(position, new RectanglePosition(x, y + 1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x, y - 1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - 1, y), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x + 1, y), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - 1, y + 1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x + 1, y + 1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - 1, y - 1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x + 1, y - 1), board, positionSet);

        return positionSet;
    }),

    /**
     * Move behavior for Rook
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the Rook's possible behaviors (positionSet)
     *     |                   from (position, board, distance)
     */
    ROOK((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        Set<RectanglePosition> positionSet = new HashSet<>();

        // Reject movement with distance < 1
        if (distance < 1) return positionSet;

        // Moves straight
        addIfCellValid(position, new RectanglePosition(x, y + distance), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x, y - distance), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x + distance, y), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - distance, y), board, positionSet);

        // Remove leaps
        positionSet.removeIf(p -> board.needsLeap(position, p));

        return positionSet;
    }),

    /**
     * Move behavior for Bishop
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the Bishop's possible behaviors (positionSet)
     *     |                     from (position, board, distance)
     */
    BISHOP((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        Set<RectanglePosition> positionSet = new HashSet<>();

        // Reject movement with distance < 1
        if (distance < 1) return positionSet;

        // Moves diagonal
        addIfCellValid(position, new RectanglePosition(x + distance, y + distance), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x + distance, y - distance), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - distance, y + distance), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x - distance, y - distance), board, positionSet);

        // Remove leaps
        positionSet.removeIf(p -> board.needsLeap(position, p));

        return positionSet;
    }),

    /**
     * Move behavior for Queen
     * Magic: Queen = Rook + Bishop
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the Queen's possible behaviors (positionSet)
     *     |                   from (position, board, distance)
     */
    QUEEN((RectanglePosition position, RectangleBoard board, Integer distance) -> new HashSet<RectanglePosition>() {{
        addAll(ROOK.getMover().apply(position, board, distance));
        addAll(BISHOP.getMover().apply(position, board, distance));
    }}),

    /**
     * Move behavior for Knight
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the Knight's possible behaviors (positionSet)
     *     |                     from (position, board, distance)
     */
    KNIGHT((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        Set<RectanglePosition> positionSet = new HashSet<>();

        /* Move along L-shape */
        addIfCellValid(position, new RectanglePosition(x+1, y+2), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x+2, y+1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x+1, y-2), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x+2, y-1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x-1, y+2), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x-2, y+1), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x-1, y-2), board, positionSet);
        addIfCellValid(position, new RectanglePosition(x-2, y-1), board, positionSet);

        return positionSet;
    }),

    /**
     * Move behavior for Pawn
     * Behavior: (position, board, distance) -> positionSet
     *     | Derive the Pawn's possible behaviors (positionSet)
     *     |                   from (position, board, distance)
     */
    PAWN((RectanglePosition position, RectangleBoard board, Integer distance) -> {

        // FIXME: Pawn only attacks diagonally

        int x = position.rank, y = position.file;
        int ranks = board.getRanks();
        int playerDirection = board.getPiece(x, y).getTag();
        Set<RectanglePosition> positionSet = new HashSet<>();

        /* If at initial rank, advance by 2 along the file */
        if (distance == 1 || (distance == 2 && (x == 1 || x == ranks - 2)))
            addIfCellValid(position, new RectanglePosition(x + distance * playerDirection, y), board, positionSet);

        return positionSet;
    });

    /** Chess piece **/
    private Piece.MoveFunction<RectangleBoard, RectanglePosition> mover;

    /**
     * Constructor, unused but required
     * @param mover mover function
     */
    DefaultPiece(Piece.MoveFunction<RectangleBoard, RectanglePosition> mover) {
        this.mover = mover;
    }

    /**
     * Create a new piece with self behavior (don't forget the enum value is a lambda!)
     * @param tag tag that can be used as player tag
     * @return piece
     */
    public Piece<RectangleBoard, RectanglePosition> newPieceWithTag(int tag) {
        return new Piece<>(name(), tag, mover);
    }

    /**
     * Get mover function
     * @return mover :: (position, board, distance) -> positionSet
     */
    public Piece.MoveFunction<RectangleBoard, RectanglePosition> getMover() {
        return mover;
    }

    /**
     * Add a position to the set after checking availability of target cell
     * @param fromPosition source position
     * @param toPosition destination position
     * @param board rectangle chess board
     * @param positionSet set of positions to add the destination position to
     */
    private static void addIfCellValid(RectanglePosition fromPosition, RectanglePosition toPosition, RectangleBoard board, Set<RectanglePosition> positionSet) {
        boolean valid = board.isValidPosition(toPosition) &&       // Destination valid
                        board.pieceExists(fromPosition) &&         // Source exists
                        !(board.pieceExists(toPosition) &&         // If destination exists, make sure it's not cannibalism
                                board.getPiece(fromPosition).getTag().equals(board.getPiece(toPosition).getTag()));

        if (valid) positionSet.add(toPosition);
    }

}

