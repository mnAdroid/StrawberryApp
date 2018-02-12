package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 08.09.2017.
 */

public class Character {
    private Weapon equipedWeapon;
    private int baseDamage;
    private int life;
    private int maxLife;
    private int defense;
    private int experience;
    private int level;
    private int maxMana;
    private int mana;

    Character(Weapon equipedWeapon, int baseDamage, int life, int maxLife, int defense, int experience, int level, int maxMana, int mana) {
        this.equipedWeapon = equipedWeapon;
        this.baseDamage = baseDamage;
        this.life = life;
        this.maxLife = maxLife;
        this.defense = defense;
        this.experience = experience;
        this.level = level;
        this.maxMana = maxMana;
        this.mana = mana;
    }

    Weapon getEquipedWeapon() {
        return equipedWeapon;
    }

    int getMeleeDamage() {
        return baseDamage + equipedWeapon.getDamage();
    }

    int getBaseDamage() {
        return baseDamage;
    }

    int getLife() {
        return life;
    }

    //reset des Lebens nach einem Kampf
    void setLife() { life = maxLife; }

    int getBaseDefense() {
        return defense;
    }

    int getExperience() {
        return experience;
    }

    int getLevel() {
        return level;
    }

    int getMaxLife() { return maxLife; }

    boolean setExperience(int experience) {
        this.experience += experience;
        return canLevelUp();
    }

    boolean canLevelUp() {
        return experience >= getExperiencedNeeded() && getExperiencedNeeded() > 0;
    }

    void setEquipedWeapon(Weapon weapon) {
        equipedWeapon = weapon;
    }

    void levelUp(int baseDamagePlus, int lifePlus, int defensePlus, int manaPlus) {
        if (canLevelUp()) {
            baseDamage += baseDamagePlus;
            maxLife += lifePlus;
            defense += defensePlus;
            if (maxMana <= 100)
                maxMana += manaPlus;

            if (level <= 15) {
                experience -= 10*level + 7;
            }
            else if (level <= 30) {
                experience -= 25*level - 38;
            }
            else if(level <= 60) {
                experience -= 45*level -158;
            }
            level++;
        }
    }

    boolean gotAttacked(int damage) {
        if (life - damage < 0) {
            life = 0;
            return false;
        }
        else
            life -= damage;
        return true;
        //return true = alive; false = dead;
    }

    int getExperiencedNeeded() {
        if (level <= 15)
            return (10 * level + 7);
        if (level <= 30)
            return (25 * level - 38);
        if (level <= 60)
            return (45 * level - 158);
        return -1;
    }

    int getMaxMana() {
        return maxMana;
    }

    int getMana() {
        return mana;
    }

    public void useMana(int mana) {
        this.mana -= mana;
    }

    void recoverMana(int mana) {
        if (this.mana + mana < maxMana) {
            this.mana += mana;
        }
        else {
            this.mana = maxMana;
        }
    }
}
