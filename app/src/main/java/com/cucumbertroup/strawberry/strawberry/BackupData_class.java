package com.cucumbertroup.strawberry.strawberry;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Max on 10.12.2017.
 */

public class BackupData_class {
    //Allgemeine Daten
    private int gold;
    private int clickCount;
    private boolean musicOn;
    private boolean soundOn;
    private boolean alphaTester;
    private boolean betaTester;
    //Farmdaten
    private int numStrawberries;
    private int numAecker;
    private int numLand;
    private String strawberryStatus;
    private int numGurken;
    //Fightdaten
    private String characterStatusEffect;
    private String characterEquippedWeapon;
    private int characterBaseDamage;
    private int characterLife;
    private int characterMaxLife;
    private int characterDefense;
    private int characterExperience;
    private int characterLevel;
    private int characterBaseAttackspeed;
    private String highscoreRunString;

    private Context fullContext;

    BackupData_class(Context context) {
        fullContext = context;
    }

    BackupData_class() {

    }

    void readBackupData() {
        //General
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        gold = sharedPreferences.getInt("gold", 5);
        clickCount = sharedPreferences.getInt("clicks", 0);
        musicOn = sharedPreferences.getBoolean("musicOn", true);
        soundOn = sharedPreferences.getBoolean("soundOn", true);
        alphaTester = sharedPreferences.getBoolean("alphaTester", true);
        betaTester = sharedPreferences.getBoolean("betaTester", false);

        //Farmmode
        sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        numLand = sharedPreferences.getInt("numLand", 1);
        strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        numGurken = sharedPreferences.getInt("numGurken", 1);

        //Fightmode
        sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
        characterStatusEffect = sharedPreferences.getString("characterStatusEffect", "default");
        characterEquippedWeapon = sharedPreferences.getString("characterEquippedWeapon", "Hacke");
        characterBaseDamage = sharedPreferences.getInt("characterBaseDamage", 1);
        characterLife = sharedPreferences.getInt("characterLife", 25);
        characterMaxLife = sharedPreferences.getInt("characterMaxLife", 25);
        characterDefense = sharedPreferences.getInt("characterDefense", 1);
        characterExperience = sharedPreferences.getInt("characterExperience", 0);
        characterLevel = sharedPreferences.getInt("characterLevel", 1);
        characterBaseAttackspeed = sharedPreferences.getInt("characterBaseAttackspeed", 1000);
        highscoreRunString = sharedPreferences.getString("highscoreRun", "");
    }

    boolean saveBackupData(Context fullContext) {
        try {
            //General
            SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("gold", gold);
            editor.putInt("clicks", clickCount);
            editor.putBoolean("musicOn", musicOn);
            editor.putBoolean("soundOn", soundOn);
            editor.putBoolean("alphaTester", alphaTester);
            editor.putBoolean("betaTester", betaTester);
            editor.apply();

            //Farmmode
            sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
            editor = sharedPreferences.edit();
            editor.putInt("numStrawberries", numStrawberries);
            editor.putInt("numAecker", numAecker);
            editor.putInt("numLand", numLand);
            editor.putInt("numGurken", numGurken);
            editor.putString("strawberryStatus", strawberryStatus);
            editor.apply();

            //Fightmode
            sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
            editor = sharedPreferences.edit();
            editor.putString("characterEquippedWeapon", characterEquippedWeapon);
            editor.putInt("characterBaseDamage", characterBaseDamage);
            editor.putInt("characterLife", characterLife);
            editor.putInt("characterMaxLife", characterMaxLife);
            editor.putInt("characterDefense", characterDefense);
            editor.putInt("characterExperience", characterExperience);
            editor.putInt("characterLevel", characterLevel);
            editor.putInt("characterBaseAttackspeed", characterBaseAttackspeed);
            editor.putString("characterStatusEffect", characterStatusEffect);
            editor.putString("highscoreRun", highscoreRunString);
            editor.apply();

            return true;
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

    public int getNumStrawberries() {
        return numStrawberries;
    }

    public int getNumAecker() {
        return numAecker;
    }

    public int getNumLand() {
        return numLand;
    }

    public String getStrawberryStatus() {
        return strawberryStatus;
    }

    public int getNumGurken() {
        return numGurken;
    }

    public String getCharacterStatusEffect() {
        return characterStatusEffect;
    }

    public String getCharacterEquippedWeapon() {
        return characterEquippedWeapon;
    }

    public int getCharacterBaseDamage() {
        return characterBaseDamage;
    }

    public int getCharacterLife() {
        return characterLife;
    }

    public int getCharacterMaxLife() {
        return characterMaxLife;
    }

    public int getCharacterDefense() {
        return characterDefense;
    }

    public int getCharacterExperience() {
        return characterExperience;
    }

    public int getCharacterLevel() {
        return characterLevel;
    }

    public int getCharacterBaseAttackspeed() {
        return characterBaseAttackspeed;
    }

    public String getHighscoreRunString() {
        return highscoreRunString;
    }

}
