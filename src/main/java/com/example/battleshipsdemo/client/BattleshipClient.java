package com.example.battleshipsdemo.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BattleshipClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Start ship placement phase
            System.out.println("Enter ship placement phase.");
            // Here you could add logic to place ships (for simplicity, we'll hardcode it)
            placeShip(out, "Carrier", 5, 2, 3, "horizontal");
            placeShip(out, "Battleship", 4, 1, 1, "vertical");

            // Wait for server response
            String response = in.readLine();
            System.out.println("Server says: " + response);

            // Now, simulate attacking (prompt user for attack)
            while (true) {
                System.out.print("Enter attack coordinates (row col): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();

                // Send attack data to the server
                sendAttack(out, row, col);

                // Read the server's response
                response = in.readLine();
                System.out.println("Server says: " + response);

                // Exit if the user types "exit"
                if (response.equalsIgnoreCase("game over")) {
                    System.out.println("Game over. Exiting...");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Method to place a ship
    private static void placeShip(PrintWriter out, String shipName, int size, int row, int col, String orientation) {
        // Simulate placing a ship
        String shipData = String.format("{\"shipName\": \"%s\", \"size\": %d, \"row\": %d, \"col\": %d, \"orientation\": \"%s\"}",
                shipName, size, row, col, orientation);
        out.println(shipData);
        System.out.println("Sent ship placement: " + shipData);
    }

    // Method to send attack coordinates
    private static void sendAttack(PrintWriter out, int row, int col) {
        // Create attack data and send to server
        String attackData = String.format("{\"action\": \"attack\", \"row\": %d, \"col\": %d}", row, col);
        out.println(attackData);
        System.out.println("Sent attack: " + attackData);
    }
}

