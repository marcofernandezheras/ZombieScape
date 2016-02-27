package com.marco.zombiescape;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by marco on 25/02/16.
 */
public class LevelEnd {
    private final Body body;
    private Level level;
    private static final Sprite sprite = new Sprite(Resources.instance.getRegion("exit", 0));//new Texture("exit_0.png")

    public LevelEnd(Level level, float x, float y) {
        this.level = level;
        sprite.setPosition((x * Constants.METER2PIXEL) - sprite.getWidth()/2.0f ,
                (y * Constants.METER2PIXEL) - sprite.getHeight()/2.0f);
        new PointLight(WorldMapFactory.rayHandler, 15, Color.RED, 1.5f, x, y).setStaticLight(true);

        BodyDef bodyDef= new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        this.body = WorldMapFactory.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void notifyLevel(){
        this.level.setLevelEnd();
    }
}
