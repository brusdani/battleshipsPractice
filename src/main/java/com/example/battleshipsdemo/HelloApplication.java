package com.example.battleshipsdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    public static GameClient gameClient;

    @Override
    public void start(Stage stage) throws IOException {
        // Load custom font
        Font.loadFont(getClass().getResource("/fonts/PressStart2P-Regular.ttf").toExternalForm(), 48);

        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("Battleships");
        stage.setScene(scene);

        // Initialize your client
        gameClient = new GameClient();

        // Attach a handler for the window close event
        stage.setOnCloseRequest(event -> {
            if (gameClient != null) {
                if (gameClient.isConnected()) {
                    gameClient.closeConnection();
                    logger.info("Game client disconnected successfully.");
                }
            }

            Platform.exit();
            System.exit(0);
        });

        // Now show the stage
        stage.show();
        logger.info("Starting the JavaFX application.");
    }

    public static void main(String[] args) {
        launch();
    }
}
