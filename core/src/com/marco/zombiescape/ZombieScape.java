package com.marco.zombiescape;

import box2dLight.PointLight;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import mapgenerator.DungeonBuilder;
import mapgenerator.MapStage;
import mapgenerator.StageBuilderConfig;

import java.util.concurrent.ThreadLocalRandom;

public class ZombieScape extends ApplicationAdapter{
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	World world;
    Player player;

    private ParticleEffect pe;

    @Override
	public void create () {
		Box2D.init();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		debugRenderer = new Box2DDebugRenderer();
		DungeonBuilder dungeonBuilder = new DungeonBuilder(new StageBuilderConfig());
		MapStage stage = new MapStage(51, 21);
		dungeonBuilder.generate(stage);

		stage.print();

		img = MapTextureFactory.getTextureFor(stage);
		world = WorldMapFactory.getWorldFor(stage);

        Vector2 v2 = new Vector2();
        Rectangle randomRoom = stage.getRandomRoom();
        randomRoom.getCenter(v2);

        player = new Player(world, v2.x , v2.y);

        /*
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("test.particles"),Gdx.files.internal(""));
        pe.setPosition(player.getX() * Constants.METER2PIXEL,player.getY() * Constants.METER2PIXEL);
        pe.start();
        */

        new PointLight(WorldMapFactory.rayHandler, 15, null, 5, player.getX(), player.getY());

        stage.getRooms().forEach(r -> {
            ThreadLocalRandom rdn = ThreadLocalRandom.current();
            int maxZombies = (int) Math.ceil(r.getWidth() * r.getHeight() / 4.0);
            if(r != randomRoom) {
                for (int i = 0; i < maxZombies; i++) {
                    Zombie.newZombie(world, (float)rdn.nextDouble(r.getX()+0.5f, r.getX() - 0.5f + r.getWidth()), (float)rdn.nextDouble(r.getY() + 0.5f, r.getY() - 0.5f + r.getHeight()));
                }
            }
        });
    }

    public void rotateBy(ParticleEffect particleEffect, float amountInDegrees) {
        Array<ParticleEmitter> emitters = particleEffect.getEmitters();
        for (int i = 0; i < emitters.size; i++) {
            ParticleEmitter.ScaledNumericValue val = emitters.get(i).getAngle();
            float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
            float h1 = amountInDegrees + amplitude;
            float h2 = amountInDegrees - amplitude;
            val.setHigh(h1, h2);
            val.setLow(amountInDegrees);
        }
    }


    Vector2 v = new Vector2();
	@Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(false);

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) Constants.DEBUG = !Constants.DEBUG;
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) Constants.LIGTHS = !Constants.LIGTHS;

        camera.zoom = 2f;
        camera.translate((player.getX() * Constants.METER2PIXEL) - camera.viewportWidth ,
                player.getY() * Constants.METER2PIXEL - camera.viewportHeight);

        camera.update();

        Vector3 unproject = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = unproject.x * Constants.PIXEL2METER;
        float my = unproject.y * Constants.PIXEL2METER;


        player.act(mx, my);
        Zombie.zombiePool.forEach(Zombie::act);

        Zombie.zombiePool.stream().filter(Zombie::isMarketToDelete).forEach(Zombie::dispose);
        Zombie.zombiePool.removeIf(Zombie::isMarketToDelete);

        Bullet.bulletPool.stream().filter(Bullet::isMarketToDelete).forEach(Bullet::dispose);
        Bullet.bulletPool.removeIf(Bullet::isMarketToDelete);

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        /*pe.update(Gdx.graphics.getDeltaTime());

        if (pe.isComplete())
            pe.reset();
        */
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(img,(int)((player.getX() * Constants.METER2PIXEL) - (camera.viewportWidth)),
                       (int)(player.getY() * Constants.METER2PIXEL - (camera.viewportHeight)),
                       (int)((player.getX() * Constants.METER2PIXEL) - camera.viewportWidth),
                       (int)(img.getHeight() - (player.getY() * Constants.METER2PIXEL + camera.viewportHeight)),
                       (int)camera.viewportWidth*2, (int)camera.viewportHeight*2);
        player.draw(batch);
        //pe.draw(batch);
        Zombie.zombiePool.forEach(zombie -> zombie.draw(batch));
        Bullet.bulletPool.forEach(b -> b.draw(batch));
        batch.end();

        Matrix4 combined = new Matrix4(camera.combined);
        combined.scale(Constants.METER2PIXEL, Constants.METER2PIXEL, 1);

        if (Constants.LIGTHS){
            WorldMapFactory.rayHandler.setCombinedMatrix(combined);
            WorldMapFactory.rayHandler.updateAndRender();
        }



        if(Constants.DEBUG) debugRenderer.render(world, combined);
	}

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        Player.dispose();
        Zombie.disposeZombies();
        WorldMapFactory.rayHandler.dispose();
        world.dispose();
    }
}
