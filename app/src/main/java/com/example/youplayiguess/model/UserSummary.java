package com.example.youplayiguess.model;

public class UserSummary {
    private String username;

    private int totalScore;

    private int totalGameAmount;

    private int performCorrectAmount;

    private int guessCorrectAmount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalGameAmount() {
        return totalGameAmount;
    }

    public void setTotalGameAmount(int totalGameAmount) {
        this.totalGameAmount = totalGameAmount;
    }

    public int getPerformCorrectAmount() {
        return performCorrectAmount;
    }

    public void setPerformCorrectAmount(int performCorrectAmount) {
        this.performCorrectAmount = performCorrectAmount;
    }

    public int getGuessCorrectAmount() {
        return guessCorrectAmount;
    }

    public void setGuessCorrectAmount(int guessCorrectAmount) {
        this.guessCorrectAmount = guessCorrectAmount;
    }
}
