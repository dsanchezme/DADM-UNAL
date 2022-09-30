package com.dadm.reto07;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {

    private static DataHolder holder;

    private DataHolder(){}

    public static DataHolder getInstance() {
        if (holder == null){
            holder = new DataHolder();
        }
        return holder;
    }

    private boolean isConnected;
    private Player player;
    private List<Player> onlinePlayers;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(List<Player> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public void addOnlinePlayer(Player player) {
        this.onlinePlayers.add(player);
    }
}