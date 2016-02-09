package com.marco.zombiescape;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import mapgenerator.MapStage;

/**
 * Created by marco on 7/02/16.
 */
public class WorldMapFactory {

    private static BodyDef bDef;
    private static FixtureDef fDef;
    private static PolygonShape shape;
    public static RayHandler rayHandler;

    private static void addWallTo(World world, MapStage.Wall wall){
        bDef.position.set(0.25f + wall.x * 0.5f, 0.25f + wall.y * 0.5f);
        Body body = world.createBody(bDef);
        body.createFixture(fDef);
    }

    public static World getWorldFor(MapStage stage){

        bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;

        shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f);

        fDef = new FixtureDef();
        fDef.shape = shape;

        World world = new World(new Vector2(), true);

        stage.getWalls().forEach(w -> addWallTo(world, w));

        bDef = null;
        fDef = null;
        shape.dispose();

        RayHandler.useDiffuseLight(true);
        RayHandler.setGammaCorrection(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 0.05f);
        rayHandler.setBlurNum(3);
        rayHandler.setShadows(true);

        return world;
    }
}
