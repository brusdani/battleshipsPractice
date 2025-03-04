package com.example.battleshipsdemo;


import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class GameClient {
    private static final String SERVER_ADDRESS = "localhost"; // Server address
    private static final int SERVER_PORT = 12345;  // Server port

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private GameBoard currentGameBoard;

    private HelloController controller; // Reference to your controller

    public GameClient(HelloController controller) {
        this.controller = controller;
    }

    // Connects to the server
    public void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to server.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listens for updates from the server

    // Sends the current game board to the server
    public void sendGameBoard(GameBoard gameBoard) {
        try {
            outputStream.writeObject(gameBoard);  // Send the game board to the server
            System.out.println("Sent game board to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a player's attack to the server
    public void sendAttack(int row, int col) {
        try {
            String attackData = row + "," + col;  // Format the attack data as a string
            outputStream.writeObject(attackData);  // Send the attack data to the server
            System.out.println("Sent attack: " + attackData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Closes the connection
    public void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

