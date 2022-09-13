package com.dadm.reto06;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TicTacToeActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button[] mBoardButtons;
    // Various text displayed
    private TextView mInfoTextView;

    private Boolean mGameOver;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_RESET_ID = 2;
    static final int DIALOG_GIVE_UP_ID = 3;

    private BoardView mBoardView;
    private char currentTurn;
    private int winner;

    private char mGoFirst;
    private int[] scores = {0,0,0};

    private TextView[] scoresTextViews = new TextView[3];

    private SharedPreferences mPrefs;

    MediaPlayer mMovementMediaPlayer;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mTiedGameMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setTheme(R.style.Theme_reto06_landscape);
        } else {
            setTheme(R.style.Theme_reto06_portrait);
        }

        setContentView(R.layout.main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        scoresTextViews[0] = findViewById(R.id.scorePlayer1);
        scoresTextViews[1] = findViewById(R.id.numberTies);
        scoresTextViews[2] = findViewById(R.id.scorePlayer2);

        mGame = new TicTacToeGame();

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        Button newGameButton = (Button) findViewById(R.id.newGameMain);
        newGameButton.setOnClickListener(view -> showCustomDialog(DIALOG_GIVE_UP_ID));

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores
        scores[0] = mPrefs.getInt("mHumanScore", 0);
        scores[1] = mPrefs.getInt("mTies", 0);
        scores[2] = mPrefs.getInt("mComputerScore", 0);
        mGameOver = mPrefs.getBoolean("mGameOver", false);

        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[mPrefs.getInt("mDifficulty", 0)]);

        if (savedInstanceState == null){
            mGoFirst = TicTacToeGame.HUMAN_PLAYER;
            startNewGame();
            showAboutDialog();
        } else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mGoFirst = savedInstanceState.getChar("mGoFirst");
            currentTurn = savedInstanceState.getChar("currentTurn");

        }
        displayScores();

        if (!mGameOver && currentTurn == TicTacToeGame.COMPUTER_PLAYER) {
            handleAndroidsMovement();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanScore", scores[0]);
        ed.putInt("mTies", scores[1]);
        ed.putInt("mComputerScore", scores[2]);
        ed.putInt("mDifficulty", mGame.getDifficultyLevel().ordinal());
        ed.putBoolean("mGameOver", mGameOver);
        ed.commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putChar("mGoFirst", mGoFirst);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("currentTurn", currentTurn);

    }


    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_dialog, null);
        builder.setView(layout);
        builder.show();
    }
    public void showCustomDialog(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(TicTacToeActivity.this);

        switch (id){
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                int selected = mGame.getDifficultyLevel().ordinal();
                System.out.println(selected);

                builder.setSingleChoiceItems(levels, selected, (dialog, item) -> {
                    dialog.dismiss(); // Close dialog
                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
                    Toast.makeText(getApplicationContext(), levels[item],
                            Toast.LENGTH_SHORT).show();
                });
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialog, id1) -> TicTacToeActivity.this.finish())
                        .setNegativeButton(R.string.no, null);
                break;

            case DIALOG_RESET_ID:
                builder.setMessage(R.string.reset_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialog, id2) -> {
                            Arrays.fill(scores, 0);
                            displayScores();
                            startNewGame();
                        })
                        .setNegativeButton(R.string.no, null);
                break;

            case DIALOG_GIVE_UP_ID:
                if (mGameOver) {
                    startNewGame();
                } else {
                    builder.setMessage(R.string.give_up_question)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, (dialog, id3) -> {
                                startNewGame();
                                scores[2]++;
                            })
                            .setNegativeButton(R.string.no, null);
                }
                break;

            default:
                System.out.println("That option does not exists!");
        }

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resetScores:
                showCustomDialog(DIALOG_RESET_ID);
                return true;
            case R.id.difficulty:
                showCustomDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.about:
                showAboutDialog();
                return true;
            case R.id.quit:
                showCustomDialog(DIALOG_QUIT_ID);
                return true;
            default:
                System.out.println("That option does not exists!");
        }
        return false;
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;

        displayScores();
        mBoardView.invalidate();

        if (mGoFirst == TicTacToeGame.HUMAN_PLAYER) {
            mInfoTextView.setText("You go first.");
            currentTurn = TicTacToeGame.HUMAN_PLAYER;
            mGoFirst = TicTacToeGame.COMPUTER_PLAYER;
        }else {
            mInfoTextView.setText("Android goes first.");
            currentTurn = TicTacToeGame.COMPUTER_PLAYER;
            mGoFirst = TicTacToeGame.HUMAN_PLAYER;
            handleAndroidsMovement();
        }
    }

    public void displayScores(){
        scoresTextViews[0].setText(String.valueOf(scores[0]));
        scoresTextViews[1].setText(String.valueOf(scores[1]));
        scoresTextViews[2].setText(String.valueOf(scores[2]));
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {

                winner = mGame.checkForWinner();

                // If no winner yet, let the computer make a move
                if (winner == 0) {
                    currentTurn = TicTacToeGame.COMPUTER_PLAYER;
                    mInfoTextView.setText("It's Android's turn.");
                }

                handleAndroidsMovement();

                if (winner == 1) {
                    mInfoTextView.setText("It's a tie!");
                    mTiedGameMediaPlayer.start();
                    scores[1] += 1;
                }else if (winner == 2) {
                    mInfoTextView.setText("You won!");
                    mHumanMediaPlayer.start();
                    scores[0] += 1;
                }

                if (winner != 0) {
                    mGameOver = true;
                    displayScores();
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private boolean setMove(char player, int location) {
        if (currentTurn == player && mGame.setMove(player, location)) {
            try {
                mMovementMediaPlayer.start();
            } catch (Exception e){
                System.out.println("Error playing movement sound");
            }

            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    private void handleAndroidsMovement(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!mGameOver && currentTurn == TicTacToeGame.COMPUTER_PLAYER) {
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                winner = mGame.checkForWinner();
                if (winner == 3){
                    mInfoTextView.setText("Android won!");
                    mComputerMediaPlayer.start();
                    scores[2] += 1;
                    mGameOver = true;

                }

                if (winner == 0) {
                    mInfoTextView.setText("It's your turn.");
                    currentTurn = TicTacToeGame.HUMAN_PLAYER;
                }
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMovementMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.movement);
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_wins);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.android_wins);
        mTiedGameMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tied_game);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMovementMediaPlayer.release();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mTiedGameMediaPlayer.release();
    }
}