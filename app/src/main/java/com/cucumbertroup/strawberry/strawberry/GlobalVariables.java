package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 09.12.2017.
 */

public class GlobalVariables {
    private int gold;
    private int clickCount;
    private boolean musicOn;
    private boolean soundOn;
    private boolean alphaTester;
    private boolean betaTester;
    private int gameMode;

    GlobalVariables(int gold, int clickCount, boolean musicOn, boolean soundOn, boolean alphaTester, boolean betaTester){
        this.alphaTester = alphaTester;
        this.betaTester = betaTester;
        this.clickCount = clickCount;
        this.gold = gold;
        this.musicOn = musicOn;
        this.soundOn = soundOn;
    }

    //Um die Variablen die die anderen Modi brauchen auch zur Verfügung zu stellen
    int getGold() {
        return gold;
    }
    int getClickCount() {
        return clickCount;
    }
    boolean getMusicOn() {
        return musicOn;
    }
    boolean getSoundOn() {
        return soundOn;
    }
    boolean getAlphaTester() {
        return alphaTester;
    }
    boolean getBetaTester() {
        return betaTester;
    }
    int getGameMode() {
        return gameMode;
    }
    void setGold(int gold) {
        this.gold = gold;
    }
    void incrementClickCount() {
        this.clickCount++;
    }
    void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
    void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }
    void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }
    void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }
}
