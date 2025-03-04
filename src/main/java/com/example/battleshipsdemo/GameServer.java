package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final Logger log = LoggerFactory.getLogger(GameServer.class);

    public static void main(String[] args) {
        log.info("Server Started");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            log.info("Server is listening on port 8080...");

            // Accept client connections continuously
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    log.info("Client connected: " + clientSocket.getInetAddress());

                    // Receive GameBoard from the client
                    GameBoard gameBoard = (GameBoard) inputStream.readObject();
                    log.info("Received GameBoard from client: {}", gameBoard);
                    String encodedGameBoard = Protocol.encodeGameBoard(gameBoard);
                    System.out.println("Encoded GameBoard:\n" + encodedGameBoard);

                    // Send a response back to the client
                    outputStream.writeObject("GameBoard received and processed");

                    // Keep the connection open for further communication
                    // The server won't close until it is manually stopped or the client disconnects
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    log.error("Error while handling client connection", e);
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while starting the server", e);
        }

        log.info("Server terminated");
    }
}
