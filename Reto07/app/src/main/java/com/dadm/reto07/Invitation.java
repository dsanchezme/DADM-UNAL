package com.dadm.reto07;

import java.util.UUID;

public class Invitation {

    private String id;
    private Player from;
    private Player to;
    private boolean accepted;
    private String gameID;

    public Invitation(){

    }

    public Invitation(Player from, Player to) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.to = to;
        this.accepted = false;
        this.gameID = UUID.randomUUID().toString();
    }

    public Player getFrom() {
        return from;
    }

    public void setFrom(Player from) {
        this.from = from;
    }

    public Player getTo() {
        return to;
    }

    public void setTo(Player to) {
        this.to = to;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getId(){
        return id;
    }

    public String getGameID() {
        return gameID;
    }

}
