package com.marco.zombiescape;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Created by marco on 25/02/16.
 */
public class WorldContactFilter implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Object userData = fixtureA.getUserData();
        Object userData1 = fixtureB.getUserData();
        if(userData instanceof Player || userData1 instanceof Player){
            return !(userData instanceof Bullet) && !(userData1 instanceof Bullet);
        }
        if(userData instanceof Zombie || userData1 instanceof Zombie){
            if(userData == null || userData1 == null) {
                Zombie zombie = userData1 != null ? (Zombie)userData1 : (Zombie)userData;
                return handleZombieWallCollision(zombie);
            }
        }
        if(userData instanceof Bullet && userData1 instanceof Bullet)
            return false;
        return userData != userData1;
    }

    private boolean handleZombieWallCollision(Zombie zombie){
        zombie.changeDirection();
        return false;
    }
}
