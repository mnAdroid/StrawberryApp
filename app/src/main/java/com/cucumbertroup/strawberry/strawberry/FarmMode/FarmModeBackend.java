package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.content.SharedPreferences;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;

import java.util.Arrays;

class FarmModeBackend {
    //Singleton
    private static FarmModeBackend instance;
    private GlobalVariables globalVariables;
    private Context fullContext;
    private FarmModeSound farmModeSound;

    //Erdbeeren Array
    private Strawberry[] strawberries;
    private int numStrawberries = 0;
    //Anzahl und Preis der Farmfläche
    private int numAecker;
    private int priceAecker;
    private final int AECKER_MAX = 32;
    //Anzahl und Preis der Länderein
    private int numLand;
    private int priceLand;
    private final int LAND_MAX = 8;
    //Anzahl und Preis der arbeitenden Gurken
    private int numGurken;
    private int priceGurken;

    //Erdbeerkosten
    private final int STRAWBERRY_PRICE = 1;

    private FarmModeBackend(GlobalVariables globalVariables, Context context) {
        this.globalVariables = globalVariables;
        fullContext = context;
        getSharedPreferences();
        farmModeSound = FarmModeSound.getInstance(globalVariables, context);

        //Preise initialisieren
        priceAecker = getPrice(0);
        priceGurken = getPrice(1);
        priceLand = getPrice(2);
    }
    static synchronized FarmModeBackend getInstance(GlobalVariables globalVariables, Context context) {
        if (FarmModeBackend.instance == null) {
            FarmModeBackend.instance = new FarmModeBackend(globalVariables, context);
        }
        return FarmModeBackend.instance;
    }

    //Erdbeeren wachsen hier automatisch durch Zeit
    void strawberriesUpdate() {
        for(int i = 0; i < numStrawberries; i++) {
            strawberries[i].update();
        }
    }

    //Was passiert wenn der Spieler im FARM Modus klickt?
    void gotClickedFarm(int zustand) {
        globalVariables.incrementClickCount();
        switch(zustand) {
            case 0:
                //Aussähen: Prüfen ob noch Platz ist, wenn ja: Aussähen.
                for(int j = 1; j <= numGurken; j++) {
                    if (numStrawberries < (numAecker * 16)) {
                        for (int i = 0; i < numAecker * 16; i++) {
                            if (globalVariables.getGold() >= STRAWBERRY_PRICE && strawberries[i].getWachsStatus() <= -1) {
                                strawberries[i].setStrawberry();
                                numStrawberries++;
                                globalVariables.setGold(globalVariables.getGold() - STRAWBERRY_PRICE);
                                if(j == 1) {
                                    farmModeSound.playSound(1, fullContext);
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            case 1:
                //Wachsen: Alles wächst viel schneller, aber es wächst auch schon so langsam.
                if (numStrawberries > 0) {
                    for (int i = 0; i < numAecker * 16; i++) {
                        strawberries[i].incrWachsStatus(1);
                    }
                }
                farmModeSound.playSound(2, fullContext);
                break;
            case 2:
                //Ernten: Prüfen ob Erdbeeren fertig, wenn ja: Gold bekommen und Platz machen zum Aussähen
                for(int j = 1; j <= numGurken; j++) {
                    for (int i = 0; i < numAecker * 16; i++) {
                        if (strawberries[i].getWachsStatus() >= 5) {
                            strawberries[i].resetStrawberry();
                            numStrawberries--;
                            globalVariables.setGold(globalVariables.getGold() + 10);
                            if (j == 1) {
                                farmModeSound.playSound(3, fullContext);
                            }
                            break;
                        }
                    }
                }
                break;
        }
    }

    //SharedPreferences auslesen
    void getSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        numLand = sharedPreferences.getInt("numLand", 1);
        String strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        numGurken = sharedPreferences.getInt("numGurken", 1);
        //Initialisierung der gespeicherten Erdbeeren
        strawberries = new Strawberry[numAecker * AECKER_MAX];

        //um keine IndexoutofBoundException zu bekommen
        if (!(strawberryStatus.equals(""))) {
            //1. String auseinander nehmen, 2. aus den Daten auslesen
            //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
            String[] strawberryStatusStrings = strawberryStatus.split("a");
            int stringsCounter = 0;
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry(Integer.parseInt(strawberryStatusStrings[stringsCounter]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 1]), Long.parseLong(strawberryStatusStrings[stringsCounter + 2]));
                stringsCounter += 3;
            }
        } else {
            for (int i = 0; i < (numAecker * 16); i++) {
                strawberries[i] = new Strawberry((i / 16) + 1);
            }
        }
    }

    //SharedPreferences wieder sicher verwahren
    void setSharedPreferences() {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numStrawberries", numStrawberries);
        editor.putInt("numAecker", numAecker);
        editor.putInt("numLand", numLand);
        editor.putInt("numGurken", numGurken);

        //Hier kommen alle derzeitigen Erdbeeren rein um gespeichert zu werden
        StringBuilder strawberryStatus = new StringBuilder();
        //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
        for (int i = 0; i < (numAecker * 16); i++) {
            strawberryStatus.append(strawberries[i].getWachsStatus());
            strawberryStatus.append("a");
            strawberryStatus.append(strawberries[i].getAcker());
            strawberryStatus.append("a");
            strawberryStatus.append(strawberries[i].getTimeThisFruit());
            strawberryStatus.append("a");
        }
        editor.putString("strawberryStatus", strawberryStatus.toString());

        editor.apply();
    }

    //Gibt den Preis der Elemente aus dem Shop aus
    private int getPrice(int whichOne) {
        //whichOne Legende: 0: Acker, 1: Gurke, 2: Land, 3: Werkzeug
        switch (whichOne) {
            case 0:
                return (int) (50*Math.pow((double) numAecker, 1.7));
            case 1:
                return (int) (500*Math.pow((double) numGurken, 1.5));
            case 2:
                return (int) (50*Math.pow((double) (numLand*8), 1.7));
            case 3:
                return 42;
        }
        return -1;
    }

    //Wenn ein Acker gekauft wurde
    void ackerGekauft() {
        //Anzahl hochzählen und Gold abbuchen
        numAecker++;
        globalVariables.setGold(globalVariables.getGold() - priceAecker);
        priceAecker = getPrice(0);

        //Neues Strawberry Array erstellen
        Strawberry[] strawberriesTemp = Arrays.copyOf(strawberries, numAecker*16);
        for (int i = ((numAecker-1) * 16); i < (numAecker * 16); i++) {
            strawberriesTemp[i] = new Strawberry((i/16) + 1);
        }
        strawberries = strawberriesTemp;
    }

    int getNumAecker() {
        return numAecker;
    }

    int getNumLand() {
        return numLand;
    }

    int getNumGurken() {
        return numGurken;
    }

    int getNumStrawberries() {
        return numStrawberries;
    }

    int getPriceAecker() {
        return priceAecker;
    }

    int getPriceLand() {
        return priceLand;
    }

    int getPriceGurken() {
        return priceGurken;
    }

    int getSTRAWBERRY_PRICE() {
        return STRAWBERRY_PRICE;
    }

    void landGekauft() {
        numLand++;
        globalVariables.setGold(globalVariables.getGold() - priceLand);
        priceLand = getPrice(2);
    }

    void gurkeGekauft() {
        numGurken++;
        globalVariables.setGold(globalVariables.getGold() - priceGurken);
        priceGurken = getPrice(1);
    }
}
