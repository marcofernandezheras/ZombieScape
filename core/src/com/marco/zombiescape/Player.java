package com.marco.zombiescape;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by marco on 10/02/16.
 */
public class Player {

    private World world;

    private static final class Direction {
        static final int UP = 0, RIGTH = 1, DOWN = 2, LEFT = 3;
        static final Sprite[] up =    {
                new Sprite(new Texture("player/up_0.png")),
                new Sprite(new Texture("player/up_1.png")),
                new Sprite(new Texture("player/up_2.png")),
                new Sprite(new Texture("player/up_3.png")),
                new Sprite(new Texture("player/up_4.png")),
                new Sprite(new Texture("player/up_5.png")),
                new Sprite(new Texture("player/up_6.png"))
        };
        static final Sprite[] down =  {
                new Sprite(new Texture("player/down_0.png")),
                new Sprite(new Texture("player/down_1.png")),
                new Sprite(new Texture("player/down_2.png")),
                new Sprite(new Texture("player/down_3.png")),
                new Sprite(new Texture("player/down_4.png")),
                new Sprite(new Texture("player/down_5.png")),
                new Sprite(new Texture("player/down_6.png")),
        };
        static final Sprite[] left =  {
                new Sprite(new Texture("player/left_0.png")),
                new Sprite(new Texture("player/left_1.png")),
                new Sprite(new Texture("player/left_2.png")),
                new Sprite(new Texture("player/left_3.png")),
                new Sprite(new Texture("player/left_4.png")),
                new Sprite(new Texture("player/left_5.png")),
                new Sprite(new Texture("player/left_6.png")),

        };
        static final Sprite[] right = {
                new Sprite(new Texture("player/right_0.png")),
                new Sprite(new Texture("player/right_1.png")),
                new Sprite(new Texture("player/right_2.png")),
                new Sprite(new Texture("player/right_3.png")),
                new Sprite(new Texture("player/right_4.png")),
                new Sprite(new Texture("player/right_5.png")),
                new Sprite(new Texture("player/right_6.png")),
        };
    }

    private int currentDirection = Direction.DOWN;
    private final Body body;
    private Body ligthBody;
    private Vector2 velocity = new Vector2();
    private ConeLight ligth;
    private float delta;
    private int frame = 0;
    private List<Body> toDelete = new ArrayList<>();

