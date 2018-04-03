package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.cucumbertroup.strawberry.strawberry.GlobalVariables;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledCoordinates;

class FarmModeBackend {
    //Singleton
    private static FarmModeBackend instance;
    private GlobalVariables globalVariables;
    private FarmModeSound farmModeSound;

    //Erdbeeren Array
    private Strawberry[] strawberries;
    private int numStrawberries = 0;
    //Anzahl und Preis der Farmfläche
    private int numAecker;
    private int priceAecker;
    //Anzahl und Preis der arbeitenden Gurken
    private int numGurken;
    private int priceGurken;

    //Qualität: 250 - 1000
    private int bitmapMainQuality;

    private int bitmapStrawberryX1, bitmapStrawberryX2,
            bitmapStrawberryX3, bitmapStrawberryX4;
    //Erdbeerkosten
    private int strawberryPrice = 1;

    private FarmModeBackend(int screenX) {
        globalVariables = GlobalVariables.getInstance();
        farmModeSound = FarmModeSound.getInstance();

        setBitmapMainQuality(500);

        //getSharedPreferences(context);

        //Standard Erdbeerkoordinaten
        bitmapStrawberryX1 = getScaledCoordinates(screenX, 1080, 50);
        bitmapStrawberryX2 = getScaledCoordinates(screenX, 1080, 290);
        bitmapStrawberryX3 = getScaledCoordinates(screenX, 1080, 530);
        bitmapStrawberryX4 = getScaledCoordinates(screenX, 1080, 770);
    }

