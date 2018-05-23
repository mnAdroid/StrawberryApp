package com.cucumbertroup.strawberry.strawberry.FarmMode;

import android.content.Context;
import android.graphics.Bitmap;

import com.cucumbertroup.strawberry.strawberry.R;

import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.decodeSampledBitmapFromResource;
import static com.cucumbertroup.strawberry.strawberry.BitmapCalculations.getScaledBitmapSize;

class FarmModeShopElement {
    private String name;
    private int price;
    private int necessaryAecker;
    private String infotext;
    private Bitmap icon;

    FarmModeShopElement(String bezeichnung, Context fullContext, int bitmapQuality, int screenX, int screenY) {
        infotext = bezeichnung;
        switch (bezeichnung) {
            case "SamenI":
                name = "Samen";
                price = 42;
                necessaryAecker = 3;
                infotext = "Hiermit wird der Einkaufspreis von Erdbeersamen von 5 auf 3 Gold gesenkt.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_samen, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "VerkaufI":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 5;
                infotext = "Hiermit werden die Einnahmen beim Verkauf von Erdbeeren von 7 auf 11 Gold erhöht.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_muenzen, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "GurkeI":
                name = "Gurke";
                price = 42;
                necessaryAecker = 6;
                infotext = "Deine erste Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_gurke, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "SamenII":
                name = "Samen";
                price = 42;
                necessaryAecker = 8;
                infotext = "Hiermit wird der Einkaufspreis von Erdbeersamen auf 2 Gold gesenkt.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_samen, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "GurkeII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 9;
                infotext = "Deine zweite Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_gurke, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "VerkaufII":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 11;
                infotext = "Hiermit werden die Einnahmen beim Verkauf von Erdbeeren auf 13 Gold erhöht.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_muenzen, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "GurkeIII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 12;
                infotext = "Deine dritte Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_gurke, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "DungerI":
                name = "Dünger";
                price = 42;
                necessaryAecker = 13;
                infotext = "Hierdurch wachsen Erdbeeren pro Klick um zwei Stufen.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_dunger, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "GurkeIV":
                name = "Gurke";
                price = 42;
                necessaryAecker = 14;
                infotext = "Deine vierte Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_gurke, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;
            case "Fabrik":
                name = "Fabrik";
                price = 42;
                necessaryAecker = 15;
                infotext = "Das einfache Bauernleben ist vorbei! Ab sofort wird das Erdbeerfranchise aufgebaut!";
                icon = decodeSampledBitmapFromResource(fullContext.getResources(), R.drawable.shopicon_fabrik, bitmapQuality, bitmapQuality);
                icon = Bitmap.createScaledBitmap(icon, getScaledBitmapSize(screenX, 1080, 200), getScaledBitmapSize(screenY, 1920, 200), false);
                break;

                default:
                    name = "Fehler";
                    price = 17;
                    necessaryAecker = 0;
                    infotext = "Das hier solltest du nicht sehen.";
                    break;
        }
    }

    String getName() {
        return name;
    }

    int getPrice() {
        return price;
    }

    public int getNecessaryAecker() {
        return necessaryAecker;
    }

    String getInfotext() {
        return infotext;
    }

    public Bitmap getIcon() {
        return icon;
    }
}
