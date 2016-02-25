package com.marco.zombiescape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by marco on 25/02/16.
 */
public class WolrdContactListener implements ContactListener {

    private void handleContactZombieBullet(Zombie zombie, Bullet bullet){
        bullet.markToDelete();
        zombie.beginHit();
        if(zombie.getLife() <= 0){
            zombie.markToDelete();
        }
    }

    private void handleBeginContactZombiePlayer(Zombie zombie, Player player, boolean inSensor){
        zombie.setAggro(player);
        if(!inSensor)
            player.beginHit();
    }

    private void handleEndContactZombiePlayer(Zombie zombie, Player player, boolean inSensor){
        if (inSensor)
            zombie.setCalm();
        player.endHit();
    }

    @Override
    public void beginContact(Contact contact) {
        Object userData = contact.getFixtureA().getUserData();
        Object userData1 = contact.getFixtureB().getUserData();
        if(userData instanceof Bullet || userData1 instanceof Bullet){
            if(userData == null || userData1 == null){//Wall
                Bullet bullet = userData != null ? (Bullet) userData : (Bullet) userData1;
                bullet.markToDelete();
            }
            else if(userData instanceof Zombie || userData1 instanceof Zombie){
                if(userData instanceof Bullet || userData1 instanceof Bullet) {
                    Zombie z = userData instanceof Zombie ? (Zombie)userData : (Zombie)userData1;
                    Bullet b = userData instanceof Bullet ? (Bullet)userData : (Bullet)userData1;
                    handleContactZombieBullet(z,b);
                }
            }
        }
        if(userData instanceof Zombie || userData1 instanceof Zombie){
            if(userData instanceof Player || userData1 instanceof Player) {
                Zombie z = userData instanceof Zombie ? (Zombie)userData : (Zombie)userData1;
                Player p = userData instanceof Player ? (Player)userData : (Player)userData1;
                boolean isSensor = userData instanceof Player ? contact.getFixtureA().isSensor() : contact.getFixtureB().isSensor();
                handleBeginContactZombiePlayer(z,p, isSensor);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object userData = contact.getFixtureA().getUserData();
        Object userData1 = contact.getFixtureB().getUserData();
        if(userData instanceof Zombie || userData1 instanceof Zombie){
            if(userData instanceof Player || userData1 instanceof Player) {
                Zombie z = userData instanceof Zombie ? (Zombie)userData : (Zombie)userData1;
                Player p = userData instanceof Player ? (Player)userData : (Player)userData1;
                boolean isSensor = userData instanceof Player ? contact.getFixtureA().isSensor() : contact.getFixtureB().isSensor();
                handleEndContactZombiePlayer(z,p, isSensor);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Object userData = contact.getFixtureA().getUserData();
        Object userData1 = contact.getFixtureB().getUserData();
        if(userData instanceof Bullet || userData1 instanceof Bullet){
            if(userData instanceof Zombie || userData1 instanceof Zombie){
                Vector2 v1 = contact.getFixtureA().getBody().getLinearVelocity().nor().scl(0.05f);
                contact.getFixtureA().getBody().setLinearVelocity(v1.x, v1.y);

                Vector2 v2 = contact.getFixtureB().getBody().getLinearVelocity().nor().scl(0.05f);
                contact.getFixtureB().getBody().setLinearVelocity(v2.x, v2.y);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
