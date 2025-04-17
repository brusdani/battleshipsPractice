package com.example.battleshipsdemo;

import java.util.List;

public class GlobalTicker implements Runnable {
    private final List<GameSession> activeSessions;

    public GlobalTicker(List<GameSession> activeSessions) {
        this.activeSessions = activeSessions;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);  // For example, check every second
                synchronized(activeSessions) {
                    for (GameSession session : activeSessions) {
                        session.tick();
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
