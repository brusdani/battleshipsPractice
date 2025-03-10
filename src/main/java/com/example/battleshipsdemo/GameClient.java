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
    private boolean isConnected = false;  // Flag to track connection status

    private MessageListener listener;

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

            listenForServerMessages();

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

    // GameClient method to receive the attack result (hit or miss)
    public String receiveAttackResult() {
        try {
            // Read the result from the server (hit or miss)
            String result = (String) inputStream.readObject();
            log.info("Received result from server" + result);
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error";  // Return error if something goes wrong
        }
    }

    public Object receiveServerMessage() {
        try {
            // Read the result from the server (it could be a String or another object)
            Object received = inputStream.readObject();
            log.info("Received from server: {}", received);

            // If the object is a String, return it directly
            if (received instanceof String) {
                return (String) received;
            }

            // If the object is an AttackResult, return it
            if (received instanceof AttackResult) {
                return (AttackResult) received;
            }

            // Handle unexpected message types
            log.error("Received unexpected message type: {}", received.getClass().getName());
            return null;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;  // Return null if something goes wrong
        }
    }

    public void listenForServerMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    // Receive the message from the server (could be a String or AttackResult)
                    Object message = receiveServerMessage();  // This method can return either String or AttackResult
                    if (message instanceof String) {
                        // If the message is a String (e.g., "Game over!" or a hit/miss message)
                        String textMessage = (String) message;
                        System.out.println("Received from server: " + textMessage);
                        if (listener != null) {
                            listener.onMessageReceived(textMessage);  // Pass the String message to the listener
                        }
                    } else if (message instanceof AttackResult) {
                        // If the message is an AttackResult object (contains row, column, and result)
                        AttackResult attackResult = (AttackResult) message;
                        System.out.println("Received attack result from server: " + attackResult.getResult());
                        if (listener != null) {
                            listener.onAttackResultReceived(attackResult);  // Pass the AttackResult to the listener
                        }
                    } else {
                        // Handle unexpected message types (if any)
                        System.out.println("Received unexpected message type: " + message.getClass().getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void setMessageListener(MessageListener listener) {
        this.listener = listener;  // Set the callback listener
    }
    // Client-side method to send the "ready" signal and GameBoard
    public void sendReadySignal() {
        try {
            // Send "ready" signal to server
            outputStream.writeObject("ready");
            outputStream.flush();
            log.info("Sent ready signal to the server");


        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error sending ready signal and GameBoard to server", e);
        }
    }




    public interface MessageListener {
        void onMessageReceived(String message);  // Called for messages like "Game over!", "Hit", "Miss"
        void onAttackResultReceived(AttackResult attackResult);  // Called when an attack result is received (e.g., coordinates and result)
    }
}





