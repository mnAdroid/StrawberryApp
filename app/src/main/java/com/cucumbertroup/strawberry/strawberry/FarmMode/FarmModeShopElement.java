package com.cucumbertroup.strawberry.strawberry.FarmMode;

class FarmModeShopElement {
    private String name;
    private int price;
    private int necessaryAecker;
    private String infotext;
    //private Bitmap icon;

    //Erstellung falls nicht vorgegeben
    FarmModeShopElement(String name, int price, int necessaryAecker) {
        this.name = name;
        this.price = price;
        this.necessaryAecker = necessaryAecker;
    }

    FarmModeShopElement(String bezeichnung) {
        infotext = bezeichnung;
        switch (bezeichnung) {
            case "SamenI":
                name = "Samen";
                price = 42;
                necessaryAecker = 3;
                infotext = "Hiermit wird der Einkaufspreis von Erdbeersamen von 5 auf 3 Gold gesenkt.";
                break;
            case "VerkaufI":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 5;
                infotext = "Hiermit werden die Einnahmen beim Verkauf von Erdbeeren von 7 auf 11 Gold erhöht.";
                break;
            case "GurkeI":
                name = "Gurke";
                price = 42;
                necessaryAecker = 6;
                infotext = "Deine erste Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                break;
            case "SamenII":
                name = "Samen";
                price = 42;
                necessaryAecker = 8;
                infotext = "Hiermit wird der Einkaufspreis von Erdbeersamen auf 2 Gold gesenkt.";
                break;
            case "GurkeII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 9;
                infotext = "Deine zweite Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                break;
            case "VerkaufII":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 11;
                infotext = "Hiermit werden die Einnahmen beim Verkauf von Erdbeeren auf 13 Gold erhöht.";
                break;
            case "GurkeIII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 12;
                infotext = "Deine dritte Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                break;
            case "DungerI":
                name = "Dünger";
                price = 42;
                necessaryAecker = 13;
                infotext = "Hierdurch wachsen Erdbeeren pro Klick um zwei Stufen.";
                break;
            case "GurkeIV":
                name = "Gurke";
                price = 42;
                necessaryAecker = 14;
                infotext = "Deine vierte Arbeitshilfe ist verfügbar! Jede Gurke lässt deine Klicks einen Klick mehr zählen.";
                break;
            case "Fabrik":
                name = "Fabrik";
                price = 42;
                necessaryAecker = 15;
                infotext = "Das einfache Bauernleben ist vorbei! Ab sofort wird das Erdbeerfranchise aufgebaut!";
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
}
