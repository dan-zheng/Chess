package edu.xwei12.chess;

import java.util.HashMap;
import java.util.Observable;

/**
 * Score manager
 * @author Xinran Wei
 */
public class ScoreManager extends Observable {

    private HashMap<Integer, Integer> scoreMap;

    /**
     * Initializer
     */
    public ScoreManager() {
        scoreMap = new HashMap<>();
    }

    /**
     * Get the winner
     * @return winner player
     */
    public Integer getWinner() {
        return scoreMap.values().stream().max(Integer::max).get();
    }

    /**
     * Get score for a player
     * @param player player
     * @return score
     */
    public Integer getScore(Integer player) {
        return scoreMap.getOrDefault(player, 0);
    }

    /**
     * Raise score for a player by magnitude
     * @param player player
     * @param magnitude magnitude
     */
    public void raise(Integer player, Integer magnitude) {
        scoreMap.put(player, scoreMap.getOrDefault(player, 0) + magnitude);
        notifyObservers(scoreMap.get(player));
    }

    /**
     * Lower score for a player by magnitude
     * @param player player
     * @param magnitude magnitude
     */
    public void lower(Integer player, Integer magnitude) {
        scoreMap.put(player, scoreMap.getOrDefault(player, 0) - magnitude);
        notifyObservers(scoreMap.get(player));
    }
}
