package com.marco.zombiescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.Disposable;
import pathfind.AStarPathFinder;
import pathfind.Path;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

/**
 * Created by marco on 11/02/16.
 */
public class Zombie implements Hitable, Deleteable, Disposable {

    @Override
    public void dispose() {
        try {
            System.out.println("Zombi dispose");
            body.getFixtureList().get(0).setUserData(null);
            WorldMapFactory.world.destroyBody(body);
        } catch(Exception e){

        }
    }

    public void setCalm() {
        aggro = false;
    }

    @Override
    public void markToDelete() {
        deleteMe = true;
    }

    @Override
    public boolean isMarketToDelete() {
        return deleteMe;
    }

    private static final class Direction {
        static final int UP = 0, RIGTH = 1, DOWN = 2, LEFT = 3;
        static final Sprite[] up =    {
                new Sprite(new Texture("zombie/up_0.png")),
                new Sprite(new Texture("zombie/up_1.png")),
                new Sprite(new Texture("zombie/up_2.png"))
        };
        static final Sprite[] down =  {
                new Sprite(new Texture("zombie/down_0.png")),
                new Sprite(new Texture("zombie/down_1.png")),
                new Sprite(new Texture("zombie/down_2.png"))
        };
        static final Sprite[] left =  {
                new Sprite(new Texture("zombie/left_0.png")),
                new Sprite(new Texture("zombie/left_1.png")),
                new Sprite(new Texture("zombie/left_2.png"))
        };
        static final Sprite[] right = {
                new Sprite(new Texture("zombie/right_0.png")),
                new Sprite(new Texture("zombie/right_1.png")),
                new Sprite(new Texture("zombie/right_2.png"))
        };
    }

    public static void disposeZombies(){
        for (Sprite sprite : Direction.up) {
            sprite.getTexture().dispose();
        }
        for (Sprite sprite : Direction.down) {
            sprite.getTexture().dispose();
        }
        for (Sprite sprite : Direction.left) {
            sprite.getTexture().dispose();
        }
        for (Sprite sprite : Direction.right) {
            sprite.getTexture().dispose();
        }
    }

    private static AStarPathFinder finder = null;

    private int currentDirection = Direction.DOWN;
    private final Body body;
    private float delta , rand = 5;
    private int frame = 0;
    boolean aggro = false;
    private Player player;
    private int life;
    private ArrayList<HitableListener> hitableListeners;
    private boolean deleteMe = false;

    public Zombie(World world, float x, float y) {
        if(finder == null){
            finder = new AStarPathFinder(WorldMapFactory.mapStage, 100, false);
        }
        hitableListeners = new ArrayList<>();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set( x, y);
        def.fixedRotation = true;
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        Fixture fixture = body.createFixture(fdef);
        fixture.setUserData(this);

        FrictionJointDef frictionJointDef = new FrictionJointDef();

        frictionJointDef.localAnchorA.set(0,0);
        frictionJointDef.localAnchorB.set(0,0);

        frictionJointDef.bodyA = body;
        frictionJointDef.bodyB = WorldMapFactory.getGroundBody();

        frictionJointDef.maxForce = 0f; //This the most force the joint will apply to your object. The faster its moving the more force applied
        frictionJointDef.maxTorque = 0; //Set to 0 to prevent rotation

        world.createJoint(frictionJointDef);

        life = ThreadLocalRandom.current().nextInt(1,30);
    }

    public void setAggro(Player player){
        this.player = player;
        aggro = true;
    }

    public void act(){
        delta += Gdx.graphics.getDeltaTime();
        if(aggro){
            Vector2 center = body.getWorldCenter();
            Path path = finder.findPath((int) (center.x * 2), (int) (center.y * 2), (int) (player.getX() * 2), (int) (player.getY() * 2));
            if(path != null && path.getLength() > 1){
                Path.Step step = path.getStep(1);
                body.setLinearVelocity(step.getX()+0.5f - center.x*2, step.getY()+0.5f - center.y*2);
                body.setLinearVelocity(body.getLinearVelocity().nor().scl(0.7f));
            }
            else{
                body.setLinearVelocity(0,0);
            }
        }
        else{
            body.setLinearVelocity(0,0);
        }

        if(abs(body.getLinearVelocity().y) > 0 ||  abs(body.getLinearVelocity().x) > 0) {
            double angle = atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);

            if (angle <= 2.35 && angle >= 0.78) {
                currentDirection = Direction.UP;
            } else if (angle < 0.78 && angle >= -0.90) {
                currentDirection = Direction.RIGTH;
            } else if (angle < -0.90 && angle >= -2.43) {
                currentDirection = Direction.DOWN;
            } else {
                currentDirection = Direction.LEFT;
            }
        }
    }

    public void draw(final SpriteBatch batch){
        Vector2 worldCenter = body.getWorldCenter();
        Sprite currentSprite;
        if (Float.compare(body.getLinearVelocity().len(), 0) == 0)
            frame = 0;
        switch (currentDirection) {
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

        currentSprite.setPosition((worldCenter.x * Constants.METER2PIXEL) - currentSprite.getWidth() / 2,
                worldCenter.y * Constants.METER2PIXEL - currentSprite.getHeight() / 2);
        currentSprite.draw(batch);
    }

    public void changeDirection(){
        if(!aggro) body.setLinearVelocity(body.getLinearVelocity().scl(-1));
    }

    @Override
    public void beginHit() {
        life--;
        System.out.println(life);
        hitableListeners.forEach(l -> l.hitted(Zombie.this));
    }

    @Override
    public void endHit() {
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public void addHitListener(HitableListener hitableListener) {
        hitableListeners.add(hitableListener);
    }
}
