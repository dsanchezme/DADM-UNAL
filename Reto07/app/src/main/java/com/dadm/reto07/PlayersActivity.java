package com.dadm.reto07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayersActivity extends AppCompatActivity {

    private EditText playerNameInput;
    private Button connectButton;
    private ListView playersListView;
    private TextView emptyListText;

    private boolean isConnected;
    private String onlineGameID;
    private String invitationID;

    private List<Player> onlinePlayers;
    private ArrayAdapter<Player> arrayAdapter;

    private Player curPlayer;

    private final GameDAO gameDAO = new GameDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        connectButton = findViewById(R.id.connectButton);
        playerNameInput = findViewById(R.id.nameOnline);
        playersListView = (ListView) findViewById(R.id.players_list);
        emptyListText = (TextView) findViewById(R.id.empty_list_text);

        playersListView.setEmptyView(emptyListText);

        onlinePlayers = new ArrayList<>();

        connectButton.setOnClickListener(v -> {
            handleConnection();
        });

        isConnected = DataHolder.getInstance().isConnected();

        if (savedInstanceState != null){
            isConnected = savedInstanceState.getBoolean("isConnected");
            if (isConnected){
                curPlayer = new Player(savedInstanceState.getString("playerName"));
                curPlayer.setId(savedInstanceState.getString("playerID"));
            }
        }

        if (isConnected){
            curPlayer = new Player(DataHolder.getInstance().getPlayer().getName());
            curPlayer.setId(DataHolder.getInstance().getPlayer().getId());
            connectButton.setText(R.string.disconnectButton);
            playerNameInput.setText(DataHolder.getInstance().getPlayer().getName());
            playerNameInput.setFocusable(false);
        }

        arrayAdapter = new PlayersListAdapter(this, R.layout.player_info, onlinePlayers);

        gameDAO.getPlayersReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onlinePlayers.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Player player = data.getValue(Player.class);
                    if (!curPlayer.getId().equals(player.getId())){
                        onlinePlayers.add(player);
                    }
                }
                playersListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(isConnected) {
            System.out.println("###############");
            System.out.println("###############");
            System.out.println(">>> " + getDeviceName() + "CONNECTED!");
            System.out.println(">>> curPlayer: " + curPlayer);
            System.out.println("###############");
            System.out.println("###############");
        }

        gameDAO.getInvitationsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isConnected){
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Invitation invitation = data.getValue(Invitation.class);
                        if (invitation.getTo().getId().equals(curPlayer.getId())) {
                            onlineGameID = invitation.getGameID();
                            invitationID = invitation.getId();
                            goToOnlineGame();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataHolder.getInstance().setConnected(isConnected);
        if (isConnected){
            DataHolder.getInstance().setPlayer(curPlayer);
            System.out.println("###############");
            System.out.println("###############");
            System.out.println(">>> " + getDeviceName() + " STOPPING!");
            System.out.println(">>> curPlayer: " + curPlayer.getName());
            System.out.println("###############");
            System.out.println("###############");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("###############");
        System.out.println("###############");
        System.out.println(">>> " + getDeviceName() + " DESTROYING!");
        if (isConnected){
            System.out.println(">>> curPlayer: " + curPlayer.getName());
//            gameDAO.removePlayer(curPlayer.getId());
//            SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
//            mPrefs.edit().remove("isConnected").remove("PlayerID").apply();
        }else{
            System.out.println(">>> Not connected!");
        }
        System.out.println("###############");
        System.out.println("###############");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        isConnected = mPrefs.getBoolean("isConnected", false);
        curPlayer = new Player(mPrefs.getString("playerName", ""));
        curPlayer.setId(mPrefs.getString("playerID", null));

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("isConnected", isConnected);
        if (isConnected){
            ed.putString("playerName", curPlayer.getName());
            ed.putString("playerID", curPlayer.getId());
        }
        ed.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isConnected", isConnected);
        if (isConnected){
            outState.putCharSequence("playerID", curPlayer.getId());
            outState.putCharSequence("playerName", curPlayer.getName());
        }
    }

    public void handleConnection(){
        if (!isConnected) {
            if (playerNameInput != null && !playerNameInput.getText().toString().trim().isEmpty()) {
                curPlayer = new Player(playerNameInput.getText().toString().trim());
                DataHolder.getInstance().setPlayer(curPlayer);
                gameDAO.addPlayer(curPlayer).addOnSuccessListener(success -> {
                    connectButton.setText(R.string.disconnectButton);
                    isConnected = true;
                    playerNameInput.setFocusable(false);
                });
                gameDAO.addPlayer(curPlayer).addOnCanceledListener(() -> {
                   Toast.makeText(getBaseContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            gameDAO.removePlayer(curPlayer.getId()).addOnSuccessListener(success -> {
                connectButton.setText(R.string.connectButton);
                isConnected = false;
                curPlayer = null;
                DataHolder.getInstance().setPlayer(curPlayer);
                playerNameInput.setFocusableInTouchMode(true);
                SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
                mPrefs.edit().remove("isConnected").remove("PlayerID").apply();
            });
        }
        playersListView.invalidateViews();
    }

    public void createGame(Invitation invitation){
        OnlineGame game = new OnlineGame(invitation.getFrom(), invitation.getTo());
        game.setId(invitation.getGameID());
        gameDAO.addGame(game);
    }

    public void goToOnlineGame(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isOnline", true);
        intent.putExtra("gameID", onlineGameID);
        intent.putExtra("invitationID", invitationID);
        System.out.println("######################");
        System.out.println("SENDING GAME-ID: " + onlineGameID);
        System.out.println("######################");
        intent.putExtra("myID", curPlayer.getId());
        startActivity(intent);
    }

    private class PlayersListAdapter extends ArrayAdapter<Player> {

        private int layout;
        private PlayersListAdapter(Context context, int resource, List<Player> objects){
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if  (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView= inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.playerName = (TextView) convertView.findViewById(R.id.playerName);
                viewHolder.inviteButton = (Button) convertView.findViewById(R.id.inviteButton);
                viewHolder.inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected){
                            Invitation invitationFrom = new Invitation(curPlayer, getItem(position));
                            gameDAO.addInvitation(invitationFrom);
                            onlineGameID = invitationFrom.getGameID();
                            invitationID = invitationFrom.getId();
                            createGame(invitationFrom);
                            goToOnlineGame();
                        }
                    }
                });

                if(isConnected) {
                    System.out.println("###############");
                    System.out.println("###############");
                    System.out.println(">>> " + getDeviceName() + "CONNECTED!");
                    System.out.println(">>> curPlayer: " + curPlayer);
                    System.out.println("###############");
                    System.out.println("###############");
                }

                if(isConnected){
                    viewHolder.inviteButton.setBackgroundColor(Color.parseColor("#2196F3"));
                }else{
                    viewHolder.inviteButton.setBackgroundColor(Color.parseColor("black"));
                }


                convertView.setTag(viewHolder);
            }else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.playerName.setText(getItem(position).getName());
            }

            return convertView;
        }
    }

    public class ViewHolder {
        TextView playerName;
        Button inviteButton;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }
}