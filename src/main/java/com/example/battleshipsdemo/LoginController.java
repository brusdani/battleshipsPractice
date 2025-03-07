package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private SceneController sceneController = new SceneController();

    private GameClient gameClient;

    @FXML
    public void initialize(){
        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
        });
    }
    @FXML
    private void loginClick(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Start the server connection in a separate thread to prevent blocking the UI
        new Thread(() -> {
            gameClient.connectToServer();  // Connect to the server
            gameClient.sendLoginDetails(username, password);  // Send login details after connection is established
        }).start();

        // After sending login details, transition to the waiting room scene
        Platform.runLater(() -> {
            try {
                sceneController.changeScene(event, "waiting-room.fxml");
            } catch (IOException e) {
                e.printStackTrace();  // Handle any errors when changing scenes
            }
        });
    }

}
