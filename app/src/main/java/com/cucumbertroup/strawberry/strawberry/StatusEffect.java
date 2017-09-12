package com.cucumbertroup.strawberry.strawberry;

/**
 * Created by Max on 12.09.2017.
 */

public class StatusEffect {
    private String name;
    private int statusEffectNumber; //-1: nix, 0: brennend, 1: stunned
    private long timeGotEffect;
    private long lastTick;

    public StatusEffect(String name) {
        switch (name) {
            case "default":
                this.name = name;
                this.statusEffectNumber = -1;
                break;
            case "ignite":
                this.name = name;
                this.statusEffectNumber = 0;
                timeGotEffect = System.currentTimeMillis();
                break;
            case "stun":
                this.name = name;
                this.statusEffectNumber = 1;
                timeGotEffect = System.currentTimeMillis();
        }
    }

    public String getName() {
        return name;
    }

    public int getStatusEffectNumber() {
        return statusEffectNumber;
    }

    public long getLastTick() {
        return lastTick;
    }

    public void setLastTick() {
        lastTick = System.currentTimeMillis();
    }

    public long getTimeGotEffect() {
        return timeGotEffect;
    }

    public void setStatusEffect(String name) {
        switch (name) {
            case "default":
                this.name = name;
                statusEffectNumber = -1;
                timeGotEffect = 0;
                lastTick = 0;
                break;
            case "ignite":
                this.name = name;
                statusEffectNumber = 0;
                timeGotEffect = System.currentTimeMillis();
                lastTick = System.currentTimeMillis() + 500;
                break;
            case "stun":
                this.name = name;
                statusEffectNumber = 1;
                timeGotEffect = System.currentTimeMillis();
                lastTick = System.currentTimeMillis() + 500;
                break;
        }
    }

    public void resetStatusEffect() {
        name = "default";
        statusEffectNumber = -1;
        timeGotEffect = 0;
    }
}
