package mapgenerator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.marco.zombiescape.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by marco on 5/02/16.
 */

public class MapStage {

    public boolean blocked(int x, int y) {
        return !tiles[y][x].isPassable();
    }

    public float getCost(int sx, int sy, int tx, int ty) {
        return 1;
    }

    public void pathFinderVisited(int x, int y) {
        tiles[y][x].setVisited(true);
    }

    public static class Wall{
        public int x, y, wallsAround;

        public Wall(int x, int y, int wallsAround) {
            this.x = x;
            this.y = y;
            this.wallsAround = wallsAround;
        }

        @Override
        public String toString() {
            return "["+x+","+y+"]";
        }
    }
    private Tile[][] tiles;

    private List<Wall> walls = new ArrayList<>();
    private List<Vector2> floors = new ArrayList<>();

    private List<Rectangle> rooms = new ArrayList<>();

    public MapStage(int width, int height) {
        this.tiles = new Tile[height][width];
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
        return tiles.length;
    }

    public int width(){
        return tiles[0].length;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void addWall(int x, int y, int around){
        walls.add(new Wall(x,y,around));
    }

    public void addFloor(int x, int y){
        floors.add(new Vector2(x,y));
    }

    public List<Vector2> getFloors() {
        return floors;
    }

    public List<Rectangle> getRooms() {
        return rooms;
    }

    public Rectangle getRandomRoom(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return rooms.get(random.nextInt(0, rooms.size()));
    }

    public Rectangle getOpositeRoom(Rectangle room){
        Rectangle temp = room;
        float maxDistance = 0.0f;
        for (Rectangle rectangle : rooms) {
            float distance = Util.distance(rectangle.x, rectangle.y, room.x, room.y);
            if(distance > maxDistance){
                maxDistance = distance;
                temp = rectangle;
            }
        }
        return temp;
    }

    public void addRoom(int x, int y, int width, int height){
        rooms.add(new Rectangle(x, y, width, height));
    }

    public void print(){
        System.out.println("Map Stage");
        for (int y = tiles.length - 1; y >= 0; y--) {
            for (int x = 0; x < tiles[0].length; x++) {
                System.out.print(tiles[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }
}