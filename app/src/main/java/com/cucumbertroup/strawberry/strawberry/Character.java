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
    private long lastAttackTime;
    private int baseAttackspeed;
    private int attackspeed;
    private final int ATTACKSPEED_CAP = 500;

    public Character(Weapon equipedWeapon, int baseDamage, int life, int maxLife, int defense, int experience, int level, int baseAttackspeed) {
        this.equipedWeapon = equipedWeapon;
        this.baseDamage = baseDamage;
        this.life = life;
        this.maxLife = maxLife;
        this.defense = defense;
        this.experience = experience;
        this.level = level;
        this.baseAttackspeed = baseAttackspeed;
        this.attackspeed = baseAttackspeed + equipedWeapon.getAttackspeed();
    }

    public Weapon getEquipedWeapon() {
        return equipedWeapon;
    }

    public int getMeleeDamage() {
        return baseDamage + equipedWeapon.getDamage();
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getLife() {
        return life;
    }

    //reset des Lebens nach einem Kampf
    public void setLife() { life = maxLife; }

    public int getBaseDefense() {
        return defense;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public int getAttackspeed() {
        return attackspeed;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime() {
        lastAttackTime = System.currentTimeMillis();
    }

    public void resetLastAttackTime() {
        lastAttackTime = 0;
    }

    public int getBaseAttackspeed() {
        return baseAttackspeed;
    }

    public boolean setExperience(int experience) {
        this.experience += experience;
        return canLevelUp();
    }

    public boolean canLevelUp() {
        if (level <= 15 && experience >= (10 * level + 7) &&  (10 * level + 7) > 0) {
            return true;
        }
        if (level <= 30 && level > 15 && experience >= (25 * level - 38) && (25 * level - 38) > 0) {
            return true;
        }
        if (level <= 60 && level > 30 && experience >= (45 * level - 158) && (45 * level - 158) > 0) {
            return true;
        }
        return false;
    }

    public void setEquipedWeapon(Weapon weapon) {
        equipedWeapon = weapon;
        attackspeedUpdate();
    }

    public void levelUp(int baseDamagePlus, int lifePlus, int defensePlus, int attackspeedPlus) {
        if (canLevelUp() == true) {
            baseDamage += baseDamagePlus;
            maxLife += lifePlus;
            defense += defensePlus;
            //Vorerst ist das Attackspeedcap 2x pro Sekunde
            if (attackspeed >= ATTACKSPEED_CAP + 100) {
                baseAttackspeed -= attackspeedPlus;
                attackspeedUpdate();
            }

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

    public void gotAttacked(int damage) {
        life -= damage;
    }

    private void attackspeedUpdate() {
        attackspeed = baseAttackspeed + equipedWeapon.getAttackspeed();
    }

}
