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
                break;
            case "VerkaufI":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 5;
                break;
            case "GurkeI":
                name = "Gurke";
                price = 42;
                necessaryAecker = 6;
                break;
            case "SamenII":
                name = "Samen";
                price = 42;
                necessaryAecker = 8;
                break;
            case "GurkeII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 9;
                break;
            case "VerkaufII":
                name = "Verkauf";
                price = 42;
                necessaryAecker = 11;
                break;
            case "GurkeIII":
                name = "Gurke";
                price = 42;
                necessaryAecker = 12;
                break;
            case "DungerI":
                name = "Dünger";
                price = 42;
                necessaryAecker = 13;
                break;
            case "GurkeIV":
                name = "Gurke";
                price = 42;
                necessaryAecker = 14;
                break;
            case "Fabrik":
                name = "Fabrik";
                price = 42;
                necessaryAecker = 15;
                infotext = "Hallo ich bin Max und mir gehts gut";
                break;

                default:
                    name = "Fehler";
                    price = 17;
                    necessaryAecker = 0;
                    infotext = "Das hier solltest\n du nicht sehen.";
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
