package me.unfamousthomas;

import me.unfamousthomas.dataobjects.Match;
import me.unfamousthomas.logic.BettingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        BettingSystem bettingSystem = new BettingSystem();
        try {
            readMatches("match_data.txt", bettingSystem);
            readPlayerAction("player_data.txt", bettingSystem);
        } catch (IOException e) {
            System.out.println("Somethign went wrong with reading files...");
        }
        bettingSystem.writeToFile("result.txt");
    }

    /**
     * Utility method that handles loading matches from file to memory
     * @param filename Where to read files from
     * @param bs BettingSystem instance we are loading into
     * @throws FileNotFoundException If something went wrong with reading the file
     */
    private static void readMatches(String filename, BettingSystem bs) throws IOException {
        File matchFile = new File(filename);
        Scanner matchScanner = new Scanner(matchFile);
        while (matchScanner.hasNextLine()) {
            String line = matchScanner.nextLine();
            String[] parts = line.split(",");
            Match match = new Match(UUID.fromString(parts[0]), bs.determineResultFromString(parts[3]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            bs.addMatch(match);
        }
    }

    /**
     * Utility method that handles loading and doing player actions from file
     * @param filename Where player actions are stored
     * @param bs BettingSystem instance we are using
     * @throws IOException If something went wrong with reading the file
     */
    private static void readPlayerAction(String filename, BettingSystem bs) throws IOException{
        File playerFile = new File(filename);
        Scanner playerScanner = new Scanner(playerFile);
        while (playerScanner.hasNextLine()) {
            String line = playerScanner.nextLine();
            bs.handlePlayer(line);
        }
    }
}