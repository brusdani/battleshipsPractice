package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class GameClient {

    private static final Logger log = LoggerFactory.getLogger(GameClient.class);

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean isConnected = false;  // Flag to track connection status

    public GameClient() {
        // Constructor: can initialize other necessary fields if needed
    }

    // Connect to the server
    public void connectToServer() {
        try {
            log.info("Attempting to connect to server...");

            // Set a connection timeout (e.g., 10 seconds)
            socket = new Socket("127.0.0.1", 8080);  // Connect to the server
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            log.info("Connected to server");

            // After connecting and initializing streams, set isConnected to true
            isConnected = true;
            log.info("Connection established, streams are ready.");

            // Send a GameBoard (or other game data) to the server after the connection is fully established
            //GameBoard gameBoard = new GameBoard();
            //sendGameBoard(gameBoard);  // Send GameBoard to server

            // Now you can start listening for updates or continue with other communication
            //listenForUpdates();

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error occurred while connecting to the server", e);
        }
    }




    // Method to send data to the server (used for other data exchange)
    public void sendData(Object data) {
        if (isConnected && outputStream != null) {
            try {
                log.info("Sending data to server: {}", data);
                outputStream.writeObject(data);  // Send the object (e.g., encoded game board)
                outputStream.flush();            // Ensure data is sent immediately
                log.info("Data sent to server.");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Failed to send data to server.", e);
            }
        } else {
            log.error("Cannot send, connection is not established yet.");
        }
    }

    // Close the connection when done
    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                log.info("Connection closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error closing the connection.", e);
        }
    }

    // Check if the client is connected
    public boolean isConnected() {
        return isConnected;
    }
    // Method to send a GameBoard object to the server
    public void sendGameBoard(GameBoard gameBoard) {
        if (isConnected && outputStream != null) {
            try {
                // Log the GameBoard being sent
                log.info("Sending GameBoard to server: {}", gameBoard);

                // Write the GameBoard object to the output stream
                outputStream.writeObject(gameBoard);

                // Flush the stream to ensure the data is sent immediately
                outputStream.flush();

                log.info("GameBoard sent to server.");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Failed to send GameBoard to server.", e);
            }
        } else {
            log.error("Cannot send GameBoard, connection is not established yet.");
        }
    }

}
