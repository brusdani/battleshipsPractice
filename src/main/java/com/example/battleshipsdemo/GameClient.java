package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;

public class GameClient {

    private static final Logger log = LoggerFactory.getLogger(GameClient.class);

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean connected = false;  // Flag to track connection status

    private GameStateListener gameStateListener;

    private GameStateNotifier gameStateNotifier;
    private volatile boolean running = true;

    private GameResult gameResult;

    public GameClient() {
        this.gameStateNotifier= new GameStateNotifier();
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
            connected = true;
            gameStateNotifier.notifyObservers("Connection successful");
            log.info("Connection established, streams are ready.");

            gameStateListener = new GameStateListener(this,inputStream,gameStateNotifier);

            //Thread gameListenerThread = new Thread(new GameStateListener(inputStream, gameStateNotifier));
            //gameListenerThread.start();

        } catch (IOException e) {
            gameStateNotifier.notifyObservers("Connection failed");
            e.printStackTrace();
            log.error("Error occurred while connecting to the server", e);
        }
    }

    // Method to send data to the server (used for other data exchange)
    public void sendData(Object data) {
        if (connected && outputStream != null) {
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
                outputStream.close();
                gameStateListener.terminateListener();
                socket.close();
                connected = false;
                log.info("Connection closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error closing the connection.", e);
        }
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    // Check if the client is connected
    public boolean isConnected() {
        return connected;
    }
    // Method to send a GameBoard object to the server
    public void sendGameBoard(GameBoard gameBoard) {
        if (connected && outputStream != null) {
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
    // GameClient method to send attack coordinates to the server
    public void sendAttack(int row, int col) {
        try {
            // Send the coordinates to the server
            log.info("Sending attack data to server");
            outputStream.writeObject(new AttackData(row, col));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendLoginDetails(String username, String password){
        try {
            log.info("Sending user data to server");
            outputStream.writeObject(new UserData(username, password));
            outputStream.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void exitGame(){

    }



    public GameStateNotifier getGameStateNotifier() {
        return gameStateNotifier;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }
}





