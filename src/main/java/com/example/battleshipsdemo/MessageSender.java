package com.example.battleshipsdemo;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageSender implements Runnable {
    private final Player player;
    private final BlockingQueue<Object> sendQueue;
    private volatile boolean running;
    private final Thread senderThread;

    public MessageSender(Player player) {
        this.player = player;
        this.sendQueue = new LinkedBlockingQueue<>();
        this.running = true;
        // Start the sender thread immediately
        this.senderThread = new Thread(this, "MessageSender-" + player.getUsername());
        this.senderThread.setDaemon(true);
        this.senderThread.start();
    }

    public void send(Object message) {
        sendQueue.offer(message);
    }

    /**
     * Stops the sender thread.
     */
    public void stop() {
        running = false;
        senderThread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object message = sendQueue.take();
                player.sendObject(message);
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally, you may want to stop or handle errors here.
            }
        }
    }
}
