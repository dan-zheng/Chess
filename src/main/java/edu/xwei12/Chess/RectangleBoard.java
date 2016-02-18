package edu.xwei12.chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Chess board class 
 * @author Xinran Wei
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
     * Get all piece names
     * @return all kinds of pieces, such as {"pawn", "king", ...}
     */
    @Override
    public Set<String> getAllPieceKinds() {
        return pieceMap.keySet();
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
     * Remove all pieces
     */
    @Override
    public void removeAllPieces() {
        Arrays.stream(cells).forEach(rank -> Arrays.stream(rank).forEach(cell -> cell.piece = null));
        pieceMap.forEach((type, set) -> set.clear());
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
        Set<RectanglePosition> set = pieceMap.getOrDefault(piece.getKind(), new HashSet<>());
        set.add(position);
        pieceMap.put(piece.getKind(), set);
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
     */
    @Override
    public void movePiece(RectanglePosition fromPosition, RectanglePosition toPosition) {

//        if (!canMovePiece(fromPosition, toPosition)) return false;

        Cell fromCell = cells[fromPosition.rank][fromPosition.file];
        Cell toCell = cells[toPosition.rank][toPosition.file];

        // Modify piece map
        if (!toCell.isEmpty())
            pieceMap.get(toCell.piece.getKind()).removeIf(x -> x.sameAs(toPosition));
        pieceMap.get(fromCell.piece.getKind()).removeIf(x -> x.sameAs(fromPosition));
        pieceMap.get(fromCell.piece.getKind()).add(toPosition);

        // Modify cells
        toCell.piece = fromCell.piece;
        fromCell.piece = null;
    }

    /**
     * Determine whether there's any piece blocking the path from source to destination,
     * i.e., whether a leap is needed
     * @param fromPosition source position
     * @param toPosition destination position
     * @return number of leaps needed
     */
    public int leapsNeeded(RectanglePosition fromPosition, RectanglePosition toPosition) {
        int x1 = fromPosition.rank, y1 = fromPosition.file;
        int x2 = toPosition.rank, y2 = toPosition.file;

        int leaps = 0;

        for (RectanglePosition pos = findNearestLeap(fromPosition, toPosition);
             pos != null && !pos.sameAs(toPosition);
             pos = findNearestLeap(pos, toPosition)) {
            leaps++;
        }

        return leaps;
    }


    /**
     * Find the first leap from source to destination.
     * @param fromPosition source position
     * @param toPosition destination position
     * @return position of the leap or null
     */
    public RectanglePosition findNearestLeap(RectanglePosition fromPosition, RectanglePosition toPosition) {
        int x1 = fromPosition.rank, y1 = fromPosition.file;
        int x2 = toPosition.rank, y2 = toPosition.file;
        // Vertical path
        if (x1 == x2)
            for (int y = y1 + 1; y < y2 && y < files; y++)
                if (!cells[x1][y].isEmpty())
                    return new RectanglePosition(x1, y);
        // Horizontal path
        if (y1 == y2)
            for (int x = x1 + 1; x < x2 && x < ranks; x++)
                if (!cells[x][y1].isEmpty())
                    return new RectanglePosition(x, y1);
        // Diagonal path
        if (Math.abs(x2 - x1) == Math.abs(y2 - y1) && x1 != x2 && y1 != y2)
            for (int x = x1 + 1, y = y1 + 1; x < x2 && x < files && y < ranks; x++, y++)
                if (!cells[x][y].isEmpty())
                    return new RectanglePosition(x, y);

        // Wrong path
        return null;
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
                        (p == null ? "" : p.getKind() + (p.getTag() == 1 ? "_P1" : "_P2")) + " ")); // slot
            }
            System.out.println();
            System.out.print("|------------------|");
            System.out.println(new String(new char[files-1]).replace("\0", "------------------|"));
        }
        System.out.println();
    }
}
