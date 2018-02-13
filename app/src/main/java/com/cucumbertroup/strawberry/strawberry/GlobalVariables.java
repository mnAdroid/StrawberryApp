package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 09.12.2017.
 */

public class GlobalVariables {
    //Singleton
    private static GlobalVariables instance;

    private int gold;
    private int clickCount;
    private boolean musicOn;
    private boolean soundOn;
    private boolean alphaTester;
    private boolean betaTester;
    private int gameMode;

    private GlobalVariables(int gold, int clickCount, boolean musicOn, boolean soundOn, boolean alphaTester, boolean betaTester){
        this.alphaTester = alphaTester;
        this.betaTester = betaTester;
        this.clickCount = clickCount;
        this.gold = gold;
        this.musicOn = musicOn;
        this.soundOn = soundOn;
    }

    public static synchronized GlobalVariables getInstance(int gold, int clickCount, boolean musicOn, boolean soundOn, boolean alphaTester, boolean betaTester) {
        if (GlobalVariables.instance == null) {
            GlobalVariables.instance = new GlobalVariables(gold, clickCount, musicOn, soundOn, alphaTester, betaTester);
        }
        return GlobalVariables.instance;
    }

    public static synchronized GlobalVariables getInstance() {
        return GlobalVariables.instance;
    }

    //Um die Variablen die die anderen Modi brauchen auch zur Verfügung zu stellen
    public int getGold() {
        return gold;
    }
    public int getClickCount() {
        return clickCount;
    }
    public boolean getMusicOn() {
        return musicOn;
    }
    public boolean getSoundOn() {
        return soundOn;
    }
    public boolean getAlphaTester() {
        return alphaTester;
    }
    public boolean getBetaTester() {
        return betaTester;
    }
    public int getGameMode() {
        return gameMode;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void incrementClickCount() {
        this.clickCount++;
    }
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }
    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }
    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }
}
