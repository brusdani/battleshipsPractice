package com.example.battleshipsdemo;

import java.util.ArrayList;
import java.util.List;

public class ShipPlacement {
    private Battleship ship;
    private List<int[]> occupiedCoordinates;  // List to store all coordinates occupied by the ship

    // Constructor to initialize the ship placement
    public ShipPlacement(Battleship ship, int row, int col, boolean isHorizontal) {
        this.ship = ship;
        this.occupiedCoordinates = new ArrayList<>();

        // Store the occupied coordinates based on the ship's position and orientation
        if (isHorizontal) {
            for (int i = 0; i < ship.getSize(); i++) {
                occupiedCoordinates.add(new int[]{row, col + i});  // Store each coordinate
            }
        } else {
            for (int i = 0; i < ship.getSize(); i++) {
                occupiedCoordinates.add(new int[]{row + i, col});  // Store each coordinate
            }
        }
    }

    // Getters
    public Battleship getShip() {
        return ship;
    }

    public List<int[]> getOccupiedCoordinates() {
        return occupiedCoordinates;
    }

    // Override toString() to display ship information
    @Override
    public String toString() {
        return ship.getName() + " occupies coordinates: " + occupiedCoordinates;
    }
}

