package com.example.battleshipsdemo;

public class ServerTimer implements Runnable {
    private final int countdownStart;  // Starting countdown time (30 seconds)
    private int remainingTime;
    private boolean running;
    private volatile boolean stopTimer = false; // Flag to control stopping the timer

    private volatile boolean terminate;
    //private GameSessionRunnable gameSessionRunnable;  // Reference to the GameSessionRunnable to switch turns
    private TimerExpiredListener listener;

    // Constructor to initialize the timer with countdown and game session reference
    public ServerTimer(int countdownStart, TimerExpiredListener listener) {
        this.countdownStart = countdownStart;
        this.remainingTime = countdownStart;
        this.running = true;
        this.stopTimer = false;
        this.terminate = false;
        this.listener = listener;
    }

    @Override
    public void run() {
        System.out.println("starting new timer");
        try {
            // Countdown loop, waits for the full 30 seconds (or countdownStart value)
            while (remainingTime > -1 && !stopTimer) {
                // Sleep for 1 second before updating the countdown
                Thread.sleep(1000);
                remainingTime--;
                //System.out.println(remainingTime);
            }

            // When the timer expires, switch turns
            if (remainingTime <= 0 || stopTimer) {
                //System.out.println("Timer reached 0");
                running = false;
                //System.out.println("ServerTimer's GameSessionRunnable hashCode: " + gameSessionRunnable.hashCode());
                //System.out.println("ServerTimer: About to call timerExpired()");
                listener.timerExpired();
                //System.out.println("it went through here");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Stop the timer manually (e.g., when the player makes their move)
    public void stopTimer() {
        this.stopTimer = true;  // This will stop the task

    }

    public boolean isRunning() {
        return running;
    }
}
