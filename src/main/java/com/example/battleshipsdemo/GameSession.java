package com.example.battleshipsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A refactored game session that encapsulates game logic without its own thread,
 * and uses MessageSender to decouple sending from logic.
 */
public class GameSession {
    private final Player player1;
    private final Player player2;
    private final MessageSender sender1;
    private final MessageSender sender2;
    private final PlayerMessageListener listener1;
    private final PlayerMessageListener listener2;

    private static final Logger log = LoggerFactory.getLogger(GameSession.class);

    private GameBoard gameBoard1;
    private GameBoard gameBoard2;
    private boolean isPlayer1Turn;
    private boolean sessionActive;
    private boolean battleStarted = false;
    private long turnStartTime;
    private static final long TURN_TIMEOUT_MS = 30_000;

    public GameSession(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.sender1 = player1.getMessageSender();
        this.sender2 = player2.getMessageSender();
        this.gameBoard1 = new GameBoard();
        this.gameBoard2 = new GameBoard();
        this.isPlayer1Turn = true;  // Player 1 starts
        this.sessionActive = true;
        this.turnStartTime = System.currentTimeMillis();
        this.listener1 = player1.getPlayerMessageListener();
        this.listener2 = player2.getPlayerMessageListener();
    }



    /**
     * Kick off the session: send prep phase message.
     */
    public void initialize() {
        // 3) Wire the old callbacks:
        listener1.setDisconnectionCallback(this::playerDisconnected);
        listener2.setDisconnectionCallback(this::playerDisconnected);
        listener1.setReadyCallback(this::startBattle);
        listener2.setReadyCallback(this::startBattle);

        // 4) **Wire your new board & attack callbacks:**
        listener1.setBoardCallback(this::setBoard);
        listener2.setBoardCallback(this::setBoard);
        listener1.setAttackCallback(this::handleAttack);
        listener2.setAttackCallback(this::handleAttack);

        sender1.send("Preparation phase");
        sender2.send("Preparation phase");

        player1.setReady(false);
        player2.setReady(false);
    }

    /**
     * Called when both players have placed their boards.
     */
    public void startBattle(Player player) {
        if (player1.isReady() && player2.isReady()) {
            sender1.send("BattleStarting");
            sender2.send("BattleStarting");
            notifyTurn();
            battleStarted = true;
        }

    }

    /**
     * Notify players whose turn it is.
     */
    public void notifyTurn() {
        if (isPlayer1Turn) {
            sender1.send("Your turn!");
            sender2.send("Waiting");
        } else {
            sender1.send("Waiting");
            sender2.send("Your turn!");
        }
        // reset timer
        turnStartTime = System.currentTimeMillis();
    }

    /**
     * Process an attack from the current player.
     */
    public void handleAttack(AttackData data) {
        AttackResult result;
        if (isPlayer1Turn) {
            boolean hit = gameBoard2.attack(data.getRow(), data.getCol());
            result = new AttackResult(data.getRow(), data.getCol(), hit ? "Hit" : "Miss", player1.getUsername());
        } else {
            boolean hit = gameBoard1.attack(data.getRow(), data.getCol());
            result = new AttackResult(data.getRow(), data.getCol(), hit ? "Hit" : "Miss", player2.getUsername());
        }
        sender1.send(result);
        sender2.send(result);
        // advance turn
        switchTurn();
    }

    /**
     * Switches turn and notifies players.
     */
    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
        notifyTurn();
    }

    /**
     * Periodic tick called by scheduler to enforce timeouts.
     */
    public void tick() {
        log.debug("[Session {}] tick() called. battleStarted={}  sessionActive={}",
                this, battleStarted, sessionActive);
        if (!sessionActive) {
            log.debug("[Session {}] tick() ignoring because sessionActive=false", this);
            return;
        }
        if (!battleStarted) {
            log.debug("[Session {}] tick() ignoring because battle not started yet", this);
            return;
        }


        if (!sessionActive || !battleStarted) return;
        long now = System.currentTimeMillis();
        if (now - turnStartTime >= TURN_TIMEOUT_MS) {
            // timeout - skip or switch
            sender1.send("Turn timed out");
            sender2.send("Turn timed out");
            switchTurn();
        }
        // check game over
        if (gameBoard1.isGameOver() || gameBoard2.isGameOver()) {
            log.warn("[Session {}] game over detected in tick(), ending session", this);
            endSession();
        }
    }

    /**
     * Clean up and notify results.
     */
    public void endSession() {
        GameResult r1 = determineResultFor(player1);
        GameResult r2 = determineResultFor(player2);
        sender1.send(r1);
        sender2.send(r2);
        sessionActive = false;
        sender1.stop();
        sender2.stop();
    }

    private GameResult determineResultFor(Player p) {
        if (gameBoard2.isGameOver() && p.equals(player1)) {
            return new GameResult(true, "You won!");
        } else if (gameBoard1.isGameOver() && p.equals(player2)) {
            return new GameResult(true, "You won!");
        } else {
            return new GameResult(false, "You lost.");
        }
    }

    // Methods to handle board placement events:
    public void setBoard(Player player, GameBoard board) {
        if (player.equals(player1)) {
            this.gameBoard1 = board;
            sender1.send("Your GameBoard has been received.");
        } else {
            this.gameBoard2 = board;
            sender2.send("Your GameBoard has been received.");
        }
    }

    private void handlePlayerDisconnection(Player disconnectedPlayer) {
        // Log the disconnection event.
        log.info("Player {} has disconnected. Ending game session.", disconnectedPlayer.getUsername());
        try {
            if (disconnectedPlayer.equals(player1)) {
                sender2.send("Opponent disconnected");
            } else {
                sender1.send("Opponent disconnected");
            }

        } catch (Exception e) {
            log.info("Exception occured");
        }
        sessionActive = false;
        System.out.println("Session active: " + sessionActive);
        // Optionally, you might break out or notify waiting threads:
        synchronized (this) {
            notifyAll();
        }
    }
    public void playerDisconnected(Player player, Exception e) {
        handlePlayerDisconnection(player);
    }

    // Additional event handlers (e.g., disconnection) can be added similarly.
}
