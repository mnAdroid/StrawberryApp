package com.cucumbertroup.strawberry.strawberry.FarmMode;

/**
 * Created by Max on 27.08.2017.
 */

public class Strawberry {
    private int wachsStatus;
    //-1: nicht gesäht, 0: ausgesäht, 1-4: Wachstumsphasen, 5: Ausgewachsen
    //wächst automatisch um 0.1 pro adc

    //Wann wurde das Ding erstellt?
    private long timeThisFruit;

    //Um zu wissen welche Erdbeere (auf welchem Acker, welche Zahl)
    private int acker;

    Strawberry(int acker) {
        wachsStatus = -1;
        this.acker = acker;
    }

    Strawberry(int wachsStatus, int acker, long timeThisFruit) {
        this.wachsStatus = wachsStatus;
        this.acker = acker;
        this.timeThisFruit = timeThisFruit;
    }

    void incrWachsStatus(int setz) {
        //wir wollen ja keine Erdbeeren wachsen lassen die nicht gesäht oder schon ausgewachsen sind
        if (wachsStatus < 5 && wachsStatus >= 0) {
            wachsStatus += setz;
        }
    }
    //um Bedingungen beim Denken zu finden
    int getWachsStatus() {
        return wachsStatus;
    }

    //zum Zeichnen
    int getAcker() {
        return acker;
    }

    //zum einlesen gespeicherter Daten
    long getTimeThisFruit() { return timeThisFruit; }

    //Erdbeere wird gesäht
    void setStrawberry() {
        wachsStatus = 0;
        timeThisFruit = System.currentTimeMillis();
    }

    //Erdbeere wird geerntet
    void resetStrawberry() {
        wachsStatus = -1;
    }

    boolean update() {
        //Zeit seit Erstellung / letztem wachsStatus-Update der Erdbeere überprüfen und möglicherweise wachsStatus erhöhen
        if (System.currentTimeMillis() - timeThisFruit > 10000 && wachsStatus <5 && wachsStatus >= 0) {
            timeThisFruit += 10000;
            wachsStatus++;
            return true;
        }
        return false;
    }
}
