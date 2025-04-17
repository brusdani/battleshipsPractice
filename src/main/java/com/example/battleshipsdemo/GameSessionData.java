package com.example.battleshipsdemo;

public class GameSessionData {
    private static GameSessionData instance;
    private GameBoard playerBoard;

    private GameSessionData() {
        playerBoard = new GameBoard();
    }

    public static GameSessionData getInstance() {
        if (instance == null) {
            instance = new GameSessionData();
        }
        return instance;
    }

    public GameBoard getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(GameBoard board) {
        this.playerBoard = board;
    }
    public void reset() {
        this.playerBoard = new GameBoard();
    }
}
