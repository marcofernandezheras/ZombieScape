package com.marco.zombiescape;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import mapgenerator.MapStage;
import mapgenerator.Tile;

import java.util.Arrays;

/**
 * Created by marco on 6/02/16.
 */
public class MapTextureFactory {
    private static final int TILE_SIZE = 64;
    private MapTextureFactory() {}
    private static Sprite wall8 = new Sprite(Resources.instance.getRegion("wall", 8));
    private static boolean[] directionAux = new boolean[4];

    private static int getWallsAround(Tile[][] tiles, int x, int y){
        int count = 0;
        for (int xi = x - 1; xi <= x + 1; xi++) {
            for (int yj = y - 1; yj <= y+1; yj++) {
                if(xi == x && yj == y) continue;//Skip self
                if(xi >= 0 && xi < tiles[0].length && yj >= 0 && yj < tiles.length){
                    if(tiles[yj][xi].equals(Tile.WALL)) count++;
                }
            }
        }
        return count;
    }

    private static boolean[] getCardinal(Tile[][] tiles, int x, int y) throws ArrayIndexOutOfBoundsException{
        Arrays.fill(directionAux, false);
        directionAux[0] = !tiles[y+1][x].equals(Tile.WALL);
        directionAux[1] = !tiles[y][x+1].equals(Tile.WALL);
        directionAux[2] = !tiles[y-1][x].equals(Tile.WALL);
        directionAux[3] = !tiles[y][x-1].equals(Tile.WALL);
        return directionAux;
    }

    private static boolean[] getDiagonal(Tile[][] tiles, int x, int y) throws ArrayIndexOutOfBoundsException{
        Arrays.fill(directionAux, false);
        directionAux[0] = !tiles[y+1][x+1].equals(Tile.WALL);
        directionAux[1] = !tiles[y-1][x+1].equals(Tile.WALL);
        directionAux[2] = !tiles[y-1][x-1].equals(Tile.WALL);
        directionAux[3] = !tiles[y+1][x-1].equals(Tile.WALL);
        return directionAux;
    }

    public static Texture getTextureFor(MapStage stage){

        SpriteBatch batch = new SpriteBatch();
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGB888, stage.width() * TILE_SIZE , stage.height() * TILE_SIZE , false);

        Tile[][] tiles = stage.getTiles();

        fbo.begin();

        Resources resources = Resources.instance;

        Sprite floor = new Sprite(resources.getRegion("floor", 0));
        floor.flip(false,true);

        Sprite wall_3 = new Sprite(resources.getRegion("wall", 3));
        wall_3.flip(false, true);

        Sprite wall_5 = new Sprite(resources.getRegion("wall", 5));
        wall_5.flip(false, true);

        Sprite wall_7 = new Sprite(resources.getRegion("wall", 7));
        wall_7.flip(false, true);

        //Set the batch to render the size of fbo
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0,0,fbo.getWidth(), fbo.getHeight());

        batch.setProjectionMatrix(matrix);

        batch.begin();

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                if(tiles[y][x].equals(Tile.WALL)){
                    int wallsAround = getWallsAround(tiles, x, y);
                    switch (wallsAround){
                        case 3:
                            if(paintWall3(tiles, x, y, wall_3, batch))
                                stage.addWall(x,y, wallsAround);
                            break;
                        case 5:
                        case 6:
                            if(paintWall5_6(tiles, x, y, wall_5, batch))
                                stage.addWall(x,y, wallsAround);
                            break;
                        case 7:
                            paintWall7(tiles, x, y, wall_7, batch);
                            break;
                        default:
                            wall8.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                            wall8.draw(batch);
                    }
                }
                else if(tiles[y][x].equals(Tile.DOOR)){
                    //TODO Doors
                    floor.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                    floor.draw(batch);
                }
                else{
                    floor.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                    floor.draw(batch);
                }
            }
        }

        batch.end();
        fbo.end();

        return fbo.getColorBufferTexture();
    }

    private static boolean paintWall7(Tile[][] tiles, int x, int y, Sprite wall7, SpriteBatch batch) {
        try {
            boolean[] diagonal = getDiagonal(tiles, x, y);

            if(diagonal[0] ){
                wall7.flip(true,false);
                wall7.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall7.draw(batch);
                wall7.flip(true,false);
                return true;

            }
            else if(diagonal[1] ){
                wall7.flip(true,true);
                wall7.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall7.draw(batch);
                wall7.flip(true,true);
                return true;
            }
            else if(diagonal[2] ){
                wall7.flip(false,true);
                wall7.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall7.draw(batch);
                wall7.flip(false,true);
                return true;

            }
            else if(diagonal[3]){
                wall7.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall7.draw(batch);
                return true;

            }
        }
        catch (Exception e){
            //wtf!?
            wall8.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
            wall8.draw(batch);
        }
        return false;
    }

    private static boolean paintWall3(Tile[][] tiles, int x, int y, Sprite wall3, SpriteBatch batch) {
        try {
            boolean[] cardinal = getCardinal(tiles, x, y);

            if(cardinal[0] && cardinal[1]){
                wall3.flip(true,false);
                wall3.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall3.draw(batch);
                wall3.flip(true,false);
                return true;
            }
            else if(cardinal[1] && cardinal[2]){
                wall3.flip(true,true);
                wall3.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall3.draw(batch);
                wall3.flip(true,true);
                return true;
            }
            else if(cardinal[2] && cardinal[3]){
                wall3.flip(false,true);
                wall3.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall3.draw(batch);
                wall3.flip(false,true);
                return true;
            }
            else if(cardinal[0] && cardinal[3]){
                wall3.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                wall3.draw(batch);
                return true;
            }
        }
        catch (Exception e){
            //Corners of the map
            wall8.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
            wall8.draw(batch);
        }
        return false;
    }

    private static boolean paintWall5_6(Tile[][] tiles, int x, int y, Sprite sprite, SpriteBatch batch) {

        try {
            boolean[] cardinal = getCardinal(tiles, x, y);

            if(cardinal[0]) {
                sprite.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                sprite.draw(batch);
                return true;
            }
            else if(cardinal[1]){
                sprite.rotate(90);
                sprite.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                sprite.draw(batch);
                sprite.rotate(-90);
                return true;
            }
            else if(cardinal[2]){
                sprite.flip(false,true);
                sprite.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                sprite.draw(batch);
                sprite.flip(false,true);
                return true;
            }
            else if(cardinal[3]){
                sprite.rotate(270);
                sprite.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
                sprite.draw(batch);
                sprite.rotate(-270);
                return true;
            }
        }
        catch (Exception e){
            //Border of the map
            wall8.setPosition(TILE_SIZE * x, (TILE_SIZE * (tiles.length - y))-TILE_SIZE);
            wall8.draw(batch);
        }
        return false;
    }
}