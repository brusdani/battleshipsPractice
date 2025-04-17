package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PreperationPhaseController implements GameObserver{

    @FXML
    protected Label statusLabel;

    @FXML
    private Label titleLabel;

    private boolean isHorizontal = true;

    @FXML
    private GridPane playerGrid;

    private static GamePhase currentPhase;
    private ClientTimer clientTimer;


    @FXML
    private ListView<Battleship> shipView;
    @FXML
    private Button resetButton;
    @FXML
    private Button readyButton;
    @FXML
    private Label countdownLabel;


    private SceneController sceneController = new SceneController();
    private ObservableList<Battleship> ships = FXCollections.observableArrayList();
    private GameClient gameClient;
    private GameBoard playerBoard = GameSessionData.getInstance().getPlayerBoard();

    private String selectedShip;

    @FXML
    private void initialize() {


        // Initialize the ObservableList with Battleships
        Platform.runLater(() -> {
            ships.addAll(
                    //new Battleship(5, "Carrier"),
                    //new Battleship(4, "Battleship"),
                    //new Battleship(3, "Cruiser"),
                    //new Battleship(3, "Submarine"),
                    new Battleship(2, "Destroyer")
            );
            shipView.setItems(ships);
            shipView.setCellFactory(param -> new ListCellBattleship());
            addSelectionListenerToShipView();
            drawBoard();
            playerGrid.setDisable(false);
            gameClient = HelloApplication.gameClient;
            gameClient.getGameStateNotifier().addObserver(this);
            startPlacementTimer(20);

            System.out.println(gameClient!=null);
        });
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
        boolean canPlace = playerBoard.isPlacementValid(selectedBattleship, row, col, isHorizontal);

        // If placement is valid, place the ship on the grid and update the UI
        if (canPlace) {
            playerBoard.placeShip(selectedBattleship, row, col, isHorizontal);
            placeShip(row, col, selectedBattleship);  // This already updates the grid visually
            statusLabel.setText("Ship placed: " + selectedBattleship.getName());
        } else {
            statusLabel.setText("Invalid placement!");
        }
    }
    private void addSelectionListenerToShipView() {
        shipView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleShipSelection(newValue);  // Call handleShipSelection with the selected Battleship object
            }
        });
    }
    @FXML
    private void startBattlePhase() {
        Stage currentStage = (Stage) readyButton.getScene().getWindow();
        if (ships.isEmpty()) {
            Platform.runLater(() -> {
                // Send game data to the server
                clientTimer.stopTimer();
                gameClient.sendGameBoard(playerBoard);
                String encodedGameBoard = Protocol.encodeGameBoard(playerBoard);
                System.out.println("Encoded GameBoard (sending to server):\n" + encodedGameBoard);
                try {
                    gameClient.getGameStateNotifier().removeObserver(this);
                    sceneController.changeScene(currentStage, "BattleShipSceneUpdated.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        } else {
            System.out.println("Place all ships before starting the game");
        }
    }
    @Override
    public void onNotified(String message) {
        Stage currentStage = (Stage) statusLabel.getScene().getWindow();
        Platform.runLater(() -> {

            if ("Disconnected from server".equals(message)) {
                if (clientTimer != null) {
                    clientTimer.stopTimer();
                }
                try {
                    gameClient.getGameStateNotifier().removeObserver(this);
                    sceneController.changeScene(currentStage,"player-disconnected.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if ("Opponent disconnected".equals(message)) {
                if (clientTimer != null) {
                    clientTimer.stopTimer();
                }
                try {
                    gameClient.getGameStateNotifier().removeObserver(this);
                    sceneController.changeScene(currentStage,"opponent-disconnected.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                statusLabel.setText(message);  // Update with hit/miss or other messages
            }
        });
    }


    @FXML
    private void exitGame() {
        // Show a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Are you sure you want to exit?");


        if (alert.showAndWait().get() == ButtonType.OK) {
            gameClient.closeConnection();
            Platform.exit();
        }
    }

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

    @FXML
    private void placeShipsOnBoard(GameBoard board, ObservableList ships) {
        // Example: Manually place ships (you can expand this later with random placement
        board.placeShip((Battleship) ships.get(0), 0, 0, true);  // Place the Aircraft Carrier horizontally
        //board.placeShip((Battleship) ships.get(1), 2, 0, true);  // Place the Battleship horizontally
        //board.placeShip((Battleship) ships.get(2), 5, 5, false); // Place the Submarine vertically
        //board.placeShip((Battleship) ships.get(3), 5, 5, false);
        //board.placeShip((Battleship) ships.get(4), 8, 8, true);
    }
    private void placeShip(int row, int col, Battleship ship) {
        int shipSize = ship.getSize();

        // Place the ship either horizontally or vertically based on the orientation
        if (isHorizontal) {
            for (int i = 0; i < shipSize; i++) {
                Button cellButton = (Button) playerGrid.getChildren().get(row * 10 + (col + i));
                removeShipFromList(ship);
                cellButton.setStyle("-fx-background-color: lightblue;");
                // Change color to show the ship
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                Button cellButton = (Button) playerGrid.getChildren().get((row + i) * 10 + col);
                removeShipFromList(ship);
                cellButton.setStyle("-fx-background-color: lightblue;");  // Change color to show the ship
            }
        }
    }
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
    @FXML
    private void resetPlacement() {
        // Print the board status (optional for debugging)
        System.out.println("Before resetPlacement, playerBoard: " + playerBoard);
        if (playerBoard == null) {
            System.out.println("Error: playerBoard is null before reset!");
        }
        System.out.println("The board has been reset.");
        // Step 1: Clear the board by resetting the GameBoard instance
        playerBoard.resetBoard();

        // Step 2: Clear the ObservableList to reset the ships in ListView
        ships.clear();  // Remove all ships from the list

        // Step 3: Refill the ListView with initial ships
        ships.addAll(
                //new Battleship(5, "Carrier"),
                //new Battleship(4, "Battleship"),
                //new Battleship(3, "Cruiser"),
                //new Battleship(3, "Submarine"),
                new Battleship(2, "Destroyer")
        );
        shipView.refresh();
        drawBoard();

        shipView.getSelectionModel().clearSelection();


    }
    private void removeShipFromList(Battleship ship) {
        ships.remove(ship);  // Remove the selected ship from the list
        System.out.println(ship + " has been removed!");
        // Clear the selection
        shipView.getSelectionModel().clearSelection();
        printObservableListContents();
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
                    cellButton.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #dddddd);");
                }
            }
        }
    }
    private void printObservableListContents() {
        for (Battleship ship : ships) {
            System.out.println(ship.getName());
        }
    }

    @Override
    public void onNotified(AttackResult attackResult) {

    }

    @Override
    public void onNotified(GameResult gameResult) {

    }
    private void startPlacementTimer(int countdownTime){
        Stage currentStage = (Stage) readyButton.getScene().getWindow();
        if (clientTimer != null) {
            clientTimer.stopTimer();  // Stop the existing timer
        }

        // Create a new ClientTimer instance with the specified countdown time
        clientTimer = new ClientTimer(countdownTime, countdownLabel, gameClient, () -> {

            resetPlacement();

            placeShipsOnBoard(playerBoard, ships);
            drawBoard();
            System.out.println("Sending board; placed ships count: " + playerBoard.getPlacedShips().size());
            gameClient.sendGameBoard(playerBoard);
            try {
                gameClient.getGameStateNotifier().removeObserver(this);
                sceneController.changeScene(currentStage,"BattleShipSceneUpdated.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        );

        // Start the ClientTimer in a new thread
        new Thread(clientTimer).start();

    }
}
