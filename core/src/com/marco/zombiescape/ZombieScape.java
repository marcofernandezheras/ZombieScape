package com.marco.zombiescape;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;

public class ZombieScape extends ApplicationAdapter{
    Level currentLevel;
    SpriteBatch hudBatch;
    OrthographicCamera camera;
    LifeHud lifeHud;

    @Override
	public void create () {
		Box2D.init();
		currentLevel = new Level().init(1);
        hudBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        lifeHud = new LifeHud(currentLevel.getPlayer());
    }

	@Override
	public void render () {
        camera.setToOrtho(false);
        Level.CODE code = currentLevel.render();
        if(code == Level.CODE.NEXT_LEVEL) {
            Player player = currentLevel.getPlayer();
            int levelNumber = currentLevel.getLevelNumber();
            currentLevel.dispose();
            currentLevel = new Level(player).init(levelNumber + 1);
            lifeHud.setHittable(currentLevel.getPlayer());
        }
        else if(code == Level.CODE.GAME_OVER){
            //TODO code == Level.CODE.GAME_OVER
        }
        else {
            hudBatch.setProjectionMatrix(camera.combined);
            hudBatch.begin();
            lifeHud.draw(hudBatch);
            hudBatch.end();
        }
	}

    @Override
    public void dispose() {
        super.dispose();
        Player.dispose();
        currentLevel.dispose();
        Zombie.disposeZombies();
        WorldMapFactory.rayHandler.dispose();
    }
}
