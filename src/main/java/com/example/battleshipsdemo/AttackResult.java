package com.example.battleshipsdemo;

import java.io.Serializable;

public class AttackResult implements Serializable {
    private int row;
    private int col;
    private String result;
    private String sunkShipName;
    private String attacker;

    // Constructor, getters, setters, etc.
    public AttackResult(int row, int col, String result, String attacker) {
        this.row = row;
        this.col = col;
        this.result = result;
        this.attacker = attacker;
    }
    public AttackResult(int row, int col, String result, String sunkShipName,String attacker) {
        this.row = row;
        this.col = col;
        this.result = result;
        this.sunkShipName = sunkShipName;
        this.attacker = attacker;
    }

    public String getSunkShipName() {
        return sunkShipName;
    }

    public void setSunkShipName(String sunkShipName) {
        this.sunkShipName = sunkShipName;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getResult() { return result; }

    public String getAttacker() {
        return attacker;
    }
}
