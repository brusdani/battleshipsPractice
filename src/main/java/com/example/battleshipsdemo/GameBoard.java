package com.example.battleshipsdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable {
    private static final int BOARD_SIZE = 10;
    private int[][] board; // A 2D array to represent the board
    private List<ShipPlacement> placedShips;  // List to track placed ships

    public GameBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE]; // Initialize the 10x10 grid
        placedShips = new ArrayList<>();  // Initialize the list for placed ships
    }

    // Place a ship on the board
    public boolean placeShip(Battleship ship, int row, int col, boolean isHorizontal) {
        if (isPlacementValid(ship, row, col, isHorizontal)) {
            // Place the ship on the board
            for (int i = 0; i < ship.getSize(); i++) {
                if (isHorizontal) {
                    board[row][col + i] = 1; // Mark ship position
                } else {
                    board[row + i][col] = 1; // Mark ship position
                }
            }
            placedShips.add(new ShipPlacement(ship, row, col, isHorizontal));
            return true; // Ship placed successfully
        }
        return false; // Invalid placement
    }

    // Check if a ship can be placed in the specified position
    public boolean isPlacementValid(Battleship ship, int row, int col, boolean isHorizontal) {
        // First, check if the ship fits within the grid horizontally or vertically
        if (isHorizontal) {
            if (col + ship.getSize() > BOARD_SIZE) return false; // Ship doesn't fit horizontally
        } else {
            if (row + ship.getSize() > BOARD_SIZE) return false; // Ship doesn't fit vertically
        }

        // Check for overlap with already placed ships in placedShips
        for (ShipPlacement placedShip : placedShips) {
            List<int[]> occupiedCoordinates = placedShip.getOccupiedCoordinates();

            // Check if the new ship's coordinates overlap with any occupied coordinate
            for (int[] occupiedCoord : occupiedCoordinates) {
                // Horizontal placement check
                if (isHorizontal) {
                    for (int i = 0; i < ship.getSize(); i++) {
                        if (occupiedCoord[0] == row && occupiedCoord[1] == col + i) {
                            return false; // Overlap with another ship
                        }
                    }
                } else {
                    // Vertical placement check
                    for (int i = 0; i < ship.getSize(); i++) {
                        if (occupiedCoord[0] == row + i && occupiedCoord[1] == col) {
                            return false; // Overlap with another ship
                        }
                    }
                }
            }
        }

        return true; // No overlap, valid placement
    }




    // Reset the entire board (clear all ships and placed ships)
    public void resetBoard() {
        // Clear the board grid
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = 0; // Reset the grid to have no ships
            }
        }

        // Clear the list of placed ships
        placedShips.clear(); // Remove all ship placements
    }

    // Handle an attack on the board
    public boolean attack(int row, int col) {
        if (board[row][col] == 1) {
            board[row][col] = 2; // Mark as hit
            return true; // Hit
        }
        board[row][col] = 3; // Mark as miss
        return false; // Miss
    }

    // Check if all ships have been sunk
    public boolean isGameOver() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 1) {
                    return false; // There are still ships on the board
                }
            }
        }
        return true; // All ships are sunk
    }

    public int[][] getBoard() {
        return board;
    }

    public List<ShipPlacement> getPlacedShips() {
        return placedShips;
    }
}

