package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.marco.zombiescape.Bullet;
import com.marco.zombiescape.Resources;

/**
 * Created by marco on 27/02/16.
 */
public class MachineGun extends Weapon {
    private final Sprite wepSprite;

    public MachineGun(Body body) {
        super(body);
        wepSprite = new Sprite(Resources.instance.getRegion("wep2"));
    }

    @Override
    protected void doShoot(float angle) {
        Vector2 worldCenter = body.getWorldCenter();
        Bullet.newBullet(damage, worldCenter.x, worldCenter.y, angle);
        Bullet.bulletSound.play(0.3f);
        ammunition--;
        if(ammunition == 0)
            currentState = state.EMPTY;
    }

    @Override
    public Sprite weaponSprite() {
        return wepSprite;
    }

}
