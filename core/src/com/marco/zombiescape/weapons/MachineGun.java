package com.marco.zombiescape.weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.marco.zombiescape.Bullet;

/**
 * Created by marco on 27/02/16.
 */
public class MachineGun extends Weapon {
    public MachineGun(Body body) {
        super(body);
    }

    @Override
    protected void doShoot(float angle) {
        Vector2 worldCenter = body.getWorldCenter();
        Bullet.newBullet(damage, worldCenter.x, worldCenter.y, angle);
        Bullet.bulletSound.play(0.3f);
    }


}
