package edu.xwei12.chess;

/**
 * Coordinate system base interface
 * @author Xinran Wei
 * @param <T> self-bounding generics
 */
public interface Coordinates<T extends Coordinates<T>> {
    /**
     * Compute distance from self to another
     * @param destination destination
     * @return distance
     */
    int distanceTo(T destination);

    /**
     * Determine whether self is the same as other
     * @param other the other position
     * @return equals or not
     */
    boolean sameAs(T other);
}
