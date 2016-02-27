package com.marco.zombiescape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by marco on 27/02/16.
 */
public abstract class Weapon {
    protected Body body;
    protected float currentDelayTime = 0.0f;
    protected float currentCoolDownTime = 0.0f;
    protected boolean onCoolDown = false;
    protected int shootCount = 0;

    protected Weapon(Body body) {
        this.body = body;
    }

    protected abstract int shootBeforeCooldDown();
    protected abstract float shootDelay();
    protected abstract float shootCoolDown();

    protected boolean onShoot = false;

    public void startShooting(){
        if(!onShoot){
            onShoot = true;
            currentDelayTime = 0;
            currentCoolDownTime = 0;
            onCoolDown = false;
            shootCount = 0;
        }
    }

    public void stopShooting(){
        onShoot = false;
    }

    public void act(float delta, float angle){
        if(onShoot){
            if(shootBeforeCooldDown() <= shootCount){
                currentCoolDownTime += delta;
                if(currentCoolDownTime >= shootCoolDown()){
                    currentCoolDownTime = 0;
                    shootCount = 0;
                }
            }
            else {
                currentDelayTime += delta;
                if (currentDelayTime > shootDelay()) {
                    currentDelayTime = 0;
                    shooTo(angle);
                    shootCount++;
                }
            }
        }
    }

    private void shooTo(float angle) {
        Vector2 worldCenter = body.getWorldCenter();
        Bullet.newBullet(worldCenter.x, worldCenter.y, angle);
    }
}
