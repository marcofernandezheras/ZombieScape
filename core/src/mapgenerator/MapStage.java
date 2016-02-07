package mapgenerator;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by marco on 5/02/16.
 */

public class MapStage {

    public class Room{
        public final int x,y,width, height;

        public Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    private Tile[][] tiles;

    private List<int[]> walls = new ArrayList<>();

    private List<Room> rooms = new ArrayList<>();

    public MapStage(int width, int height) {
        this.tiles = new Tile[width][height];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = Tile.EMPTY;
            }
        }
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int height(){
        return tiles[0].length;
    }

    public int width(){
        return tiles.length;
    }

    public List<int[]> getWalls() {
        return walls;
    }

    public void addWall(int x, int y){
        walls.add(new int[]{x,y});
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRandomRoom(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return rooms.get(random.nextInt(0, rooms.size()));
    }

    public void addRoom(int x, int y, int width, int height){
        rooms.add(new Room(x, y, width, height));
    }

    public void print(){
        System.out.println("Map Stage");
        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                System.out.print(tiles[j][i]);
            }
            System.out.println();
        }
        System.out.println();
    }
}