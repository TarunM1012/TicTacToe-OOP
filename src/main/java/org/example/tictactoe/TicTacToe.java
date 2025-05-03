package org.example.tictactoe;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Arrays;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class TicTacToe extends Application {
    private final GridPane gridPane = new GridPane();
    private final BorderPane borderPane = new BorderPane();
    private final Label title = new Label("Tic Tac Toe");
    private final Button[] btns = new Button[9];
    private final Button restartButton = new Button("Restart Now");
    Font font = Font.font("Roboto", FontWeight.BOLD, 30);
    private boolean gameOver = false;
    private int activePlayer = 0;
    private final String[] players = {"O", "X"};
    private final int[] gameStates = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private final int[][] winningPositions = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    @Override
    public void start(Stage stage) {
        this.createGUI();
        this.handleEvent();
        Scene scene = new Scene(borderPane, 550, 650);
        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
    }

    private void createGUI() {
        title.setFont(font);
        restartButton.setFont(font);
        restartButton.setDisable(!gameOver);
        borderPane.setTop(title);
        borderPane.setBottom(restartButton);
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setAlignment(restartButton, Pos.CENTER);
        borderPane.setPadding(new Insets(20, 20, 20, 20));
        int label = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setId(String.valueOf(label));
                button.setFont(font);
                button.setPrefHeight(150);
                button.setPrefWidth(150);
                gridPane.add(button, j, i);
                gridPane.setAlignment(Pos.CENTER);
                btns[label++] = button;
            }
        }
        borderPane.setCenter(gridPane);
    }

    private boolean isWinner(int[] pos) {
        if (gameStates[pos[0]] == -1) return false;
        return (gameStates[pos[0]] == gameStates[pos[1]]) &&
                (gameStates[pos[0]] == gameStates[pos[2]]);
    }

    private boolean isBoardFull() {
        for (int state : gameStates) {
            if (state == -1) return false;
        }
        return true;
    }

    private void checkForWinner() {
        if (gameOver) return;
        for (int i = 0; i < 8; i++) {
            if (isWinner(winningPositions[i])) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setContentText("Player " + players[activePlayer] + " won");
                alert.show();
                gameOver = true;
                restartButton.setDisable(false);
                return;
            }
        }
        if (!gameOver && isBoardFull()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setContentText("It's a draw!");
            alert.show();
            gameOver = true;
            restartButton.setDisable(false);
        }
    }

    private int minimaxWithAlphaBeta(int depth, int player, int alpha, int beta) {
        for (int[] pos : winningPositions) {
            if (isWinner(pos)) return (player == 0 ? 1 : -1);
        }
        if (isBoardFull()) return 0;
        if (player == 0) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (gameStates[i] == -1) {
                    gameStates[i] = player;
                    int score = minimaxWithAlphaBeta(depth + 1, 1 - player, alpha, beta);
                    gameStates[i] = -1;
                    best = Math.max(best, score);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha) break;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (gameStates[i] == -1) {
                    gameStates[i] = player;
                    int score = minimaxWithAlphaBeta(depth + 1, 1 - player, alpha, beta);
                    gameStates[i] = -1;
                    best = Math.min(best, score);
                    beta = Math.min(beta, best);
                    if (beta <= alpha) break;
                }
            }
            return best;
        }
    }

    private void makeMoveWithAI() {
        int bestMove = -1, bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        for (int i = 0; i < 9; i++) {
            if (gameStates[i] == -1) {
                gameStates[i] = activePlayer;
                int score = minimaxWithAlphaBeta(0, 1 - activePlayer, alpha, beta);
                gameStates[i] = -1;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        if (bestMove != -1) {
            btns[bestMove].setGraphic(new ImageView(new Image("file:target/classes/assets/cross.png/", 100, 100, false, false)));
            gameStates[bestMove] = activePlayer;
            checkForWinner();
            activePlayer = 0;
        }
    }

    private void handleEvent() {
        restartButton.setOnAction(e -> {
            gameOver = false;
            activePlayer = 0;
            Arrays.fill(gameStates, -1);
            for (Button button : btns) button.setGraphic(null);
            restartButton.setDisable(true);
        });

        for (Button button : btns) {
            button.setOnAction(e -> {
                if (gameOver) return;
                int id = Integer.parseInt(button.getId());
                if (gameStates[id] == -1) {
                    button.setGraphic(new ImageView(new Image("file:target/classes/assets/circle.png/", 100, 100, false, false)));
                    gameStates[id] = activePlayer;
                    checkForWinner();
                    activePlayer = 1;
                    if (!gameOver) {
                        PauseTransition pause = new PauseTransition(Duration.seconds(1));
                        pause.setOnFinished(ev -> makeMoveWithAI());
                        pause.play();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}