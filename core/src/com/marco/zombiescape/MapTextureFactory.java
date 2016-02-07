package com.marco.zombiescape;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private static Texture wall_8_tex;
    private static boolean[] directionAux = new boolean[4];

    private static int getWallsAround(Tile[][] tiles, int x, int y){
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y+1; j++) {
                if(i == x && j == y) continue;//Skip self
                if(i >= 0 && i < tiles[0].length && j >= 0 && j < tiles.length){
                    if(tiles[i][j].equals(Tile.WALL)) count++;
                }
            }
        }
        return count;
    }

    private static boolean[] getCardinal(Tile[][] tiles, int x, int y) throws ArrayIndexOutOfBoundsException{
        Arrays.fill(directionAux, false);
        directionAux[0] = !tiles[x-1][y].equals(Tile.WALL);
        directionAux[1] = !tiles[x][y+1].equals(Tile.WALL);
        directionAux[2] = !tiles[x+1][y].equals(Tile.WALL);
        directionAux[3] = !tiles[x][y-1].equals(Tile.WALL);
        return directionAux;
    }

    private static boolean[] getDiagonal(Tile[][] tiles, int x, int y) throws ArrayIndexOutOfBoundsException{
        Arrays.fill(directionAux, false);
        directionAux[0] = !tiles[x-1][y-1].equals(Tile.WALL);
        directionAux[1] = !tiles[x+1][y-1].equals(Tile.WALL);
        directionAux[2] = !tiles[x+1][y+1].equals(Tile.WALL);
        directionAux[3] = !tiles[x-1][y+1].equals(Tile.WALL);
        return directionAux;
    }

    public static Texture getTextureFor(MapStage stage){

        SpriteBatch batch = new SpriteBatch();
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGB888, stage.width() * TILE_SIZE , stage.height() * TILE_SIZE , false);

        Tile[][] tiles = stage.getTiles();

        fbo.begin();

        Texture floor_tex = new Texture("floor_0.png");
        Texture wall_3_tex = new Texture("wall_3.png");
        Texture wall_5_tex = new Texture("wall_5.png");
        Texture wall_7_tex = new Texture("wall_7.png");

        Texture point_tex = new Texture("point.png");

        wall_8_tex = new Texture("wall_8.png");


        Sprite floor = new Sprite(floor_tex);
        floor.flip(false,true);

        Sprite wall_3 = new Sprite(wall_3_tex);
        wall_3.flip(false, true);

        Sprite wall_5 = new Sprite(wall_5_tex);
        wall_5.flip(false, true);

        Sprite wall_7 = new Sprite(wall_7_tex);
        wall_7.flip(false, true);

        //Set the batch to render the size of fbo
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0,0,fbo.getWidth(), fbo.getHeight());

        batch.setProjectionMatrix(matrix);

        batch.begin();

        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {

                if(tiles[j][i].equals(Tile.WALL)){
                    int wallsAround = getWallsAround(tiles, j, i);
                    switch (wallsAround){
                        case 3:
                            if(paintWall3(tiles, j, i, wall_3, batch))
                                stage.addWall(j,i);
                            break;
                        case 5:
                        case 6:
                            if(paintWall5_6(tiles, j, i, wall_5, batch))
                                stage.addWall(j,i);
                            break;
                        case 7:
                            if(paintWall7(tiles, j, i, wall_7, batch))
                                stage.addWall(j,i);
                            break;
                        default:
                            batch.draw(wall_8_tex,TILE_SIZE * j, TILE_SIZE * i);
                    }
                }
                else if(tiles[j][i].equals(Tile.DOOR)){
                    //TODO Doors
                    floor.setPosition(TILE_SIZE * j, TILE_SIZE * i);
                    floor.draw(batch);
                }
                else{
                    floor.setPosition(TILE_SIZE * j, TILE_SIZE * i);
                    floor.draw(batch);
                }
            }
        }


        batch.end();
        fbo.end();

        //Dispose tile textures
        floor_tex.dispose();
        wall_3_tex.dispose();
        wall_5_tex.dispose();
        wall_7_tex.dispose();

        point_tex.dispose();
        wall_8_tex.dispose();

        return fbo.getColorBufferTexture();
    }

    private static boolean paintWall7(Tile[][] tiles, int x, int y, Sprite sprite, SpriteBatch batch) {
        try {
            boolean[] cardinal = getDiagonal(tiles, x, y);

            if(cardinal[0] ){
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                return true;
            }
            else if(cardinal[1] ){
                sprite.flip(true,false);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(true,false);
                return true;

            }
            else if(cardinal[2] ){
                sprite.flip(true,true);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(true,true);
                return true;
            }
            else if(cardinal[3]){
                sprite.flip(false,true);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(false,true);
                return true;
            }
        }
        catch (Exception e){
            //wtf!?
            batch.draw(wall_8_tex, TILE_SIZE * x, TILE_SIZE * y);
        }
        return false;
    }

    private static boolean paintWall3(Tile[][] tiles, int x, int y, Sprite sprite, SpriteBatch batch) {
        try {
            boolean[] cardinal = getCardinal(tiles, x, y);

            if(cardinal[0] && cardinal[1]){
                sprite.flip(false,true);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(false,true);
                return true;
            }
            else if(cardinal[1] && cardinal[2]){
                sprite.flip(true,true);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(true,true);
                return true;
            }
            else if(cardinal[2] && cardinal[3]){
                sprite.flip(true,false);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(true,false);
                return true;
            }
            else if(cardinal[0] && cardinal[3]){
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                return true;
            }
        }
        catch (Exception e){
            //Corners of the map
            batch.draw(wall_8_tex, TILE_SIZE * x, TILE_SIZE * y);
        }
        return false;
    }

    private static boolean paintWall5_6(Tile[][] tiles, int x, int y, Sprite sprite, SpriteBatch batch) {

        try {
            boolean[] cardinal = getCardinal(tiles, x, y);

            if(cardinal[0]) {
                sprite.rotate(270);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.rotate(-270);
                return true;
            }
            else if(cardinal[1]){
                sprite.flip(false,true);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.flip(false,true);
                return true;
            }
            else if(cardinal[2]){
                sprite.rotate(90);
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                sprite.rotate(-90);
                return true;
            }
            else if(cardinal[3]){
                sprite.setPosition(TILE_SIZE * x, TILE_SIZE * y);
                sprite.draw(batch);
                return true;
            }
        }
        catch (Exception e){
            //Border of the map
            batch.draw(wall_8_tex, TILE_SIZE * x, TILE_SIZE * y);
        }
        return false;
    }
}