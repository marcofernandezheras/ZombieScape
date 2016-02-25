package mapgenerator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by marco on 5/02/16.
 */
public class DungeonBuilder extends StageBuilder {

    protected class Junction{
        int a, b;
        Vector2 pos;

        public Junction(int a, int b, Vector2 pos) {
            this.a = a;
            this.b = b;
            this.pos = pos;
        }

        public boolean has(int x){
            return a==x || b==x;
        }
    }

    private static Tile[][] inflate(Tile[][] tiles){
        Tile[][] inflated = new Tile[tiles.length * 2][tiles[0].length * 2];

        int x = 0; int y = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                inflated[x][y] = tiles[i][j];
                inflated[x][y+1] = tiles[i][j];
                inflated[x+1][y] = tiles[i][j];
                inflated[x+1][y+1] = tiles[i][j];
                y+=2;
            }
            y = 0;
            x+=2;
        }
        return inflated;
    }

    private StageBuilderConfig config;
    private List<Rectangle> rooms = new ArrayList<>();

    public DungeonBuilder(StageBuilderConfig config) {
        this.config = config;
    }


    /// For each open position in the dungeon, the index of the connected region
    /// that that position is a part of.
    private static final int NO_REGION = -2;
    int[][] _regions;
    int _currentRegion = -1;

    //Rectangle cache to leave alone the garbage collector
    private Rectangle _room = new Rectangle();
    private Rectangle _auxRoom = new Rectangle();

    @Override
    public void generate(MapStage stage) {
        if (stage.width() % 2 == 0 || stage.height() % 2 == 0) {
            throw new IllegalArgumentException("The stage must be odd-sized.");
        }
        bindStage(stage);

        fill(Tile.WALL);
        _regions = new int[stage.width()][stage.height()];
        for (int[] region : _regions) {
            Arrays.fill(region, NO_REGION);
        }

        _addRooms();

        // Fill in all of the empty space with mazes.
        for (int y = 1; y < stage.height(); y += 2) {
            for (int x = 1; x < stage.width(); x += 2) {
                if (!getTile(x,y).equals(Tile.WALL)) continue;
                _growMaze(x, y);
            }
        }

        _connectRegions();
        _removeDeadEnds();

        stage.setTiles(inflate(stage.getTiles()));
    }

    private void _addRooms() {

        for (int i = 0; i < config.numRoomTries; i++) {

            Rectangle room = randomRoom();
            Rectangle aux = inflateRoom(room);

            boolean overlaps = false;
            for (Rectangle rect : rooms) {
                if(rect.overlaps(aux)){
                    overlaps = true;
                    break;
                }
            }
            if (overlaps) continue;
            rooms.add(new Rectangle(room));
            stage.addRoom((int)room.x, (int)room.y, (int)room.width, (int)room.height);
            carveRoom(room);
        }
    }

    private Rectangle randomRoom(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int size = random.nextInt(1, 3 + config.roomExtraSize )* 2 + 1;
        int rectangular = random.nextInt(0, 1 + size / 2) * 2;
        int width = size;
        int height = size;
        if (random.nextInt(0,2) == 0) {
            width += rectangular;
        } else {
            height += rectangular;
        }

        int x = random.nextInt(0,(stage.width() - width) / 2) * 2 + 1;
        int y = random.nextInt(0,(stage.height() - height) / 2) * 2 + 1;
        return _room.set(x,y,width,height);
    }

    private Rectangle inflateRoom(Rectangle room){
        return _auxRoom.set(room.x-1, room.y-1, room.width+2, room.height+2);
    }

    private void carveRoom(Rectangle room){
        _startRegion();
        for (int w = 0; w < room.width; w++) {
            for (int z = 0; z < room.height; z++) {
                _carve((int)room.x+w, (int)room.y+z, Tile.ROOM);
            }
        }
    }

    private void _removeDeadEnds() {
        boolean done = false;

        while (!done) {
            done = true;

            for (int i = 1; i < stage.width()-1; i++) {
                for (int j = 1; j < stage.height()-1; j++) {
                    if (getTile(i,j).equals(Tile.WALL)) continue;

                    // If it only has one exit, it's a dead end.
                    int exits = 0;
                    for (int w = -1; w <= 1; w++) {
                        for (int z = -1; z <= 1 ; z++) {
                            if((w == 0 || z == 0) && w!=z){
                                if(!getTile(i+w,j+z).equals(Tile.WALL)){
                                    exits++;
                                }
                            }
                        }
                    }

                    if (exits != 1) continue;
                    setTile(i,j, Tile.WALL);
                    done = false;
                }
            }
        }
    }

    private void _connectRegions() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // Find all of the tiles that can connect two regions.
        List<Junction> allJunctions = new ArrayList<>();

        for (int i = 1; i < stage.width()-1; i++) {
            for (int j = 1; j < stage.height()-1; j++) {
                // Can't already be part of a region.
                if (!getTile(i,j).equals(Tile.WALL)) continue;

                List<Integer> regions = new ArrayList<>();
                for (int w = i-1; w <= i+1; w++) {
                    for (int z = j-1; z < j+1; z++) {
                        if(w == i || j == z) {
                            if (w == i && z == j) continue; //Avoid self
                            if (_regions[w][z] > -2 && !regions.contains(_regions[w][z])) regions.add(_regions[w][z]);
                        }
                    }
                }

                if (regions.size() < 2) continue;
                allJunctions.add(new Junction(regions.get(0), regions.get(1), new Vector2(i,j)));
            }
        }

        while (allJunctions.size() > 0){
            Junction junction = allJunctions.get(random.nextInt(0, allJunctions.size()));
            int a = junction.a;
            int b = junction.b;
            List<Junction> junctions = allJunctions.stream().filter(j -> j.has(a)).filter(j -> j.has(b)).collect(Collectors.toList());
            Junction newDoor = junctions.get(random.nextInt(0, junctions.size()));
            _addJunction(newDoor.pos);

            if(random.nextInt(0, 100) < config.extraConnectorChance){//random door
                Junction otherDoor = junctions.get(random.nextInt(0, junctions.size()));
                _addJunction(otherDoor.pos);
            }

            allJunctions.removeAll(junctions);
        }
    }

    private void _growMaze(int x, int y) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<int[]> cells = new ArrayList<>();
        int[] lastDir = null;
        cells.add(new int[]{x,y});

        _startRegion();
        _carve(cells.get(0), Tile.FLOOR);


        while (cells.size() > 0) {
            int[] cell = cells.get(cells.size() - 1);

            // See which adjacent cells are open.
            List<int[]> unmadeCells = new ArrayList<>();

            for (int i = -1; i <= 1 ; i++) {
                for (int j = -1; j <= 1 ; j++) {
                    if(i == 0 || j == 0)
                        if (_canCarve(cell, i, j)) unmadeCells.add(new int[]{i, j});
                }
            }

            if (unmadeCells.size() > 0) {
                // Based on how "windy" passages are, try to prefer carving in the
                // same direction.
                int[] dir;
                final int[] fd = lastDir;
                if (lastDir!= null && unmadeCells.stream().anyMatch(d -> d[0] == fd[0] && d[1] == fd[1]) && random.nextInt(1, 100) > config.windingPercent) {
                    dir = lastDir;
                } else {
                    dir = unmadeCells.get(random.nextInt(0, unmadeCells.size()));
                }

                _carve(cell[0] + dir[0], cell[1] + dir[1], Tile.FLOOR);
                _carve(cell[0] + (dir[0] * 2), cell[1] + (dir[1] *2), Tile.FLOOR);

                cells.add(new int[]{cell[0] + (dir[0] * 2), cell[1] + (dir[1] *2)});

                lastDir = dir;
            } else {
                // No adjacent uncarved cells.
                cells.remove(cells.size()-1);

                // This path has ended.
                lastDir = null;
            }
        }
    }

    private boolean _canCarve(int[] cell, int x, int y) {
        x *= 2;
        y *= 2;
        if(cell[0]+x <= 0 || cell[0]+x >= stage.width()-1) return false;
        if(cell[1]+y <= 0 || cell[1]+y >= stage.height()-1) return false;
        if(x == y) return false;
        return getTile(cell[0]+x, cell[1]+y).equals(Tile.WALL);
    }

    private void _carve(int[] pos, Tile tile) {
        _carve(pos[0], pos[1], tile);
    }

    private void _carve(int x, int y, Tile tile){
        if(x >= 0 && x < stage.width() && y >= 0 && y < stage.height()) {
            setTile(x, y, tile);
            _regions[x][y] = _currentRegion;
        }
    }

    private void _addJunction(Vector2 connector) {
        //TODO other junctions
        setTile((int)connector.x, (int)connector.y, Tile.DOOR);
    }

    private void _startRegion() {
        _currentRegion++;
    }


}
