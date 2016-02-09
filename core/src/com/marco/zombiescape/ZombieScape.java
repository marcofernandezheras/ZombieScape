package com.marco.zombiescape;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import mapgenerator.DungeonBuilder;
import mapgenerator.MapStage;
import mapgenerator.StageBuilderConfig;

import java.util.concurrent.ThreadLocalRandom;

public class ZombieScape extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	World world;

	int x = -128 , y = -128;
    private Body body;
    boolean debug = true;

    @Override
	public void create () {
		Box2D.init();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		debugRenderer = new Box2DDebugRenderer();
		DungeonBuilder dungeonBuilder = new DungeonBuilder(new StageBuilderConfig());
		MapStage stage = new MapStage(31, 11);
		dungeonBuilder.generate(stage);

		stage.print();

		img = MapTextureFactory.getTextureFor(stage);
		world = WorldMapFactory.getWorldFor(stage);

        Vector2 v2 = new Vector2();
        stage.getRandomRoom().getCenter(v2);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set( v2.x, v2.y);
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.15f, 0.15f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        body.createFixture(fdef);
    }

    Vector2 v = new Vector2();
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false);
        v.set(0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.A)) x-=10;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) x+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.W)) y+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) y-=10;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) v.set(-0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) v.set(0.1f, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) v.set(0, 0.1f);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) v.set(0, -0.1f);
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) debug = !debug;


        body.applyLinearImpulse(v,body.getWorldCenter(), true);
        world.step(Gdx.graphics.getDeltaTime(), 6,2);

        camera.zoom = 1;
        camera.translate(x,y);


        camera.update();

        if(Gdx.input.isButtonPressed(0)){
            Vector3 unproject = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            float mx = unproject.x * Constants.PIXEL2METER;
            float my = unproject.y * Constants.PIXEL2METER;
            new ConeLight(WorldMapFactory.rayHandler,15,null,5,mx,my,0,25).setSoftnessLength(0.01f);
            //new PointLight(WorldMapFactory.rayHandler, 50, Color.BLUE, 1, mx, my).setSoft(false);
        }

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img,0,0);
		batch.end();


		camera.update();
		Matrix4 combined = new Matrix4(camera.combined);
		combined.scale(Constants.METER2PIXEL, Constants.METER2PIXEL, 1);

        WorldMapFactory.rayHandler.setCombinedMatrix(combined);
        //WorldMapFactory.rayHandler.updateAndRender();

        if(debug)
		debugRenderer.render(world, combined);
	}
}
