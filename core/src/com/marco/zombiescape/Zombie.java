package com.marco.zombiescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.marco.zombiescape.weapons.AmmunitionPool;
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

    private final Sound sound;

    private static final class Direction {
        private Direction(){}
        static final int UP = 0, RIGTH = 1, DOWN = 2, LEFT = 3;
        static final Sprite[][][] sprites = new Sprite[8][4][3];

        static {
            String [] aux = { "z%dup", "z%dright",  "z%ddown", "z%dleft"};
            Resources resources = Resources.instance;
            for (int i = 0; i < Direction.sprites.length; i++) {
                for (int j = 0; j < Direction.sprites[i].length; j++) {
                    for (int k = 0; k < Direction.sprites[i][j].length; k++) {
                        Direction.sprites[i][j][k] = new Sprite(resources.getRegion(String.format(aux[j],i), k));//new Texture(String.format("zombie%d/%s", i, String.format(aux[j], k)))
                    }
                }
            }
        }
    }

    private AStarPathFinder finder = null;
    protected static final List<Zombie> zombiePool = new ArrayList<>();
    protected static final List<Zombie> bloodPool = new ArrayList<>();

    private int currentDirection = Direction.DOWN;
    private final Body body;
    private final int spriteNumber;
    private float delta;
    private float sounddelta;
    private int frame = 0;
    boolean aggro = false;
    private Player player;
    private float life;
    private ArrayList<HittableListener> hittableListeners;
    private boolean deleteMe = false;
    private Sprite bloodSprite;
    float bloodX;
    float bloodY;

    public float getDamage() {
        return damage;
    }

    private final float damage;

    private Zombie(World world, float x, float y) {
        damage = (float) ThreadLocalRandom.current().nextDouble(1, 5);
        finder = new AStarPathFinder(WorldMapFactory.mapStage, 20, false);
        bloodSprite = new Sprite(Resources.instance.getRegion("blood", ThreadLocalRandom.current().nextInt(0,7)));
        spriteNumber = ThreadLocalRandom.current().nextInt(0, Direction.sprites.length);
        sound = Gdx.audio.newSound(Gdx.files.internal(String.format("zombieSound_%d.wav", ThreadLocalRandom.current().nextInt(0,3))));
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
        sound.play(0.3f);
    }

    public void act(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        delta += deltaTime;
        if(delta> .07){
            frame = (frame + 1)%3;
            delta = 0;
        }
        if(aggro){
            sounddelta += deltaTime;
            if(sounddelta > 5){
                sound.play(0.5f);
                sounddelta = 0;
            }
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
        Sprite currentSprite;
        if(!isMarketToDelete()) {
            Vector2 worldCenter = body.getWorldCenter();
            if (Float.compare(body.getLinearVelocity().len(), 0) == 0)
                frame = 0;

            currentSprite = Direction.sprites[spriteNumber][currentDirection][frame];

            currentSprite.setPosition((worldCenter.x * Constants.METER2PIXEL) - currentSprite.getWidth() / 2,
                    worldCenter.y * Constants.METER2PIXEL - currentSprite.getHeight() / 2);
        }
        else{
            currentSprite = bloodSprite;
            currentSprite.setPosition(bloodX - currentSprite.getWidth() / 2.0f, bloodY - currentSprite.getHeight() / 2.0f);
        }
        currentSprite.draw(batch);
    }

    public void setCalm() {
        aggro = false;
    }

    public void changeDirection(){
        if(!aggro) body.setLinearVelocity(body.getLinearVelocity().scl(-1));
    }

    //DELETABLE
    @Override
    public void markToDelete() {
        Vector2 worldCenter = body.getWorldCenter();
        bloodX = worldCenter.x * Constants.METER2PIXEL;
        bloodY = worldCenter.y * Constants.METER2PIXEL;
        deleteMe = true;
    }

    @Override
    public boolean isMarketToDelete() {
        return deleteMe;
    }

    //HITTABLE
    @Override
    public void beginHit(float damage) {
        life -= damage;
        System.out.println("Zombie life: " + life);
        hittableListeners.forEach(l -> l.hitted(Zombie.this));
        if(life <= 0){
            markToDelete();
        }
    }

    @Override
    public void endHit() {
    }

    @Override
    public float getLife() {
        return life;
    }

    @Override
    public float getMaxLife() {
        return 0;
    }

    @Override
    public void addHitListener(HittableListener hittableListener) {
        hittableListeners.add(hittableListener);
    }

    //DISPOSABLE
    @Override
    public void dispose() {
        Vector2 worldCenter = body.getWorldCenter();
        try {

            System.out.println("Zombi dispose");
            body.getFixtureList().get(0).setUserData(null);
            WorldMapFactory.world.destroyBody(body);
            bloodPool.add(this);
        } catch(Exception e){}
    }

    public float getX(){
        return body.getWorldCenter().x;
    }

    public float getY(){
        return body.getWorldCenter().y;
    }
}
