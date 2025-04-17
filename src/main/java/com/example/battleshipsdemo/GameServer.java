package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer implements PlayerMessageListener.DisconnectionCallback {
    private static final Logger log = LoggerFactory.getLogger(GameServer.class);
    
    protected static final ConcurrentLinkedQueue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();

    protected static final List<GameSession> activeGames = new ArrayList<>();

    private Player player;
    private static GameServer instance;


    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        instance = gameServer;
        log.info("Server Started");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            synchronized(activeGames) {
                for (GameSession session : activeGames) {
                    session.tick();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            log.info("Server is listening on port 8080...");
            new Thread(() -> {
                try {
                    monitorWaitingQueue();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            while (true) {

                // Accept client connections
                Socket clientSocket = serverSocket.accept();
                log.info("Client connected: " + clientSocket.getInetAddress());
                Player player = new Player(clientSocket);
                try {
                    UserData userData = (UserData) player.getInputStream().readObject();
                    player.setUsername(userData.getUsername());
                    PlayerMessageListener playerMessageListener = new PlayerMessageListener(player);
                    playerMessageListener.setDisconnectionCallback(gameServer);
                    player.setPlayerMessageListener(playerMessageListener);
                    MessageSender messageSender = new MessageSender(player);
                    player.setMessageSender(messageSender);

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                synchronized (waitingPlayers) {
                    waitingPlayers.add(player);
                    log.info("Player added to waiting queue. Waiting players: {}", waitingPlayers.size());
                }

            }
        } catch (IOException e) {
            log.error("Error occurred while starting the server", e);
        }
        //scheduler.shutdown();
        log.info("Server terminated");
    }

    private static void monitorWaitingQueue() throws IOException {
        while (true) {
            try {
                Thread.sleep(3000);  // Check the queue every 3000ms
                //logActiveGames();
                synchronized (waitingPlayers) {
                    // If there are two players in the queue, start a new game session
                    if (waitingPlayers.size() >= 2) {
                        Player player1 = waitingPlayers.poll();
                        Player player2 = waitingPlayers.poll();
                        // Create a new GameSession for these two players
                        GameSession gameSession = new GameSession(player1,player2);
                        activeGames.add(gameSession);
                        gameSession.initialize();
                        log.info("Game session started with players: {} and {}", player1.getUsername(), player2.getUsername());
                        // Start the game session in a new thread
                        //Thread gameSessionThread = new Thread(gameSession);
                        //gameSessionThread.start();
                    }
                }
                //logActiveGames();
            } catch (InterruptedException e) {
                log.error("Queue monitoring thread interrupted", e);
            }
        }

    }
    private static void logActiveGames() {
        // Log the current active games
        log.info("Active Games Count: {}", activeGames.size());
        for (GameSession game : activeGames) {
            log.info("Active Game: {}", game);  // You can customize what details of the game to log
        }
    }
    public static void requeuePlayer(Player player) {
        synchronized (waitingPlayers) {
            player.getPlayerMessageListener().setDisconnectionCallback(instance);
            waitingPlayers.add(player);
        }
        log.info("Player {} requeued. Total waiting players: {}", player.getUsername(), waitingPlayers.size());
    }


    @Override
    public void playerDisconnected(Player player, Exception e) {
        // Remove player from the waiting queue if present.
        synchronized (GameServer.waitingPlayers) {
            if (GameServer.waitingPlayers.remove(player)) {
                log.info("Player {} disconnected and was removed from the waiting queue.", player.getUsername());
            }
        }
    }
}
