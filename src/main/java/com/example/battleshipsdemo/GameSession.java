package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class GameSession {
    private static final Logger log = LoggerFactory.getLogger(GameClient.class);
    private final Socket player1;
    private final Socket player2;
    public final ObjectOutputStream outputStream1;
    public final ObjectOutputStream outputStream2;
    public final ObjectInputStream inputStream1;
    public final ObjectInputStream inputStream2;

    private GameBoard gameBoard1;
    private GameBoard gameBoard2;
    private boolean isPlayer1Turn;
    private boolean isPlayer2Turn;

    private boolean isPlayer1Ready = false;
    private boolean isPlayer2Ready = false;
    public GamePhase currentPhase; // New phase variable


    public GameSession(Socket player1, Socket player2) throws IOException {
        this.player1 = player1;
        this.player2 = player2;
        this.outputStream1 = new ObjectOutputStream(player1.getOutputStream());
        this.outputStream2 = new ObjectOutputStream(player2.getOutputStream());
        this.inputStream1 = new ObjectInputStream(player1.getInputStream());
        this.inputStream2 = new ObjectInputStream(player2.getInputStream());

        this.gameBoard1 = new GameBoard();
        this.gameBoard2 = new GameBoard();
        this.isPlayer1Turn = true; // Player 1 starts first
        this.currentPhase = GamePhase.PREPARATION; // Start with preparation phase
    }

    // Getter and Setter methods
    public GameBoard getGameBoard1() {
        return gameBoard1;
    }

    public GameBoard getGameBoard2() {
        return gameBoard2;
    }

    public void setGameBoard1(GameBoard gameBoard) {
        this.gameBoard1 = gameBoard;
    }

    public void setGameBoard2(GameBoard gameBoard) {
        this.gameBoard2 = gameBoard;
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public boolean isPlayer2Turn() {
        return isPlayer2Turn;
    }

    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        currentPhase = phase;
    }

    // Method to handle the attack
    public AttackResult handleAttack(AttackData attackData) {
        String result;

        // Determine if it's Player 1's or Player 2's turn
        if (isPlayer1Turn) {
            // Player 1 is attacking Player 2's board
            result = gameBoard2.attack(attackData.getRow(), attackData.getCol()) ? "Hit" : "Miss";
        } else {
            // Player 2 is attacking Player 1's board
            result = gameBoard1.attack(attackData.getRow(), attackData.getCol()) ? "Hit" : "Miss";
        }

        // Return an AttackResult instance with the result
        return new AttackResult(attackData.getRow(), attackData.getCol(), result);
    }


    // Method to check if the game is over
    public boolean isGameOver() {
        return gameBoard1.isGameOver() || gameBoard2.isGameOver();
    }

    // Method to check if both players are ready and change the game phase to BATTLE
    public boolean arePlayersReady() {
        return gameBoard1 != null && gameBoard2 != null && currentPhase == GamePhase.PREPARATION;
    }

    public boolean isPlayer1Ready() {
        return isPlayer1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        isPlayer1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return isPlayer2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        isPlayer2Ready = player2Ready;
    }

    public void handlePlayerBoard(int playerNumber, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        try {
            // Receive the GameBoard from the client
            GameBoard gameBoard = (GameBoard) inputStream.readObject();
            if (playerNumber == 1) {
                this.setGameBoard1(gameBoard); // Store GameBoard for Player 1
                log.info("Received GameBoard for Player 1: {}", gameBoard);
            } else if (playerNumber == 2) {
                this.setGameBoard2(gameBoard); // Store GameBoard for Player 2
                log.info("Received GameBoard for Player 2: {}", gameBoard);
            }

            // Send confirmation to the client
            outputStream.writeObject("Your GameBoard has been received.");
            outputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Error receiving GameBoard from player", e);
        }
    }

    public void notifyTurn() {
        try {
            if (isPlayer1Turn) {
                outputStream1.writeObject("Your turn!");
                outputStream2.writeObject("Waiting");
            } else {
                outputStream1.writeObject("Waiting");
                outputStream2.writeObject("Your turn!");
            }
            outputStream1.flush();
            outputStream2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startBattle(){
        isPlayer1Turn=true;
        try {
            outputStream1.writeObject("Your turn!");
            outputStream2.writeObject("Waiting");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
