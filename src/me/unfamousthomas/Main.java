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

    private static void readMatches(String filename, BettingSystem bs) throws FileNotFoundException {
        File matchFile = new File(filename);
        Scanner matchScanner = new Scanner(matchFile);
        while (matchScanner.hasNextLine()) {
            String line = matchScanner.nextLine();
            String[] parts = line.split(",");
            Match match = new Match(UUID.fromString(parts[0]), bs.determineResultFromString(parts[3]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            bs.addMatch(match);
        }
    }

    private static void readPlayerAction(String filename, BettingSystem bs) throws IOException{
        File playerFile = new File("player_data.txt");
        Scanner playerScanner = new Scanner(playerFile);
        while (playerScanner.hasNextLine()) {
            String line = playerScanner.nextLine();
            bs.handlePlayer(line);
        }
    }
}