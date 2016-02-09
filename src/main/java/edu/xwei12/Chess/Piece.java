package edu.xwei12.Chess;

import java.util.Set;

/**
 * Piece.java
 * Created by xinranmsn on 1/31/16.
 */

/**
 * Piece class
 *
 * Properties:
 *     mover :: (position, board, distance) -> positionSet
 *
 * The mover derives a set of possible moves (destination set)
 * from the context (position, board cells, moving distance)
 *
 */
public class Piece<B extends Board, C extends Coordinates<C>> {
    public String getIdentifier() {
        return identifier;
    }

    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getTag() {
        return tag;
    }

    protected void setTag(Integer tag) {
        this.tag = tag;
    }

    /**
     * MoveFunction = (position, board, distance) -> positionSet
     * @param <B> Board
     * @param <C> Coordinate system
     */
    @FunctionalInterface
    interface MoveFunction<B extends Board, C extends Coordinates<C>> {
        /** Function signature **/
        Set<C> apply(C position, B board, Integer distance);
    }

    /** The move function **/
    private MoveFunction<B, C> mover;

    /** Kind identifier **/
    private String identifier;

    /** Tag for player info, etc **/
    private Integer tag;

    /**
     * Constructor
     * @param moveFunction :: (position, board, distance) -> positionSet
     */
    public Piece(String kind, int tag, MoveFunction<B, C> moveFunction) {
        this.identifier = kind;
        this.tag = tag;
        this.mover = moveFunction;
    }

    /**
     * Get move function
     * @return Move function
     */
    public MoveFunction<B, C> getMover() {
        return mover;
    }
}
