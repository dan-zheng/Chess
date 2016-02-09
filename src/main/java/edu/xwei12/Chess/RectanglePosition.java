package edu.xwei12.Chess;

/**
 * Created by xinranmsn on 2/1/16.
 */
public class RectanglePosition implements Coordinates<RectanglePosition> {

    /** Parameters **/
    public int rank, file;

    /**
     * Constructor of coordinates
     * @param rank rank-coordinate
     * @param file file-coordinate
     */
    public RectanglePosition(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

    /**
     * Compute distance from self to another
     * @param destination destination position
     * @return distance
     */
    @Override
    public int distanceTo(RectanglePosition destination) {
        // Along rank
        if (rank == destination.rank) return Math.abs(destination.file - file);

        // Along file
        else if (file == destination.file) return Math.abs(destination.rank - rank);

        // Diagonal
        else return Math.abs(destination.rank - rank);
    }


    /**
     * Determine whether self is the same as other
     * @param other the other position
     * @return equals or not
     */
    @Override
    public boolean sameAs(RectanglePosition other) {
        return other.rank == rank && other.file == file;
    }


}
