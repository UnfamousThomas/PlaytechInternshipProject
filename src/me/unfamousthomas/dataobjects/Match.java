package me.unfamousthomas.dataobjects;

import java.util.UUID;

/**
 * Represents a match
 */
public class Match {
    /**
     * Unique identifier to represents a match
     */
    private final UUID uuid;
    /**
     * What the result of the match is
     */
    private final MatchResult result;
    /**
     * What the multiplier for the winning side is, 0 if a draw
     */
    private final double coinsMultiplier;

    public Match(UUID uuid, MatchResult result, Double coinsMultiplierA, Double coinsMultiplierB) {
        this.uuid = uuid;
        this.result = result;
        if (result == MatchResult.A_WIN) {
            coinsMultiplier = coinsMultiplierA;
        } else if (result == MatchResult.B_WIN) {
            coinsMultiplier = coinsMultiplierB;
        } else {
            coinsMultiplier = 0;
        }
    }

    public MatchResult getResult() {
        return result;
    }

    public double getCoinsMultiplier() {
        return coinsMultiplier;
    }

    public UUID getUuid() {
        return uuid;
    }
}
