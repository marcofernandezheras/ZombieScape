package com.marco.zombiescape.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.marco.zombiescape.Constants;
import com.marco.zombiescape.Resources;

/**
 * Created by Marco A. Fernández Heras on 10/03/16.
 */
public abstract class AbstractAmmunition implements Ammunition {

    private final Body body;
    Sprite sprite;
    private World world;
    private boolean deleteMe = false;

    public AbstractAmmunition(World world, float x, float y) {
        this.world = world;
        this.sprite = new Sprite(Resources.instance.getRegion("ammunition"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x,y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((float)(sprite.getWidth()/2.0) * Constants.PIXEL2METER, (float)(sprite.getHeight()/2.0) * Constants.PIXEL2METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(this);

        getSprite().setPosition( (x * Constants.METER2PIXEL) - sprite.getWidth()/2.0f, (y * Constants.METER2PIXEL) - sprite.getHeight()/2.0f);
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
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