    static synchronized FarmModeBackend getInstance(int screenX) {
        if (FarmModeBackend.instance == null) {
            FarmModeBackend.instance = new FarmModeBackend(screenX);
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
    void gotClickedFarm(int zustand, Context fullContext) {
        globalVariables.incrementClickCount();
        switch(zustand) {
            case 0:
                //Aussähen: Prüfen ob noch Platz ist, wenn ja: Aussähen.
                for(int j = 1; j <= numGurken; j++) {
                    if (numStrawberries < (numAecker * 8)) {
                        for (int i = 0; i < numAecker * 8; i++) {
                            if (globalVariables.getGold() >= strawberryPrice && strawberries[i].getWachsStatus() <= -1) {
                                strawberries[i].setStrawberry();
                                numStrawberries++;
                                globalVariables.setGold(globalVariables.getGold() - strawberryPrice);
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
                //Wachsen / Giessen: Man gießt jede Pflanze einzeln.
                for (int i = 0; i < numAecker * 8; i++) {
                    boolean tmp = false;
                    for(int j = 0; j < numGurken; j++) {
                        if (i < numStrawberries)
                            tmp = strawberries[i].incrWachsStatus();
                        if (tmp) {
                            i++;
                            if (j == 0) {
                                farmModeSound.playSound(2, fullContext);
                            }
                        }
                    }
                    if (tmp)
                        break;
                }
                break;
            case 2:
                //Ernten: Prüfen ob Erdbeeren fertig, wenn ja: Gold bekommen und Platz machen zum Aussähen
                for(int j = 1; j <= numGurken; j++) {
                    for (int i = 0; i < numAecker * 8; i++) {
                        if (strawberries[i].getWachsStatus() >= 4) {
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
    void getSharedPreferences(final Context fullContext) {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        numStrawberries = sharedPreferences.getInt("numStrawberries", 0);
        numAecker = sharedPreferences.getInt("numAecker", 1);
        String strawberryStatus = sharedPreferences.getString("strawberryStatus", "");
        numGurken = sharedPreferences.getInt("numGurken", 1);
        strawberryPrice = sharedPreferences.getInt("strawberryPrice",5);

        //Initialisierung der gespeicherten Erdbeeren
        strawberries = new Strawberry[numAecker * 8];

        Log.d("numGurken", "" + numGurken);
        Log.d("numGurkenShared", "" +  sharedPreferences.getInt("numGurken", 1));
        //Preise initialisieren
        priceAecker = calcPrice(0);
        priceGurken = calcPrice(1);

        //Erdbeer Array erstellen
        //um keine IndexoutofBoundException zu bekommen
        if (!(strawberryStatus.equals(""))) {
            //1. String auseinander nehmen, 2. aus den Daten auslesen
            //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
            //vierte: x wert fuenfte: y wert
            String[] strawberryStatusStrings = strawberryStatus.split("@");
            int stringsCounter = 0;
            try {
                for (int i = 0; i < (numAecker * 8); i++) {
                    strawberries[i] = new Strawberry(Integer.parseInt(strawberryStatusStrings[stringsCounter]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 1]), Long.parseLong(strawberryStatusStrings[stringsCounter + 2]), Integer.parseInt(strawberryStatusStrings[stringsCounter + 3]), Boolean.parseBoolean(strawberryStatusStrings[stringsCounter + 4]));
                    stringsCounter += 5;
                }
            }
            //Wenn das Einlesen des Strings nicht geklappt hat
            catch (NumberFormatException e) {
                try {
                    Toast.makeText(fullContext, "Leider sind die Erdbeeren verloren gegangen.", Toast.LENGTH_SHORT).show();
                } catch (RuntimeException runtimeException) {
                    //unlucky
                    //eine eigene Klasse um Messages anzuzeigen könnte hier genutzt werden?
                }
                //Neues StrawberryArray erstellen
                strawberries = newStrawberrieArray(strawberries, 0, (numAecker * 8));
                numStrawberries = 0;
            }
        } else {
            //Neues StrawberryArray erstellen
            strawberries = newStrawberrieArray(strawberries, 0, (numAecker * 8));
        }
    }

    //Das StrawberryArray an einer Stelle vergrößern oder (bei erstem Start) erstellen
    private Strawberry[] newStrawberrieArray(Strawberry[] strawberry, int range1, int range2) {
        boolean reihe1 = false;
        for (int i = range1; i < range2; i++) {
            //In welcher Zeile (pro Acker) existiert die Erdbeere?
            if (i%4 == 0 ) {
                    reihe1 = !reihe1;
            }
            //In welcher Spalte wird die Erdbeere eingefügt?
            switch (i % 4) {
                case 0:
                    strawberry[i] = new Strawberry((i / 8) + 1, bitmapStrawberryX1,  reihe1);
                    break;
                case 1:
                    strawberry[i] = new Strawberry((i / 8) + 1, bitmapStrawberryX2,  reihe1);
                    break;
                case 2:
                    strawberry[i] = new Strawberry((i / 8) + 1, bitmapStrawberryX3,  reihe1);
                    break;
                case 3:
                    strawberry[i] = new Strawberry((i / 8) + 1, bitmapStrawberryX4,  reihe1);
                    break;
            }
        }
        return strawberry;
    }

    //SharedPreferences wieder sicher verwahren
    void setSharedPreferences(Context fullContext) {
        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberrySettings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numStrawberries", numStrawberries);
        editor.putInt("numAecker", numAecker);
        editor.putInt("numGurken", numGurken);
        editor.putInt("strawberryPrice", strawberryPrice);

        //Hier kommen alle derzeitigen Erdbeeren rein um gespeichert zu werden
        StringBuilder strawberryStatus = new StringBuilder();
        //Der erste Teil: wachsstatus, der zweite: Ackernummer, der dritte: Zeit
        //der vierte: X Koordinate
        //der fuenfte: Y Koordinate
        for (int i = 0; i < (numAecker * 8); i++) {
            strawberryStatus.append(strawberries[i].getWachsStatus());
            strawberryStatus.append("@");
            strawberryStatus.append(strawberries[i].getAcker());
            strawberryStatus.append("@");
            strawberryStatus.append(strawberries[i].getTimeThisFruit());
            strawberryStatus.append("@");
            strawberryStatus.append(strawberries[i].getCoordinateX());
            strawberryStatus.append("@");
            strawberryStatus.append(strawberries[i].isReihe1());
            strawberryStatus.append("@");
        }
        editor.putString("strawberryStatus", strawberryStatus.toString());

        editor.apply();
    }

    //Gibt den Preis der Elemente aus dem Shop aus
    private int calcPrice(int whichOne) {
        //whichOne Legende: 0: Acker, 1: Gurke, 2: früher Land, 3: Werkzeug
        switch (whichOne) {
            case 0:
                return (int) (50*Math.pow((double) numAecker, 1.7));
            case 1:
                return (int) (500*Math.pow((double) numGurken, 1.5));
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
        priceAecker = calcPrice(0);

        //Neues Strawberry Array erstellen
        Strawberry[] strawberriesTemp = Arrays.copyOf(strawberries, numAecker*8);
        //Das Strawberry Array wird in extra Funktion gefüllt
        strawberries = newStrawberrieArray(strawberriesTemp, ((numAecker-1) * 8), (numAecker * 8));
    }

    int getNumAecker() {
        return numAecker;
    }

    int getNumGurken() {
        return numGurken;
    }

    int getPriceAecker() {
        return priceAecker;
    }

    int getPriceGurken() {
        return priceGurken;
    }

    int getStrawberryPrice() {
        return strawberryPrice;
    }

    Strawberry getSpecificStrawberry(int index) {
        if (strawberries.length > index) {
            return strawberries[index];
        }
        return null;
    }

    void gurkeGekauft() {
        numGurken++;
        globalVariables.setGold(globalVariables.getGold() - priceGurken);
        priceGurken = calcPrice(1);
    }

    int getBitmapMainQuality() {
        return bitmapMainQuality;
    }

    void setBitmapMainQuality(int bitmapMainQuality) {
        if (bitmapMainQuality >= 250 && bitmapMainQuality <= 1000)
            this.bitmapMainQuality = bitmapMainQuality;
    }

    ArrayList<FarmModeShopElement> getShopElements(Context fullContext) {
        ArrayList<FarmModeShopElement> shopElements = null;

        SharedPreferences sharedPreferences = fullContext.getSharedPreferences("StrawberryShopElements", 0);
        String listString = sharedPreferences.getString("listString", "@@SamenI@@VerkaufI@GurkeI@@SamenII@GurkeII@@VerkaufII@GurkeIII@DungerI@GurkeIV@@Fabrik@");

        //um keine IndexoutofBoundException zu bekommen
        if (listString.equals("")) {
            Log.e("StrawberryShopElements", "Einlesen fehlgeschlagen");
            listString = "@@SamenI@@VerkaufI@GurkeI@@SamenII@GurkeII@@VerkaufII@GurkeIII@DungerI@GurkeIV@@Fabrik@";
        }

        /*Stringgestaltung:
        1. -
        2. -
        3. Samen I
        4. -
        5. Verkauf I
        6. Gurke I
        7. -
        8. Samen II
        9. Gurke II
        10. -
        11. Verkauf II
        12. Gurke III
        13. Dunger I
        14. Gurke IV
        15 -
        16. Fabrik
         */

        //1. String auseinander nehmen, 2. aus den Daten auslesen
        String[] shopElementsString = listString.split("@");

        for (String aShopElementsString : shopElementsString) {
            //Ist das Element leer ist es egal
            //"€" ist das zeichen für Verkauft
            if (aShopElementsString.equals("") && shopElementsString[0].equals("€"))
                continue;

            //Neues Shop Element einfügen
            shopElements.add(new FarmModeShopElement(aShopElementsString));

            //Wenn bereits drei Elemente abgerufen wurden reichts
            if (shopElements.size() >= 3)
                break;
        }

        return shopElements;
    }

    void recycle() {
        //Erdbeeren aufräumen
        for (int i = 0; i < strawberries.length; i++)
            strawberries[i] = null;
        //Erdbeeren Array aufräumen
        strawberries = null;
        //Klasse selbst vernichten
        instance = null;
    }
}
