package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 08.09.2017.
 */

public class Character {
    private Weapon equipedWeapon;
    private int baseDamage;
    private int life;
    private int defense;
    private int experience;
    private int level;

    public Character() {
        equipedWeapon = new Weapon("Hacke");
        baseDamage = 1;
        life = 10;
        defense = 1;
        experience = 0;
        level = 1;
    }

    public Character(Weapon equipedWeapon, int baseDamage, int life, int defense, int experience, int level) {
        this.equipedWeapon = equipedWeapon;
        this.baseDamage = baseDamage;
        this.life = life;
        this.defense = defense;
        this.experience = experience;
        this.level = level;
    }

    public Weapon getEquipedWeapon() {
        return equipedWeapon;
    }

    public int getMeleeDamage() {
        return baseDamage + equipedWeapon.getDamage();
    }

    public int getRangeDamage() {
        return baseDamage;
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
        return canLevelUp();
    }

    public boolean canLevelUp() {
        if (level <= 15 && experience >= 10*level + 7) {
            return true;
        }
        if (level <= 30 && experience >= 25*level - 38) {
            return true;
        }
        if (level <= 60 && experience >= 45*level -158 ) {
            return true;
        }
        return false;
    }

    public void setEquipedWeapon(Weapon weapon) {
        equipedWeapon = weapon;
    }

    public void levelUp(int baseDamagePlus, int lifePlus, int defensePlus) {
        baseDamage += baseDamagePlus;
        life += lifePlus;
        defense += defensePlus;
        experience = 0;
        level++;
    }

}
