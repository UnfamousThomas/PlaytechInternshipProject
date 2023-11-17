package me.unfamousthomas.logic;

import me.unfamousthomas.dataobjects.Match;
import me.unfamousthomas.dataobjects.MatchResult;
import me.unfamousthomas.dataobjects.Player;
import me.unfamousthomas.exceptions.OperationReadingException;

import java.io.*;
import java.util.*;

public class BettingSystem {
    private final Map<UUID, Match> matches;
    private final Map<UUID, Player> players;
    private int casinoBalanceChange;
    private Map<UUID, String> firstIllegal;

    public BettingSystem() {
        this.matches = new HashMap<>();
        this.players = new HashMap<>();
        this.firstIllegal = new HashMap<>();
        this.casinoBalanceChange = 0;
    }

    public void addMatch(Match match) {
        matches.put(match.getUuid(), match);
    }


    /**
     * Method to call for each line of player_data.txt. Handles choosing method as well as saving first illegal action.
     * @param line Line currently handling
     */
    public void handlePlayer(String line) {
        String[] parts = line.split(",");
        UUID playerId = UUID.fromString(parts[0]);
        Player player = players.getOrDefault(playerId, new Player(playerId));
        switch (parts[1]) {
            case "BET":
                performBet(player, Integer.parseInt(parts[3]), determineResultFromString(parts[4]), matches.get(UUID.fromString(parts[2])));
                break;
            case "DEPOSIT":
                player.deposit(Long.parseLong(parts[3]));
                break;
            case "WITHDRAW":
                player.withdraw(Long.parseLong(parts[3]));
                break;
            default:
                throw new OperationReadingException(line);
        }
        if (player.isIllegal() && !firstIllegal.containsKey(playerId)) {
            firstIllegal.put(playerId, modifyIllegal(line));
        }
        players.put(playerId, player);
    }

    /**
     * Simple utility method to change player_data.txt format into result.txt format
     * @param input Line we are editing
     * @return The correct format line
     */
    private String modifyIllegal(String input) {
        input = input.replaceAll("(,,)", ",null,");
        if (input.endsWith(",")) {
            input += "null";
        }

        return input.replaceAll(","," ");
    }

    /**
     * Method to fully perform bet. This also handles keeping track of changes to casinos balance.
     *
     * @param player          Player we are performing bet for
     * @param amount          The amount the player is betting
     * @param predictedResult What is predicted outcome
     * @param match           What match we are betting for
     */
    public void performBet(Player player, int amount, MatchResult predictedResult, Match match) {
        double initialBalance = player.getCoins(); //500
        player.bet(amount, predictedResult, match);
        double updatedBalance = player.getCoins(); //0

        if (initialBalance > updatedBalance) { //500 > 0
            casinoBalanceChange += (int) (initialBalance - updatedBalance);
        } else {
            casinoBalanceChange -= (int) (updatedBalance - initialBalance);
        }
    }

    /**
     * Utility method to get the MatchResult enum from a string using the first char
     *
     * @param result The string to use as input, should be the last part of the match data file
     * @return MatchResult or RuntimeException
     */
    public MatchResult determineResultFromString(String result) {
        char winner = result.toUpperCase().charAt(0);
        return switch (winner) {
            case 'A' -> MatchResult.A_WIN;
            case 'B' -> MatchResult.B_WIN;
            case 'D' -> MatchResult.DRAW;
            default -> throw new RuntimeException("Something went wrong finding winner. Check file.");
        };
    }


    /**
     * Method to write the processed data into a file.
     * Internally calls {@link #writeIllegalActions(BufferedWriter)} and {@link #writePlayerData(BufferedWriter)}
     * @param filename Filename to write data to. Usually "results.txt"
     */
    public void writeToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            writePlayerData(bw);
            writeIllegalActions(bw);
            bw.newLine();
            bw.write(String.valueOf(casinoBalanceChange));
        } catch (IOException e) {
            System.out.println("Something went wrong!");
        }
    }

    /**
     * Utility method that handles writing the player balances into the file
     * @param bw bufferedwriter that was opened in {@link #writeToFile(String)}
     * @throws IOException If something went wrong with writing to file
     */
    private void writePlayerData(BufferedWriter bw) throws IOException {
        for (Player player : players.values()) {
            if (!player.isIllegal()) {
                bw.write(player.toString());
                bw.newLine();
            } else {
                casinoBalanceChange += player.getTotalEarnings();
            }
        }
        bw.newLine();
    }

    /**
     * Utility method that handles writing the illegal actions into the file
     * @param bw bufferedwriter that was opened in {@link #writeToFile(String)} ()}
     * @throws IOException If something went wrong with writing to file
     */

    private void writeIllegalActions(BufferedWriter bw) throws IOException {
        for (String illegalAction : firstIllegal.values()) {
            bw.write(illegalAction);
            bw.newLine();
        }
    }
}

