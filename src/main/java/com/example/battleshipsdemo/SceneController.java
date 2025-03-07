package com.example.battleshipsdemo;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private static String mode;
    public SceneController(){
    }
    public void changeScene(ActionEvent event, String sceneName) throws IOException {
        setStage(event);
        scene = new Scene(FXMLLoader.load(getClass().getResource(sceneName)));
        scene.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.show();
    }
    public void changeScene(Stage stage, String sceneName) throws IOException {
        this.stage = stage;
        scene = new Scene(FXMLLoader.load(getClass().getResource(sceneName)));
        scene.getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.show();
    }
    private void setStage(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Node) {
            this.stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        } else {
            System.out.println("Source is not a Node.");
        }
    }
    public Scene getScene(){return scene;}
    public static String getMode(){return mode;}
    public void setMode(String mode){
        this.mode = mode;
    }
}

