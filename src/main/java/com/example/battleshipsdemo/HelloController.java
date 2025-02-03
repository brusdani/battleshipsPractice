package com.example.battleshipsdemo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label statusLabel;

    @FXML
    private RadioButton horizontalButton;  // RadioButton for Horizontal orientation

    @FXML
    private RadioButton verticalButton;    // RadioButton for Vertical orientation

    private boolean isHorizontal = true;  // Default orientation (Horizontal)

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private GridPane enemyGrid;

    @FXML
    private GridPane playerGrid;

    @FXML
    private ListView<Battleship> shipView;

    private ObservableList<String> ships = FXCollections.observableArrayList(
            "Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"
    );  // ObservableList to hold the ships

    private ObservableList<Battleship> battleships;

    private String selectedShip;  // Selected ship to be placed

    private GameBoard player1Board = new GameBoard();
    private GameBoard player2Board = new GameBoard();

    private GameBoard playerBoard = new GameBoard();
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

    @FXML
    private void handlePlacementClick(ActionEvent event){
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
        // Get the selected ship from the ListView (assume it's already set from ship selection)
        Battleship selectedBattleship = shipView.getSelectionModel().getSelectedItem();
        if (selectedBattleship == null) {
            statusLabel.setText("No ship selected!");
            return;
        }
        // Check if the ship can be placed at the selected position
        boolean canPlace = true;

        // If placement is valid, place the ship on the grid and update the UI
        if (canPlace) {
            placeShip(row, col, selectedBattleship);  // This already updates the grid visually
            statusLabel.setText("Ship placed: " + selectedBattleship.getName());
        } else {
            statusLabel.setText("Invalid placement!");
        }
    }


    @FXML
    private void initialize(){

        // Add Battleship objects to the ObservableList
        ObservableList<Battleship> ships = FXCollections.observableArrayList(
                new Battleship(5, "Carrier"),
                new Battleship(4, "Battleship"),
                new Battleship(3, "Cruiser"),
                new Battleship(3, "Submarine"),
                new Battleship(2, "Destroyer")
        );

        shipView.setItems(ships);

        shipView.setCellFactory(param -> new ListCell<Battleship>() {
            @Override
            protected void updateItem(Battleship ship, boolean empty) {
                super.updateItem(ship, empty);
                if (empty || ship == null) {
                    setText(null);
                } else {
                    setText(ship.getName());  // Display the ship's name in the ListView
                }
            }
        });

        // Add listener to ListView to detect item selection
        shipView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleShipSelection(newValue);  // Call handleShipSelection with the selected Battleship object
            }
        });



        //playerShips = new Battleship[] {
                //new Battleship(5, "Aircraft Carrier"),
                //new Battleship(4, "Battleship"),
                //new Battleship(3, "Submarine")
        //};
        //placeShipsOnBoard(playerBoard,playerShips);

        drawBoard();
    }

    // Called when a ship is selected from the ListView
    @FXML
    protected void handleShipSelection(Battleship battleship) {
        // Get the selected Battleship object from the ListView
        Battleship selectedShip = shipView.getSelectionModel().getSelectedItem();

        if (selectedShip != null) {
            // Access the name and size of the selected ship
            System.out.println("Selected Ship: " + selectedShip.getName());
            System.out.println("Ship Size: " + selectedShip.getSize());
        }
    }
    private void placeShipsOnBoard(GameBoard board, Battleship[] ships) {
        // Example: Manually place ships (you can expand this later with random placement or user input)
        board.placeShip(ships[0], 0, 0, true);  // Place the Aircraft Carrier horizontally
        board.placeShip(ships[1], 2, 0, true);  // Place the Battleship horizontally
        board.placeShip(ships[2], 5, 5, false); // Place the Submarine vertically
    }
    private void placeShip(int row, int col, Battleship ship) {
        int shipSize = ship.getSize();

        // Place the ship either horizontally or vertically based on the orientation
        if (isHorizontal) {
            for (int i = 0; i < shipSize; i++) {
                Button cellButton = (Button) playerGrid.getChildren().get(row * 10 + (col + i));
                cellButton.setStyle("-fx-background-color: lightblue;");  // Change color to show the ship
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                Button cellButton = (Button) playerGrid.getChildren().get((row + i) * 10 + col);
                cellButton.setStyle("-fx-background-color: lightblue;");  // Change color to show the ship
            }
        }
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
    // Handle orientation changes when Horizontal button is selected
    @FXML
    private void setHorizontal() {
        isHorizontal = true;
        System.out.println("Orientation: Horizontal");
    }

    // Handle orientation changes when Vertical button is selected
    @FXML
    private void setVertical() {
        isHorizontal = false;
        System.out.println("Orientation: Vertical");
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
    // Update the grid visually (change button color or add image)

    // Remove the selected ship from the ObservableList
    private void removeShipFromList(Battleship ship) {
        ships.remove(ship);  // Remove the selected ship from the list
        System.out.println(ship + " has been placed!");
    }
    private void placeShipHorizontally(int row, int col, int shipSize) {
        for (int i = 0; i < shipSize; i++) {
            Button cellButton = (Button) playerGrid.getChildren().get((row * 10) + (col + i));
            cellButton.setStyle("-fx-background-color: lightblue;");  // Ship placement visual feedback
            // You can also store the ship's position in a list/array for reference
        }
    }
    private void placeShipVertically(int row, int col, int shipSize) {
        for (int i = 0; i < shipSize; i++) {
            Button cellButton = (Button) playerGrid.getChildren().get(((row + i) * 10) + col);
            cellButton.setStyle("-fx-background-color: lightblue;");  // Ship placement visual feedback
            // Store the ship's position for reference if needed
        }
    }
    private void drawBoard() {
        int[][] board = playerBoard.getBoard();
        int index = 0;

        // Loop through each cell in the 2D array and update the corresponding button color
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Button cellButton = (Button) playerGrid.getChildren().get(index);
                index = index + 1;

                // Update the button's style based on the board data
                if (board[row][col] == 1) {
                    // Ship is placed in this cell
                    cellButton.setStyle("-fx-background-color: lightblue;");
                } else {
                    // Empty cell
                    cellButton.setStyle("-fx-background-color: white;");
                }
            }
        }
    }

}