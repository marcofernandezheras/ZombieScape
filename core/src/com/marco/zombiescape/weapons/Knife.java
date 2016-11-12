package com.marco.zombiescape.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.marco.zombiescape.Constants;
import com.marco.zombiescape.Resources;
import com.marco.zombiescape.Zombie;

/**
 * Created by marco on 28/02/16.
 */
public class Knife extends Weapon {
    private final Sprite wepSprite;
    boolean doDraw = false;
    float drawPhases = 0;
    float currentAngle;

    private final Sprite sprite;
    public Knife(Body body) {
        super(body);
        this.damage = 7;
        sprite = new Sprite(Resources.instance.getRegion("knife"));
        sprite.setOrigin(0, sprite.getHeight()/2.0f);
        shootBeforeReload = 1;
        reloadDelay = 1f;
        wepSprite = new Sprite(Resources.instance.getRegion("wep0"));
    }

    @Override
    protected void doShoot(float angle) {
        System.out.println("doShoot");
        doDraw = true;
        drawPhases = 0;
        World world = body.getWorld();
        Vector2 center = body.getWorldCenter();
        currentAngle = (float) Math.toDegrees(angle);

        world.rayCast((fixture, point, normal, fraction)->{
            if(fixture.getUserData() instanceof Zombie){
                ((Zombie)fixture.getUserData()).beginHit(damage);
                return 0;
            }
            return 1;
        }, center, new Vector2((float) (center.x + (Math.cos(angle)*.6)), (float) (center.y + (Math.sin(angle)*.6))));
    }

    @Override
    public Sprite weaponSprite() {
        return wepSprite;
    }

    @Override
    public int getAmmunition() {
        return -1;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if(doDraw){
            sprite.rotate(currentAngle);
            Vector2 center = body.getWorldCenter();
            sprite.setPosition(center.x * Constants.METER2PIXEL, center.y * Constants.METER2PIXEL);
            sprite.draw(batch);
            drawPhases+= Gdx.graphics.getDeltaTime();
            if(drawPhases > .3){
                doDraw = false;
                drawPhases = 0;
            }
            sprite.rotate(-currentAngle);
        }

    }
}
