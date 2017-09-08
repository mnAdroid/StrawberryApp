package com.cucumbertroup.strawberry.strawberry;

import android.graphics.Bitmap;

/**
 * Created by Max on 07.09.2017.
 */

public class Weapon {
    private int damage;
    private int defense;
    private boolean nahkampf;
    private String name;
    private Bitmap bitmapWeapon;

    public Weapon(int damage, int defense, boolean nahkampf, String name, Bitmap bitmapWeapon) {
        this.damage = damage;
        this.defense = defense;
        this.nahkampf = nahkampf;
        this.name = name;
        this.bitmapWeapon = bitmapWeapon;
    }

    public Weapon(int damage, int defense, boolean nahkampf, String name) {
        this.damage = damage;
        this.defense = defense;
        this.nahkampf = nahkampf;
        this.name = name;
    }

    public Weapon(String name) {
        switch (name) {
            case "Knüppel":
                this.damage = 5;
                this.defense = 0;
                this.nahkampf = true;
                this.name = name;
                break;
            case "Schwert":
                this.damage = 10;
                this.defense = 0;
                this.nahkampf = true;
                this.name = name;
                break;
            case "Riesenschwert":
                this.damage = 20;
                this.defense = 0;
                this.nahkampf = true;
                this.name = name;
                break;
            case "Holzschild":
                this.damage = 1;
                this.defense = 5;
                this.nahkampf = true;
                this.name = name;
                break;
            case "Hacke":
                this.damage = 2;
                this.defense = 0;
                this.nahkampf = true;
                this.name = name;
                break;
        }
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    public boolean getNahkampf() {
        return nahkampf;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmapWeapon() {
        return bitmapWeapon;
    }
}
