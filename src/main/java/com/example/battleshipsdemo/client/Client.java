package com.example.battleshipsdemo.client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Start an interactive loop to send messages to the server
            String message;
            while (true) {
                // Prompt the user for input
                System.out.print("Enter message to send to server: ");
                message = scanner.nextLine(); // Read the input message

                // Send the message to the server
                out.println(message);

                // Read and print the server's response
                String response = in.readLine();
                System.out.println("Server says: " + response);

                // Exit the loop if the user types "exit"
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
