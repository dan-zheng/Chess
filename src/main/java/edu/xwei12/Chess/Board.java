package edu.xwei12.Chess;

import java.util.Set;

public interface Board<B extends Board<B, C>, C extends Coordinates<C>> {

    /**
     * Get locations of pieces of a kind
     * @param kind name of kind
     * @return piece set
     */
    Set<C> getPiecesByKind(String kind);

    /**
     * Get all piece locations
     * @return all piece locations
     */
    Set<C> getAllPieces();

    /**
     * Determines if a position is on the board
     * @param position position
     * @return valid or not
     */
    boolean isValidPosition(C position);

    /**
     * Determines if a piece exists at position
     * @param position position
     * @return exists or not
     */
    default boolean pieceExists(C position) {
        return getPiece(position) != null;
    }

    /**
     * Add a piece
     * @param piece a chess piece
     * @param position position that the piece will be placed at
     */
    void addPiece(Piece<B, C> piece, C position);

    /**
     * Piece at position
     * @param position position of the piece, dependent on the coordinate system (Coordinates)
     * @return piece or null
     */
    Piece<B, C> getPiece(C position);

    /**
     * Get a set of possible moves at distance for the piece at position
     * @param position source position
     * @param distance distance of move
     * @return position set
     */
    Set<C> getPossibleMoves(C position, int distance);

    /**
     * Determine whether piece can be moved from a position to another
     * @param fromPosition source position
     * @param toPosition destination position
     * @return can or can not
     */
    boolean canMovePiece(C fromPosition, C toPosition);

    /**
     * Move a piece from one position to another, and also attack
     * @param fromPosition source position
     * @param toPosition destination position
     * @return success
     */
    boolean movePiece(C fromPosition, C toPosition);
}
