package edu.xwei12.Chess;

/**
 * Created by xinranmsn on 2/3/16.
 */
public interface GameObserver<B extends Board<B, C>, C extends Coordinates<C>> {

    /**
     * Delegate method for game state update
     * @param game game instance
     * @param move move
     */
    void onChessGameStateUpdate(Game<B, C> game, Game<B, C>.Move move);


}
