package me.unfamousthomas.logic;

import me.unfamousthomas.dataobjects.Match;
import me.unfamousthomas.dataobjects.MatchResult;
import me.unfamousthomas.dataobjects.Player;

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
        try {
            readMatches();
        } catch (FileNotFoundException e) {
            System.out.println("Something went wrong reading matches!");
        }
    }

    /**
     * Method to load matches into memory. Reads through the match_data.txt file and loads matches.
     * @throws FileNotFoundException If the match_data.txt filer was not found
     */
    public void readMatches() throws FileNotFoundException {
        File matchFile = new File("match_data.txt");
        Scanner matchScanner = new Scanner(matchFile);
        while (matchScanner.hasNextLine()) {
            String line = matchScanner.nextLine();
            String[] parts = line.split(",");
            Match match = new Match(UUID.fromString(parts[0]), determineResultFromString(parts[3]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            matches.put(match.getUuid(), match);
        }
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
                throw new RuntimeException("Something went wrong with reading player operation");
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

        return input;
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
    private MatchResult determineResultFromString(String result) {
        char winner = result.toUpperCase().charAt(0);
        return switch (winner) {
            case 'A' -> MatchResult.A_WIN;
            case 'B' -> MatchResult.B_WIN;
            case 'D' -> MatchResult.DRAW;
            default -> throw new RuntimeException("Something went wrong finding winner. Check file.");
        };
    }

    /**
     * Method to call when we have looked through all the files and done the logic.
     * Makes the file
     *
     * @throws IOException Exception thrown when something went wrong with manipulating the file
     */
    public void finishLogic() throws IOException {
        File file = new File("result.txt");
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        for (Map.Entry<UUID, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            if (!player.isIllegal()) {
                bw.write(player.toString());
                bw.newLine();
            } else {
                casinoBalanceChange += player.getTotalEarnings();
            }
        }
        bw.newLine();

        for (Map.Entry<UUID, String> entry : firstIllegal.entrySet()) {
            bw.write(entry.getValue());
            bw.newLine();
        }
        bw.newLine();
        bw.write(String.valueOf(casinoBalanceChange));
        bw.close();

    }
}

