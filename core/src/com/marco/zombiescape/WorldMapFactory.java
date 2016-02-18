package com.marco.zombiescape;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import mapgenerator.MapStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by marco on 7/02/16.
 */
public class WorldMapFactory {

    public static RayHandler rayHandler;
    public static World world;
    public static MapStage mapStage;

    public static Body getGroundBody() {
        return groundBody;
    }

    private static Body groundBody;

    public static World getWorldFor(MapStage stage){
        mapStage = stage;

        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f);

        FixtureDef fDef = new FixtureDef();

        world = new World(new Vector2(), true);

        List<MapStage.Wall> walls = stage.getWalls();
        List<MapStage.Wall> used = new ArrayList<>();
        List<MapStage.Wall> tested = new ArrayList<>();
        boolean exit = false;
        while (!exit) {
            List<MapStage.Wall> notUsed = walls.stream().filter(w -> !used.contains(w)).collect(Collectors.toList());
            Optional<MapStage.Wall> op_start = notUsed.stream()
                    .filter(w -> !tested.contains(w))
                    .filter(w -> w.wallsAround != 5)
                    .findFirst();

            if (op_start.isPresent()) {
                MapStage.Wall start = op_start.get();
                tested.add(start);

                List<MapStage.Wall> useNow = new ArrayList<>();
                useNow.add(start);

                List<MapStage.Wall> onRow = notUsed.stream().filter(w -> w.y == start.y)
                        .filter(w -> w != start)
                        .collect(Collectors.toList());

                int aux = 1;
                for (MapStage.Wall wall : onRow) {
                    if (wall.x <= start.x) continue;
                    if (wall.x == start.x + aux) {
                        aux++;
                        useNow.add(wall);
                    }
                }

                if (useNow.size() > 1 && useNow.get(useNow.size() - 1).wallsAround != 5) {
                    shape.setAsBox(0.25f * useNow.size(), 0.25f);
                    fDef.shape = shape;
                    bDef.position.set((start.x * 0.5f) + (useNow.size() / 4.0f), 0.25f + (start.y * 0.5f));
                    Body body = world.createBody(bDef);
                    body.createFixture(fDef);
                    used.addAll(useNow);
                }
            } else {
                exit = true;
            }
        }

        exit = false;
        walls = walls.stream().filter(w -> !used.contains(w)).collect(Collectors.toList());
        while (!exit) {
            List<MapStage.Wall> notUsed = walls.stream().filter(w -> !used.contains(w)).collect(Collectors.toList());
            Optional<MapStage.Wall> op_start = notUsed.stream()
                    .findFirst();
            if (op_start.isPresent()) {
                MapStage.Wall start = op_start.get();
                List<MapStage.Wall> useNow = new ArrayList<>();
                useNow.add(start);

                List<MapStage.Wall> onColumn = notUsed.stream()
                        .filter(w -> w.x == start.x)
                        .filter(w -> w != start)
                        .collect(Collectors.toList());

                int aux = 1;
                for (MapStage.Wall wall : onColumn) {
                    if (wall.y <= start.y) continue;
                    if (wall.y == start.y + aux) {
                        aux++;
                        useNow.add(wall);
                    }
                }

                shape.setAsBox( 0.25f, 0.25f * useNow.size());
                fDef.shape = shape;
                bDef.position.set( bDef.position.set(0.25f + (start.x * 0.5f), (start.y * 0.5f) + (useNow.size()/4.0f)));
                Body body = world.createBody(bDef);
                body.createFixture(fDef);
                used.addAll(useNow);
            }
            else {
                exit = true;
            }
        }

        shape.dispose();

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(stage.width()/4, stage.height()/4);
        groundBody = world.createBody(groundBodyDef);
        PolygonShape groundshape = new PolygonShape();
        groundshape.setAsBox(stage.width()/4 + 0.5f, stage.height()/4 + 0.5f);
        FixtureDef groundFixture = new FixtureDef();
        groundFixture.density=0.0f;
        groundFixture.shape = groundshape;
        groundFixture.restitution = .5f;
        groundFixture.friction=0f;
        Fixture fixture = groundBody.createFixture(groundFixture);
        fixture.setUserData(WorldMapFactory.class);
        groundshape.dispose();

        RayHandler.useDiffuseLight(true);
        RayHandler.setGammaCorrection(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 0.05f);
        rayHandler.setBlurNum(3);
        rayHandler.setShadows(true);


        return world;
    }
}
