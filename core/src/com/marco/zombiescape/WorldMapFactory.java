package com.marco.zombiescape;

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

    private static void addWallTo(World world, int x, int y){
        bDef.position.set(0.25f + x * 0.5f, -0.25f -y * 0.5f);
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

        stage.getWalls().forEach(w -> addWallTo(world, w[0], w[1]));

        bDef = null;
        fDef = null;
        shape.dispose();
        return world;
    }
}
