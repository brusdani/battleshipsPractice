package com.example.battleshipsdemo;

import java.io.Serializable;

public class Battleship implements Serializable {
    private int size;
    private String name;
    private boolean isSunk;

    public Battleship(int size, String name) {
        this.size = size;
        this.name = name;
        this.isSunk = false;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean isSunk) {
        this.isSunk = isSunk;
    }
}

