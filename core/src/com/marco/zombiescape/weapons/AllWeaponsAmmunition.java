package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

/**
 * Created by Marco A. Fernández Heras on 10/03/16.
 */
public class AllWeaponsAmmunition extends AbstractAmmunition {

    public AllWeaponsAmmunition(World world, float x, float y) {
        super(world, x, y);
    }

    @Override
    public void handleAmmunition(List<Weapon> weapons) {
        for (Weapon weapon : weapons) {
            if(!(weapon instanceof Knife))
                weapon.setAmmunition(weapon.getAmmunition() + 10);
        }
    }
}
