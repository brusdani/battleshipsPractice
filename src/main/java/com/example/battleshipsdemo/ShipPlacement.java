package com.example.battleshipsdemo;

public class ShipPlacement {
    private Battleship ship;
    private int row;
    private int col;
    private boolean isHorizontal;

    // Constructor to initialize the ship placement
    public ShipPlacement(Battleship ship, int row, int col, boolean isHorizontal) {
        this.ship = ship;
        this.row = row;
        this.col = col;
        this.isHorizontal = isHorizontal;
    }

    // Getters
    public Battleship getShip() {
        return ship;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    // Setters (if needed, e.g., for updating the position)
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    // Override toString() to display ship information
    @Override
    public String toString() {
        return ship.getName() + " at (" + row + ", " + col + ") - " + (isHorizontal ? "Horizontal" : "Vertical");
    }
}
