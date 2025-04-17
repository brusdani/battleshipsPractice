package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class WaitingRoomController implements GameObserver {
    @FXML
    private Button readyButton;
    @FXML
    private Label waitingLabel;
    @FXML
    private Label countdownLabel;

    private SceneController sceneController = new SceneController();

    private GameClient gameClient;
    private ClientTimer clientTimer;


    @FXML
    public void initialize(){

        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
            if (!gameClient.getGameStateNotifier().getObservers().contains(this)) {
                gameClient.getGameStateNotifier().addObserver(this);
            }
            readyButton.setDisable(true);
            countdownLabel.setVisible(false);
        });
    }
    @FXML
    private void readyButtonClick (ActionEvent event)throws IOException {


        // Show an alert if login is successful or not
        gameClient.getGameStateNotifier().removeObserver(this);
        clientTimer.stopTimer();
        sceneController.changeScene(event, "preparation-phase.fxml");
    }
    @Override
    public void onNotified(String message) {
        Stage currentStage = (Stage) readyButton.getScene().getWindow();
        Platform.runLater(() -> {
            System.out.println("Received message: " + message);  // Log the received message
            if ("Preparation phase".equals(message)) {
                readyButton.setDisable(false);  // Enable the "Ready" button
                waitingLabel.setText("Preparation phase started!");
                countdownLabel.setVisible(true);
                startTurnTimer(3);
            } else if ("Waiting for other players".equals(message)) {
                waitingLabel.setText("Waiting for other players to join...");
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
                
            }
        });
    }
    private void startTurnTimer(int countdownTime) {
        // If a timer is already running, stop it
        if (clientTimer != null) {
            clientTimer.stopTimer();  // Stop the existing timer
        }

        // Create a new ClientTimer instance with the specified countdown time
        clientTimer = new ClientTimer(countdownTime, countdownLabel, gameClient, () -> {
            Stage currentStage = (Stage) readyButton.getScene().getWindow();
            gameClient.getGameStateNotifier().removeObserver(this);
            try {
                sceneController.changeScene(currentStage,"preparation-phase.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start the ClientTimer in a new thread
        new Thread(clientTimer).start();
    }

    @Override
    public void onNotified(AttackResult attackResult) {

    }

    @Override
    public void onNotified(GameResult gameResult) {

    }

}
