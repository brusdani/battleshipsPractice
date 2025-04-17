package com.example.battleshipsdemo;

import java.io.*;
import java.net.Socket;

public class Player {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private PlayerMessageListener playerMessageListener;
    private MessageSender messageSender;

    // Additional player information (e.g., username) can be added here
    private String username;

    private volatile boolean ready;

    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    // Queue to store incoming messages from the player


    // Getter methods for socket, inputStream, and outputStream
    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    // Setter and getter for the player's username (optional)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Send an object to the player
    public void sendObject(Object obj) throws IOException {
        outputStream.writeObject(obj);
        outputStream.flush();  // Ensure data is sent immediately
    }

    // Read an object from the player
    public Object readObject() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    // Close the player's streams and socket
    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    public PlayerMessageListener getPlayerMessageListener() {
        return playerMessageListener;
    }

    public void setPlayerMessageListener(PlayerMessageListener playerMessageListener) {
        this.playerMessageListener = playerMessageListener;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}

