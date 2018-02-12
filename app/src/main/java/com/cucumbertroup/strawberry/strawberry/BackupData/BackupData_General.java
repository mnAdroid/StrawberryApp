package com.cucumbertroup.strawberry.strawberry.BackupData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Max on 18.01.2018.
 */

public class BackupData_General {
    private int gold;
    private int clickCount;
    private boolean musicOn;
    private boolean soundOn;
    private boolean alphaTester;
    private boolean betaTester;

    private Context fullContext;

    public BackupData_General(Context context) {
        fullContext = context;
    }

    public BackupData_General() { }

    public void readBackupData() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        gold = sharedPreferences.getInt("gold", 5);
        clickCount = sharedPreferences.getInt("clicks", 0);
        musicOn = sharedPreferences.getBoolean("musicOn", true);
        soundOn = sharedPreferences.getBoolean("soundOn", true);
        alphaTester = sharedPreferences.getBoolean("alphaTester", true);
        betaTester = sharedPreferences.getBoolean("betaTester", false);
    }
    public boolean saveBackupData(Context fullContext) {
        try {
            if (clickCount > 0) {
                SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("gold", gold);
                editor.putInt("clicks", clickCount);
                editor.putBoolean("musicOn", musicOn);
                editor.putBoolean("soundOn", soundOn);
                editor.putBoolean("alphaTester", alphaTester);
                editor.putBoolean("betaTester", betaTester);
                editor.apply();

                return true;
            }
            return false;
        } catch (NullPointerException e) {
            //War noch nicht eingelesen
            return false;
        }
    }

    public int getGold() {
        return gold;
    }

    public int getClickCount() {
        return clickCount;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public boolean isAlphaTester() {
        return alphaTester;
    }

    public boolean isBetaTester() {
        return betaTester;
    }
}
