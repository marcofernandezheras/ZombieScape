package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.marco.zombiescape.Bullet;
import com.marco.zombiescape.Resources;

/**
 * Created by marco on 28/02/16.
 */
public class Shotgun extends Weapon {
    private final Sprite wepSprite;

    public Shotgun(Body body) {
        super(body);
        reloadDelay = 1.1f;
        shootBeforeReload = 1;
        damage = 5;
        wepSprite = new Sprite(Resources.instance.getRegion("wep3"));
    }

    @Override
    protected void doShoot(float angle) {
        Vector2 worldCenter = body.getWorldCenter();
        Bullet.newBullet(damage, worldCenter.x, worldCenter.y, angle - 0.3f);
        Bullet.newBullet(damage, worldCenter.x, worldCenter.y, angle);
        Bullet.newBullet(damage, worldCenter.x, worldCenter.y, angle + 0.3f);
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
