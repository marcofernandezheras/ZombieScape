package mapgenerator;

/**
 * Created by marco on 5/02/16.
 */
public class Tile {

    static final Tile ROOM = new Tile("*", true);
    static final Tile FLOOR = new Tile("#", true);
    static final Tile WALL = new Tile("·", false);
    static final Tile DOOR = new Tile("=", false);
    static final Tile EMPTY = new Tile("EMPTY", true);

    private final String name;
    private final boolean isPassable;

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
