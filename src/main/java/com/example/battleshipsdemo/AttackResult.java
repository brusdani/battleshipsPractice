package com.example.battleshipsdemo;

import java.io.Serializable;

public class AttackResult implements Serializable {
    private int row;
    private int col;
    private String result;

    // Constructor, getters, setters, etc.
    public AttackResult(int row, int col, String result) {
        this.row = row;
        this.col = col;
        this.result = result;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getResult() { return result; }
}
