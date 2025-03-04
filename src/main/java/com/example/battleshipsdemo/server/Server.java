package com.example.battleshipsdemo.server;
import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Wait for a client connection
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                // Handle each client in a separate thread
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    // Inner class to handle communication with each client
    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Error setting up streams: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                // Keep listening for messages from the client
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    out.println("Server received: " + message);  // Echo back the message

                    // Example: If client sends "exit", stop communication
                    if (message.equalsIgnoreCase("exit")) {
                        System.out.println("Client disconnected.");
                        break; // Break the loop and stop the connection
                    }
                }
            } catch (IOException e) {
                System.err.println("Error communicating with client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close(); // Ensure the socket is closed when done
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
