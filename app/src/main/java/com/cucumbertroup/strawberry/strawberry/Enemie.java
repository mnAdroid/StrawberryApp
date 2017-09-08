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
    private int lootLevel;
    private boolean alive;

    public Enemie(int level, int damage, int defense, Bitmap bitmapEnemie, String name, Weapon weapon, int life, int experience, int lootLevel, boolean alive) {
        this.level = level;
        this.damage = damage;
        this.defense = defense;
        this.bitmapEnemie = bitmapEnemie;
        this.name = name;
        this.weapon = weapon;
        this.life = life;
        this.experience = experience;
        this.lootLevel = lootLevel;
        this.alive = alive;
    }

    public Enemie(String name, int level) {
        switch (name) {
            case "Goblin":
                this.weapon = new Weapon("Knüppel");
                this.level = level;
                this.damage = level + weapon.getDamage();
                this.defense = level;
                this.name = name;
                this.life = 6 + 2*level;
                this.experience = 5 + Math.abs(level/4);
                this.lootLevel = 0;
                this.alive = true;
                break;
            case "Ork":
                this.weapon = new Weapon("Knüppel");
                this.level = level;
                this.damage = (int) (1.5*level + weapon.getDamage() + 4);
                this.defense = 5 + (int) (1.5*level);
                this.name = name;
                this.life = 20 + 3* level;
                this.experience = 15 + Math.abs(level/4);
                this.lootLevel = 1;
                this.alive = true;
                break;
            case "Dieb":
                this.weapon = new Weapon("Schwert");
                this.level = level;
                this.damage = 7 + level + weapon.getDamage();
                this.defense = 5 + level;
                this.name = name;
                this.life = 20 + 2*level;
                this.experience = 20 + Math.abs(level/4);
                this.lootLevel = 1;
                this.alive = true;
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

    public boolean getLifeStatus() { return alive; }

    public void defend(int damage) {
        if (life > 0) {
            //Die ersten 10 Defense Punkte zehlen 1 zu 1 als Abwehr. Danach nur noch zur Hälfte
            if (defense < 10) {
                if (damage >= defense) //Damit man beim Angreifen nicht heilt
                    life -= Math.abs(defense - damage);
            } else {
                if (damage - 10 >= ((defense - 10) / 2))
                    life -= Math.abs(((defense - 10) / 2) - (damage - 10));
            }
            if (life <= 0) {
                alive = false;
            }
        } else {
            alive = false;
        }
    }


}
