package com.example.battleshipsdemo;

import java.util.List;

public class Protocol {

    // Method to convert GameBoard into a custom protocol message (text format)
    public static String encodeGameBoard(GameBoard gameBoard) {
        StringBuilder protocolMessage = new StringBuilder();

        // Begin the custom protocol message
        protocolMessage.append("GAMEBOARD\n");

        // Add the board data (2D array)
        protocolMessage.append("BOARD:\n");
        int[][] board = gameBoard.getBoard();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                protocolMessage.append(board[row][col]);
                if (col < board[row].length - 1) {
                    protocolMessage.append(",");
                }
            }
            protocolMessage.append("\n");
        }

        // Add the ship data (ship name and coordinates)
        protocolMessage.append("SHIPS:");
        List<ShipPlacement> placedShips = gameBoard.getPlacedShips();
        for (ShipPlacement ship : placedShips) {
            protocolMessage.append(ship.getShip().getName())
                    .append(" ")
                    .append(ship.getShip().getSize())
                    .append(" ");
            for (int[] coord : ship.getOccupiedCoordinates()) {
                protocolMessage.append("[").append(coord[0]).append(",").append(coord[1]).append("] ");
            }
            protocolMessage.append("\n");
        }

        // Return the formatted protocol message
        return protocolMessage.toString();
    }

    // Method to decode the protocol message back into a GameBoard object (client-side)
    public static GameBoard decodeGameBoard(String message) {
        // Create a new GameBoard object
        GameBoard gameBoard = new GameBoard();

        // Split the message by lines
        String[] lines = message.split("\n");

        // Extract board data
        String[] boardData = lines[1].split(":")[1].split(",");
        int index = 0;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                gameBoard.getBoard()[row][col] = Integer.parseInt(boardData[index]);
                index++;
            }
        }

        // Extract ship data
        for (int i = 2; i < lines.length; i++) {
            String[] shipData = lines[i].split(" ");
            String shipName = shipData[0];
            int shipSize = Integer.parseInt(shipData[1]);
            // Process ship coordinates (example, you need to adapt this based on your ShipPlacement structure)
            // You may want to create a method to place ships based on the coordinates
        }

        // Return the reconstructed GameBoard object
        return gameBoard;
    }
}
