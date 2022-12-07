package com.example.youplayiguess.model;

public class ClientMessage {
    private int type;
    private String roomNo;
    private String username;
    private String word;

    public ClientMessage(int type, String roomNo, String username, String word) {
        this.type = type;
        this.roomNo = roomNo;
        this.username = username;
        this.word = word;
    }
}
