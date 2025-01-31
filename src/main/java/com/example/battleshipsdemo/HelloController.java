package com.example.battleshipsdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label statusLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private GridPane grid;
    private GameBoard player1Board = new GameBoard();
    private GameBoard player2Board = new GameBoard();
    private Battleship[] playerShips;
    // Create the ships for both players




    @FXML
    protected void handleCellClick(ActionEvent event) {
        // Logic to handle button click, e.g., determine row, col, and update game state
        System.out.println("Cell clicked!");
        Button clickedButton = (Button) event.getSource();

        // Get the row and column of the clicked button based on its position in the GridPane
        Integer row = GridPane.getRowIndex(clickedButton);
        Integer col = GridPane.getColumnIndex(clickedButton);

        // If row or col are null, set them to a default value of 0
        if (row == null) {
            row = 0;
        }
        if (col == null) {
            col = 0;
        }

        handleAttack(row,col,clickedButton);


    }

    private static final int GRID_SIZE = 10; //


    @FXML
    private void initialize(){
        playerShips = new Battleship[] {
                new Battleship(5, "Aircraft Carrier"),
                new Battleship(4, "Battleship"),
                new Battleship(3, "Submarine")
        };
        placeShipsOnBoard(player1Board,playerShips);
    }
    private void placeShipsOnBoard(GameBoard board, Battleship[] ships) {
        // Example: Manually place ships (you can expand this later with random placement or user input)
        board.placeShip(ships[0], 0, 0, true);  // Place the Aircraft Carrier horizontally
        board.placeShip(ships[1], 2, 0, true);  // Place the Battleship horizontally
        board.placeShip(ships[2], 5, 5, false); // Place the Submarine vertically
    }
    // Handle a player's attack on the opponent
    public void handleAttack(int row, int col, Button clickedButton) {
        boolean hit = player1Board.attack(row, col);
        // Update the UI based on whether it was a hit or miss
        if (hit) {
            statusLabel.setText("Hit!");
            clickedButton.setStyle("-fx-background-color: green;");
        } else {
            statusLabel.setText("Miss!");
            clickedButton.setStyle("-fx-background-color: red;");
        }

        // Check if the game is over (if opponent's ships are all sunk)
        if (player1Board.isGameOver()) {
            statusLabel.setText("Player wins");
        }
    }
}