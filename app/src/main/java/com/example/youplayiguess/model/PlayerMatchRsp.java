package com.example.youplayiguess.model;

public class PlayerMatchRsp {
    private int code;
    private String roomNo;
    private int matchAccountNum;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getMatchAccountNum() {
        return matchAccountNum;
    }

    public void setMatchAccountNum(int matchAccountNum) {
        this.matchAccountNum = matchAccountNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
