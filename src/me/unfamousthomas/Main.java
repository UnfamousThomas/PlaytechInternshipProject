package me.unfamousthomas;

import me.unfamousthomas.logic.BettingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        BettingSystem bettingSystem = new BettingSystem();
        try {
            File playerFile = new File("player_data.txt");
            Scanner playerScanner = new Scanner(playerFile);
            while (playerScanner.hasNextLine()) {
                String line = playerScanner.nextLine();
                bettingSystem.handlePlayer(line);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Could not find or open file...");
        }
        try {
            bettingSystem.finishLogic();
        } catch (IOException e) {
            System.out.println("Something went wrong!");
        }
    }
}