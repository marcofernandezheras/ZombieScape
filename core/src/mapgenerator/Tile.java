package mapgenerator;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by marco on 5/02/16.
 */
public class Tile {

    public static final Tile ROOM = new Tile("*", true);
    public static final Tile FLOOR = new Tile("·", true);
    public static final Tile WALL = new Tile("#", false);
    public static final Tile DOOR = new Tile("=", true);
    public static final Tile EMPTY = new Tile(" ", true);

    private final String name;
    private final boolean isPassable;
    private boolean visited = false;

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    private Texture tex = null;


    public Tile(String name, boolean isPassable) {
        this.name = name;
        this.isPassable = isPassable;
    }

    public String getName() {
        return name;
    }

    public boolean isPassable() {
        return isPassable;
    }

    @Override
    public String toString() {
        return name;
    }
}
