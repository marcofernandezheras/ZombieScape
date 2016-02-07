package mapgenerator;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by marco on 5/02/16.
 */

public class MapStage {
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    private Tile[][] tiles;
    private List<Rectangle> rooms = new ArrayList<>();

    public MapStage(int width, int height) {
        this.tiles = new Tile[width][height];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = Tile.EMPTY;
            }
        }
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

    public List<Rectangle> getRooms() {
        return rooms;
    }

    public void addRoom(Rectangle room){
        rooms.add(new Rectangle(room));
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