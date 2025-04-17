package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameStateNotifier {
    private static final Logger log = LoggerFactory.getLogger(GameStateNotifier.class);

    private Set<GameObserver> observers = new HashSet<>();  // List of observers (controllers)

    // Register an observer (controller)
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("Observer added: {}", observer);
        } else {
            log.info("Observer already exists: {}", observer);  // Log if observer is already added
        }
    }
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
        log.info("Observer removed: {}", observer);
    }


    // Notify all observers when a game state change occurs
    public void notifyObservers(String message) {
        for (GameObserver observer : observers) {
            observer.onNotified(message);  // Notify each observer about the state
            log.info("Notifying observer with message: {}", message);
        }
    }
    public void notifyObserversWithAttackResult(AttackResult attackResult) {
        for (GameObserver observer : observers) {
            observer.onNotified(attackResult);  // Notify each observer about the attack result
        }
    }
    public void notifyObserversWithGameResult(GameResult gameResult) {
        for (GameObserver observer : observers) {
            observer.onNotified(gameResult);  // Notify each observer about the attack result
        }
    }

    public Set<GameObserver> getObservers() {
        return observers;
    }
}

