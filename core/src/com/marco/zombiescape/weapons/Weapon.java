package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by marco on 27/02/16.
 */
public abstract class Weapon {
    public void setBody(Body body) {
        this.body = body;
    }

    protected Body body;
    protected float currentDelayTime = 0.0f;
    protected float currentReloadTime = 0.0f;
    protected int shootCount = 0;

    protected float reloadDelay = 2;
    protected float shootDelay = .1f;

    protected int shootBeforeReload = 25;

    protected float damage = 1;
    boolean wantToShot = false;

    private enum state {
        STOPPED, SHOOTING, BETWEEN_SHOOTS, RELOADING
    }

    private state currentState = state.STOPPED;

    protected Weapon(Body body) {
        this.body = body;
    }

    protected abstract void doShoot(float angle);

    public void startShooting(){
        wantToShot = true;
        if(currentState == state.STOPPED){
            currentState = state.SHOOTING;
        }
    }

    public void stopShooting(){
        wantToShot = false;
        if(currentState != state.RELOADING && currentState != state.BETWEEN_SHOOTS)
            currentState = state.STOPPED;
    }

    public void manualReload(){
        currentState = state.RELOADING;
    }

    public void act(float delta, float angle){
        if(currentState == state.RELOADING){
            currentReloadTime += delta;
            if(currentReloadTime > reloadDelay){
                currentState = state.STOPPED;
                currentReloadTime = 0;
                shootCount = 0;
            }
        }
        else if(currentState == state.BETWEEN_SHOOTS){
            currentDelayTime += delta;
            if(currentDelayTime > shootDelay){
                currentDelayTime = 0;
                currentState = wantToShot ? state.SHOOTING : state.STOPPED;
            }
        }

        if (currentState == state.SHOOTING && wantToShot){
            doShoot(angle);
            shootCount++;
            currentState = shootCount >= shootBeforeReload ? state.RELOADING : state.BETWEEN_SHOOTS;
        }
    }

    public void draw(SpriteBatch batch){}


    public void incDamage(float inc) {
        damage += inc;
    }


    public void decDamage(float dec) {
        damage -= dec;
    }


    public void incShootDelay(float inc) {
        shootDelay += inc;
    }


    public void decShootDelay(float dec) {
        shootDelay -= dec;
    }


    public void incReloadDelay(float inc) {
        reloadDelay += inc;
    }


    public void decReloadDelay(float dec) {
        reloadDelay -= dec;
    }


    public void incShootBeforeReload(int inc) {
        shootBeforeReload += inc;
    }


    public void decShootBeforeReload(int dec) {
        shootBeforeReload -= dec;
    }
}
