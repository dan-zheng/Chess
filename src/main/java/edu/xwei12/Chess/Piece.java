package edu.xwei12.chess;

import java.util.Set;

/**
 * Piece class
 * @author Xinran Wei
 *
 * Properties:
 *     mover :: (position, board, distance) -> positionSet
 *
 * The mover derives a set of possible moves (destination set)
 * from the context (position, board cells, moving distance)
 *
 */
public class Piece<B extends Board, C extends Coordinates<C>> {
    public String getKind() {
        return kind;
    }

    protected void setKind(String kind) {
        this.kind = kind;
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

    /** Kind kind **/
    private String kind;

    /** Tag for player info, etc **/
    private Integer tag;

    /**
     * Constructor
     * @param moveFunction :: (position, board, distance) -> positionSet
     */
    public Piece(String kind, int tag, MoveFunction<B, C> moveFunction) {
        this.kind = kind;
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
