package com.example.battleshipsdemo.client;
import java.util.Scanner;

public class ConsoleEcho {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Start a loop to continuously take user input
        while (true) {
            // Ask for user input
            System.out.print("Enter something: ");
            String input = scanner.nextLine(); // Read user input

            // Print the input back to the console
            System.out.println("You said: " + input);

            // If the user types "exit", stop the loop
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }
        }

        // Close the scanner
        scanner.close();
    }
}
