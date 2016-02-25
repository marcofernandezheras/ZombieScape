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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

/**
 * Created by marco on 11/02/16.
 */
public class Zombie implements Hittable, Deletable, Disposable {

    private static final class Direction {
        private Direction(){}
        static final int UP = 0, RIGTH = 1, DOWN = 2, LEFT = 3;
        static final Sprite[][][] sprites = new Sprite[8][4][3];

        static {
            String [] aux = { "up_%d.png", "right_%d.png",  "down_%d.png", "left_%d.png"};

            for (int i = 0; i < Direction.sprites.length; i++) {
                for (int j = 0; j < Direction.sprites[i].length; j++) {
                    for (int k = 0; k < Direction.sprites[i][j].length; k++) {
                        Direction.sprites[i][j][k] = new Sprite(new Texture(String.format("zombie%d/%s", i, String.format(aux[j], k))));
                        Direction.sprites[i][j][k].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    }
                }
            }
        }
    }

    private AStarPathFinder finder = null;
    protected static final List<Zombie> zombiePool = new ArrayList<>();

    private int currentDirection = Direction.DOWN;
    private final Body body;
    private final int spriteNumber;
    private float delta;
    private int frame = 0;
    boolean aggro = false;
    private Player player;
    private int life;
    private ArrayList<HittableListener> hittableListeners;
    private boolean deleteMe = false;

    private Zombie(World world, float x, float y) {
        finder = new AStarPathFinder(WorldMapFactory.mapStage, 100, false);
        spriteNumber = ThreadLocalRandom.current().nextInt(0, Direction.sprites.length);
        hittableListeners = new ArrayList<>();

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

    public static Zombie newZombie(World world, float x, float y){
        Zombie zombie = new Zombie(world, x, y);
        zombiePool.add(zombie);
        return zombie;
    }

    public void setAggro(Player player){
        this.player = player;
        aggro = true;
    }

    public void act(){
        delta += Gdx.graphics.getDeltaTime();
        if(delta> .07){
            frame = (frame + 1)%3;
            delta = 0;
        }
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

        if(body.getLinearVelocity().len() > 0) {
            updateDirection();
        }
    }

    private void updateDirection() {
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

    public void draw(final SpriteBatch batch){
        Vector2 worldCenter = body.getWorldCenter();
        Sprite currentSprite;
        if (Float.compare(body.getLinearVelocity().len(), 0) == 0)
            frame = 0;

        currentSprite = Direction.sprites[spriteNumber][currentDirection][frame];

        currentSprite.setPosition((worldCenter.x * Constants.METER2PIXEL) - currentSprite.getWidth() / 2,
                worldCenter.y * Constants.METER2PIXEL - currentSprite.getHeight() / 2);
        currentSprite.draw(batch);
    }

    public void setCalm() {
        aggro = false;
    }

    public void changeDirection(){
        if(!aggro) body.setLinearVelocity(body.getLinearVelocity().scl(-1));
    }

    public static void disposeZombies(){
        for (int i = 0; i < Direction.sprites.length; i++) {
            for (int j = 0; j < Direction.sprites[i].length; j++) {
                for (int k = 0; k < Direction.sprites[i][j].length; k++) {
                    Direction.sprites[i][j][k].getTexture().dispose();
                }
            }
        }
    }

    //DELETABLE
    @Override
    public void markToDelete() {
        deleteMe = true;
    }

    @Override
    public boolean isMarketToDelete() {
        return deleteMe;
    }

    //HITTABLE
    @Override
    public void beginHit() {
        life--;
        System.out.println(life);
        hittableListeners.forEach(l -> l.hitted(Zombie.this));
    }

    @Override
    public void endHit() {
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public void addHitListener(HittableListener hittableListener) {
        hittableListeners.add(hittableListener);
    }

    //DISPOSABLE
    @Override
    public void dispose() {
        try {
            System.out.println("Zombi dispose");
            body.getFixtureList().get(0).setUserData(null);
            WorldMapFactory.world.destroyBody(body);
        } catch(Exception e){}
    }
}
