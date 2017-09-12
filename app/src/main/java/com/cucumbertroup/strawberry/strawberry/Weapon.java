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
    0: Keule
    1: Schwert
    2: Bogen
    3: Schild
     */
    private String name;
    private Bitmap bitmapWeapon;
    private int attackspeed; //in millisekunden

    public Weapon(int damage, int defense, int waffenart, String name, Bitmap bitmapWeapon, int attackspeed) {
        this.damage = damage;
        this.defense = defense;
        this.waffenart = waffenart;
        this.name = name;
        this.bitmapWeapon = bitmapWeapon;
        this.attackspeed = attackspeed;
    }

    public Weapon(int damage, int defense, int waffenart, String name, int attackspeed) {
        this.damage = damage;
        this.defense = defense;
        this.waffenart = waffenart;
        this.name = name;
        this.attackspeed = attackspeed;
    }

    public Weapon(String name) {
        switch (name) {
            case "Knüppel":
                this.damage = 5;
                this.defense = 0;
                this.waffenart = 0;
                this.name = name;
                this.attackspeed = 3000;
                break;
            case "Schwert":
                this.damage = 10;
                this.defense = 0;
                this.waffenart = 1;
                this.name = name;
                this.attackspeed = 1500;
                break;
            case "Riesenschwert":
                this.damage = 20;
                this.defense = 0;
                this.waffenart = 1;
                this.name = name;
                this.attackspeed = 2000;
                break;
            case "Holzschild":
                this.damage = 1;
                this.defense = 5;
                this.waffenart = 3;
                this.name = name;
                this.attackspeed = 2000;
                break;
            case "Hacke":
                this.damage = 2;
                this.defense = 0;
                this.waffenart = 0;
                this.name = name;
                this.attackspeed = 3000;
                break;
        }
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    public int getWaffenart() {
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
        this.attackspeed = 0;
    }

    public int getAttackspeed() { return attackspeed; }
}
