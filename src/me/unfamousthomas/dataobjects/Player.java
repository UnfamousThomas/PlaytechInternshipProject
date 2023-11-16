package me.unfamousthomas.dataobjects;

import java.util.*;

/**
 * A class to represent each player. Contains data about their past bets as well as coins.
 */
public class Player {
    /**
     * UUID for a unique identifier
     */
    private final UUID playerId;
    /**
     * How many coins this account currently has
     */
    private long coins;
    /**
     * If this player has performed a illegal action
     */
    private boolean illegal;
    /**
     * How many bets this account has won
     */
    private int betsWon;

    /**
     * Stores a list of bets by their match id. This is mainly used to rollback bets for when player does illegal things.
     */
    private final Map<UUID, Integer> bets;


    public Player(UUID uuid) {
        this.coins = 0;
        this.playerId = uuid;
        this.illegal = false;
        this.bets = new HashMap<>();
        this.betsWon = 0;
    }

    /**
     * Method to deposit coins into players account
     *
     * @param amount How many coins to deposit
     */
    public void deposit(long amount) {
        if (!illegal) coins += amount;
    }

    /**
     * Method to withdraw coins from the players account. Action is cancelled when trying to draw more coins than player has.
     *
     * @param amount Amount of coins to remove
     */
    public void withdraw(long amount) {
        if (illegal) return;
        if (amount > coins) {
            this.illegal = true;
            return;
        }
        coins -= amount;
    }

    /**
     * Method to do bet. The basic requirements are:
     * 1) Player is not illegal
     * 2) Player has enough coins
     * 3) Player has not participated in this match before
     * <p>
     * After that the logic is largely dependent on the result. In the case it is a draw, nothing happens.
     * If the match is a win for one side and the user bet correctly, the formula floor(amount * multiplier) is used to add coins to user account
     * If the match is a win for one side and the user bet incorrectly, the user loses the coins he used for this bet
     *
     * @param amount          Amount of coins to bet
     * @param predictedResult What the player predicts is the result of the match
     * @param match           Match instance we are betting for
     */
    public void bet(int amount, MatchResult predictedResult, Match match) {
        if (illegal) return;
        if (amount > coins) {
            this.illegal = true;
            return;
        }

        if (bets.get(match.getUuid()) != null) return;

        int gains = 0;
        if (predictedResult == match.getResult()) {
            gains += (int) Math.floor(amount * match.getCoinsMultiplier());
            betsWon++;
        } else if (match.getResult() != MatchResult.DRAW) {
            gains -= amount;
        }

        coins += gains;
        bets.put(match.getUuid(), gains);
    }

    public double getCoins() {
        return coins;
    }

    /**
     * Method to calculate users win rate with 2 decimal points
     *
     * @return What the users current win rate is
     */
    public double getWinRate() {
        double rate = (double) betsWon / bets.size();
        rate = (double) Math.round(rate * 100) / 100;
        return rate;
    }

    public boolean isIllegal() {
        return illegal;
    }

    /**
     * Method used to calculate the sum of bets
     *
     * @return Total sum as an integer
     */
    public int getTotalEarnings() {
        int reverse = 0;
        for (Map.Entry<UUID, Integer> entry : bets.entrySet()) {
            reverse += entry.getValue();
        }
        return reverse;
    }

    @Override
    public String toString() {
        return playerId + " " + coins + " " + getWinRate();
    }
}
