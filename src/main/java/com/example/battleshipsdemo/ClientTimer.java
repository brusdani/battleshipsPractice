package com.example.battleshipsdemo;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ClientTimer extends Task<Void> {
    private final Label timerLabel;  // Label to display the timer
    private final int countdownStart;  // Initial countdown value
    private int remainingTime;  // Remaining time in countdown

    private final Runnable timeoutCallback;

    private GameClient gameClient;

    // Constructor to initialize the timer and label
    public ClientTimer(int countdownStart, Label timerLabel, GameClient gameClient, Runnable timeoutCallback) {
        this.countdownStart = countdownStart;
        this.remainingTime = countdownStart;
        this.timerLabel = timerLabel;
        this.gameClient = gameClient;
        this.timeoutCallback = timeoutCallback;
    }

    @Override
    protected Void call() throws Exception {
        // Countdown loop
        while (remainingTime > 0 && !isCancelled()) {
            // Update the UI with the remaining time every second
            Platform.runLater(() -> timerLabel.setText(String.valueOf(remainingTime)));

            // Sleep for 1 second
            Thread.sleep(1000);

            // Decrease the remaining time
            remainingTime--;
        }

        // When the timer expires, update the label
        if (remainingTime <= 0) {
            //gameClient.getGameStateNotifier().notifyObservers("time is up");
            Platform.runLater(() -> {
                timerLabel.setText("Time's up!");
                if (timeoutCallback != null) {
                    timeoutCallback.run();
                }
            });
        }

        return null;
    }


    // Method to stop the timer (can be called when the player finishes their move)
    public void stopTimer() {
        this.cancel();  // This will stop the task
        Platform.runLater(() -> timerLabel.setText("Time is up."));
    }
}
