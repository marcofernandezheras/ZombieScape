package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import com.marco.zombiescape.Deletable;

import java.util.List;

/**
 * Created by Marco A. Fernández Heras on 10/03/16.
 */
public interface Ammunition extends Disposable, Deletable{
    void handleAmmunition(List<Weapon> weapons);
    Sprite getSprite();
}
