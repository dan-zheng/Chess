package edu.xwei12.chess;

/**
 * Game observer delegation
 * @author Xinran Wei
 */
public interface GameObserver<B extends Board<B, C>, C extends Coordinates<C>> {

    /**
     * Delegate method for game state update
     * @param game game instance
     * @param move move
     */
    void onChessGameStateUpdate(Game<B, C> game, Game<B, C>.Move move);
}
