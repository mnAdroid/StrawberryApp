package com.cucumbertroup.strawberry.strawberry.Model;

/**
 * Created by Max on 21.01.2018.
 */

public class Ranking_Run {
    private String userName;
    private int score;

    public Ranking_Run() {
    }

    public Ranking_Run(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