    public Player(World world, float x, float y) {
        this.world = world;
        body = createBody(world, x, y);
        PointLight pointLight = new PointLight(WorldMapFactory.rayHandler, 15, null, .5f, body.getWorldCenter().x, body.getWorldCenter().y);
        pointLight.attachToBody(body);
        pointLight.setSoft(false);

        ligth = new ConeLight(WorldMapFactory.rayHandler,15, Color.WHITE, 15.5f, body.getWorldCenter().x, body.getWorldCenter().y, (float) toRadians(90), 25f);
        ligth.attachToBody(ligthBody);
        ligth.setSoftnessLength(.5f);
        createFriction(world);

        world.setContactFilter(new ContactFilter() {
            @Override
            public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
                Object userData = fixtureA.getUserData();
                Object userData1 = fixtureB.getUserData();
                if(userData == Player.this || userData1 == Player.this){
                    return !"BULLET".equals(userData) && !"BULLET".equals(userData1);
                }
                return userData != userData1;
            }
        });

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object userData = contact.getFixtureA().getUserData();
                Object userData1 = contact.getFixtureB().getUserData();
                if("BULLET".equals(userData) || "BULLET".equals(userData1)){
                    if(userData == null || userData1 == null){//Wall
                        if("BULLET".equals(userData)) {
                            toDelete.add(contact.getFixtureA().getBody());
                        }
                        if("BULLET".equals(userData1)) {
                            toDelete.add(contact.getFixtureB().getBody());
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private Body createBody(World world, float x, float y) {
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set( x, y);


        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.15f, 0.15f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        Fixture fixture = body.createFixture(fdef);
        fixture.setUserData(this);

        ligthBody = world.createBody(def);

        return body;
    }

    private void createFriction(World world) {

        FrictionJointDef frictionJointDef = new FrictionJointDef();

        frictionJointDef.localAnchorA.set(0,0);
        frictionJointDef.localAnchorB.set(0,0);

        frictionJointDef.bodyA = body;
        frictionJointDef.bodyB = WorldMapFactory.getGroundBody();

        frictionJointDef.maxForce = 2.5f; //This the most force the joint will apply to your object. The faster its moving the more force applied
        frictionJointDef.maxTorque = 5; //Set to 0 to prevent rotation

        world.createJoint(frictionJointDef);
    }

    public void draw(SpriteBatch batch){
        for (int i = 0; i < toDelete.size(); i++) {
            try {
                toDelete.get(i).getFixtureList().get(0).setUserData(null);
                world.destroyBody(toDelete.get(i));
            } catch (Exception e) {/* Already destroyed? */}

        }
        toDelete.clear();
        Sprite currentSprite;
        if (body.getLinearVelocity().len() == 0)
            frame = 0;
        switch (currentDirection){
            case Direction.UP:
                currentSprite = Direction.up[frame];
                break;
            case Direction.LEFT:
                currentSprite = Direction.left[frame];
                break;
            case Direction.RIGTH:
                currentSprite = Direction.right[frame];
                break;
            default:
                currentSprite = Direction.down[frame];
        }
        currentSprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Vector2 worldCenter = body.getWorldCenter();
        currentSprite.setPosition((worldCenter.x * Constants.METER2PIXEL) - currentSprite.getWidth()/2,
                worldCenter.y * Constants.METER2PIXEL - currentSprite.getHeight()/4);
        currentSprite.draw(batch);
    }

    public float getX(){
        return body.getWorldCenter().x;
    }

    public float getY(){
        return body.getWorldCenter().y;
    }


    public float x = 0;
    public void act(float mx, float my){
        delta += Gdx.graphics.getDeltaTime();
        if(delta> .05){
            frame = (frame + 1)%7;
            delta = 0;
        }
        velocity.set(0,0);
        Vector2 center = body.getWorldCenter();
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.set(-0.08f, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.set(0.08f, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.set(0, 0.08f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            velocity.set(0, -0.08f);
        }
        float deltaX = mx - center.x;
        float deltaY = my - center.y;
        double angle = atan2(deltaY, deltaX);

        ligthBody.setTransform(center.x, center.y, (float) angle);

        if(angle <= 2.35 && angle >= 0.78){
            currentDirection = Direction.UP;
        }
        else if(angle < 0.78 && angle >= -0.90)
        {
            currentDirection = Direction.RIGTH;
        }
        else if(angle < -0.90 && angle >= -2.43){
            currentDirection = Direction.DOWN;
        }
        else{
            currentDirection = Direction.LEFT;
        }

        x += Gdx.graphics.getDeltaTime();

        if(Gdx.input.isButtonPressed(0) && x > .05){
            x = 0;
            shooTo(deltaX, deltaY);
        }

        body.applyLinearImpulse(velocity, center, true);
        Vector2 v = body.getLinearVelocity();
    }

    private void shooTo(float x, float y) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.bullet = true;
        def.position.set(body.getWorldCenter().x, body.getWorldCenter().y);
        def.linearVelocity.set(x,y).nor().scl(30);
        Body bullet = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.05f, 0.05f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Fixture fixture = bullet.createFixture(fixtureDef);
        fixture.setUserData("BULLET");

        FrictionJointDef frictionJointDef = new FrictionJointDef();

        frictionJointDef.localAnchorA.set(0,0);
        frictionJointDef.localAnchorB.set(0,0);

        frictionJointDef.bodyA = bullet;
        frictionJointDef.bodyB = WorldMapFactory.getGroundBody();

        frictionJointDef.maxForce = 0f; //This the most force the joint will apply to your object. The faster its moving the more force applied
        frictionJointDef.maxTorque = 0; //Set to 0 to prevent rotation

        world.createJoint(frictionJointDef);
    }
}
