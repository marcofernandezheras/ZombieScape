package com.marco.zombiescape;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import mapgenerator.DungeonBuilder;
import mapgenerator.MapStage;
import mapgenerator.StageBuilderConfig;

public class ZombieScape extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	World world;

	int x = 0 , y = 0;
	@Override
	public void create () {
		Box2D.init();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		debugRenderer = new Box2DDebugRenderer();
		DungeonBuilder dungeonBuilder = new DungeonBuilder(new StageBuilderConfig());
		MapStage stage = new MapStage(21, 21);
		dungeonBuilder.generate(stage);

		stage.print();
		img = MapTextureFactory.getTextureFor(stage);
		world = WorldMapFactory.getWorldFor(stage);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false);
        if(Gdx.input.isKeyPressed(Input.Keys.A)) x-=10;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) x+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.W)) y+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) y-=10;


        world.step(Gdx.graphics.getDeltaTime(), 6,2);

        camera.zoom = 2;
        camera.translate(x,y);
        //camera.translate(player.getX() - camera.viewportWidth/2, player.getY() - camera.viewportHeight/2);
        camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img,0,-img.getHeight(),img.getWidth(), img.getHeight());
		batch.end();


		camera.update();
		Matrix4 combined = new Matrix4(camera.combined);
		combined.scale(Constants.METER2PIXEL, Constants.METER2PIXEL, 1);

		debugRenderer.render(world, combined);
	}
}
