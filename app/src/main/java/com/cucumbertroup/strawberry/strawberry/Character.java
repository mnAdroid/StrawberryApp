package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 08.09.2017.
 */

public class Character {
    private Weapon equipedWeapon;
    private int meleeDamage;
    private int rangeDamage;
    private int life;
    private int defense;
    private int experience;
    private int level;

    public Character() {
        equipedWeapon = new Weapon("Hacke");
        meleeDamage = 1;
        rangeDamage = 1;
        life = 10;
        defense = 1;
        experience = 0;
    }

    public Character(Weapon equipedWeapon, int meleeDamage, int rangeDamage, int life, int defense, int experience) {
        this.equipedWeapon = equipedWeapon;
        this.meleeDamage = meleeDamage;
        this.rangeDamage = rangeDamage;
        this.life = life;
        this.defense = defense;
        this.experience = experience;
    }

    public Weapon getEquipedWeapon() {
        return equipedWeapon;
    }

    public int getMeleeDamage() {
        return meleeDamage + equipedWeapon.getDamage();
    }

    public int getRangeDamage() {
        return rangeDamage;
    }

    public int getLife() {
        return life;
    }

    public int getDefense() {
        return defense;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public boolean setExperience(int experience) {
        this.experience += experience;
        return false;
    }

    public void setEquipedWeapon(Weapon weapon) {
        equipedWeapon = weapon;
    }
    public void levelUp(int meleeDamagePlus, int rangeDamagePlus, int lifePlus, int defensePlus) {
        meleeDamage += meleeDamagePlus;
        rangeDamage += rangeDamagePlus;
        life += lifePlus;
        defense += defensePlus;
    }

}
