package com.cucumbertroup.strawberry.strawberry.FightMode;

import java.util.Calendar;

/**
 * Created by Max on 28.11.2017.
 */

//Der Score im Run Mode
public class ScoreRun {
    private int points;
    private int[] date = new int[6];

    public ScoreRun(String imported) {
        if (!(imported.equals("")) && !(imported.equals(";;;;;;;"))) {
            String[] importedStrings = imported.split(";");

            this.points = Integer.parseInt(importedStrings[0]);
            if (date != null) {
                this.date[0] = Integer.parseInt(importedStrings[1]);
                this.date[1] = Integer.parseInt(importedStrings[2]);
                this.date[2] = Integer.parseInt(importedStrings[3]);
                this.date[3] = Integer.parseInt(importedStrings[4]);
                this.date[4] = Integer.parseInt(importedStrings[5]);
                this.date[5] = Integer.parseInt(importedStrings[6]);
            }
        }
        else {
            this.points = 0;
            if (date != null) {
                this.date[0] = Calendar.getInstance().get(Calendar.YEAR);
                this.date[1] = Calendar.getInstance().get(Calendar.MONTH);
                this.date[2] = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                this.date[3] = Calendar.getInstance().get(Calendar.HOUR);
                this.date[4] = Calendar.getInstance().get(Calendar.MINUTE);
                this.date[5] = Calendar.getInstance().get(Calendar.SECOND);
            }
        }
    }

    public ScoreRun(int points, int year, int month, int day, int hour, int minute, int seconds) {
        this.points = points;
        if (date != null) {
            this.date[0] = year;
            this.date[1] = month;
            this.date[2] = day;
            this.date[3] = hour;
            this.date[4] = minute;
            this.date[5] = seconds;
        }
    }

    public ScoreRun() {
        this.points = 0;
        if (date != null) {
            this.date[0] = Calendar.getInstance().get(Calendar.YEAR);
            this.date[1] = Calendar.getInstance().get(Calendar.MONTH);
            this.date[2] = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            this.date[3] = Calendar.getInstance().get(Calendar.HOUR);
            this.date[4] = Calendar.getInstance().get(Calendar.MINUTE);
            this.date[5] = Calendar.getInstance().get(Calendar.SECOND);
        }
    }

    public String exportScoreRun() {
        String export = "";
        export += points + ";";
        if (date != null) {
            export += date[0] + ";";
            export += date[1] + ";";
            export += date[2] + ";";
            export += date[3] + ";";
            export += date[4] + ";";
            export += date[5] + ";";
        }
        else {
            export += ";;;;;;";
        }
        return export;
    }

    public void incrementPoints() {
        points++;
    }

    public int getPoints() {
        return points;
    }
}
