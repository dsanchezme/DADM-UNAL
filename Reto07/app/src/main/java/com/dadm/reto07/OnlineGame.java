package com.dadm.reto07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OnlineGame {

    private String id;
    private List<String> board;
    private String currentTurn;
    private int winner;
    private String goFirst;
    private List<Integer> scores;
    private boolean gameOver;
    private Player player1;
    private Player player2;

    public OnlineGame() {
    }

    public OnlineGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        board = new ArrayList<>(9);
        this.player1.setMovementChar(TicTacToeGame.PLAYER_1);
        this.player2.setMovementChar(TicTacToeGame.PLAYER_2);
        goFirst = TicTacToeGame.PLAYER_1;
        currentTurn = goFirst;
        scores = new ArrayList<>(Arrays.asList(0,0,0));
    }

    public List<String> getBoard() {
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public String getGoFirst() {
        return goFirst;
    }

    public void setGoFirst(String goFirst) {
        this.goFirst = goFirst;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("board", board);
        result.put("currentTurn", currentTurn);
        result.put("winner", winner);
        result.put("goFirst", goFirst);
        result.put("scores", scores);
        result.put("gameOver", gameOver);
        result.put("player1", player1);
        result.put("player2", player2);
        return result;
    }

    @Override
    public String toString() {
        return "OnlineGame{" +
                "id='" + id + '\'' +
                ", board=" + board +
                ", currentTurn='" + currentTurn + '\'' +
                ", winner=" + winner +
                ", goFirst='" + goFirst + '\'' +
                ", scores=" + scores +
                ", gameOver=" + gameOver +
                ", player1=" + player1 +
                ", player2=" + player2 +
                '}';
    }
}
