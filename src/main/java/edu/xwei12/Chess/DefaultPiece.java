package edu.xwei12.chess;

import java.util.HashSet;
import java.util.Set;

/**
 * Default pieces for rectangle board + rectangle coordinates
 * @author Xinran Wei
 */
public enum DefaultPiece {

    /**
     * Move behavior for King as a lambda function
     * (position, board, distance) -> positionSet
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
     * Move behavior for Rook as a lambda function
     * (position, board, distance) -> positionSet
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
        positionSet.removeIf(p -> board.leapsNeeded(position, p) > 0);

        return positionSet;
    }),

    /**
     * Move behavior for Bishop as a lambda function
     * (position, board, distance) -> positionSet
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
        positionSet.removeIf(p -> board.leapsNeeded(position, p) > 0);

        return positionSet;
    }),

    /**
     * Move behavior for Queen as a lambda function
     * (position, board, distance) -> positionSet
     */
    QUEEN((RectanglePosition position, RectangleBoard board, Integer distance) -> new HashSet<RectanglePosition>() {{
        addAll(ROOK.getMover().apply(position, board, distance));
        addAll(BISHOP.getMover().apply(position, board, distance));
    }}),

    /**
     * Move behavior for Knight as a lambda function
     * (position, board, distance) -> positionSet
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
     * Move behavior for Pawn as a lambda function
     * (position, board, distance) -> positionSet
     */
    PAWN((RectanglePosition position, RectangleBoard board, Integer distance) -> {
        int x = position.rank, y = position.file;
        int ranks = board.getRanks();
        int playerDirection = board.getPiece(x, y).getTag();
        Set<RectanglePosition> positionSet = new HashSet<>();

        /* If at initial rank, advance by 2 along the file */
        if (distance == 1 || (distance == 2 && (x == 1 || x == ranks - 2))) {

            RectanglePosition forward = new RectanglePosition(x + distance * playerDirection, y);
            // Does not attack forward
            if (board.isValidPosition(forward) && !board.pieceExists(forward)) {
                addIfCellValid(position, forward, board, positionSet);
            }

            // Attacks diagonally
            if (distance == 1) {
                RectanglePosition diagonal1 = new RectanglePosition(x + playerDirection, y + 1);
                if (board.isValidPosition(diagonal1) && board.pieceExists(diagonal1)) {
                    addIfCellValid(position, diagonal1, board, positionSet);
                }
                RectanglePosition diagonal2 = new RectanglePosition(x + playerDirection, y - 1);
                if (board.isValidPosition(diagonal2) && board.pieceExists(diagonal2)) {
                    addIfCellValid(position, diagonal2, board, positionSet);
                }
            }
        }

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
        return new Piece<>(name().toLowerCase(), tag, mover);
    }

    /**
     * Get mover function
     * @return mover :: (position, board, distance) -> positionSet
     */
    public Piece.MoveFunction<RectangleBoard, RectanglePosition> getMover() {
        return mover;
    }

    /**
     * Get kind identifier
     * @return lowercase of enum name as identifier
     */
    public String getKind() {
        return name().toLowerCase();
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
                        board.pieceExists(fromPosition)   &&       // Source exists
                        !(board.pieceExists(toPosition)   &&       // If destination exists, make sure it's not cannibalism
                                board.getPiece(fromPosition).getTag().equals(board.getPiece(toPosition).getTag()));

        if (valid) positionSet.add(toPosition);
    }

}

