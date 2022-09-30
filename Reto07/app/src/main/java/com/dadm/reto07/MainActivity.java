package com.dadm.reto07;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button[] mBoardButtons;
    // Various text displayed
    private TextView mInfoTextView;

    private Boolean mGameOver;
    private Boolean mGameStarted;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_RESET_ID = 2;
    static final int DIALOG_GIVE_UP_ID = 3;

    private BoardView mBoardView;
    private String currentTurn;
    private int winner;

    private String mGoFirst;
    private List<Integer> scores = Arrays.asList(0, 0, 0);

    private boolean isOnline;
    private String onlineGameID;
    private OnlineGame onlineGame;
    private String myID;
    private Player me;
    private Player friend;
    private String invitationID;
    private TextView player1Name;
    private TextView player2Name;


    private TextView[] scoresTextViews = new TextView[3];

    private MediaPlayer mMovementMediaPlayer;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private MediaPlayer mTiedGameMediaPlayer;

    private final GameDAO gameDAO = new GameDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setTheme(R.style.Theme_reto07_landscape);
        } else {
            setTheme(R.style.Theme_reto07_portrait);
        }

        setContentView(R.layout.main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        scoresTextViews[0] = findViewById(R.id.scorePlayer1);
        scoresTextViews[1] = findViewById(R.id.numberTies);
        scoresTextViews[2] = findViewById(R.id.scorePlayer2);

        player1Name = findViewById(R.id.playerOne);
        player2Name = findViewById(R.id.playerTwo);

        mGame = new TicTacToeGame();

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        Button newGameButton = (Button) findViewById(R.id.newGameMain);
        newGameButton.setOnClickListener(view -> showCustomDialog(DIALOG_GIVE_UP_ID));

        Intent intent = getIntent();
        isOnline = intent.getBooleanExtra("isOnline", false);
        onlineGameID = intent.getStringExtra("gameID");
        invitationID = intent.getStringExtra("invitationID");

        myID = intent.getStringExtra("myID");
        mGameStarted = false;

        if (isOnline) {

        gameDAO.getGamesReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    onlineGame = data.getValue(OnlineGame.class);
                    if (onlineGame.getId().equals(onlineGameID)){

                        mGame.setBoardState(onlineGame.getBoard());
                        currentTurn = onlineGame.getCurrentTurn();
                        winner = onlineGame.getWinner();
                        mGoFirst = onlineGame.getGoFirst();
                        scores = onlineGame.getScores();
                        mGameOver = onlineGame.isGameOver();

                        if(onlineGame.getPlayer1().getId().equals(myID)){
                            me = onlineGame.getPlayer1();
                            friend = onlineGame.getPlayer2();
                            player1Name.setText(me.getName());
                            player2Name.setText(friend.getName());
                        }else{
                            me = onlineGame.getPlayer2();
                            friend = onlineGame.getPlayer1();
                            player1Name.setText(friend.getName());
                            player2Name.setText(me.getName());
                        }

                        if (currentTurn.equals(me.getMovementChar())){
                            if(mGame.hasStarted()){
                                try {
                                    mMovementMediaPlayer.start();
                                } catch (Exception e){
                                    System.out.println("Error playing movement sound");
                                }
                            }
                            mInfoTextView.setText("It's your turn.");
                        }else{
                            mInfoTextView.setText("It's " + friend.getName() + "'s turn.");
                        }

                        mBoardView.invalidate();

                        setMessagesOnGameOver();
                        displayScores();

                        if (!mGameStarted){
                            startNewGame();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }else {

            player1Name.setText("You");
            player2Name.setText("Android");

            SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

            // Restore the scores
            scores.set(0, mPrefs.getInt("mHumanScore", 0));
            scores.set(1, mPrefs.getInt("mTies", 0));
            scores.set(2, mPrefs.getInt("mComputerScore", 0));
            mGameOver = mPrefs.getBoolean("mGameOver", false);
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[mPrefs.getInt("mDifficulty", 0)]);
        }

        if (savedInstanceState == null){
            mGoFirst = TicTacToeGame.PLAYER_1;
            if (!isOnline){
                startNewGame();
            }

        } else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getStringArrayList("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getString("info"));
            mGoFirst = savedInstanceState.getString("mGoFirst");
            currentTurn = savedInstanceState.getString("currentTurn");
        }

        displayScores();

        if (!isOnline && !mGameOver && Objects.equals(currentTurn, TicTacToeGame.PLAYER_2)) {
            handleAndroidsMovement();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanScore", scores.get(0));
        ed.putInt("mTies", scores.get(1));
        ed.putInt("mComputerScore", scores.get(2));
        ed.putInt("mDifficulty", mGame.getDifficultyLevel().ordinal());
//        ed.putBoolean("mGameOver", mGameOver);
        ed.apply();
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
        outState.putStringArrayList("board", new ArrayList<>(mGame.getBoardState()));
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("mGoFirst", mGoFirst);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putCharSequence("currentTurn", currentTurn);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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
                        .setPositiveButton(R.string.yes, (dialog, id1) -> MainActivity.this.finish())
                        .setNegativeButton(R.string.no, null);
                break;

            case DIALOG_RESET_ID:
                builder.setMessage(R.string.reset_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialog, id2) -> {
                            scores = Arrays.asList(0, 0, 0);
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
                                scores.set(2, scores.get(2)+1);
                                displayScores();
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
        mGameStarted = true;
        winner = 0;
        displayScores();
        mBoardView.invalidate();

        if (Objects.equals(mGoFirst, TicTacToeGame.PLAYER_1)) {
            if (isOnline){
                mInfoTextView.setText(onlineGame.getPlayer1().getName() + " goes first.");
            }else{
                mInfoTextView.setText( "You go first.");
            }
            currentTurn = TicTacToeGame.PLAYER_1;
            mGoFirst = TicTacToeGame.PLAYER_2;
        }else {
            if (isOnline){
                mInfoTextView.setText(onlineGame.getPlayer2().getName() + " goes first.");
            }else{
                mInfoTextView.setText("Android goes first.");
            }
            currentTurn = TicTacToeGame.PLAYER_2;
            mGoFirst = TicTacToeGame.PLAYER_1;
            if(!isOnline){
                handleAndroidsMovement();
            }
        }

        if (isOnline){
            onlineGame.setBoard(mGame.getBoardState());
            onlineGame.setGameOver(mGameOver);
            onlineGame.setCurrentTurn(currentTurn);
            onlineGame.setGoFirst(mGoFirst);
            onlineGame.setScores(scores);
            onlineGame.setWinner(0);
            updateOnlineGame();
        }

    }

    public void displayScores(){
        scoresTextViews[0].setText(String.valueOf(scores.get(0)));
        scoresTextViews[1].setText(String.valueOf(scores.get(1)));
        scoresTextViews[2].setText(String.valueOf(scores.get(2)));
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (isOnline){
                if (!mGameOver && setMove(me.getMovementChar(), pos)){
                    winner = mGame.checkForWinner();
                    onlineGame.setWinner(winner);
                    updateOnlineGame();
                    if (winner == 0) {
                        currentTurn = friend.getMovementChar();
                        onlineGame.setCurrentTurn(currentTurn);
                        updateOnlineGame();
                    }
                    updateScoresOnGameOver();
                    setMessagesOnGameOver();
                    if (winner != 0) {
                        mGameOver = true;
                        displayScores();
                        onlineGame.setScores(scores);
                        onlineGame.setGameOver(mGameOver);
                        updateOnlineGame();
                    }
                }

            }else {
                if (!mGameOver && setMove(TicTacToeGame.PLAYER_1, pos)) {
                    winner = mGame.checkForWinner();
                    // If no winner yet, let the computer make a move
                    if (winner == 0) {
                        currentTurn = TicTacToeGame.PLAYER_2;
                        mInfoTextView.setText("It's Android's turn.");
                    }
                    handleAndroidsMovement();
                    if (winner == 1) {
                        mInfoTextView.setText("It's a tie!");
                        mTiedGameMediaPlayer.start();
                        scores.set(1, scores.get(1) + 1);
                    } else if (winner == 2) {
                        mInfoTextView.setText("You won!");
                        mHumanMediaPlayer.start();
                        scores.set(0, scores.get(0) + 1);
                    }
                    if (winner != 0) {
                        mGameOver = true;
                        displayScores();
                    }
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    public void updateScoresOnGameOver(){
        if (winner == 1) {
            scores.set(1, scores.get(1) + 1);
        } else if (winner == 2) {
            scores.set(0, scores.get(0) + 1);
        } else if (winner == 3) {
            scores.set(2, scores.get(2) + 1);
        }
    }

    public void setMessagesOnGameOver(){
        if (winner == 1) {
            mInfoTextView.setText("It's a tie!");
            mTiedGameMediaPlayer.start();
        } else if (winner == 2) {
            if ( me.getMovementChar().equals(TicTacToeGame.PLAYER_1)){
                mInfoTextView.setText("You won!");
                mHumanMediaPlayer.start();
            }else{
                mInfoTextView.setText("You lost!");
                mComputerMediaPlayer.start();
            }
        } else if (winner == 3) {
            if ( me.getMovementChar().equals(TicTacToeGame.PLAYER_2)){
                mInfoTextView.setText("You won!");
                mHumanMediaPlayer.start();
            }else{
                mInfoTextView.setText("You lost!");
                mComputerMediaPlayer.start();
            }
        }
    }

    private boolean setMove(String player, int location) {
        if (Objects.equals(currentTurn, player) && mGame.setMove(player, location)) {
            if (isOnline){
                onlineGame.setBoard(mGame.getBoardState());
                updateOnlineGame();
            }
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
            if (!mGameOver && currentTurn == TicTacToeGame.PLAYER_2) {
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.PLAYER_2, move);
                winner = mGame.checkForWinner();
                if (winner == 3){
                    mInfoTextView.setText("Android won!");
                    mComputerMediaPlayer.start();
                    scores.set(2, scores.get(2) + 1);
                    mGameOver = true;
                }

                if (winner == 0) {
                    mInfoTextView.setText("It's your turn.");
                    currentTurn = TicTacToeGame.PLAYER_1;
                } else if (winner == 1) {
                    mInfoTextView.setText("It's a tie!");
                    mTiedGameMediaPlayer.start();
                    scores.set(1, scores.get(1) + 1);
                }else if (winner == 2) {
                    mInfoTextView.setText("You won!");
                    mHumanMediaPlayer.start();
                    scores.set(0, scores.get(0) + 1);
                }

                if (winner != 0) {
                    mGameOver = true;
                    displayScores();
                }
            }
        }, 1000);
    }

    private void updateOnlineGame(){
        gameDAO.getGamesReference().child(onlineGameID).updateChildren(onlineGame.toMap());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isOnline){
            gameDAO.removePlayer(myID);
            gameDAO.removeGame(onlineGameID);
            gameDAO.removeInvitation(invitationID);
        }
    }
}