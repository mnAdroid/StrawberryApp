package com.cucumbertroup.strawberry.strawberry;

import android.graphics.Bitmap;

/**
 * Created by Max on 07.09.2017.
 */

public class Enemie {
    private int level;
    private int damage;
    private int defense;
    private Bitmap bitmapEnemie;
    private String name;
    private Weapon weapon;
    private int life;
    private int positionX;
    private int positionY;
    private int experience;

    public Enemie(int level, int damage, int defense, Bitmap bitmapEnemie, String name, Weapon weapon, int life, int experience) {
        this.level = level;
        this.damage = damage;
        this.defense = defense;
        this.bitmapEnemie = bitmapEnemie;
        this.name = name;
        this.weapon = weapon;
        this.life = life;
        this.experience = experience;
    }

    public Enemie(String name, int level) {
        switch (name) {
            case "Goblin":
                this.weapon = new Weapon("Knüppel");
                this.level = level;
                this.damage = level + weapon.getDamage();
                this.defense = level/2;
                this.name = name;
                this.life = 2*level;
                this.experience = 5;
                break;
            case "Ork":
                this.weapon = new Weapon("Knüppel");
                this.level = level;
                this.damage = (int) (1.5*level + weapon.getDamage());
                this.defense = (int) (1.5*level);
                this.name = name;
                this.life = 4*level;
                this.experience = 15;
                break;
            case "Dieb":
                this.weapon = new Weapon("Schwert");
                this.level = level;
                this.damage = level + weapon.getDamage();
                this.defense = level;
                this.name = name;
                this.life = 2*level;
                this.experience = 20;
                break;
        }
    }

    public int getLevel() {
        return level;
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    public Bitmap getBitmapEnemie() {
        return bitmapEnemie;
    }

    public String getName() {
        return name;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int getLife() {
        return life;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getExperience() {
        return experience;
    }

    public boolean defend(int damage) {
        life = defense/2 - damage;
        if (life <= 0) {
            return false;
        }
        return true;

    }
}
