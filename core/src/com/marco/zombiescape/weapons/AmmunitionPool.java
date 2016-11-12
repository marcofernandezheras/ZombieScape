package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Marco A. Fernández Heras on 10/03/16.
 */
public class AmmunitionPool {
    public static final AmmunitionPool instance = new AmmunitionPool();

    List<Ammunition> ammunitions = new ArrayList<>();

    public Ammunition newAmmunition(World world, float x, float y){
        //TODO other Ammunitions
        int i = ThreadLocalRandom.current().nextInt(0, 100);
        Ammunition am;
        if(i < 10) {
            am = new AllWeaponsAmmunition(world, x, y);
        }
        else if(i < 60){
            am = new SpecificWeaponAmmunition(world, x, y, Pistol.class);
        }
        else if(i < 90){
            am = new SpecificWeaponAmmunition(world, x, y, MachineGun.class);
        }
        else{
            am = new SpecificWeaponAmmunition(world, x, y, Shotgun.class);
        }
        ammunitions.add(am);
        return am;
    }

    public void draw(final SpriteBatch batch){
        ammunitions.forEach(a -> a.getSprite().draw(batch));
    }

    public void act(){
        ammunitions.stream().filter(a -> a.isMarketToDelete()).forEach(a -> a.dispose());
        ammunitions.removeIf(a -> a.isMarketToDelete());
    }

    public void clear(){
        ammunitions.clear();
    }
}
