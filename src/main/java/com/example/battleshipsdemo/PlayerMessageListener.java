package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;

public class PlayerMessageListener implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PlayerMessageListener.class);
    private final Player player;
    private volatile boolean keepListening;
    private final Thread listenerThread;

    private DisconnectionCallback disconnectionCallback;
    private ReadyCallback readyCallback;
    private BoardCallback boardCallback;
    private AttackCallback attackCallback;

    public interface DisconnectionCallback {
        void playerDisconnected(Player player, Exception e);
    }
    public interface ReadyCallback {
        void onPlayerReady(Player player);
    }
    public interface BoardCallback {
        void onBoardReceived(Player player, GameBoard board);
    }
    public interface AttackCallback {
        void onAttackReceived(AttackData data);
    }

    public PlayerMessageListener(Player player) {
        this.player = player;
        this.keepListening = true;
        this.listenerThread = new Thread(this, "PlayerMessageListener-" + player.getUsername());
        this.listenerThread.setDaemon(true);
        this.listenerThread.start();
    }

    @Override
    public void run() {
        ObjectInputStream inputStream = player.getInputStream();
        try {
            while (keepListening) {
                Object message = inputStream.readObject();
                if (message == null) continue;

                // Handle control commands
                if (message instanceof String) {
                    String cmd = ((String) message).trim();
                    if ("PLAY_AGAIN".equalsIgnoreCase(cmd)) {
                        GameServer.requeuePlayer(player);
                        continue;
                    } else if ("EXIT".equalsIgnoreCase(cmd)) {
                        stopListening();
                        continue;
                    } else if ("READY_FOR_BATTLE".equalsIgnoreCase(cmd)) {
                        player.setReady(true);
                        if (readyCallback != null) readyCallback.onPlayerReady(player);
                        log.info("{} is ready", player);
                        continue;
                    }
                }

                // Handle game events
                if (message instanceof GameBoard) {
                    if (boardCallback != null) {
                        boardCallback.onBoardReceived(player, (GameBoard) message);
                        log.info("received gameboard from player {}", player);
                    }
                    continue;
                }
                if (message instanceof AttackData) {
                    if (attackCallback != null) {
                        attackCallback.onAttackReceived((AttackData) message);
                        log.info("received AttackData from player {}", player);
                    }
                    continue;
                }

                // Unexpected message types can be logged or ignored
                System.err.println("Unhandled message type " + message.getClass() + " from " + player.getUsername());
            }
        } catch (IOException | ClassNotFoundException e) {
            keepListening = false;
            if (disconnectionCallback != null) {
                disconnectionCallback.playerDisconnected(player, e);
            }
        }
    }

    public void stopListening() {
        keepListening = false;
        listenerThread.interrupt();
    }

    // Setters for event callbacks
    public void setDisconnectionCallback(DisconnectionCallback cb) { this.disconnectionCallback = cb; }
    public void setReadyCallback(ReadyCallback cb) { this.readyCallback = cb; }
    public void setBoardCallback(BoardCallback cb) { this.boardCallback = cb; }
    public void setAttackCallback(AttackCallback cb) { this.attackCallback = cb; }
}