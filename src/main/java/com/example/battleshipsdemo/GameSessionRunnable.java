///*
//package com.example.battleshipsdemo;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.util.concurrent.*;
//
//import static com.example.battleshipsdemo.GameServer.activeGames;
//import static com.example.battleshipsdemo.GameServer.waitingPlayers;
//
//
//
//public class GameSessionRunnable implements Runnable, TimerExpiredListener, PlayerMessageListener.DisconnectionCallback {
//
//        private static final Logger log = LoggerFactory.getLogger(GameSessionRunnable.class);
//        private final Player player1;
//        private final Player player2;
//        private GameBoard gameBoard1;
//        private GameBoard gameBoard2;
//        private volatile boolean isPlayer1Turn;
//        private volatile boolean sessionActive;
//
//        private GamePhase currentPhase;
//
//        private ServerTimer serverTimer;
//        private PlayerMessageListener listener1;
//        private PlayerMessageListener listener2;
//        private final CountDownLatch readyLatch = new CountDownLatch(2);
//
//
//    public ServerTimer getServerTimer() {
//        return serverTimer;
//    }
//
//    public GameSessionRunnable(Player player1, Player player2) throws IOException {
//            this.player1 = player1;
//            this.player2 = player2;
//            this.gameBoard1 = new GameBoard();
//            this.gameBoard2 = new GameBoard();
//            this.isPlayer1Turn = true;
//            this.sessionActive = true;
//            this.currentPhase = GamePhase.PREPARATION; // Start with preparation phase
//        }
//
//        @Override
//        public void run() {
//            try {
//                // Initialize game session
//                log.info("Game session initialized");
//
//                // Send initial message to both players
//                player1.getOutputStream().writeObject("Preparation phase");
//                player2.getOutputStream().writeObject("Preparation phase");
//
//
//                // Start the game phase setup (preparation phase)
//                clearInputStream(player1.getInputStream());
//                clearInputStream(player2.getInputStream());
//
//                listener1 = player1.getPlayerMessageListener();
//                listener2 = player2.getPlayerMessageListener();
//
//                listener1.setDisconnectionCallback(this);
//                listener2.setDisconnectionCallback(this);
//                listener1.setReadyCallback(this::signalBattleReady);
//                listener2.setReadyCallback(this::signalBattleReady);
//
//
//                if (currentPhase == GamePhase.PREPARATION) {
//
//                    // Handle both players' ship placement concurrently
//                    //Thread player1Thread = new Thread(() -> handlePlayerBoard(1, player1.getInputStream(), player1.getOutputStream()));
//                    //Thread player2Thread = new Thread(() -> handlePlayerBoard(2, player2.getInputStream(), player2.getOutputStream()));
//                    Thread player1Thread = new Thread(() -> handlePlayerBoardRefactor(1, player1 , player1.getPlayerMessageListener()));
//                    Thread player2Thread = new Thread(() -> handlePlayerBoardRefactor(2, player2 , player2.getPlayerMessageListener()));
//                    player1Thread.start();
//                    player2Thread.start();
//
//
//                    player1Thread.join();
//                    player2Thread.join();
//
//
//                    if(sessionActive) {
//                        log.info("Waiting for both players to be ready for battle...");
//                        readyLatch.await();
//                    }
//
//                    if (currentPhase == GamePhase.PREPARATION) {
//                        startBattle();  // Start the battle phase if players are done early
//                    }
//                }
//
//
//
//                // Main game loop
//                while (sessionActive) {
//                    if (isGameOver()) {
//                        Integer player1Remaining = countRemainingShips(gameBoard1);
//                        Integer player2Remaining = countRemainingShips(gameBoard2);
//                        GameResult gameResultP1 = determineGameResultFor(player1);
//                        GameResult gameResultP2 = determineGameResultFor(player2);
//                        GameResult updatedResultP1 = new GameResult(gameResultP1.isWon(),gameResultP1.getResultMessage(),player1Remaining,player2Remaining);
//                        GameResult updatedResultP2 = new GameResult(gameResultP2.isWon(),gameResultP2.getResultMessage(), player2Remaining, player1Remaining);
//                        player1.getOutputStream().writeObject(updatedResultP1);
//                        player2.getOutputStream().writeObject(updatedResultP2);
//                        serverTimer.stopTimer();
//
//
//                        //handlePlayerDecision(player1, listener1);
//                        //handlePlayerDecision(player2, listener2);
//                        break; // Exit the game loop
//                    }
//                    // Handle player attacks (turn-based)
//                        if (isPlayer1Turn()) {
//                            log.info("Processing Player 1's turn...");
//                            handlePlayerAttack3(player1, player2, listener1);
//                        } else {
//                            log.info("Processing Player 2's turn...");
//                            handlePlayerAttack3(player2, player1, listener2);
//                        }
//                        waitForTurnSwitch();
//
//                    }
//                    activeGames.remove(this);
//
//            } catch (IOException | ClassNotFoundException | InterruptedException e) {
//                log.error("Error during game session", e);
//            }
//            finally {
//                if (listener1 != null) listener1.setDisconnectionCallback(null);
//                if (listener2 != null) listener2.setDisconnectionCallback(null);
//                if (listener1 != null) listener1.setReadyCallback(null);
//                if (listener2 != null) listener2.setReadyCallback(null);
//                log.info("Game session finished");
//            }
//        }
//
//        private void handlePlayerBoard(int playerNumber, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
//            log.info("Starting timer");
//            try {
//                GameBoard gameBoard = (GameBoard) inputStream.readObject();
//                if (playerNumber == 1) {
//                    setGameBoard1(gameBoard);
//                    log.info("Player 1's GameBoard received and ready");
//                } else {
//                    setGameBoard2(gameBoard);
//                    log.info("Player 2's GameBoard received and ready");
//                }
//                outputStream.writeObject("Your GameBoard has been received.");
//                outputStream.flush();
//            } catch (IOException | ClassNotFoundException e) {
//                log.error("Error handling player board", e);
//            }
//        }
//
//        private void handlePlayerAttack(Player attackingPlayer, Player defendingPlayer) throws IOException, ClassNotFoundException, InterruptedException {
//            AttackData attackData = (AttackData) attackingPlayer.getInputStream().readObject();
//            System.out.println("Received attack data from player: " + attackData);
//            AttackResult attackResult = handleAttack(attackData);
//            System.out.println("Handle attack method called");
//            attackingPlayer.getOutputStream().writeObject(attackResult);
//            defendingPlayer.getOutputStream().writeObject(attackResult);
//            serverTimer.stopTimer();
//        }
//
//    private void startBattle() throws IOException {
//        if(sessionActive) {
//            player1.getOutputStream().writeObject("BattleStarting");
//            player2.getOutputStream().writeObject("BattleStarting");
//            isPlayer1Turn = true;
//
//            startTurnTimer();
//            notifyTurn();
//        }
//        else return;
//        }
//
//    public synchronized void switchTurn() {
//        isPlayer1Turn = !isPlayer1Turn;
//        System.out.println("Turn switched. isPlayer1Turn: " + isPlayer1Turn); // Add this log
//        //notifyAll();
//    }
//
//    public void notifyTurn() {
//            try {
//                if (isPlayer1Turn) {
//                    player1.getOutputStream().writeObject("Your turn!");
//                    player2.getOutputStream().writeObject("Waiting");
//                } else {
//                    player1.getOutputStream().writeObject("Waiting");
//                    player2.getOutputStream().writeObject("Your turn!");
//                }
//                player1.getOutputStream().flush();
//                player2.getOutputStream().flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private boolean isGameOver() {
//            return gameBoard1.isGameOver() || gameBoard2.isGameOver();
//        }
//
//        private String getWinner() {
//            if (gameBoard1.isGameOver()) {
//                return "Player 2 wins!";
//            } else if (gameBoard2.isGameOver()) {
//                return "Player 1 wins!";
//            }
//            return "No winner yet.";
//        }
//    private GameResult determineGameResultFor(Player player) {
//        // Determine outcome based on game boards:
//        if (gameBoard1.isGameOver()) {
//            // If player1's board is over, then player1 lost, player2 won
//
//            return player.equals(player1) ? new GameResult(false, "You lost!") : new GameResult(true, "You won!");
//        } else if (gameBoard2.isGameOver()) {
//            return player.equals(player2) ? new GameResult(false, "You lost!") : new GameResult(true, "You won!");
//        }
//        return new GameResult(false, "Game over!",0,0);
//    }
//
//
//        // Getter and Setter methods
//        public GameBoard getGameBoard1() {
//            return gameBoard1;
//        }
//
//        public void setGameBoard1(GameBoard gameBoard1) {
//            this.gameBoard1 = gameBoard1;
//        }
//
//        public GameBoard getGameBoard2() {
//            return gameBoard2;
//        }
//
//        public void setGameBoard2(GameBoard gameBoard2) {
//            this.gameBoard2 = gameBoard2;
//        }
//
//        public boolean isPlayer1Turn() {
//            return isPlayer1Turn;
//        }
//
//        // Clear input stream (utility method)
//        private void clearInputStream(ObjectInputStream inputStream) throws IOException {
//            while (inputStream.available() > 0) {
//                inputStream.read();
//            }
//        }
//    public AttackResult handleAttack(AttackData attackData) {
//        String result;
//        String attacker;
//
//        // Determine if it's com.example.battleshipsdemo.Player 1's or com.example.battleshipsdemo.Player 2's turn
//        if (isPlayer1Turn) {
//            attacker= player1.getUsername();
//            //Player 1 is attacking Player 2's board
//            result = gameBoard2.attack(attackData.getRow(), attackData.getCol()) ? "Hit" : "Miss";
//        } else {
//            attacker = player2.getUsername();
//            // Player 2 is attacking Player 1's board
//            result = gameBoard1.attack(attackData.getRow(), attackData.getCol()) ? "Hit" : "Miss";
//        }
//
//        // Return an AttackResult instance with the result
//        return new AttackResult(attackData.getRow(), attackData.getCol(), result, attacker);
//    }
//    public AttackResult handleAttack2(AttackData attackData) {
//        String result;
//        String sunkShipName = null;
//        Battleship sunkShip = null;
//        String attacker;
//
//        if (isPlayer1Turn) {
//            attacker = player1.getUsername();
//            boolean isHit = gameBoard2.attack(attackData.getRow(), attackData.getCol());
//            result = isHit ? "Hit" : "Miss";
//            if (isHit) {
//                sunkShip = gameBoard2.getHitShip(attackData.getRow(), attackData.getCol());
//            }
//        } else {
//            attacker = player2.getUsername();
//            boolean isHit = gameBoard1.attack(attackData.getRow(), attackData.getCol());
//            result = isHit ? "Hit" : "Miss";
//            if (isHit) {
//                sunkShip = gameBoard1.getHitShip(attackData.getRow(), attackData.getCol());
//            }
//        }
//
//        if (sunkShip != null && sunkShip.isSunk()) {
//            sunkShipName = sunkShip.getName();
//        }
//
//        // Create the AttackResult with the additional sunkShipName info.
//        return new AttackResult(attackData.getRow(), attackData.getCol(), result, sunkShipName, attacker);
//    }
//
//    private void handlePlayerDecision(Player player, PlayerMessageListener messageListener) {
//        try {
//            // Wait for the player's decision (either "Play Again" or "Exit")
//            //String playerResponse = (String) player.getInputStream().readObject();
//            Object message = messageListener.pollMessage(30, TimeUnit.SECONDS);
//            String playerResponse;
//
//            if (message == null) {
//                // No response received within the timeout period.
//                // You can choose a default behavior here—here, we log a warning and treat it as "Exit"
//                log.warn("No response received from player {} within the timeout period.", player.getUsername());
//                playerResponse = "Exit";
//                messageListener.stopListening();
//            } else {
//                playerResponse = (String) message;
//
//            }
//            log.info("Received decision from player: {}", playerResponse);
//
//            if ("Exit".equals(playerResponse)) {
//                // If the player chooses "Exit", close their connection
//                closeConnection(player.getSocket(), player.getInputStream(), player.getOutputStream());
//                log.info("Player chose to exit, connection closed.");
//            } else if ("Play Again".equals(playerResponse)) {
//                // If the player chooses "Play Again", add them back to the waiting queue
//                synchronized (waitingPlayers) {
//                    waitingPlayers.add(player);
//                    log.info("Player added to queue: {}", player.getUsername());
//                    log.info("Player added back to waiting queue: Waiting players: {}", waitingPlayers.size());
//                }
//            } else {
//                log.error("Unexpected response from player: {}", playerResponse);
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void closeConnection(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
//        try {
//            inputStream.close();
//            outputStream.close();
//            socket.close();
//        } catch (IOException e) {
//            log.error("Error closing connection", e);
//        }
//    }
//    public void startTurnTimer() {
//        if (!sessionActive) {
//            return;
//        }
//        // Create and start a new ServerTimer for the current player's turn (30 seconds)
//        if (serverTimer != null) {
//            //System.out.println("there is existing timer - stopping hashCode: " + serverTimer.hashCode());
//            serverTimer.stopTimer();  // Stop any existing timer before starting a new one
//        }
//
//        serverTimer = new ServerTimer(30, this);
//        //System.out.println("Starting new timer with GameSessionRunnable hashCode: " + this.hashCode());
//        new Thread(serverTimer).start();
//    }
//    // Wait for the turn to be switched
//    public void waitForTurnSwitch() throws InterruptedException {
//        log.info("Main thread about to enter synchronized block in waitForTurnSwitch.");
//        synchronized (this) {
//            if (!sessionActive) {
//                return;
//            }
//            log.info("Main thread entering wait state."); // Add this
//            wait();  // Wait until timer expires or some other condition
//            log.info("Main thread woke up from wait state."); // Add this
//        }
//    }
//    @Override
//    public void timerExpired() {
//        //log.info(">>> timerExpired() called due to natural expiration <<<");
//        //log.info("Current isPlayer1Turn: {}", isPlayer1Turn)
//        synchronized (this) {
//            switchTurn();
//            log.info("After switchTurn(), isPlayer1Turn: {}", isPlayer1Turn);
//            notifyTurn();
//            //log.info("After startTurnTimer(), serverTimer hashCode: {}", serverTimer != null ? serverTimer.hashCode() : "null");
//            notifyAll();
//            log.info("notifyTurn called in timerExpired()");
//            System.out.println("Timer ended");
//        }
//    }
//    private void handlePlayerAttack3(Player attackingPlayer, Player defendingPlayer, PlayerMessageListener messageListener)
//            throws IOException, ClassNotFoundException, InterruptedException {
//
//        final long totalTimeoutMs = 30_000;
//        final long pollIntervalMs = 3000;
//        long waited = 0;
//        //Object message = null;
//
//        // Loop until we've waited for 30 seconds OR the session becomes inactive.
//        //while (waited < totalTimeoutMs && sessionActive && message == null) {
//            //long interval = Math.min(pollIntervalMs, totalTimeoutMs - waited);
//            //message = messageListener.pollMessage(interval, TimeUnit.MILLISECONDS);
//            //waited += interval;
//        //}
//        // Poll for an AttackData object from the listener with a 30-second timeout.
//        Object message = messageListener.pollMessage(30, TimeUnit.SECONDS);
//
//        if (message != null && message instanceof AttackData) {
//            AttackData attackData = (AttackData) message;
//            log.info("Received attack data from player: " + attackData);
//            AttackResult attackResult = handleAttack2(attackData);
//            log.info("Handle attack method called");
//
//            // Send the attack result to both players.
//            attackingPlayer.getOutputStream().writeObject(attackResult);
//            defendingPlayer.getOutputStream().writeObject(attackResult);
//        } else {
//            //log.info("Received an unexpected message. Type: {}. Contents: {}",
//                    //message.getClass().getName(), message.toString());
//            // Timeout or invalid message: Player didn't send valid attack data in time.
//            log.info("Player did not send attack data within 30 seconds. Skipping the attack...");
//            // Optionally, notify both players about the skipped turn.
//        }
//
//        // Stop the turn timer.
//        serverTimer.stopTimer();
//        if (sessionActive) {
//            startTurnTimer();
//        }
//    }
//    private void handlePlayerBoardRefactor(int playerNumber, Player player, PlayerMessageListener listener) {
//        log.info("Handling player board (refactored) for player {}", playerNumber);
//
//        // Early exit if the session is already inactive.
//        if (!sessionActive) {
//            log.info("Session inactive, exiting board placement for player {}", player.getUsername());
//            return;
//        }
//
//        try {
//            Object message = listener.pollMessage(30, TimeUnit.SECONDS);
//            if (!sessionActive) {  // Check again after unblocking
//                log.info("Session inactive after waiting, exiting board placement for player {}", player.getUsername());
//                return;
//            }
//            if (message == null) {
//                log.warn("Timeout: No GameBoard received from player {} ({}).", playerNumber, player.getUsername());
//                // Handle disconnection if needed, e.g.,
//                handlePlayerDisconnection(player);
//                return;
//            } else if (message instanceof GameBoard) {
//                GameBoard board = (GameBoard) message;
//                if (playerNumber == 1) {
//                    setGameBoard1(board);
//                    log.info("Player 1's GameBoard received and ready via listener.");
//                } else {
//                    setGameBoard2(board);
//                    log.info("Player 2's GameBoard received and ready via listener.");
//                }
//            } else {
//                log.error("Unexpected message type received from player {}: {}.", player.getUsername(), message.getClass().getName());
//            }
//            player.getOutputStream().writeObject("Your GameBoard has been received.");
//            player.getOutputStream().flush();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error("Interrupted while waiting for GameBoard from player {}.", player.getUsername());
//            handlePlayerDisconnection(player);
//        } catch (IOException e) {
//            log.error("IOException while sending acknowledgment to player {}: {}", player.getUsername(), e.getMessage());
//            handlePlayerDisconnection(player);
//        }
//    }
//
//    private void handlePlayerDisconnection(Player disconnectedPlayer) {
//        // Log the disconnection event.
//        log.info("Player {} has disconnected. Ending game session.", disconnectedPlayer.getUsername());
//        // Stop the timer if running.
//        if (serverTimer != null) {
//            serverTimer.stopTimer();
//            System.out.println("Timer stopped");
//        }
//        try {
//            if (disconnectedPlayer.equals(player1)) {
//                player2.getOutputStream().writeObject("Opponent disconnected");
//                player2.getOutputStream().flush();
//            } else {
//                player1.getOutputStream().writeObject("Opponent disconnected");
//                player1.getOutputStream().flush();
//            }
//        } catch (IOException e) {
//            log.error("Error notifying remaining player about disconnection", e);
//        }
//        sessionActive = false;
//        System.out.println("Session active: " + sessionActive);
//        // Optionally, you might break out or notify waiting threads:
//        synchronized (this) {
//            notifyAll();
//        }
//    }
//    @Override
//    public void playerDisconnected(Player player, Exception e) {
//        handlePlayerDisconnection(player);
//    }
//    public void signalBattleReady(Player player) {
//        log.info("Player {} is ready for battle.", player.getUsername());
//        readyLatch.countDown();
//    }
//    private Integer countRemainingShips(GameBoard board) {
//        Integer remaining = 0;
//        for (ShipPlacement placement : board.getPlacedShips()) {
//            if (!placement.getShip().isSunk()){
//                remaining++;
//            }
//        }
//        return remaining;
//    }
//}
//
//
//
//
//
//
//
//
//
//*/
