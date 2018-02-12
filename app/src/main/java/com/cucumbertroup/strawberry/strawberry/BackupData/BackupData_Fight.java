package com.cucumbertroup.strawberry.strawberry.BackupData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Max on 18.01.2018.
 */

public class BackupData_Fight {
    //Fightdaten
    private String characterStatusEffect;
    private String characterEquippedWeapon;
    private int characterBaseDamage;
    private int characterLife;
    private int characterMaxLife;
    private int characterDefense;
    private int characterExperience;
    private int characterLevel;
    private int characterMaxMana;
    private int characterCurrentMana;

    private Context fullContext;

    public BackupData_Fight(Context context) {
        fullContext = context;
    }

    public BackupData_Fight() { }

    public void readBackupData() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
        characterStatusEffect = sharedPreferences.getString("characterStatusEffect", "default");
        characterEquippedWeapon = sharedPreferences.getString("characterEquippedWeapon", "Hacke");
        characterBaseDamage = sharedPreferences.getInt("characterBaseDamage", 1);
        characterLife = sharedPreferences.getInt("characterLife", 25);
        characterMaxLife = sharedPreferences.getInt("characterMaxLife", 25);
        characterDefense = sharedPreferences.getInt("characterDefense", 1);
        characterExperience = sharedPreferences.getInt("characterExperience", 0);
        characterLevel = sharedPreferences.getInt("characterLevel", 1);
        characterMaxMana = sharedPreferences.getInt("characterMaxMana", 30);
        characterCurrentMana = sharedPreferences.getInt("characterCurrentMana", 30);
    }

    public boolean saveBackupData(Context fullContext) {
        try {
            if (characterMaxLife > 0) {
                SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberryFight", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("characterEquippedWeapon", characterEquippedWeapon);
                editor.putInt("characterBaseDamage", characterBaseDamage);
                editor.putInt("characterLife", characterLife);
                editor.putInt("characterMaxLife", characterMaxLife);
                editor.putInt("characterDefense", characterDefense);
                editor.putInt("characterExperience", characterExperience);
                editor.putInt("characterLevel", characterLevel);
                editor.putInt("characterMaxMana", characterMaxMana);
                editor.putInt("characterCurrentMana", characterCurrentMana);
                editor.putString("characterStatusEffect", characterStatusEffect);
                editor.apply();

                return true;
            }
            return false;
        } catch (NullPointerException e) {
            //War noch nicht eingelesen
            return false;
        }
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

    public int getCharacterMaxMana() {
        return characterMaxMana;
    }

    public int getCharacterCurrentMana() {
        return characterCurrentMana;
    }
}
