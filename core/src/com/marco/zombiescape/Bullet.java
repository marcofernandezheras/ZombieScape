package com.marco.zombiescape;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by marco on 12/02/16.
 */
public class Bullet implements Deleteable, Disposable {
    private final Body bullet;
    private static final Texture BULLET_TEX = new Texture("bullet.png");
    private final Sprite sprite;

    public Bullet(float fromX, float fromY, float toX, float toY) {
        sprite = new Sprite(BULLET_TEX);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.bullet = true;
        def.position.set(fromX, fromY);
        def.linearVelocity.set(toX, toY).nor().scl(30);
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

        frictionJointDef.maxForce = 0f; //This the most force the joint will apply to your object. The faster its moving the more force applied
        frictionJointDef.maxTorque = 0; //Set to 0 to prevent rotation

        WorldMapFactory.world.createJoint(frictionJointDef);
    }

    public void draw(SpriteBatch batch){
        Vector2 center = bullet.getWorldCenter();
        sprite.setPosition(center.x * Constants.METER2PIXEL, center.y * Constants.METER2PIXEL);
        sprite.setRotation((float) Math.toDegrees(bullet.getAngle()));
        sprite.draw(batch);
    }

    public void dispose(){
        try {
            bullet.getFixtureList().get(0).setUserData(null);
            WorldMapFactory.world.destroyBody(bullet);
        } catch(Exception e){

        }
    }
}
