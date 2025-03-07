package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class WaitingRoomController implements GameClient.MessageListener {
    @FXML
    private Button readyButton;
    @FXML
    private Label waitingLabel;

    private SceneController sceneController = new SceneController();

    private GameClient gameClient;


    @FXML
    public void initialize(){

        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
            gameClient.setMessageListener(this);
            readyButton.setDisable(true);
        });
    }
    @FXML
    private void readyButtonClick (ActionEvent event)throws IOException {


        // Show an alert if login is successful or not
        sceneController.changeScene(event, "BattleShipScene.fxml");
    }
    @Override
    public void onMessageReceived(String message) {
        Platform.runLater(() -> {
            System.out.println("Received message: " + message);  // Add log to verify message delivery
            if ("Preparation phase".equals(message)) {
                readyButton.setDisable(false);  // Enable the "Ready" button
                waitingLabel.setText("You are in the preparation phase! Place your ships.");
            } else if ("Waiting for other players".equals(message)) {
                waitingLabel.setText("Waiting for other players to join...");
            }
        });
    }

    @Override
    public void onAttackResultReceived(AttackResult attackResult) {
    }
}
