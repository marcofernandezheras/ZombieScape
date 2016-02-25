package com.marco.zombiescape;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.physics.box2d.Box2D;

public class ZombieScape extends ApplicationAdapter{
    Level currentLevel;


    @Override
	public void create () {
		Box2D.init();
		currentLevel = new Level().init(1);
    }

	@Override
	public void render () {
        if(currentLevel.render()) {
            Player player = currentLevel.getPlayer();
            int levelNumber = currentLevel.getLevelNumber();
            currentLevel.dispose();
            currentLevel = new Level(player).init(levelNumber + 1);
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
