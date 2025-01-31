package com.example.battleshipsdemo;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final GameBoard player1Board = new GameBoard();
    private static final GameBoard player2Board = new GameBoard();

    private static Socket player1Socket;
    private static Socket player2Socket;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running... Waiting for players...");

            // Wait for the first player
            player1Socket = serverSocket.accept();
            System.out.println("Player 1 connected!");

            // Wait for the second player
            player2Socket = serverSocket.accept();
            System.out.println("Player 2 connected!");

            // Create input and output streams for both players
            BufferedReader player1In = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            PrintWriter player1Out = new PrintWriter(player1Socket.getOutputStream(), true);

            BufferedReader player2In = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
            PrintWriter player2Out = new PrintWriter(player2Socket.getOutputStream(), true);

            // Initialize game state (place ships, etc.)
            player1Out.println("Game Started! Your turn.");
            player2Out.println("Game Started! Waiting for Player 1...");

            // Start game loop
            while (true) {
                String player1Move = player1In.readLine();  // Get move from Player 1
                if (player1Move == null) break;  // End game if no input

                // Process attack and check if it hit or miss
                String result = handleMove(player1Move, 1);
                player2Out.println(result);  // Send result to Player 2
                player1Out.println(result);  // Send result to Player 1

                // Switch turns
                player1Out.println("Your turn!");
                player2Out.println("Player 1's turn...");

                String player2Move = player2In.readLine();  // Get move from Player 2
                if (player2Move == null) break;  // End game if no input

                result = handleMove(player2Move, 2);
                player1Out.println(result);  // Send result to Player 1
                player2Out.println(result);  // Send result to Player 2

                // Switch turns again
                player2Out.println("Your turn!");
                player1Out.println("Player 2's turn...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle a player's move (attack)
    private static String handleMove(String move, int player) {
        String[] moveParts = move.split(",");
        int row = Integer.parseInt(moveParts[0]);
        int col = Integer.parseInt(moveParts[1]);

        GameBoard targetBoard = (player == 1) ? player2Board : player1Board;
        boolean hit = targetBoard.attack(row, col);

        return hit ? "Hit!" : "Miss!";
    }
}

