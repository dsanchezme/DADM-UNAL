package com.dadm.reto05;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;

public class TicTacToeActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;

    private Boolean mGameOver;

    private Button newGameButton;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private BoardView mBoardView;
    private char currentTurn;
    private int winner;

    MediaPlayer mMovementMediaPlayer;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mTiedGameMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
//        mBoardView.setBackground(ContextCompat.getDrawable(this, R.drawable.board_background));

        startNewGame();

        newGameButton = (Button) findViewById(R.id.newGameMain);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });

        showAboutDialog();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        return true;
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

                builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss(); // Close dialog
                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
                        Toast.makeText(getApplicationContext(), levels[item],
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                break;

        }


        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGame:
                startNewGame();
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
        }
        return false;
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;

        mBoardView.invalidate();

        // Human goes first
        mInfoTextView.setText("You go first.");
        currentTurn = TicTacToeGame.HUMAN_PLAYER;
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){

                winner = mGame.checkForWinner();

                // If no winner yet, let the computer make a move
                if (winner == 0){
                    currentTurn = TicTacToeGame.COMPUTER_PLAYER;
                    mInfoTextView.setText("It's Android's turn.");
                    System.out.println("Android's turn :)");
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentTurn == TicTacToeGame.COMPUTER_PLAYER) {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            winner = mGame.checkForWinner();
                            if (winner == 3){
                                mInfoTextView.setText("Android won!");
                                mComputerMediaPlayer.start();
                            }
                        }
                        if (winner == 0) {
                            mInfoTextView.setText("It's your turn.");
                            System.out.println("It's your turn :(");
                            currentTurn = TicTacToeGame.HUMAN_PLAYER;

                        }
                    }
                }, 1000);

                if (winner == 1) {
                    mInfoTextView.setText("It's a tie!");
                    mTiedGameMediaPlayer.start();
                }else if (winner == 2) {
                    mInfoTextView.setText("You won!");
                    mHumanMediaPlayer.start();
                }

                if (winner != 0)
                    mGameOver = true;

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }

        private boolean setMove(char player, int location) {
            if (currentTurn == player && mGame.setMove(player, location)) {
                mMovementMediaPlayer.start();
                mBoardView.invalidate(); // Redraw the board
                return true;
            }
            return false;
        }
    };

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