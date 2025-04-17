package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;

public class GameStateListener implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GameStateListener.class);

    private ObjectInputStream inputStream;
    private GameClient gameClient;

    private boolean keepAlive;
    private Thread thread;

    private GameStateNotifier gameStateNotifier;

    public GameStateListener(GameClient gameClient, ObjectInputStream inputStream, GameStateNotifier gameStateNotifier){
        this.gameClient = gameClient;
        this.inputStream = inputStream;
        this.gameStateNotifier=gameStateNotifier;
        keepAlive = true;
        thread = new Thread(this);
        thread.setName("Listener");
        thread.start();
    }


    @Override
    public void run(){
        try {
            while (keepAlive) {
                Object message = receiveServerMessage();
                if (message == null) {
                    // Notify the client that the connection is lost.
                    gameClient.closeConnection();
                    gameStateNotifier.notifyObservers("Disconnected from server");
                    break;  // Break out of the loop.
                }
                if (message instanceof String) {
                    String textMessage = (String) message;
                    // Notify observers (controllers) about the game state change
                    gameStateNotifier.notifyObservers(textMessage);
                } else if (message instanceof AttackResult) {
                    // If the message is an AttackResult object (contains row, column, and result)
                    AttackResult attackResult = (AttackResult) message;
                    System.out.println("Received attack result from server: " + attackResult.getResult());
                    gameStateNotifier.notifyObserversWithAttackResult(attackResult);
                } else if (message instanceof GameResult) {
                    log.info("Received gameResult {}", message);
                    // If the message is an AttackResult object (contains row, column, and result)
                    GameResult gameResult = (GameResult) message;
                    gameClient.setGameResult(gameResult);
                    gameStateNotifier.notifyObserversWithGameResult(gameResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Object receiveServerMessage() {
        try {
            // Read the result from the server (it could be a String or another object)
            Object received = inputStream.readObject();
            log.info("Received from server: {}", received);

            // If the object is a String, return it directly
            if (received instanceof String) {
                return (String) received;
            }

            // If the object is an AttackResult, return it
            if (received instanceof AttackResult) {
                return (AttackResult) received;
            }
            if (received instanceof GameResult){
                return (GameResult) received;
            }

            // Handle unexpected message types
            log.error("Received unexpected message type: {}", received.getClass().getName());
            return null;

        } catch (java.net.SocketException se) {
            if (!keepAlive) {
                log.info("Socket closed as part of a graceful shutdown.");
                return null;
            }
            // If it wasn't an intentional shutdown, handle it as an error.
            log.error("Socket exception encountered", se);
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void terminateListener(){
        log.info("Terminating Listener");
        keepAlive = false;
        try {
            // Closing the input stream can help unblock readObject()
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}
