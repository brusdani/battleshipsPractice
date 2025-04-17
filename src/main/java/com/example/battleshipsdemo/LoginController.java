package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements GameObserver {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private SceneController sceneController = new SceneController();
    private Stage currentStage;

    private boolean isConnected;

    private GameClient gameClient;

    @FXML
    public void initialize(){
        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
            gameClient.getGameStateNotifier().addObserver(this);

        });
    }
    @FXML
    private void loginClick(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Start the server connection in a separate thread to prevent blocking the UI


        new Thread (() -> {
            gameClient.connectToServer();  // Connect to the server
            if (gameClient.isConnected()) {
                gameClient.sendLoginDetails(username, password);
            } else {
                gameClient.getGameStateNotifier().notifyObservers("Connection failed");
            }
        }).start();

    }


    @Override
    public void onNotified(String message) {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        if ("Connection successful".equals(message)) {
            // Connection is successful, update the UI on the JavaFX application thread
            Platform.runLater(() -> {
                try {
                    gameClient.getGameStateNotifier().removeObserver(this);
                    sceneController.changeScene(currentStage, "waiting-room.fxml"); // Transition to waiting room
                } catch (IOException e) {
                    e.printStackTrace(); // Handle any errors when changing scenes
                }
            });
        } else if ("Connection failed".equals(message)) {
            // If connection failed, transition to the error scene
            Platform.runLater(() -> {
                try {
                    gameClient.getGameStateNotifier().removeObserver(this);
                    sceneController.changeScene(currentStage, "connection-error.fxml"); // Show connection error
                } catch (IOException e) {
                    e.printStackTrace(); // Handle any errors when changing scenes
                }
            });
        }
    }

    @Override
    public void onNotified(AttackResult attackResult) {

    }

    @Override
    public void onNotified(GameResult gameResult) {

    }
}
