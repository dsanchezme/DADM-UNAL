package com.dadm.reto07;

import java.util.UUID;

public class Player {

    private String name;
    private String id;
    private String movementChar;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovementChar() {
        return movementChar;
    }

    public void setMovementChar(String movementChar) {
        this.movementChar = movementChar;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", movementChar='" + movementChar + '\'' +
                '}';
    }
}
