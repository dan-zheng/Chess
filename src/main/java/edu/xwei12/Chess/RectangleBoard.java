package edu.xwei12.Chess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Chess board class 
 *
 */
public class RectangleBoard implements Board<RectangleBoard, RectanglePosition> {

    private class Cell {
        /** Piece **/
        public Piece<RectangleBoard, RectanglePosition> piece;

        /** Initializer **/
        Cell(Piece<RectangleBoard, RectanglePosition> piece) {
            this.piece = piece;
        }

        /** Empty cell predicate **/
        public boolean isEmpty() {
            return piece == null;
        }
    }

    public int getFiles() {
        return files;
    }

    public int getRanks() {
        return ranks;
    }

    /** number of ranks and number of files **/
    private int ranks, files;

    /** Cell matrix **/
    private Cell cells[][];

    /** Piece map **/
    private HashMap<String, Set<RectanglePosition>> pieceMap;

	/**
	 * Construct a board with dimensions
     * @param ranks number of ranks
     * @param files number of files
	 */
	public RectangleBoard(int ranks, int files) {
        this.ranks = ranks;
        this.files = files;

        // Initialize cells
        cells = new Cell[ranks][files];
        for (int i = 0; i < ranks; i++)
            for (int j = 0; j < files; j++)
                cells[i][j] = new Cell(null);

        // Initialize piece map
        pieceMap = new HashMap<>(ranks * files);
	}

    /**
     * Get piece at (rank, file)
     * @param rank rank-coordinate
     * @param file file-coordinate
     * @return cell
     */
    protected Piece<RectangleBoard, RectanglePosition> getPiece(int rank, int file) {
        if (file >= ranks || file >= files) return null;
        return cells[rank][file].piece;
    }

    /**
     * Get locations of pieces of a kind
     * @param kind name of the kind
     * @return piece set
     */
    @Override
    public Set<RectanglePosition> getPiecesByKind(String kind) {
        return pieceMap.get(kind);
    }

    /**
     * Get all piece locations
     * @return all piece locations
     */
    @Override
    public Set<RectanglePosition> getAllPieces() {
        return pieceMap.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Determines if a position is on the board
     * @param position position
     * @return valid or not
     */
    @Override
    public boolean isValidPosition(RectanglePosition position) {
        return position.rank >= 0 && position.file >= 0 &&
                position.rank < ranks && position.file < files;
    }

    /**
     * Piece at position
     * @param position position of the piece, dependent on the coordinate system (Coordinates)
     * @return piece or null
     */
    @Override
    public Piece<RectangleBoard, RectanglePosition> getPiece(RectanglePosition position) {
        return cells[position.rank][position.file].piece;
    }

    /**
     * Add a piece
     * @param piece a chess piece
     * @param position position that the piece will be placed at
     */
    @Override
    public void addPiece(Piece<RectangleBoard, RectanglePosition> piece, RectanglePosition position) {
        cells[position.rank][position.file].piece = piece;

        // Add piece to piece map
        Set<RectanglePosition> set = pieceMap.getOrDefault(piece.getIdentifier(), new HashSet<>());
        set.add(position);
        pieceMap.put(piece.getIdentifier(), set);
    }

    /**
     * Get a set of possible moves at distance for the piece at position
     * @param position source position
     * @param distance distance of move
     * @return position set
     */
    @Override
    public Set<RectanglePosition> getPossibleMoves(RectanglePosition position, int distance) {
        Cell cell = cells[position.rank][position.file];

        // Wrong position
        if (cell.isEmpty()) return null;

        // All possible moves of distance under context
        return cell.piece.getMover().apply(position, this, distance);
    }

    /**
     * Determine whether piece can be moved from a position to another
     * @param fromPosition source position
     * @param toPosition destination position
     * @return can or can not
     */
    @Override
    public boolean canMovePiece(RectanglePosition fromPosition, RectanglePosition toPosition) {
        Cell fromCell = cells[fromPosition.rank][fromPosition.file];

        // Wrong source position
        if (fromCell.isEmpty()) return false;

        // Calculate distance (squares)
        int distance = fromPosition.distanceTo(toPosition);

        // Possible moves
        Set<RectanglePosition> moves = getPossibleMoves(fromPosition, distance);

        return moves.stream().anyMatch(x -> x.sameAs(toPosition));
    }

    /**
     * Move a piece from one position to another, and also attacks
     * @param fromPosition current position of piece
     * @param toPosition of movement
     * @return success
     */
    @Override
    public boolean movePiece(RectanglePosition fromPosition, RectanglePosition toPosition) {

        if (!canMovePiece(fromPosition, toPosition)) return false;

        Cell fromCell = cells[fromPosition.rank][fromPosition.file];
        Cell toCell = cells[toPosition.rank][toPosition.file];

        // Modify piece map
        if (!toCell.isEmpty())
            pieceMap.get(toCell.piece.getIdentifier()).removeIf(x -> x.sameAs(toPosition));
        pieceMap.get(fromCell.piece.getIdentifier()).removeIf(x -> x.sameAs(fromPosition));
        pieceMap.get(fromCell.piece.getIdentifier()).add(toPosition);

        // Modify cells
        toCell.piece = fromCell.piece;
        fromCell.piece = null;

        return true;
    }

    /**
     * Determine whether there's any piece blocking the path from source to destination,
     * i.e., whether a leap is needed
     * @param fromPosition source position
     * @param toPosition to position
     * @return whether a leap is needed
     */
    public boolean needsLeap(RectanglePosition fromPosition, RectanglePosition toPosition) {
        int x1 = fromPosition.rank, y1 = fromPosition.file;
        int x2 = toPosition.rank, y2 = toPosition.file;

        // Vertical path
        if (x1 == x2)
            for (int y = y1 + 1; y < y2 && y < files; y++)
                if (!cells[x1][y].isEmpty()) return true;
        // Horizontal path
        if (y1 == y2)
            for (int x = x1 + 1; x < x2 && x < ranks; x++)
                if (!cells[x][y1].isEmpty()) return true;
        // Diagonal path
        if (x1 != x2 && y1 != y2)
            for (int x = x1 + 1, y = y1 + 1; x < x2 && x < files && y < ranks; x++, y++)
                if (!cells[x][y].isEmpty()) return true;

        return false;
    }

    /**
     * Print chess board (helper)
     */
    public void print() {
        System.out.println("| Chess board:");
        System.out.print("|------------------|");
        System.out.println(new String(new char[files-1]).replace("\0", "------------------|"));
        for (int i = ranks - 1; i >= 0; i--) {
            System.out.print("|");
            for (int j = 0; j < files; j++) {
                Piece<RectangleBoard, RectanglePosition> p = getPiece(i, j);
                System.out.print(String.format(" %-6s%10s |",
                        "(" + i + "," + j + ")", // position
                        (p == null ? "" : p.getIdentifier() + (p.getTag() == 1 ? "_P1" : "_P2")) + " ")); // slot
            }
            System.out.println();
            System.out.print("|------------------|");
            System.out.println(new String(new char[files-1]).replace("\0", "------------------|"));
        }
        System.out.println();
    }
}
