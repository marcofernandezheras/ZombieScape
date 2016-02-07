package com.marco.zombiescape;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mapgenerator.DungeonBuilder;
import mapgenerator.MapStage;
import mapgenerator.StageBuilderConfig;

public class ZombieScape extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	int x = 0 , y = 0;
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();

		DungeonBuilder dungeonBuilder = new DungeonBuilder(new StageBuilderConfig());
		MapStage stage = new MapStage(51, 51);
		dungeonBuilder.generate(stage);

		stage.print();
		img = MapTextureFactory.getTextureFor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false);
        camera.zoom = 2f;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) x-=10;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) x+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.W)) y+=10;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) y-=10;
        camera.translate(x,y);
        camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img,0,0,img.getWidth(), img.getHeight());
		batch.end();
	}
}
