package com.cucumbertroup.strawberry.strawberry.FightMode;

/**
 * Created by Max on 03.10.2017.
 */

import android.graphics.Path;

public class FingerPath {

    public int strokeWidth;
    public Path path;

    public FingerPath(int strokeWidth, Path path) {
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}