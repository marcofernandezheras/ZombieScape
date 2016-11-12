package com.marco.zombiescape.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    //public static Sound emptySound = Gdx.audio.newSound();
    private static Music empty = Gdx.audio.newMusic(Gdx.files.internal("emptyGun.mp3"));


    protected int ammunition = 10;

    protected enum state {
        STOPPED, SHOOTING, BETWEEN_SHOOTS, RELOADING, EMPTY
    }

    protected state currentState = state.STOPPED;

    protected Weapon(Body body) {
        this.body = body;
    }

    protected abstract void doShoot(float angle);
    public abstract Sprite weaponSprite();

    public  int getAmmunition(){
        return ammunition;
    }

    public void setAmmunition(int ammunition) {
        this.ammunition = ammunition;
        currentState = state.STOPPED;
    }

    public void startShooting(){
        if (currentState == state.EMPTY) {
            if(!empty.isPlaying())
                empty.play();
            return;
        }
        wantToShot = true;
        if(currentState == state.STOPPED){
            currentState = state.SHOOTING;
        }
    }

    public void stopShooting(){
        if (currentState == state.EMPTY)
            return;
        wantToShot = false;
        if(currentState != state.RELOADING && currentState != state.BETWEEN_SHOOTS)
            currentState = state.STOPPED;
    }

    public void manualReload(){
        currentState = state.RELOADING;
    }

    public void act(float delta, float angle){
        if (currentState == state.EMPTY)
            return;

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
            if(currentState != state.EMPTY)
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
