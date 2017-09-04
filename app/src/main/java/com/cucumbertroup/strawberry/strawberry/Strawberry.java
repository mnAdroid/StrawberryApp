package com.cucumbertroup.strawberry.strawberry;

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

    public Strawberry(int acker) {
        wachsStatus = -1;
        this.acker = acker;
    }

    public Strawberry(int wachsStatus, int acker, long timeThisFruit) {
        this.wachsStatus = wachsStatus;
        this.acker = acker;
        this.timeThisFruit = timeThisFruit;
    }

    public void incrWachsStatus(int setz) {
        //wir wollen ja keine Erdbeeren wachsen lassen die nicht gesäht oder schon ausgewachsen sind
        if (wachsStatus < 5 && wachsStatus >= 0) {
            wachsStatus += setz;
        }
    }
    //um Bedingungen beim Denken zu finden
    public int getWachsStatus() {
        return wachsStatus;
    }

    //zum Zeichnen
    public int getAcker() {
        return acker;
    }

    //zum einlesen gespeicherter Daten
    public long getTimeThisFruit() { return timeThisFruit; }

    //Erdbeere wird gesäht
    public void setStrawberry() {
        wachsStatus = 0;
        timeThisFruit = System.currentTimeMillis();
    }

    //Erdbeere wird geerntet
    public void resetStrawberry() {
        wachsStatus = -1;
    }

    public void update() {
        //Zeit seit Erstellung / letztem wachsStatus-Update der Erdbeere überprüfen und möglicherweise wachsStatus erhöhen
        if (System.currentTimeMillis() - timeThisFruit > 10000 && wachsStatus <5 && wachsStatus >= 0) {
            timeThisFruit += 10000;
            wachsStatus++;
        }
    }
}
