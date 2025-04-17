package com.example.battleshipsdemo;

import java.io.Serializable;

public class GameResult  implements Serializable {

    private String resultMessage;

    private boolean won;
    private Integer playerShipRemaining;
    private Integer opponentShipRemaining;

    public GameResult(boolean won,String resultMessage) {
        this.won = won;
        this.resultMessage = resultMessage;
    }

    public GameResult(boolean won,String resultMessage, Integer playerShipRemaining, Integer opponentShipRemaining) {
        this.won = won;
        this.resultMessage = resultMessage;
        this.playerShipRemaining = playerShipRemaining;
        this.opponentShipRemaining = opponentShipRemaining;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Integer getPlayerShipRemaining() {
        return playerShipRemaining;
    }

    public Integer getOpponentShipRemaining() {
        return opponentShipRemaining;
    }
}
