package com.dadm.reto07;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TicTacToeGame {

    public static final int BOARD_SIZE = 9;

    public static final String PLAYER_1 = "X";
    public static final String PLAYER_2 = "O";
    public static final String OPEN_SPOT = "-";

    private List<String> mBoard = new ArrayList<>(Collections.nCopies(BOARD_SIZE, OPEN_SPOT));

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert;};
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Harder;

    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        mBoard = new ArrayList<>(Collections.nCopies(BOARD_SIZE, OPEN_SPOT));
    }

    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public boolean setMove(String player, int location){
        if (!Objects.equals(mBoard.get(location), PLAYER_1) && !Objects.equals(mBoard.get(location), PLAYER_2)){
            mBoard.set(location, player);
            return true;
        }
        return false;
    }

    public int getRandomMove(){
        int move;
        do{
            move = mRand.nextInt(BOARD_SIZE);
        } while (Objects.equals(mBoard.get(move), PLAYER_1) || Objects.equals(mBoard.get(move), PLAYER_2));

        System.out.println("Computer is moving to " + (move + 1));
        return move;
    }

    public int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!Objects.equals(mBoard.get(i), PLAYER_1) && !Objects.equals(mBoard.get(i), PLAYER_2)) {
                String curr = mBoard.get(i);
                mBoard.set(i,PLAYER_2);
                if (checkForWinner() == 3) {
                    System.out.println("Computer is moving to " + (i + 1));
                    mBoard.set(i, curr);
                    return i;
                }
                mBoard.set(i, curr);
            }
        }
        return -1;
    }

    public int getBlockingMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!Objects.equals(mBoard.get(i), PLAYER_1) && !Objects.equals(mBoard.get(i), PLAYER_2)) {
                String curr = mBoard.get(i);
                mBoard.set(i,PLAYER_1);
                if (checkForWinner() == 2) {
                    System.out.println("Computer is moving to " + (i + 1));
                    mBoard.set(i, curr);
                    return i;
                }
                mBoard.set(i, curr);

            }
        }
        return -1;
    }

    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    public int getComputerMove(){
        int move = -1;
//        System.out.println("LEVEL: " + mDifficultyLevel);
        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }
    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (Objects.equals(mBoard.get(i), PLAYER_1) &&
                    Objects.equals(mBoard.get(i + 1), PLAYER_1) &&
                    Objects.equals(mBoard.get(i + 2), PLAYER_1))
                return 2;
            if (Objects.equals(mBoard.get(i), PLAYER_2) &&
                    Objects.equals(mBoard.get(i + 1), PLAYER_2) &&
                    Objects.equals(mBoard.get(i + 2), PLAYER_2))
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (Objects.equals(mBoard.get(i), PLAYER_1) &&
                    Objects.equals(mBoard.get(i + 3), PLAYER_1) &&
                    Objects.equals(mBoard.get(i + 6), PLAYER_1))
                return 2;
            if (Objects.equals(mBoard.get(i), PLAYER_2) &&
                    Objects.equals(mBoard.get(i + 3), PLAYER_2) &&
                    Objects.equals(mBoard.get(i + 6), PLAYER_2))
                return 3;
        }

        // Check for diagonal wins
        if ((Objects.equals(mBoard.get(0), PLAYER_1) &&
                Objects.equals(mBoard.get(4), PLAYER_1) &&
                Objects.equals(mBoard.get(8), PLAYER_1)) ||
                (Objects.equals(mBoard.get(2), PLAYER_1) &&
                        Objects.equals(mBoard.get(4), PLAYER_1) &&
                        Objects.equals(mBoard.get(6), PLAYER_1)))
            return 2;
        if ((Objects.equals(mBoard.get(0), PLAYER_2) &&
                Objects.equals(mBoard.get(4), PLAYER_2) &&
                Objects.equals(mBoard.get(8), PLAYER_2)) ||
                (Objects.equals(mBoard.get(2), PLAYER_2) &&
                        Objects.equals(mBoard.get(4), PLAYER_2) &&
                        Objects.equals(mBoard.get(6), PLAYER_2)))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (!Objects.equals(mBoard.get(i), PLAYER_1) && !Objects.equals(mBoard.get(i), PLAYER_2))
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public String getBoardOccupant(int location){
        return mBoard.get(location);
    }

    public List<String> getBoardState() {
        return mBoard;
    }

    public void setBoardState(List<String> mBoard){
        this.mBoard = mBoard;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.mDifficultyLevel = difficultyLevel;
    }

    public boolean hasStarted(){
        boolean started = false;
        if (mBoard == null){
            return false;
        }
        for (String cell: mBoard){
            if (!cell.equals(OPEN_SPOT)){
                started = true;
                break;
            }
        }
        return started;
    }

}