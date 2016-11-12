package com.marco.zombiescape;

import box2dLight.PointLight;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.marco.zombiescape.weapons.AmmunitionPool;
import mapgenerator.DungeonBuilder;
import mapgenerator.MapStage;
import mapgenerator.StageBuilderConfig;
import mapgenerator.Tile;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by marco on 25/02/16.
 */
public class Level {

    private static final int MAX_SIZE = 63; //TODO Tested for framebuffer max size, split background on multiple textures
    private CODE returnCode;

    public void setLevelEnd() {
        returnCode = CODE.NEXT_LEVEL;
    }

    public enum CODE{
        CONTINUE, NEXT_LEVEL, GAME_OVER, PAUSE
    }

    private Texture background;
    private World world;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    private Rectangle initRoom;
    private int levelNumber;
    private LevelEnd levelEndObject;
    private AmmunitionPool ammunitionPool;

    public Player getPlayer() {
        return player;
    }

    private Player player = null;

    public MapStage getStage() {
        return stage;
    }

    private MapStage stage;

    public Level(Player player) {
        this.player = player;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        debugRenderer = new Box2DDebugRenderer();
        ammunitionPool = AmmunitionPool.instance;
    }

    public Level() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        debugRenderer = new Box2DDebugRenderer();
        ammunitionPool = AmmunitionPool.instance;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public Level init(int level){
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        this.levelNumber = level <= 15 ? level : 15;
        DungeonBuilder dungeonBuilder = new DungeonBuilder(new StageBuilderConfig((level + 2) * 2 , levelNumber * 100 / 12));
        stage = new MapStage((2 * levelNumber) + (rnd.nextInt(10, 16) * 2) + 1, (2 * levelNumber)  + (rnd.nextInt(10, 16) * 2) + 1);
        dungeonBuilder.generate(stage);
        background = MapTextureFactory.getTextureFor(stage);
        world = WorldMapFactory.getWorldFor(stage);
        stage.print();
        initRoom = stage.getRandomRoom();
        initPlayer(player);
        initZombies();

        Rectangle opositeRoom = stage.getOpositeRoom(initRoom);
        Vector2 v2 = new Vector2();
        opositeRoom.getCenter(v2);
        levelEndObject = new LevelEnd(this, v2.x, v2.y);
        return this;
    }

    private void initPlayer(Player oldPlayer){
        Vector2 v2 = new Vector2();
        initRoom.getCenter(v2);
        if (oldPlayer != null){
            player = new Player(world, v2.x , v2.y, oldPlayer);
        }
        else {
            player = new Player(world, v2.x , v2.y);
        }
        new PointLight(WorldMapFactory.rayHandler, 15, Color.RED, 5, player.getX(), player.getY()).setStaticLight(true);
    }

    private void initZombies(){
        ThreadLocalRandom rdn = ThreadLocalRandom.current();
        for (Rectangle r : stage.getRooms()) {
            int maxZombies = (int) Math.ceil(r.getWidth() * r.getHeight() / 4.0);
            if(r != initRoom) {
                for (int i = 0; i < maxZombies; i++) {
                    Zombie.newZombie(world, (float)rdn.nextDouble(r.getX()+0.5f, r.getX() - 0.5f + r.getWidth()), (float)rdn.nextDouble(r.getY() + 0.5f, r.getY() - 0.5f + r.getHeight()));
                }
            }
        }

        int maxZ = levelNumber * 5;
        List<Vector2> floors = stage.getFloors();
        for (int i = 0; i < maxZ; i++){
            Vector2 pos = floors.get(rdn.nextInt(0, floors.size()));
            Zombie.newZombie(world, (pos.x+0.5f)*0.5f, (pos.y+0.5f)*0.5f);
        }
    }

    public CODE render(){
        if(returnCode == CODE.NEXT_LEVEL)
            return returnCode;
        
        returnCode = CODE.CONTINUE;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(false);


        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) returnCode = CODE.NEXT_LEVEL;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) returnCode = CODE.PAUSE;
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

        Zombie.zombiePool.stream().filter(Zombie::isMarketToDelete).forEach(z -> {
            int i = ThreadLocalRandom.current().nextInt(0, 100);
            if(i < (levelNumber + (80 * (1 - levelNumber/15)))){
                AmmunitionPool.instance.newAmmunition(world, z.getX(), z.getY());
            }
            z.dispose();
        });
        Zombie.zombiePool.removeIf(Zombie::isMarketToDelete);

        Bullet.bulletPool.stream().filter(Bullet::isMarketToDelete).forEach(Bullet::dispose);
        Bullet.bulletPool.removeIf(Bullet::isMarketToDelete);

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        ammunitionPool.act();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,(int)((player.getX() * Constants.METER2PIXEL) - (camera.viewportWidth)),
                (int)(player.getY() * Constants.METER2PIXEL - (camera.viewportHeight)),
                (int)((player.getX() * Constants.METER2PIXEL) - camera.viewportWidth),
                (int)(background.getHeight() - (player.getY() * Constants.METER2PIXEL + camera.viewportHeight)),
                (int)camera.viewportWidth*2, (int)camera.viewportHeight*2);
        levelEndObject.draw(batch);
        Zombie.bloodPool.forEach(b -> b.draw(batch));
        ammunitionPool.draw(batch);
        player.draw(batch);

        Zombie.zombiePool.forEach(zombie -> zombie.draw(batch));
        Bullet.bulletPool.forEach(b -> b.draw(batch));


        batch.end();

        Matrix4 combined = new Matrix4(camera.combined);
        combined.scale(Constants.METER2PIXEL, Constants.METER2PIXEL, 1);

        if (Constants.LIGTHS){
            WorldMapFactory.rayHandler.setCombinedMatrix(combined);
            WorldMapFactory.rayHandler.updateAndRender();
        }

        if(player.getLife() <= 0)
            returnCode = CODE.GAME_OVER;

        if(Constants.DEBUG) debugRenderer.render(world, combined);
        return returnCode;
    }

    public void dispose(){
        background.dispose();
        batch.dispose();
        debugRenderer.dispose();
        ammunitionPool.clear();
        Zombie.zombiePool.clear();
        Zombie.bloodPool.clear();
        Bullet.bulletPool.clear();
        world.dispose();
    }
}
