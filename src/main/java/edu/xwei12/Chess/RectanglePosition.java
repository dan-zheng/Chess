package edu.xwei12.chess;

/**
 * Rectangle coordinate system
 * @author Xinran Wei
 */
public class RectanglePosition implements Coordinates<RectanglePosition> {

    /** Rank component of the coordinates **/
    public int rank;

    /** File component of the coordinates **/
    public int file;

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
        if (rank == destination.rank)
            return Math.abs(destination.file - file);

        // Along file
        else if (file == destination.file)
            return Math.abs(destination.rank - rank);

        // Diagonal
        else if (Math.abs(destination.file - file) == Math.abs(destination.rank - rank))
            return Math.abs(destination.rank - rank);

        // Not applicable
        return -1;
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
