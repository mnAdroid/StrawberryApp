package com.cucumbertroup.strawberry.strawberry;

import android.graphics.Bitmap;

/**
 * Created by Max on 07.09.2017.
 */

public class Weapon {
    private int damage;
    private int defense;
    private int waffenart;
    /*Waffenart:
    0: Einhand
    1: Zweihand
    2: Bogen
    3: Schild
     */
    private String name;
    private Bitmap bitmapWeapon;

    /*public Weapon(int damage, int defense, int waffenart, String name, Bitmap bitmapWeapon) {
        this.damage = damage;
        this.defense = defense;
        this.waffenart = waffenart;
        this.name = name;
        this.bitmapWeapon = bitmapWeapon;
    }*/

    Weapon(String name) {
        switch (name) {
            case "Knüppel":
                this.damage = 5;
                this.defense = 0;
                this.waffenart = 2;
                this.name = name;
                break;
            case "Schwert":
                this.damage = 10;
                this.defense = 0;
                this.waffenart = 0;
                this.name = name;
                break;
            case "Riesenschwert":
                this.damage = 20;
                this.defense = 0;
                this.waffenart = 1;
                this.name = name;
                break;
            case "Holzschild":
                this.damage = 1;
                this.defense = 5;
                this.waffenart = 3;
                this.name = name;
                break;
            case "Bogen":
                this.damage = 2;
                this.defense = 0;
                this.waffenart = 2;
                this.name = name;
                break;
        }
    }

    int getDamage() {
        return damage;
    }

    int getDefense() {
        return defense;
    }

    int getWaffenart() {
        return waffenart;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmapWeapon() {
        return bitmapWeapon;
    }

    public void destroyWeapon() {
        this.damage = 0;
        this.defense = 0;
        this.waffenart = -1;
        this.name = null;
        if (bitmapWeapon != null) {
            this.bitmapWeapon.recycle();
            this.bitmapWeapon = null;
        }
    }
}
