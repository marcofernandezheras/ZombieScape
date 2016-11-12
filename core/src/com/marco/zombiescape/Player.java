package com.marco.zombiescape;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.marco.zombiescape.weapons.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by marco on 10/02/16.
 */
public class Player implements Hittable {

    private float currentDamage;

    private static final class Direction {
        static final int UP = 0;
        static final int RIGHT = 1;
        static final int DOWN = 2;
        static final int LEFT = 3;
        static final Sprite[][] sprites = new Sprite[4][7];

        private Direction(){}

        static {
            Resources resources = Resources.instance;
            String[] aux = {"playerup", "playerright", "playerdown", "playerleft"};
            for (int i = 0; i < sprites.length; i++) {
                for (int j = 0; j < sprites[i].length; j++) {
                    sprites[i][j] = new Sprite(resources.getRegion(aux[i], j));//new Texture(String.format(aux[i],j))
                }
            }
        }
    }

    private World world;
    private List<HittableListener> hittableListeners = new ArrayList<>();
    private boolean inHit = false;
    private float life = 100;
    private int currentDirection = Direction.DOWN;
    private final Body body;
    private Body lightBody;
    private Vector2 velocity = new Vector2();
    private float delta;
    private int frame = 0;

    private float maxLife = 100;

    public List<Weapon> getWeapons() {
        return weapons;
    }

    private List<Weapon> weapons = new ArrayList<>();

    public Weapon getCurrentWeapon() {
        return weapons.get(currentWeapon);
    }

    int currentWeapon;

    public Player(World world, float x, float y) {
        this.world = world;
        body = createBody(world, x, y);

        weapons.add(new Knife(body));
        weapons.add(new Pistol(body));
        weapons.add(new MachineGun(body));
        weapons.add(new Shotgun(body));
        currentWeapon = 0;

        PointLight pointLight = new PointLight(WorldMapFactory.rayHandler, 15, null, .5f, body.getWorldCenter().x, body.getWorldCenter().y);
        pointLight.attachToBody(body);
        pointLight.setSoft(false);

        ConeLight light = new ConeLight(WorldMapFactory.rayHandler, 15, Color.WHITE, 8.5f, body.getWorldCenter().x, body.getWorldCenter().y, (float) toRadians(90), 25f);
        light.attachToBody(lightBody);
        light.setSoftnessLength(1.5f);
        createFriction(world);
    }

    public Player(World world, float x, float y, Player oldPlayer) {
        this(world,x,y);
        this.life = oldPlayer.life;
        this.weapons = oldPlayer.weapons;
        this.currentWeapon = oldPlayer.currentWeapon;
        weapons.forEach(w -> w.setBody(body));
    }
    private Body createBody(World world, float x, float y) {
        Body bodyAux;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set( x, y);

        bodyAux = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.15f, 0.15f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        Fixture fixture = bodyAux.createFixture(fdef);
        fixture.setUserData(this);

        lightBody = world.createBody(def);

        FixtureDef aggroSensorDef = new FixtureDef();

        CircleShape aggroShape = new CircleShape();
        aggroShape.setRadius(5);

        aggroSensorDef.shape = aggroShape;
        aggroSensorDef.isSensor = true;
        Fixture aggroFix = bodyAux.createFixture(aggroSensorDef);

        aggroFix.setUserData(this);

        shape.dispose();
        aggroShape.dispose();
        return bodyAux;
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

    public void draw(final SpriteBatch batch){
        Sprite currentSprite;
        if (body.getLinearVelocity().len() <= 0)
            frame = 0;

        currentSprite = Direction.sprites[currentDirection][frame];

        Vector2 worldCenter = body.getWorldCenter();
        currentSprite.setPosition((worldCenter.x * Constants.METER2PIXEL) - currentSprite.getWidth()/2,
                worldCenter.y * Constants.METER2PIXEL - currentSprite.getHeight()/4);

        if(currentDirection == Direction.UP){
            weapons.get(currentWeapon).draw(batch);
            currentSprite.draw(batch);
        }
        else{
            currentSprite.draw(batch);
            weapons.get(currentWeapon).draw(batch);
        }

    }

    public float getX(){
        return body.getWorldCenter().x;
    }

    public float getY(){
        return body.getWorldCenter().y;
    }


    public void act(float mx, float my){
        float deltaTime = Gdx.graphics.getDeltaTime();
        delta += deltaTime;

        Vector2 center = body.getWorldCenter();
        if(inHit){
            final boolean[] stillInHit = {false};
            world.QueryAABB(f-> {
                if(f.getUserData() instanceof Zombie){
                    stillInHit[0] = true;
                    return false;
                }
                return true;
            }, center.x - 0.18f, center.y - 0.18f, center.x + 0.18f, center.y + 0.18f);

            if(stillInHit[0]) {
                life -= currentDamage;
                hittableListeners.forEach(l -> l.hitted(Player.this));
            }
        }

        if(delta> .05){
            frame = (frame + 1)%7;
            delta = 0;
        }

        handleInput();

        float angle = Util.angle(mx, my, center.x, center.y);

        lightBody.setTransform(center.x, center.y,  angle);

        updateDirection(angle);

        if(Gdx.input.isButtonPressed(0)){
            weapons.get(currentWeapon).startShooting();
        }
        else{
            weapons.get(currentWeapon).stopShooting();
        }

        weapons.get(currentWeapon).act(deltaTime, angle);
    }

    private void updateDirection(double angle) {
        if(angle <= 2.35 && angle >= 0.78){
            currentDirection = Direction.UP;
        }
        else if(angle < 0.78 && angle >= -0.90)
        {
            currentDirection = Direction.RIGHT;
        }
        else if(angle < -0.90 && angle >= -2.43){
            currentDirection = Direction.DOWN;
        }
        else{
            currentDirection = Direction.LEFT;
        }
    }

    private void handleInput() {
        Vector2 center = body.getWorldCenter();
        velocity.set(0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.set(-0.075f, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.set(0.075f, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.set(0, 0.075f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            velocity.set(0, -0.075f);
        }
        body.applyLinearImpulse(velocity, center, true);

        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            currentWeapon--;
            currentWeapon = currentWeapon < 0 ? 0 : currentWeapon;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            weapons.get(currentWeapon).manualReload();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            currentWeapon++;
            currentWeapon = currentWeapon >= weapons.size() ? weapons.size()-1 : currentWeapon;
        }
    }


    //HITTABLE
    @Override
    public void beginHit(float damage) {
        currentDamage = damage;
        inHit = true;
    }

    @Override
    public void endHit() {
        inHit = false;
    }

    @Override
    public float getLife() {
        return life;
    }

    @Override
    public float getMaxLife() {
        return maxLife;
    }

    @Override
    public void addHitListener(HittableListener hittableListener) {
        hittableListeners.add(hittableListener);
    }
}
