package com.cucumbertroup.strawberry.strawberry.BackupData;

import android.content.Context;
import android.content.SharedPreferences;

public class BackupData_Farm {
    //Farmdaten
    private int numStrawberries;
    private int numAecker;
    private String strawberryStatus;
    private int numGurken;

    private Context fullContext;

    public BackupData_Farm(Context context) {
        fullContext = context;
    }

    public BackupData_Farm() { }

    public void readBackupData() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        numGurken = sharedPreferences.getInt("numGurken", 1);
    }

    public boolean saveBackupData(Context fullContext) {
        try {
            if (numAecker > 0) {
                SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("numStrawberries", numStrawberries);
                editor.putInt("numAecker", numAecker);
                editor.putInt("numGurken", numGurken);
                editor.putString("strawberryStatus", strawberryStatus);
                editor.apply();

                return true;
            }
            return false;
        } catch (NullPointerException e) {
            //War noch nicht eingelesen
            return false;
        }
    }

    public int getNumStrawberries() {
        return numStrawberries;
    }

    public int getNumAecker() {
        return numAecker;
    }

    public String getStrawberryStatus() {
        return strawberryStatus;
    }

    public int getNumGurken() {
        return numGurken;
    }
}
