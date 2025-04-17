package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class RetryController {
    private SceneController sceneController = new SceneController();

    private GameClient gameClient;
    @FXML
    private Button retryButton;

    @FXML
    private Label errorLabel;

    public void initialize(){

        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
        });
    }
    @FXML
    private void retryButtonClick (ActionEvent event)throws IOException {


        // Show an alert if login is successful or not
        sceneController.changeScene(event, "login.fxml");
    }

}
