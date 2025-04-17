package com.example.battleshipsdemo;

public interface GameObserver {
        void onNotified(String message);

        // Method to handle AttackResult objects
        void onNotified(AttackResult attackResult);
        void onNotified(GameResult gameResult);
}
