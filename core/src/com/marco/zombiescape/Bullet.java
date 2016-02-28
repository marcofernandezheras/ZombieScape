package com.marco.zombiescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 12/02/16.
 */
public class Bullet implements Deletable, Disposable {
    private final Body bullet;
    private static final Sprite sprite = new Sprite(Resources.instance.getRegion("bullet"));
    private boolean deleteMe = false;

    public float getDamage() {
        return damage;
    }

    private final float damage;
    public static Sound bulletSound = Gdx.audio.newSound(Gdx.files.internal("shootSound.mp3"));

    protected static final List<Bullet> bulletPool = new ArrayList<>();

    private Bullet(float damage, float fromX, float fromY, float angle) {
        this.damage = damage;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.bullet = true;
        def.position.set(fromX, fromY);
        def.linearVelocity.set((float) (1 * Math.cos(angle)), (float) (1 * Math.sin(angle))).nor().scl(30);
        bullet = WorldMapFactory.world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.05f, 0.05f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Fixture fixture = bullet.createFixture(fixtureDef);
        fixture.setUserData(this);

        FrictionJointDef frictionJointDef = new FrictionJointDef();

        frictionJointDef.localAnchorA.set(0,0);
        frictionJointDef.localAnchorB.set(0,0);

        frictionJointDef.bodyA = bullet;
        frictionJointDef.bodyB = WorldMapFactory.getGroundBody();

        frictionJointDef.maxForce = 0f; //This the most force the joint will applya to your object. The faster its moving the more force applied
        frictionJointDef.maxTorque = 0; //Set to 0 to prevent rotation

        WorldMapFactory.world.createJoint(frictionJointDef);
    }

    public static Bullet newBullet(float damage, float fromX, float fromY, float angle){
        Bullet bullet = new Bullet(damage, fromX, fromY, angle);
        bulletPool.add(bullet);
        return bullet;
    }

    public void draw(SpriteBatch batch){
        Vector2 center = bullet.getWorldCenter();
        sprite.setPosition(center.x * Constants.METER2PIXEL, center.y * Constants.METER2PIXEL);
        sprite.setRotation((float) Math.toDegrees(bullet.getAngle()));
        sprite.draw(batch);
    }

    @Override
    public void dispose(){
        try {
            bullet.getFixtureList().get(0).setUserData(null);
            WorldMapFactory.world.destroyBody(bullet);
        } catch(Exception e){/*expected*/}
    }

    @Override
    public void markToDelete() {
        deleteMe = true;
    }

    @Override
    public boolean isMarketToDelete() {
        return deleteMe;
    }
}
