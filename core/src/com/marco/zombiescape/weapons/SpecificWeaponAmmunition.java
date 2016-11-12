package com.marco.zombiescape.weapons;

import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

/**
 * Created by Marco A. Fernández Heras on 10/03/16.
 */
public class SpecificWeaponAmmunition extends AbstractAmmunition{
    private Class<? extends Weapon> clazz;
    int add = 10;

    public SpecificWeaponAmmunition(World world, float x, float y, Class<? extends  Weapon> clazz) {
        super(world, x, y);
        this.clazz = clazz;
        if(clazz.equals(Shotgun.class))
            add = 5;
    }

    @Override
    public void handleAmmunition(List<Weapon> weapons) {
        for (Weapon weapon : weapons) {
            if(weapon.getClass().equals(clazz)){
                weapon.setAmmunition(weapon.getAmmunition() + add);
            }
        }

    }
}
