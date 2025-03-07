package com.example.battleshipsdemo;

import java.io.Serializable;

public class AttackData implements Serializable {
    private int row;
    private int col;

    public AttackData(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

