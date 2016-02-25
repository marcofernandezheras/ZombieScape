package com.marco.zombiescape;

import com.badlogic.gdx.math.Vector2;

import static java.lang.Math.atan2;

/**
 * Created by marco on 25/02/16.
 */
public class Util {
    private Util(){}

    public static float angle(float x1, float y1, float x2, float y2){
        float deltaX = x1 - x2;
        float deltaY = y1 - y2;
        return (float) atan2(deltaY, deltaX);
    }

    private static Vector2 distanceCache = new Vector2();
    public static float distance(float x1, float y1, float x2, float y2){
        return distanceCache.set(x1- x2, y1 - y2).len();
    }
}
