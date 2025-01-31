package com.example.battleshipsdemo;

public class GameBoard {
    private static final int BOARD_SIZE = 10;
    private int[][] board; // A 2D array to represent the board

    public GameBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE]; // Initialize the 10x10 grid
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
            return true; // Ship placed successfully
        }
        return false; // Invalid placement
    }

    // Check if a ship can be placed in the specified position
    private boolean isPlacementValid(Battleship ship, int row, int col, boolean isHorizontal) {
        if (isHorizontal) {
            // Ensure the ship fits within the grid horizontally
            if (col + ship.getSize() > BOARD_SIZE) return false;
            // Ensure the ship does not overlap another ship
            for (int i = 0; i < ship.getSize(); i++) {
                if (board[row][col + i] == 1) return false;
            }
        } else {
            // Ensure the ship fits within the grid vertically
            if (row + ship.getSize() > BOARD_SIZE) return false;
            // Ensure the ship does not overlap another ship
            for (int i = 0; i < ship.getSize(); i++) {
                if (board[row + i][col] == 1) return false;
            }
        }
        return true;
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
}

