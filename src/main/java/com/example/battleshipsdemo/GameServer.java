package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServer {
    private static final Logger log = LoggerFactory.getLogger(GameServer.class);
    private static final List<Socket> waitingPlayers = new ArrayList<>();
    private static final List<GameSession> activeGames = new ArrayList<>();

    public static void main(String[] args) {
        log.info("Server Started");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            log.info("Server is listening on port 8080...");

            while (true) {
                // Accept client connections
                Socket clientSocket = serverSocket.accept();
                log.info("Client connected: " + clientSocket.getInetAddress());

                synchronized (waitingPlayers) {
                    waitingPlayers.add(clientSocket);
                    log.info("Player added to waiting queue. Waiting players: {}", waitingPlayers.size());

                    // If there are two players in the queue, start a new game session
                    if (waitingPlayers.size() >= 2) {
                        Socket player1 = waitingPlayers.remove(0);
                        Socket player2 = waitingPlayers.remove(0);

                        GameSession gameSession = new GameSession(player1, player2);
                        activeGames.add(gameSession);
                        log.info("Game session started with players: {} and {}", player1.getInetAddress(), player2.getInetAddress());

                        // Start the game session in a separate thread
                        new Thread(() -> startGameSession(gameSession)).start();

                    }
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while starting the server", e);
        }
        log.info("Server terminated");
    }

    private static void startGameSession(GameSession gameSession) {
        try {
            // Wait for UserData (Login)
            UserData userData1 = (UserData) gameSession.inputStream1.readObject();
            UserData userData2 = (UserData) gameSession.inputStream2.readObject();

            // Log received UserData
            log.info("Player 1 login: {} / Player 2 login: {}", userData1.getUsername(), userData2.getUsername());
            // Send initial message to both players about the preparation phase
            log.info("Game session initialized");
            Thread.sleep(2000);
            // Notify both players about the preparation phase
            gameSession.outputStream1.writeObject("Preparation phase");
            gameSession.outputStream2.writeObject("Preparation phase");

            clearInputStream(gameSession.inputStream1);
            clearInputStream(gameSession.inputStream2);


                if (gameSession.getCurrentPhase() == GamePhase.PREPARATION) {
                    log.info("inside condition");
                    // Use threads to handle both players' ship placement simultaneously
                    // Use threads to handle both players' ship placement simultaneously
                    Thread player1Thread = new Thread(() -> {
                        try {
                            log.info("Waiting for Player 1's GameBoard...");
                            GameBoard gameBoard1 = (GameBoard) gameSession.inputStream1.readObject();
                            gameSession.setGameBoard1(gameBoard1);  // Save Player 1's game board
                            gameSession.outputStream1.writeObject("Your GameBoard has been received.");
                            gameSession.outputStream1.flush();
                            gameSession.setPlayer1Ready(true);
                            log.info("Player 1's GameBoard received and ready");
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });

                    Thread player2Thread = new Thread(() -> {
                        try {
                            log.info("Waiting for Player 2's GameBoard...");
                            GameBoard gameBoard2 = (GameBoard) gameSession.inputStream2.readObject();
                            gameSession.setGameBoard2(gameBoard2);  // Save Player 2's game board
                            gameSession.outputStream2.writeObject("Your GameBoard has been received.");
                            gameSession.outputStream2.flush();
                            gameSession.setPlayer2Ready(true);
                            log.info("Player 2's GameBoard received and ready");
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });

// Start both threads
                    player1Thread.start();
                    player2Thread.start();

// Wait for both threads to finish before proceeding to the next phase
                    try {
                        player1Thread.join();
                        player2Thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // Start both threads
                    player1Thread.start();
                    player2Thread.start();

                    try {
                        player1Thread.join();
                        player2Thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    // Switch turn
                    gameSession.switchTurn();
                }


        } catch (IOException e) {
            log.error("Error in game session", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static GameBoard receiveGameBoard(ObjectInputStream inputStream) {
        try {
            // Log that we're waiting to receive a GameBoard
            log.info("Waiting to receive GameBoard from client...");

            // Read the GameBoard object from the input stream
            GameBoard gameBoard = (GameBoard) inputStream.readObject();

            // Log the GameBoard received
            log.info("Received GameBoard from client: {}", gameBoard);

            return gameBoard; // Return the received GameBoard
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Failed to receive GameBoard from client.", e);
        }
        return null; // Return null if there was an error
    }
    private static void clearInputStream(ObjectInputStream inputStream) {
        try {
            // Continuously read from the stream and discard the data until EOF
            while (inputStream.available() > 0) {
                inputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
