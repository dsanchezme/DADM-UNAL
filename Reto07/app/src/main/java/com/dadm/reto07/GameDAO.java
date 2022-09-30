package com.dadm.reto07;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameDAO {

    private FirebaseDatabase mDatabase;
    private DatabaseReference playersReference;
    private DatabaseReference invitationsReference;
    private DatabaseReference gamesReference;

    public GameDAO(){
        mDatabase = FirebaseDatabase.getInstance("https://tictactoe-online-unal-default-rtdb.firebaseio.com");
        playersReference = mDatabase.getReference("players");
        invitationsReference = mDatabase.getReference("invitations");
        gamesReference = mDatabase.getReference("games");
    }

    public Task<Void> addPlayer(Player player){
        return playersReference.child(player.getId()).setValue(player);
    }

    public Task<Void> removePlayer(String playerId){
        return playersReference.child(playerId).removeValue();
    }

    public DatabaseReference getPlayersReference(){
        return playersReference;
    }

    public Task<Void> addInvitation(Invitation invitation){
        return invitationsReference.child(invitation.getId()).setValue(invitation);
    }

    public Task<Void> removeInvitation(String invitationId){
        return invitationsReference.child(invitationId).removeValue();
    }

    public DatabaseReference getInvitationsReference(){
        return invitationsReference;
    }

    public Task<Void> addGame(OnlineGame game){
        return gamesReference.child(game.getId()).setValue(game);
    }

    public Task<Void> removeGame(String gameId){
        return gamesReference.child(gameId).removeValue();
    }

    public DatabaseReference getGamesReference(){
        return gamesReference;
    }



}
