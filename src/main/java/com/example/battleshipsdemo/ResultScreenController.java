package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class ResultScreenController {
    private SceneController sceneController = new SceneController();

    private GameResult gameResult;

    private GameClient gameClient;

    @FXML
    private Label gameResultLabel;
    @FXML
    private Label playerShipsLabel;
    @FXML
    private Label opponentShipsLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button exitButton;


    public void initialize(){

        Platform.runLater(() -> {
            gameClient = HelloApplication.gameClient;
            gameResult = gameClient.getGameResult();
            if (gameResult != null) {
                gameResultLabel.setText(gameResult.getResultMessage());
                playerShipsLabel.setText("Your ships remaining: " + gameResult.getPlayerShipRemaining().toString());
                opponentShipsLabel.setText("Opponent's ships remaining: " + gameResult.getOpponentShipRemaining().toString());
            }
        });
    }
    @FXML
    private void playAgainButtonClick (ActionEvent event)throws IOException {
        gameClient.sendData("PLAY_AGAIN");
        GameSessionData.getInstance().reset();
        sceneController.changeScene(event, "waiting-room.fxml");
    }
    @FXML
    private void exitButtonClick (ActionEvent event)throws IOException {
        gameClient.sendData("EXIT");
        gameClient.closeConnection();
        GameSessionData.getInstance().reset();
        Platform.exit();
        System.exit(0);
    }
}
