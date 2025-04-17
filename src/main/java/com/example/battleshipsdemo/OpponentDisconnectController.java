package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class OpponentDisconnectController {
    @FXML
    private Button requeueButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label victoryLabel;
    @FXML
    private Label forfeitLabel;
    @FXML
    private Label countdownLabel;

    private SceneController sceneController = new SceneController();

    private GameClient gameClient;
    private ClientTimer clientTimer;
    public void initialize(){

        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
        });
    }
    @FXML
    private void requeueButtonClick (ActionEvent event)throws IOException {
        gameClient.sendData("PLAY_AGAIN");
        GameSessionData.getInstance().reset();
        sceneController.changeScene(event, "waiting-room.fxml");
    }
    @FXML
    private void exitButtonClick (ActionEvent event)throws IOException {
        gameClient.sendData("EXIT");
        gameClient.closeConnection();
        Platform.exit();
        System.exit(0);
    }
}
