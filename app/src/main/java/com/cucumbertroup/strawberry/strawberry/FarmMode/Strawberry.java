package com.cucumbertroup.strawberry.strawberry.FarmMode;

class Strawberry {
    private int wachsStatus;
    //-1: nicht gesäht, 0: ausgesäht, 1-3: Wachstumsphasen, 4: Ausgewachsen
    //wächst automatisch um 0.1 pro adc

    //Wann wurde das Ding erstellt?
    private long timeThisFruit;

    //Um zu wissen welche Erdbeere (auf welchem Acker, welche Zahl)
    private int acker;
    private int coordinateX;
    private boolean reihe1;

    Strawberry(int acker, int x, boolean reihe1) {
        wachsStatus = -1;
        this.acker = acker;
        coordinateX = x;
        this.reihe1 = reihe1;
    }

    Strawberry(int wachsStatus, int acker, long timeThisFruit, int x, boolean reihe1) {
        this.wachsStatus = wachsStatus;
        this.acker = acker;
        this.timeThisFruit = timeThisFruit;
        coordinateX = x;
        this.reihe1 = reihe1;
    }

    boolean incrWachsStatus() {
        //wir wollen ja keine Erdbeeren wachsen lassen die nicht gesäht oder schon ausgewachsen sind
        if (wachsStatus < 4 && wachsStatus >= 0) {
            wachsStatus++;
            return true;
        }
        return false;
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
        if (System.currentTimeMillis() - timeThisFruit > 10000 && wachsStatus <4 && wachsStatus >= 0) {
            timeThisFruit += 10000;
            wachsStatus++;
            return true;
        }
        return false;
    }

    int getCoordinateX() {
        return coordinateX;
    }

    boolean isReihe1() {
        return reihe1;
    }
}
